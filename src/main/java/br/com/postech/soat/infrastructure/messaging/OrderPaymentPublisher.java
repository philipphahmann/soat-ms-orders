package br.com.postech.soat.infrastructure.messaging;

import br.com.postech.soat.infrastructure.messaging.dto.PaymentRequestedMessage;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderPaymentPublisher {

    private final SnsTemplate snsTemplate;
    private final String orderPaymentsTopicArn;
    private final Logger logger = LoggerFactory.getLogger(OrderPaymentPublisher.class);

    public OrderPaymentPublisher(SnsTemplate snsTemplate,
            @Value("${app.messaging.order-payments-topic}") String orderPaymentsTopicArn) {
        this.snsTemplate = snsTemplate;
        this.orderPaymentsTopicArn = orderPaymentsTopicArn;
    }

    public void publish(PaymentRequestedMessage message) {
        logger.info("Publishing payment request for order {} to topic {}", message.orderId(), orderPaymentsTopicArn);
        snsTemplate.convertAndSend(orderPaymentsTopicArn, message);
    }
}
