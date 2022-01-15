package xiaoyf.demo.kafkaprocessorapi.converter;

import demo.model.CustomerEvent;
import demo.model.MonetaryActivity;
import demo.model.MonetaryActivityStoreValue;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventConverter implements Converter<MonetaryActivityStoreValue, CustomerEvent> {
    @Override
    public CustomerEvent convert(MonetaryActivityStoreValue source) {
        return CustomerEvent.newBuilder()
                .setUserId(source.getUserId())
                .setTransactionId(source.getTransactionId())
                .setAmount(source.getAmount())
                .setCategory(source.getCategory())
                .setEventType(source.getEventType())
                .build();
    }
}
