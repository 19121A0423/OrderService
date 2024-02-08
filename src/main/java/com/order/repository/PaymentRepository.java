package com.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.order.entity.PaymentEntity;

@Transactional
public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer>{
	
}
