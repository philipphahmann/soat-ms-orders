package br.com.postech.soat.infrastructure.http;

import br.com.postech.soat.commons.application.Pagination;
import br.com.postech.soat.openapi.api.OrderApi;
import br.com.postech.soat.openapi.model.GetOrders200ResponseInnerDto;
import br.com.postech.soat.openapi.model.PostOrders201ResponseDto;
import br.com.postech.soat.openapi.model.PostOrdersRequestDto;
import br.com.postech.soat.application.command.CreateOrderCommand;
import br.com.postech.soat.application.repositories.OrderRepository;
import br.com.postech.soat.application.usecases.CreateOrderUseCase;
import br.com.postech.soat.application.usecases.ListActiveOrdersUseCase;
import br.com.postech.soat.domain.entity.Order;
import br.com.postech.soat.infrastructure.http.mapper.CreateOrderCommandMapper;
import br.com.postech.soat.infrastructure.http.mapper.OrderResponseMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController implements OrderApi {

    private final CreateOrderUseCase createOrderUseCase;
    private final ListActiveOrdersUseCase listActiveOrdersUseCase;

    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(CreateOrderUseCase createOrderUseCase, OrderRepository orderRepository) {
        this.createOrderUseCase = createOrderUseCase;
        this.listActiveOrdersUseCase = new ListActiveOrdersUseCase(orderRepository);
    }

    @Override
    public ResponseEntity<List<GetOrders200ResponseInnerDto>> getOrders(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        logger.info("Retrieving active orders list with pagination - page: {}, size: {}", page, size);

        final Pagination pagination = new Pagination(page, size);
        final List<Order> orders = listActiveOrdersUseCase.execute(pagination);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        final List<GetOrders200ResponseInnerDto> response = orders.stream()
                .map(OrderResponseMapper.INSTANCE::toListResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PostOrders201ResponseDto> postOrders(PostOrdersRequestDto postOrdersRequest) {
        logger.info("Initiating order creation process: {}", postOrdersRequest);
        final CreateOrderCommand command = CreateOrderCommandMapper.INSTANCE.mapFrom(postOrdersRequest);
        final Order orderCrated = createOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponseMapper.INSTANCE.toResponse(orderCrated));
    }
}
