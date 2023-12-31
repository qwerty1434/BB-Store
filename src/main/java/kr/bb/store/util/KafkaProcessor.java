package kr.bb.store.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProcessor<T> {
    private final KafkaTemplate<String,T> kafkaTemplate;

    public void send(String topicName, T data) {
        kafkaTemplate.send(topicName, data);
        log.info("kafka send data[{}] to topic[{}]", data, topicName);
    }
}
