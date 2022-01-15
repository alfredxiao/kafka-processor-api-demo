package xiaoyf.demo.kafkaprocessorapi;

public interface Const {
    String STORE_NAME = "monetaryActivityStore";
    String MONETARY_ACTIVITY_TOPIC = "monetaryActivity";
    String CUSTOMER_EVENT_TOPIC = "customerEvent";
    String CHANGELOG_TOPIC = "processor-api-demo-" + STORE_NAME + "-changelog";

}
