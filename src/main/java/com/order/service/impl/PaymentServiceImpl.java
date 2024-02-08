package com.order.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.order.bean.Orders;
import com.order.bean.Payment;
import com.order.controller.AddressController;
import com.order.entity.OrderEntity;
import com.order.entity.PaymentEntity;
import com.order.exceptions.PaymentNotFoundException;
import com.order.repository.PaymentRepository;
import com.order.service.OrderService;
import com.order.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;
	
	private static Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class.getSimpleName());

	
	private OrderService orderService = new OrderServiceImpl();

	@Override
	public void savePayment(Payment payment,OrderEntity orderEntity) {
		log.info("PaymentServiceImpl::savePayment::Started");
	    if (payment.getAmount() <= 0 || orderEntity.getOrderId()==null || payment.getPaymentMode()==null) {
	        throw new IllegalArgumentException("Invalid payment information");
	    }
	    
	    PaymentEntity paymentEntity = new PaymentEntity();
	    paymentEntity.setOrder(orderEntity);
	    beanToEntity(payment, paymentEntity);
	    paymentRepository.save(paymentEntity);
	    log.info("PaymentServiceImpl::savePayment::Ended");
	}


	@Override
	public Payment getPaymentById(int id) throws PaymentNotFoundException {
		log.info("PaymentServiceImpl::getPaymentById::Started");
		PaymentEntity paymentEntity = paymentRepository.findById(id)
				.orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));

		Payment payment = new Payment();
		entityToBean(payment, paymentEntity);
		log.info("PaymentServiceImpl::getPaymentById::Ended");
		return payment;
	}

	@Override
	public List<Payment> getAllPayments() throws PaymentNotFoundException {
		log.info("PaymentServiceImpl::getAllPayments::Started");
		List<PaymentEntity> paymentEntities = paymentRepository.findAll();
		if(paymentEntities.isEmpty()) {
			throw new PaymentNotFoundException("No payments found");
		}
		else {
			List<Payment> payments = new ArrayList<>();
			entitiesToBeans(payments, paymentEntities);
			log.info("PaymentServiceImpl::getAllPayments::Ended");
			return payments;
		}
	}

	@Override
	public void beanToEntity(Payment payment, PaymentEntity paymentEntity) {
		log.info("PaymentServiceImpl::beanToEntity::Started");
		paymentEntity.setAmount(payment.getAmount());
		paymentEntity.setPaymentDate(LocalDate.now());
		paymentEntity.setPaymentMode(payment.getPaymentMode());
		paymentEntity.setStatus(payment.getStatus());
		log.info("PaymentServiceImpl::beanToEntity::Ended");
	}

	@Override
	public void entityToBean(Payment payment, PaymentEntity paymentEntity) {
		log.info("PaymentServiceImpl::entityToBean::Started");
		payment.setPaymentId(paymentEntity.getPaymentId());
		payment.setAmount(paymentEntity.getAmount());
		Orders order = new Orders();
		orderService.entityToBean(order, paymentEntity.getOrder());
		payment.setOrder(order);
		payment.setPaymentDate(paymentEntity.getPaymentDate());
		payment.setPaymentMode(paymentEntity.getPaymentMode());
		payment.setStatus(paymentEntity.getStatus());
		log.info("PaymentServiceImpl::entityToBean::Ended");
	}
	
	@Override
	public void entitiesToBeans(List<Payment> payments , List<PaymentEntity> paymentEntities) {
		log.info("PaymentServiceImpl::entitiesToBeans::Started");
		paymentEntities.stream().forEach(paymentEntity -> {
			Payment payment = new Payment();
			payment.setPaymentId(paymentEntity.getPaymentId());
			payment.setAmount(paymentEntity.getAmount());
			Orders order = new Orders();
			orderService.entityToBean(order, paymentEntity.getOrder());
			payment.setOrder(order);
			payment.setPaymentDate(paymentEntity.getPaymentDate());
			payment.setPaymentMode(paymentEntity.getPaymentMode());
			payment.setStatus(paymentEntity.getStatus());
			
			payments.add(payment);
			log.info("PaymentServiceImpl::entitiesToBeans::Ended");
		});
		
	}
	
}
