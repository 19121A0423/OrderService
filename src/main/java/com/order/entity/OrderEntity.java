package com.order.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable{

	private static final long serialVersionUID = -4194911857077683391L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Integer orderId;

	@Column(name = "ordered_date")
	private LocalDate orderedDate;

	@Column(name = "status")
	private String status;
	
	@OneToOne
	@JoinColumn(name = "address_id" , referencedColumnName = "address_id")
	private AddressEntity address;

	@Column(name = "cart_id")
	private Integer cartId;

	@OneToOne(mappedBy = "order")
	@JsonIgnore
	private PaymentEntity payment;
	
	@OneToOne(mappedBy = "order")
	@JsonIgnore
	private FeedbackEntity feedback;

	public OrderEntity() {
		super();
	}

	public OrderEntity(Integer orderId, LocalDate orderedDate, String status, AddressEntity address, Integer cartId,
			PaymentEntity payment, FeedbackEntity feedback) {
		super();
		this.orderId = orderId;
		this.orderedDate = orderedDate;
		this.status = status;
		this.address = address;
		this.cartId = cartId;
		this.payment = payment;
		this.feedback = feedback;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public LocalDate getOrderedDate() {
		return orderedDate;
	}

	public void setOrderedDate(LocalDate orderedDate) {
		this.orderedDate = orderedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AddressEntity getAddress() {
		return address;
	}

	public void setAddress(AddressEntity address) {
		this.address = address;
	}

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}

	public PaymentEntity getPayment() {
		return payment;
	}

	public void setPayment(PaymentEntity payment) {
		this.payment = payment;
	}

	public FeedbackEntity getFeedback() {
		return feedback;
	}

	public void setFeedback(FeedbackEntity feedback) {
		this.feedback = feedback;
	}

	@Override
	public String toString() {
		return "OrderEntity [orderId=" + orderId + ", orderedDate=" + orderedDate + ", status=" + status + ", address="
				+ address + ", cartId=" + cartId + ", payment=" + payment + ", feedback=" + feedback + "]";
	}

	

}
