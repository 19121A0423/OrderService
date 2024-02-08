package com.order.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.order.bean.Address;
import com.order.bean.Cart;
import com.order.bean.Orders;
import com.order.bean.Payment;
import com.order.entity.AddressEntity;
import com.order.entity.OrderEntity;
import com.order.exceptions.AddressNotFoundException;
import com.order.exceptions.CartNotFoundException;
import com.order.exceptions.OrderNotFoundException;
import com.order.repository.OrderRepository;
import com.order.service.OrderService;
import com.order.service.PaymentService;
import com.order.structure.ResponseStructure;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private AddressServiceImpl addressService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class.getSimpleName());

	@Override
	public void placeOrder(Orders order) throws AddressNotFoundException {
		log.info("OrderServiceImpl::placeOrder::Started");
		
		Address address = order.getAddress();
		AddressEntity addressEntity = new AddressEntity();
		if(address.getAddressId()==null) {
			address = addressService.saveAddress(address);
			addressEntity.setAddressId(address.getAddressId());
			addressService.beanToEntity(address, addressEntity);
		}
		else {
			try {
				address = addressService.getAddressById(address.getAddressId());
				addressEntity.setAddressId(address.getAddressId());
				addressService.beanToEntity(address, addressEntity);

			} catch (AddressNotFoundException e) {
				log.error("Address is not found with id "+address.getAddressId());
				throw e;
			}
			
		}
		
		if (order.getCartId()==null || order.getAddress()==null || order.getPayment()==null) {
			throw new IllegalArgumentException("Order properties cannot be null");
		}
		
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setAddress(addressEntity);
		beanToEntity(order, orderEntity);
		orderRepository.save(orderEntity);

		Payment payment = order.getPayment();
		payment.setOrder(order);
		paymentService.savePayment(payment,orderEntity);
		
		log.info("OrderServiceImpl::placeOrder::Ended");

	}

	@Override
	public Orders getOrderById(int id) throws OrderNotFoundException {
		log.info("OrderServiceImpl::getOrderById::Started");
		OrderEntity orderEntity = orderRepository.findById(id)
				.orElseThrow(() -> new OrderNotFoundException("Address not found with ID: " + id));

		Orders order = new Orders();
		entityToBean(order, orderEntity);
		log.info("OrderServiceImpl::getOrderById::Ended");
		return order;
	}

	@Override
	public List<Orders> getAllOrders() throws OrderNotFoundException {
		log.info("OrderServiceImpl::getAllOrders::Started");
		List<OrderEntity> orderEntities = orderRepository.findAll();
		if(orderEntities.isEmpty()) {
			throw new OrderNotFoundException("No orders found");
		}
		else {
			List<Orders> orders = new ArrayList<>();
			entitiesToBeans(orders, orderEntities);
			log.info("OrderServiceImpl::getAllOrders::Ended");
			return orders;
		}

	}

	@Override
	public void updateStatusById(int id) throws OrderNotFoundException {
		log.info("OrderServiceImpl::updateStatusById::Started");
		if (orderRepository.existsById(id)) {
			orderRepository.updateStatusById(id);
		} else {
			throw new OrderNotFoundException("Order not found with ID: " + id);
		}
		log.info("OrderServiceImpl::updateStatusById::Ended");

	}
	
	@Override
	public Cart getCart(int id) throws CartNotFoundException {
		log.info("OrderServiceImpl::getCart::Started");
		String url = "http://localhost:8084/medicine/cart/"+id ;
		
		ParameterizedTypeReference<ResponseStructure<Cart>> responseType =
		        new ParameterizedTypeReference<ResponseStructure<Cart>>() {};
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> httpEntity = new HttpEntity<>(headers);

		ResponseEntity<ResponseStructure<Cart>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity,responseType);
		ResponseStructure<Cart> response = responseEntity.getBody();
		Cart cart = response.getData();
		
		if(cart==null) {
			throw new CartNotFoundException("Cart not found with id : "+id);
		}
		else {
			log.info("OrderServiceImpl::getCart::Ended");
			return cart;
		}
	}

	@Override
	public void beanToEntity(Orders order, OrderEntity orderEntity) {
		log.info("OrderServiceImpl::beanToEntity::Started");
		orderEntity.setCartId(order.getCartId());
		orderEntity.setOrderedDate(LocalDate.now());
		orderEntity.setStatus("in progress");
		log.info("OrderServiceImpl::beanToEntity::Ended");

	}

	@Override
	public void entityToBean(Orders order, OrderEntity orderEntity) {
		log.info("OrderServiceImpl::entityToBean::Started");
		order.setOrderId(orderEntity.getOrderId());
		Address address = new Address();
		addressService.entityToBean(address, orderEntity.getAddress());
		order.setAddress(address);
		order.setCartId(orderEntity.getCartId());
		order.setOrderedDate(orderEntity.getOrderedDate());
		order.setStatus(orderEntity.getStatus());
		log.info("OrderServiceImpl::entityToBean::Ended");
	}

	@Override
	public void entitiesToBeans(List<Orders> orders, List<OrderEntity> orderEntities) {
		log.info("OrderServiceImpl::entitiesToBeans::Started");
		orderEntities.stream().forEach(orderEntity -> {
			Orders order = new Orders();
			order.setOrderId(orderEntity.getOrderId());
			Address address = new Address();
			addressService.entityToBean(address, orderEntity.getAddress());
			order.setAddress(address);
			order.setCartId(orderEntity.getCartId());
			order.setOrderedDate(orderEntity.getOrderedDate());
			order.setStatus(orderEntity.getStatus());
			orders.add(order);
		});
		
		log.info("OrderServiceImpl::entitiesToBeans::Ended");
	}

}
