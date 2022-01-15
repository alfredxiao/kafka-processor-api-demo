package xiaoyf.demo.kafkaprocessorapi.transformer;

import demo.model.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xiaoyf.demo.kafkaprocessorapi.Const;
import xiaoyf.demo.kafkaprocessorapi.converter.MonetaryActivityConverter;
import xiaoyf.demo.kafkaprocessorapi.converter.MonetaryActivityStoreKeyConverter;
import xiaoyf.demo.kafkaprocessorapi.converter.MonetaryActivityStoreValueConverter;

import java.util.Objects;

@Component
public class DedupeTransformer implements Transformer<MonetaryActivityKey, MonetaryActivity,
        KeyValue<MonetaryActivityStoreKey, MonetaryActivityStoreValue>> {

    @Autowired MonetaryActivityStoreKeyConverter keyConverter;
    @Autowired MonetaryActivityStoreValueConverter valueConverter;
    @Autowired MonetaryActivityConverter maConverter;

    private KeyValueStore<MonetaryActivityStoreKey, MonetaryActivityStoreValue> store;

    @Override
    public void init(ProcessorContext context) {
        this.store = context.getStateStore(Const.STORE_NAME);
    }

    @Override
    public KeyValue<MonetaryActivityStoreKey, MonetaryActivityStoreValue> transform(MonetaryActivityKey key, MonetaryActivity value) {
        var storeKey = keyConverter.convert(key);
        var storeValue = valueConverter.convert(value);

        MonetaryActivityStoreValue oldStoreValue = store.get(storeKey);

        if (Objects.nonNull(oldStoreValue)) {
            // first time this event is seen
            store.put(storeKey, storeValue);
            return KeyValue.pair(storeKey, storeValue);
        }

        var oldValue = maConverter.convert(oldStoreValue);
        if (oldValue.equals(value)) {
            // same value as previous seen, ignore this event
            return null;
        }

        // event with diff value than previous one, so it is an UPDATE
        storeValue.setEventType(CustomerEventType.UPDATE);
        store.put(storeKey, storeValue);
        return KeyValue.pair(storeKey, storeValue);
    }

    @Override
    public void close() {

    }
}
