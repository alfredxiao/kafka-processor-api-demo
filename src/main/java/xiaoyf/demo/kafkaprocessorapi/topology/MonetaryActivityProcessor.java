package xiaoyf.demo.kafkaprocessorapi.topology;

import demo.model.MonetaryActivity;
import demo.model.MonetaryActivityKey;
import demo.model.MonetaryActivityStoreKey;
import demo.model.MonetaryActivityStoreValue;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xiaoyf.demo.kafkaprocessorapi.Const;
import xiaoyf.demo.kafkaprocessorapi.transformer.CustomerEventTransformer;
import xiaoyf.demo.kafkaprocessorapi.transformer.DedupeTransformer;

import static xiaoyf.demo.kafkaprocessorapi.Const.MONETARY_ACTIVITY_TOPIC;

@RequiredArgsConstructor
@Configuration
public class MonetaryActivityProcessor {

    private final DedupeTransformer dedupeTransformer;
    private final CustomerEventTransformer customerEventTransformer;
    private final StoreBuilder<KeyValueStore<MonetaryActivityStoreKey, MonetaryActivityStoreValue>> storeBuilder;

    @Bean
    public KStream<MonetaryActivityKey, MonetaryActivity> processMonetaryActivity(final StreamsBuilder builder) {
        builder.addStateStore(storeBuilder);

        KStream<MonetaryActivityKey, MonetaryActivity> stream = builder.stream(MONETARY_ACTIVITY_TOPIC);

        stream
                .transform(
                        () -> dedupeTransformer,
                        Const.STORE_NAME)
                .transform(
                        () -> customerEventTransformer,
                        Named.as("Transform"))
                .to(Const.CUSTOMER_EVENT_TOPIC);

        return stream;
    }
}
