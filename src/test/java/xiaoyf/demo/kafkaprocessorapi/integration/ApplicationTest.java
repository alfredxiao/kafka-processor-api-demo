package xiaoyf.demo.kafkaprocessorapi.integration;

import demo.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import xiaoyf.demo.kafkaprocessorapi.Const;
import xiaoyf.demo.kafkaprocessorapi.utils.MockSpecificAvroSerde;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static xiaoyf.demo.kafkaprocessorapi.testdata.TestFixtures.monetaryActivity;
import static xiaoyf.demo.kafkaprocessorapi.testdata.TestFixtures.monetaryActivityKey;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(
        topics = {Const.MONETARY_ACTIVITY_TOPIC, Const.CUSTOMER_EVENT_TOPIC},
        brokerProperties = {
                "log.dirs=./build/kafka-logs",
                "log.cleaner.enabled=false"
        }
)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Slf4j
class ApplicationTest {

    @Autowired private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired TestListeners testListeners;

    @BeforeEach
    void setup() {
        this.testListeners.outputEvents.clear();
    }

    @AfterEach
    void clean() throws IOException {
        File stateDir = new File("./build/tmp/state-dir");
        FileUtils.deleteDirectory(stateDir);
    }

    @Test
    void shouldGenerateCustomerEventFromMonetaryActivity() throws Exception {
        log.info("RunningTest shouldGenerateCustomerEventFromMonetaryActivity");

        var sourceKey = monetaryActivityKey();
        var sourceValue = monetaryActivity();

        assertNotNull(kafkaTemplate);

        kafkaTemplate.send(Const.MONETARY_ACTIVITY_TOPIC, sourceKey, sourceValue);

        Awaitility.await().timeout(30, TimeUnit.SECONDS).until(() ->
                this.testListeners.outputEvents.size() == 1);

        log.info("FinishedTest shouldGenerateCustomerEventFromMonetaryActivity");
    }

    @Test
    void shouldDedupeMonetaryActivity() throws Exception {
        log.info("RunningTest shouldDedupeMonetaryActivity");
        var sourceKey = monetaryActivityKey();
        var sourceValue = monetaryActivity();

        assertNotNull(kafkaTemplate);

        kafkaTemplate.send(Const.MONETARY_ACTIVITY_TOPIC, sourceKey, sourceValue);
        kafkaTemplate.send(Const.MONETARY_ACTIVITY_TOPIC, sourceKey, sourceValue);
        kafkaTemplate.send(Const.MONETARY_ACTIVITY_TOPIC, sourceKey, sourceValue);

        Awaitility.await().timeout(20, TimeUnit.SECONDS).until(() ->
                this.testListeners.outputEvents.size() == 1);

        log.info("FinishedTest shouldDedupeMonetaryActivity");
    }

    @Test
    void shouldDeriveEventType() throws Exception {
        log.info("RunningTest shouldDeriveEventType");
        var sourceKey = monetaryActivityKey();
        var sourceValue1 = monetaryActivity();
        var sourceValue2 = monetaryActivity();
        sourceValue2.setCategory("Games");
        sourceValue2.setAmount(new BigDecimal("20.50"));

        assertNotNull(kafkaTemplate);

        kafkaTemplate.send(Const.MONETARY_ACTIVITY_TOPIC, sourceKey, sourceValue1);
        kafkaTemplate.send(Const.MONETARY_ACTIVITY_TOPIC, sourceKey, sourceValue2);

        Awaitility.await().timeout(20, TimeUnit.SECONDS).until(() ->
                this.testListeners.outputEvents.size() == 2);

        var output = testListeners.outputEvents.get(1).value();

        assertThat(output.getEventType()).isEqualTo(CustomerEventType.UPDATE);

        log.info("FinishedTest shouldDeriveEventType");
    }

    @TestConfiguration
    @Slf4j
    public static class TestListeners {
        private final List<ConsumerRecord<CustomerEventKey, CustomerEvent>> outputEvents = new ArrayList<>();

        @KafkaListener(topics = Const.CUSTOMER_EVENT_TOPIC)
        public void consumeCustomerEvent(final ConsumerRecord<CustomerEventKey, CustomerEvent> record, Acknowledgment ack) {
            log.info("Seen output event: {}", record);
            this.outputEvents.add(record);
            ack.acknowledge();
        }

        @Bean
        @Primary
        Serde<MonetaryActivityStoreKey> storeKeySerde() {
            return new MockSpecificAvroSerde<>();
        }

        @Bean
        @Primary
        Serde<MonetaryActivityStoreValue> storeValueSerde() {
            return new MockSpecificAvroSerde<>();
        }
    }
}
