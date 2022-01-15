package xiaoyf.demo.kafkaprocessorapi.converter;

import demo.model.MonetaryActivity;
import demo.model.MonetaryActivityKey;
import demo.model.MonetaryActivityStoreKey;
import demo.model.MonetaryActivityStoreValue;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MonetaryActivityStoreValueConverter implements Converter<MonetaryActivity, MonetaryActivityStoreValue> {
    @Override
    public MonetaryActivityStoreValue convert(MonetaryActivity source) {
        return MonetaryActivityStoreValue.newBuilder()
                .setUserId(source.getUserId())
                .setTransactionId(source.getTransactionId())
                .setAmount(source.getAmount())
                .setCategory(source.getCategory())
                .build();
    }
}
