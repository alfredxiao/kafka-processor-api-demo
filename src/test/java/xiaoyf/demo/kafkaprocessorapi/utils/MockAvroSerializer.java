package xiaoyf.demo.kafkaprocessorapi.utils;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import java.util.Map;

public class MockAvroSerializer extends KafkaAvroSerializer {

    public MockAvroSerializer() {
        super.autoRegisterSchema = true;
        super.schemaRegistry = new MockSchemaRegistryClient();
    }

    public MockAvroSerializer(SchemaRegistryClient client) {
        super(new MockSchemaRegistryClient());
    }

    public MockAvroSerializer(SchemaRegistryClient client, Map<String, ?> props) {
        super(new MockSchemaRegistryClient(), props);
    }

    // needed?
    @Override
    public byte[] serialize(String topic, Object record) {
        return super.serialize(topic, record);
    }
}
