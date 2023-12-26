package kr.bb.store.message;

import bloomingblooms.domain.notification.NotificationData;
import bloomingblooms.domain.notification.NotificationKind;
import bloomingblooms.domain.notification.NotificationURL;
import bloomingblooms.domain.notification.PublishNotificationInformation;
import bloomingblooms.domain.notification.question.QuestionRegister;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionSQSPublisher {
    private final AmazonSQS sqs;
    private final ObjectMapper objectMapper;

    @Value("${cloud.aws.sqs.question-register-notification-queue.url}")
    private String queueUrl;

    public void publish(Long storeId) {
        try {
            QuestionRegister questionRegister = QuestionRegister.builder()
                    .storeId(storeId)
                    .build();
            PublishNotificationInformation notificationInformation =
                    PublishNotificationInformation.getData(NotificationURL.QUESTION, NotificationKind.QUESTION);
            NotificationData<QuestionRegister> questionRegisterNotificationData =
                    NotificationData.notifyData(questionRegister, notificationInformation);
            SendMessageRequest sendMessageRequest = new SendMessageRequest(
                    queueUrl, objectMapper.writeValueAsString(questionRegisterNotificationData)
            );
            sqs.sendMessage(sendMessageRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
