package xiaoyf.demo.kafkaprocessorapi.topology;

import demo.model.MonetaryActivity;
import demo.model.MonetaryActivityKey;
import demo.model.MonetaryActivityStoreKey;
import demo.model.MonetaryActivityStoreValue;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xiaoyf.demo.kafkaprocessorapi.Const;

@Configuration
public class MonetaryActivityStore {

    @Bean
    public StoreBuilder<KeyValueStore<MonetaryActivityStoreKey, MonetaryActivityStoreValue>> createStoreBuilder(
            Serde<MonetaryActivityStoreKey> keySerde,
            Serde<MonetaryActivityStoreValue> valueSerde
    ) {
        return Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(Const.STORE_NAME),
                keySerde,
                valueSerde
        ).withCachingEnabled();
    }

    @Bean
    Serde<MonetaryActivityStoreKey> createStoreKeySerde() {
        return new SpecificAvroSerde<>();
    }

    @Bean
    Serde<MonetaryActivityStoreValue> createStoreValueSerde() {
        return new SpecificAvroSerde<>();
    }
}
