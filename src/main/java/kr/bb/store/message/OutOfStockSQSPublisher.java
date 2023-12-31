package kr.bb.store.message;

import bloomingblooms.domain.notification.NotificationData;
import bloomingblooms.domain.notification.NotificationKind;
import bloomingblooms.domain.notification.NotificationURL;
import bloomingblooms.domain.notification.PublishNotificationInformation;
import bloomingblooms.domain.notification.stock.OutOfStockNotification;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutOfStockSQSPublisher {
    private final AmazonSQS sqs;
    private final ObjectMapper objectMapper;

    @Value("${cloud.aws.sqs.out-of-stock-notification-queue.url}")
    private String queueUrl;

    public void publish(Long storeId) {
        try {
            OutOfStockNotification outOfStockNotification = OutOfStockNotification.builder()
                    .storeId(storeId)
                    .build();
            PublishNotificationInformation notificationInformation =
                    PublishNotificationInformation.getData(NotificationURL.OUT_OF_STOCK, NotificationKind.OUT_OF_STOCK);
            NotificationData<OutOfStockNotification> outOfStockNotificationData =
                    NotificationData.notifyData(outOfStockNotification, notificationInformation);
            SendMessageRequest sendMessageRequest = new SendMessageRequest(
                    queueUrl, objectMapper.writeValueAsString(outOfStockNotificationData)
            );
            sqs.sendMessage(sendMessageRequest);
            log.info("outOfStock sqs published to store {}. message kind is : {}", storeId, NotificationKind.OUT_OF_STOCK);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}