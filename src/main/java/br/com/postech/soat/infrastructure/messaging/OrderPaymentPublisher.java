package br.com.postech.soat.infrastructure.messaging;

import br.com.postech.soat.infrastructure.messaging.dto.PaymentRequestedMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderPaymentPublisher {

    private static final Logger logger = LoggerFactory.getLogger(OrderPaymentPublisher.class);

    private final SnsTemplate snsTemplate;
    private final ObjectMapper objectMapper;
    private final String orderPaymentsTopicArn;

    public OrderPaymentPublisher(
            SnsTemplate snsTemplate,
            ObjectMapper objectMapper,
            @Value("${app.messaging.order-payments-topic}") String orderPaymentsTopicArn
    ) {
        this.snsTemplate = snsTemplate;
        this.objectMapper = objectMapper;
        this.orderPaymentsTopicArn = orderPaymentsTopicArn;
    }

    public void publish(PaymentRequestedMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);

            logger.info(
                    "Publishing payment request for order {} to topic {} | payload={}",
                    message.orderId(),
                    orderPaymentsTopicArn,
                    json
            );

            snsTemplate.convertAndSend(orderPaymentsTopicArn, json);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Erro ao serializar PaymentRequestedMessage para JSON", e
            );
        }
    }
}

