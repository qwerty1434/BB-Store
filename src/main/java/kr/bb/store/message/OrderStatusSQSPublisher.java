package kr.bb.store.message;

import bloomingblooms.domain.notification.NotificationData;
import bloomingblooms.domain.notification.NotificationKind;
import bloomingblooms.domain.notification.NotificationURL;
import bloomingblooms.domain.notification.PublishNotificationInformation;
import bloomingblooms.domain.order.OrderStatusNotification;
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
public class OrderStatusSQSPublisher {
    private final AmazonSQS sqs;
    private final ObjectMapper objectMapper;

    @Value("${cloud.aws.sqs.new-order-status-queue.url}")
    private String queueUrl;

    public void publish(Long userId, String phoneNumber, NotificationKind notificationKind) {
        try {
            OrderStatusNotification orderStatusNotification = OrderStatusNotification.builder()
                    .userId(userId)
                    .phoneNumber(phoneNumber)
                    .build();
            PublishNotificationInformation notificationInformation =
                    PublishNotificationInformation.getData(NotificationURL.ORDER_FAIL, notificationKind);
            NotificationData<OrderStatusNotification> orderStatusNotificationData =
                    NotificationData.notifyData(orderStatusNotification, notificationInformation);
            SendMessageRequest sendMessageRequest = new SendMessageRequest(
                    queueUrl, objectMapper.writeValueAsString(orderStatusNotificationData)
            );
            sqs.sendMessage(sendMessageRequest);
            log.info("orderStatus Change sqs published to user {}. message kind is : {}", userId, notificationKind);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

