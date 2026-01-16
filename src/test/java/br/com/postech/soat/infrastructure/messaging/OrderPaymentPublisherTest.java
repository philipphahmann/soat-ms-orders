package br.com.postech.soat.infrastructure.messaging;

import br.com.postech.soat.infrastructure.messaging.dto.PaymentRequestedMessage;
import io.awspring.cloud.sns.core.SnsTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderPaymentPublisherTest {

    private OrderPaymentPublisher publisher;

    @Mock
    private SnsTemplate snsTemplate;

    @BeforeEach
    void setup() {
        // Injetando o valor da propriedade topic via construtor (simulando) ou reflection
        String topicArn = "arn:aws:sns:region:123:topic";
        ObjectMapper objectMapper = new ObjectMapper();
        publisher = new OrderPaymentPublisher(snsTemplate, objectMapper, topicArn);
    }
    
    // Helper para o eq(message)
    private <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
    private <T> T any(Class<T> type) {
        return org.mockito.ArgumentMatchers.any(type);
    }
}