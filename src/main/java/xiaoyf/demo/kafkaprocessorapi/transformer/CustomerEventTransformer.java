package xiaoyf.demo.kafkaprocessorapi.transformer;

import demo.model.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xiaoyf.demo.kafkaprocessorapi.converter.CustomerEventConverter;
import xiaoyf.demo.kafkaprocessorapi.converter.CustomerEventKeyConverter;

@Component
public class CustomerEventTransformer implements Transformer<MonetaryActivityStoreKey, MonetaryActivityStoreValue,
        KeyValue<CustomerEventKey, CustomerEvent>> {

    @Autowired
    CustomerEventKeyConverter keyConverter;

    @Autowired
    CustomerEventConverter valueConverter;

    @Override
    public void init(ProcessorContext context) {

    }

    @Override
    public KeyValue<CustomerEventKey, CustomerEvent> transform(MonetaryActivityStoreKey key, MonetaryActivityStoreValue value) {
        return KeyValue.pair(keyConverter.convert(key), valueConverter.convert(value));
    }

    @Override
    public void close() {

    }
}
