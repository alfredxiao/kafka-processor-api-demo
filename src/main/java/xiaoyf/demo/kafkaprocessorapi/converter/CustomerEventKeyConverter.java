package xiaoyf.demo.kafkaprocessorapi.converter;

import demo.model.CustomerEventKey;
import demo.model.MonetaryActivityStoreKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventKeyConverter implements Converter<MonetaryActivityStoreKey, CustomerEventKey> {
    @Override
    public CustomerEventKey convert(MonetaryActivityStoreKey source) {
        return CustomerEventKey.newBuilder()
                .setUserId(source.getUserId())
                .setTransactionId(source.getTransactionId())
                .build();
    }
}
