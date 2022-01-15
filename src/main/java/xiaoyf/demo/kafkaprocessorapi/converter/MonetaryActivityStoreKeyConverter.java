package xiaoyf.demo.kafkaprocessorapi.converter;

import demo.model.MonetaryActivityKey;
import demo.model.MonetaryActivityStoreKey;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MonetaryActivityStoreKeyConverter implements Converter<MonetaryActivityKey, MonetaryActivityStoreKey> {
    @Override
    public MonetaryActivityStoreKey convert(MonetaryActivityKey source) {
        return MonetaryActivityStoreKey.newBuilder()
                .setUserId(source.getUserId())
                .setTransactionId(source.getTransactionId())
                .build();
    }
}
