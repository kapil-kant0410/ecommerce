package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.order.OrderResponseDto;
import com.ql.ecommerce.entity.Address;
import com.ql.ecommerce.entity.Order;
import com.ql.ecommerce.entity.PaymentMethod;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class OrderMapper {

    private final AddressMapper addressMapper;
    private final UserMapper userMapper;
    private final PaymentMethodMapper paymentMethodMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper,AddressMapper addressMapper,UserMapper userMapper,PaymentMethodMapper paymentMethodMapper){
        this.addressMapper=addressMapper;
        this.userMapper=userMapper;
        this.paymentMethodMapper=paymentMethodMapper;
        this.orderItemMapper=orderItemMapper;
    }

    public static Order createOrder(User customer, Address shippingAddress, PaymentMethod paymentMethod, Long totalAmount){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PROCESSING);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setTotalAmount(totalAmount);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public OrderResponseDto toDto(Order order){
      return OrderResponseDto.builder()
              .id(order.getId())
              .shippingAddress(addressMapper.toDto(order.getShippingAddress()))
              .paymentMethod(paymentMethodMapper.toDto(order.getPaymentMethod()))
              .customer(userMapper.toDto(order.getCustomer()))
              .orderItems(orderItemMapper.toDtoList(order.getOrderItems()))
              .orderNumber(order.getOrderNumber())
              .status(order.getStatus())
              .totalAmount(order.getTotalAmount())
              .createdAt(order.getCreatedAt())
              .updatedAt(order.getUpdatedAt())
              .build();

    }

    public List<OrderResponseDto> toDtoList(List<Order> orders){
       return orders.stream().map(this::toDto) .toList();
    }


}
