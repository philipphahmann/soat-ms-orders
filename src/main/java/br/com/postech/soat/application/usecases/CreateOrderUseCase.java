package br.com.postech.soat.application.usecases;

import br.com.postech.soat.commons.infrastructure.exception.NotFoundException;
import br.com.postech.soat.commons.application.gateways.CustomerGateway;
import br.com.postech.soat.commons.application.gateways.ProductGateway;
import br.com.postech.soat.commons.infrastructure.aop.monitorable.Monitorable;
import br.com.postech.soat.application.command.CreateOrderCommand;
import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.domain.valueobject.CustomerId;
import br.com.postech.soat.infrastructure.messaging.OrderPaymentPublisher;
import br.com.postech.soat.infrastructure.messaging.dto.PaymentRequestedMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Monitorable
public class CreateOrderUseCase {
    private final OrderRepository orderRepository;
    private final OrderPaymentPublisher orderPaymentPublisher;

    // Gateways para comunicação com outros microsserviços
    private final CustomerGateway customerGateway;
    private final ProductGateway productGateway;

    @Value("${app.messaging.default-payment-method:PIX}")
    private String defaultPaymentMethod;

    private final Logger logger = LoggerFactory.getLogger(CreateOrderUseCase.class);

    @Transactional
    public Order execute(CreateOrderCommand command) {
        try {
            var customer = customerGateway.findCustomer(command.cpf())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + command.cpf()));

            for (var item : command.orderItems()) {
                // 1. Busca os dados reais no microsserviço de produtos
                var productDto = productGateway.findProduct(item.getProductId())
                    .orElseThrow(() -> new NotFoundException("Produto não encontrado: " + item.getProductId()));
                
                // 2. Segurança: Atualiza o preço com o valor oficial
                item.updateCurrentPrice(productDto.price());
            }

            final Order order = Order.receive(
                new CustomerId(customer.id()),
                command.orderItems(),
                command.discounts(),
                command.observations());
            order.prepare();
            
            logger.info("Domain order created: {}", order);
            final Order savedOrder = orderRepository.save(order);

            publishPaymentRequest(savedOrder);
            
            return savedOrder;
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void publishPaymentRequest(Order order) {
        try {
            PaymentRequestedMessage message = new PaymentRequestedMessage(
                    order.getId().getValue(),
                    order.getCustomerId().getValue(),
                    order.getTotalPrice(),
                    defaultPaymentMethod);
            orderPaymentPublisher.publish(message);
        } catch (Exception exception) {
            logger.error(
                    "Error publishing payment request for order {}: {}",
                    order.getId().getValue(),
                    exception.getMessage(),
                    exception);
        }
    }
}
