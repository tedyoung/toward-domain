package dev.ted.towarddomain.usecase;

import dev.ted.towarddomain.domain.Order;
import dev.ted.towarddomain.domain.OrderStatus;
import dev.ted.towarddomain.doubles.TestOrderRepository;
import dev.ted.towarddomain.doubles.TestShipmentService;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class OrderShipmentUseCaseTest {
    private final TestOrderRepository orderRepository = new TestOrderRepository();
    private final TestShipmentService shipmentService = new TestShipmentService();
    private final OrderShipmentUseCase useCase = new OrderShipmentUseCase(orderRepository, shipmentService);

    @Test
    public void shipApprovedOrder() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setId(1);
        initialOrder.setStatus(OrderStatus.APPROVED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(1);

        useCase.run(request);

        assertThat(orderRepository.getSavedOrder().getStatus())
            .isEqualByComparingTo(OrderStatus.SHIPPED);
        assertThat(shipmentService.getShippedOrder())
            .isEqualTo(initialOrder);
    }

    @Test
    public void createdOrdersCannotBeShipped() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setId(1);
        initialOrder.setStatus(OrderStatus.CREATED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(1);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isExactlyInstanceOf(OrderCannotBeShippedException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
        assertThat(shipmentService.getShippedOrder())
            .isNull();
    }

    @Test
    public void rejectedOrdersCannotBeShipped() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setId(1);
        initialOrder.setStatus(OrderStatus.REJECTED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(1);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isExactlyInstanceOf(OrderCannotBeShippedException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
        assertThat(shipmentService.getShippedOrder())
            .isNull();
    }

    @Test
    public void shippedOrdersCannotBeShippedAgain() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setId(1);
        initialOrder.setStatus(OrderStatus.SHIPPED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(1);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isExactlyInstanceOf(OrderCannotBeShippedTwiceException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
        assertThat(shipmentService.getShippedOrder())
            .isNull();
    }
}
