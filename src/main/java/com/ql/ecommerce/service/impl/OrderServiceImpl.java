package com.ql.ecommerce.service.impl;

import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.dto.order.OrderResponseDto;
import com.ql.ecommerce.dto.order.PlaceOrderRequestDto;
import com.ql.ecommerce.entity.*;
import com.ql.ecommerce.enums.OrderStatus;
import com.ql.ecommerce.enums.Role;
import com.ql.ecommerce.exception.*;
import com.ql.ecommerce.mapper.OrderItemMapper;
import com.ql.ecommerce.mapper.OrderMapper;
import com.ql.ecommerce.repository.*;
import com.ql.ecommerce.security.AuthUtil;
import com.ql.ecommerce.service.OrderService;
import com.ql.ecommerce.util.ResponseBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ResponseBuilder responseBuilder;

    public OrderServiceImpl(ResponseBuilder responseBuilder,OrderItemRepository orderItemRepository,OrderRepository orderRepository,OrderMapper orderMapper,OrderItemMapper orderItemMapper,CartItemRepository cartItemRepository,AddressRepository addressRepository,PaymentMethodRepository paymentMethodRepository,AuthUtil authUtil,UserRepository userRepository){
        this.authUtil=authUtil;
        this.userRepository=userRepository;
        this.addressRepository=addressRepository;
        this.paymentMethodRepository=paymentMethodRepository;
        this.cartItemRepository=cartItemRepository;
        this.orderMapper=orderMapper;
        this.orderItemMapper=orderItemMapper;
        this.orderRepository=orderRepository;
        this.orderItemRepository=orderItemRepository;
        this.responseBuilder=responseBuilder;
    }


    public ResponseEntity<ApiResponse<Map<String, Object>>> placeOrder(PlaceOrderRequestDto placeOrderRequestDto){

          String email=authUtil.getCurrentUserEmail();
          User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFound("User not found with email: " + email));

          if(!customer.getRole().equals(Role.ROLE_CUSTOMER)){
              throw new Forbidden("Only customers can place orders.");
          }

          Long addressId=placeOrderRequestDto.getAddressId();
          Long paymentMethodId=placeOrderRequestDto.getPaymentMethodId();

         Address shippingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFound("Shipping address not found with ID: " + addressId));

         PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
               .orElseThrow(() -> new PaymentMethodNotFound("Payment method not found with ID: " + paymentMethodId));

         Cart cart=customer.getCart();

         if(cart==null){
             throw new CartNotFound("Cart not found for user: " + email);
         }

         List<CartItem> cartItems=cartItemRepository.findByCart(cart);

        if (cartItems.isEmpty()) {
            throw new EmptyCart("Cannot place order with empty cart.");
        }

        for (CartItem item : cartItems) {
            ProductVariant variant = item.getProductVariant();
            long availableQuantity = variant.getStockQuantity() - variant.getReservedQuantity();
            if (item.getQuantity() > availableQuantity) {
                throw new BadRequest("Product variant " + variant.getSku() + " is out of stock or does not have enough quantity. Available: " + availableQuantity);
            }
        }

        long totalAmount = Math.round(cartItems.stream()
                .mapToDouble(item -> item.getProductVariant().getPrice() * item.getQuantity())
                .sum());

        Order order = OrderMapper.createOrder(customer, shippingAddress, paymentMethod, totalAmount);

        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItems = new ArrayList<>();

        for(CartItem cartItem:cartItems){
            ProductVariant variant = cartItem.getProductVariant();
            // Reserve quantity
            variant.setReservedQuantity(variant.getReservedQuantity() + cartItem.getQuantity());
            OrderItem orderItem=new OrderItem();
            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductVariant(cartItem.getProductVariant());
            orderItem.setPrice(cartItem.getPrice());
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);
        cartItemRepository.deleteAll(cartItems);
        savedOrder.setOrderItems(orderItems);

        OrderResponseDto orderResponseDto=orderMapper.toDto(order);

        return responseBuilder.build("order", orderResponseDto, "Order placed successfully");

    }

    public ResponseEntity<ApiResponse<Map<String,Object>>> getMyOrders(){

        String email=authUtil.getCurrentUserEmail();
        User customer=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("user not found with this email "+email));


        if(!customer.getRole().equals(Role.ROLE_CUSTOMER)){
            throw new Forbidden("Only customers can view their orders");
        }

        List<Order> orders = orderRepository.findByCustomer(customer);
        List<OrderResponseDto> orderResponseDtos=orderMapper.toDtoList(orders);

        return responseBuilder.build("orders", orderResponseDtos, "Orders fetched successfully");

    }

    public ResponseEntity<ApiResponse<Map<String,Object>>> getOrderDetails(Long orderId){
        String email=authUtil.getCurrentUserEmail();
        User customer=userRepository.findByEmail(email).orElseThrow(()-> new UserNotFound("User not found with this email "+email));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFound("Order not found with ID: " + orderId));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new Forbidden("You are not allowed to view this order.");
        }

        OrderResponseDto orderResponseDto=orderMapper.toDto(order);

        return responseBuilder.build("order", orderResponseDto, "Order details fetched successfully");

    }

    public ResponseEntity<ApiResponse<Map<String,Object>>> cancelOrder(Long orderId){

      String email = authUtil.getCurrentUserEmail();
      User customer = userRepository.findByEmail(email)
               .orElseThrow(() -> new UserNotFound("User not found with email: " + email));

      Order order = orderRepository.findById(orderId)
              .orElseThrow(() -> new OrderNotFound("Order not found with ID: " + orderId));

      if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new Forbidden("You are not allowed to cancel this order.");
      }

      if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED||order.getStatus()==OrderStatus.PLACED||order.getStatus()==OrderStatus.SHIPPED) {
            throw new Forbidden("Order cannot be cancelled.");
      }

        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getProductVariant();
            long reserved = variant.getReservedQuantity();
            long toRelease = item.getQuantity();

            if (toRelease > reserved) {
                throw new BadRequest("Inconsistent state: Reserved quantity less than to-be-released quantity.");
            }

            variant.setReservedQuantity(reserved - toRelease);
        }

      order.setStatus(OrderStatus.CANCELLED);
      order.setUpdatedAt(LocalDateTime.now());
      orderRepository.save(order);

      OrderResponseDto orderResponseDto=orderMapper.toDto(order);

      return responseBuilder.build("order", orderResponseDto, "Order cancelled successfully");

    }

}

