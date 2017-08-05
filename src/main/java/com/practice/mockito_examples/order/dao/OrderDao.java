package com.practice.mockito_examples.order.dao;

import java.util.List;

import com.practice.mockito_examples.common.DataAccessException;
import com.practice.mockito_examples.order.model.entity.OrderEntity;

public interface OrderDao {

	OrderEntity findById(long id) throws DataAccessException;
	int insert(OrderEntity order) throws DataAccessException;
	
	List<OrderEntity> findOrdersByCustomer(long customerId) throws DataAccessException;
}
