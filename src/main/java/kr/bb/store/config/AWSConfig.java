package kr.bb.store.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

@Configuration
public class AWSConfig {
    @Value("${cloud.aws.credentials.ACCESS_KEY_ID}")
    private String accessKeyId;

    @Value("${cloud.aws.credentials.SECRET_ACCESS_KEY}")
    private String secretAccessKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    public AwsCredentialsProvider getAwsCredentials() {
        AwsBasicCredentials awsBasicCredentials =
                AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        return () -> awsBasicCredentials;
    }

    @Primary
    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        return AmazonSQSAsyncClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }

}