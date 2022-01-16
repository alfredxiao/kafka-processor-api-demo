package xiaoyf.demo.kafkaprocessorapi.utils;

import demo.model.*;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.springframework.test.util.ReflectionTestUtils;
import org.apache.commons.lang3.tuple.Pair;
import xiaoyf.demo.kafkaprocessorapi.Const;

import java.util.Map;

@Slf4j
public class MockAvroDeserializer extends KafkaAvroDeserializer {

    private static final Map<Pair<String, Boolean>, Schema> SCHEMA_MAP = Map.of(
            Pair.of(Const.MONETARY_ACTIVITY_TOPIC, true), MonetaryActivityKey.SCHEMA$,
            Pair.of(Const.MONETARY_ACTIVITY_TOPIC, false), MonetaryActivity.SCHEMA$,
            Pair.of(Const.CHANGELOG_TOPIC, true), MonetaryActivityStoreKey.SCHEMA$,
            Pair.of(Const.CHANGELOG_TOPIC, false), MonetaryActivityStoreValue.SCHEMA$,
            Pair.of(Const.CUSTOMER_EVENT_TOPIC, true), CustomerEventKey.SCHEMA$,
            Pair.of(Const.CUSTOMER_EVENT_TOPIC, false), CustomerEvent.SCHEMA$
    );

    @Override
    public Object deserialize(String topic, byte[] bytes) {
        log.info("MockAvroDeserializer.deserialize() {}", topic);
        Pair<String, Object> entry = Pair.of(topic, ReflectionTestUtils.getField(this, "isKey"));

        this.schemaRegistry = mockSchemaRegistryClient(entry);

        super.useSpecificAvroReader = true;
        return super.deserialize(topic, bytes);
    }

    private SchemaRegistryClient mockSchemaRegistryClient(Pair<String, Object> entry) {
        return new MockSchemaRegistryClient() {
            @Override
            public synchronized ParsedSchema getSchemaById(int id) {
                return new AvroSchema(SCHEMA_MAP.get(entry));
            }
        };
    }
}
