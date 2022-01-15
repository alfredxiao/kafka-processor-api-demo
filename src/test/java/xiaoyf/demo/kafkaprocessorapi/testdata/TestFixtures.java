package xiaoyf.demo.kafkaprocessorapi.testdata;

import demo.model.MonetaryActivity;
import demo.model.MonetaryActivityKey;

import java.math.BigDecimal;

public class TestFixtures {
    private static final String USER_ID = "user001";
    private static final String TX_ID = "tx001";
    private static final String CATEGORY = "Travel";
    private static final BigDecimal AMOUNT = new BigDecimal("789.00");

    public static MonetaryActivityKey monetaryActivityKey() {
        return MonetaryActivityKey.newBuilder()
                .setUserId(USER_ID)
                .setTransactionId(TX_ID)
                .build();
    }

    public static MonetaryActivity monetaryActivity() {
        return MonetaryActivity.newBuilder()
                .setUserId(USER_ID)
                .setTransactionId(TX_ID)
                .setCategory(CATEGORY)
                .setAmount(AMOUNT)
                .build();
    }
}
