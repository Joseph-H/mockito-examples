package com.practice.mockito_examples.order.service.impl;

import java.util.LinkedList;
import java.util.List;

import com.practice.mockito_examples.common.DataAccessException;
import com.practice.mockito_examples.common.ServiceException;
import com.practice.mockito_examples.order.dao.OrderDao;
import com.practice.mockito_examples.order.model.domain.OrderSummary;
import com.practice.mockito_examples.order.model.entity.OrderEntity;
import com.practice.mockito_examples.order.model.transformer.OrderEntityToOrderSummaryTransformer;
import com.practice.mockito_examples.order.service.OrderService;

public class OrderServiceImpl implements OrderService {

	private OrderDao orderDao = null;
	private OrderEntityToOrderSummaryTransformer transformer = null;
	
	public void setOrderDao(final OrderDao orderDao) {
		this.orderDao = orderDao;
	}
	
	public void setTransformer(final OrderEntityToOrderSummaryTransformer transformer) {
		this.transformer = transformer;
	}
	
	@Override
	public List<OrderSummary> getOrderSummary(long customerId)
			throws ServiceException {
		
		// Goal - interact with the dao to gather entities and 
		// create summary domain objects
		
		List<OrderSummary> resultList = new LinkedList<>();
		
		try {
			List<OrderEntity> orderEntityList = this.orderDao.findOrdersByCustomer(customerId);
			
			for (OrderEntity currentOrderEntity : orderEntityList) {
				
				OrderSummary orderSummary = this.transformer.transform(currentOrderEntity);
				resultList.add(orderSummary);
			}
			
		} catch (DataAccessException e) {
			// You should log the error
			throw new ServiceException("Data access error occurred", e);
		}
		
		return resultList;
	}

}
