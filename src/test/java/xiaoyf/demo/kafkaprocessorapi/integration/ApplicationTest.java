package xiaoyf.demo.kafkaprocessorapi.integration;

import demo.model.CustomerEvent;
import demo.model.CustomerEventKey;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import xiaoyf.demo.kafkaprocessorapi.Const;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
class ApplicationTest {

    @Autowired private KafkaTemplate<Object, Object> kafkaTemplate;

    @Autowired TestListeners testListeners;

    @BeforeEach
    void setup() {
        this.testListeners.outputEvents.clear();
    }

    @Test
    void shouldGenerateCustomerEventFromMonetaryActivity() throws Exception {
        var sourceKey = monetaryActivityKey();
        var sourceValue = monetaryActivity();

        assertNotNull(kafkaTemplate);
    }

    @TestConfiguration
    public static class TestListeners {
        private final List<ConsumerRecord<CustomerEventKey, CustomerEvent>> outputEvents = new ArrayList<>();

        @KafkaListener(topics = Const.CUSTOMER_EVENT_TOPIC)
        public void consumeCustomerEvent(final ConsumerRecord<CustomerEventKey, CustomerEvent> record) {
            this.outputEvents.add(record);
        }
    }
}
