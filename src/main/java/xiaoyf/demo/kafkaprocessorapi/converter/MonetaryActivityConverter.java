package xiaoyf.demo.kafkaprocessorapi.converter;

import demo.model.MonetaryActivity;
import demo.model.MonetaryActivityStoreValue;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MonetaryActivityConverter implements Converter<MonetaryActivityStoreValue, MonetaryActivity> {
    @Override
    public MonetaryActivity convert(MonetaryActivityStoreValue source) {
        return MonetaryActivity.newBuilder()
                .setUserId(source.getUserId())
                .setTransactionId(source.getTransactionId())
                .setAmount(source.getAmount())
                .setCategory(source.getCategory())
                .build();
    }
}
