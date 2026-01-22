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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPaymentPublisherTest {

    private OrderPaymentPublisher publisher;

    @Mock
    private SnsTemplate snsTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private final String TOPIC_ARN = "arn:aws:sns:us-west-2:123456789:order-payments";

    @BeforeEach
    void setup() {
        publisher = new OrderPaymentPublisher(snsTemplate, objectMapper, TOPIC_ARN);
    }

    @Test
    void publish_ShouldSendSnsMessage_WhenSerializationWorks() throws JsonProcessingException {
        // Cenário
        PaymentRequestedMessage message = new PaymentRequestedMessage(
                UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, "PIX"
        );
        String jsonString = "{\"json\":\"mock\"}";
        
        when(objectMapper.writeValueAsString(message)).thenReturn(jsonString);

        // Execução
        publisher.publish(message);

        // Verificação
        verify(objectMapper).writeValueAsString(message);
        verify(snsTemplate).convertAndSend(eq(TOPIC_ARN), eq(jsonString));
    }

    @Test
    void publish_ShouldThrowException_WhenSerializationFails() throws JsonProcessingException {
        // Cenário
        PaymentRequestedMessage message = new PaymentRequestedMessage(
                UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, "PIX"
        );
        
        when(objectMapper.writeValueAsString(message)).thenThrow(new JsonProcessingException("Error"){});

        // Execução & Verificação
        Assertions.assertThrows(IllegalStateException.class, () -> publisher.publish(message));
        verify(snsTemplate, never()).convertAndSend(anyString(), anyString());
    }
}