package com.practice.mockito_examples.order.service;

import java.util.List;

import com.practice.mockito_examples.common.ServiceException;
import com.practice.mockito_examples.order.model.domain.OrderSummary;

public interface OrderService {

	List<OrderSummary> getOrderSummary(long customerId) throws ServiceException;
}
