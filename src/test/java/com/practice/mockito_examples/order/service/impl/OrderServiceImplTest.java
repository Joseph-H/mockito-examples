package com.practice.mockito_examples.order.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.practice.mockito_examples.common.DataAccessException;
import com.practice.mockito_examples.common.ServiceException;
import com.practice.mockito_examples.order.dao.OrderDao;
import com.practice.mockito_examples.order.model.domain.OrderSummary;
import com.practice.mockito_examples.order.model.entity.OrderEntity;
import com.practice.mockito_examples.order.model.transformer.OrderEntityToOrderSummaryTransformer;

public class OrderServiceImplTest {
	
	private final static long CUSTOMER_ID = 1L;

	@Test
	public void test_getOrderSummary_success() throws ServiceException, DataAccessException {
		OrderServiceImpl classUnderTest = new OrderServiceImpl();
		
		//Mocking example
		OrderDao mockOrderDao = Mockito.mock(OrderDao.class);
		classUnderTest.setOrderDao(mockOrderDao);
		
		OrderEntityToOrderSummaryTransformer mockTransformer = Mockito.mock(OrderEntityToOrderSummaryTransformer.class);
		classUnderTest.setTransformer(mockTransformer);
		
		OrderEntity orderEntityFixture = new OrderEntity();
		List<OrderEntity> orderEntityFixtures = new LinkedList<OrderEntity>();
		orderEntityFixtures.add(orderEntityFixture);
		
		//Tell the mock to return something specific
		Mockito.when(mockOrderDao.findOrdersByCustomer(CUSTOMER_ID)).thenReturn(orderEntityFixtures);
		
		OrderSummary orderSummaryFixture = new OrderSummary();
		Mockito.when(mockTransformer.transform(orderEntityFixture)).thenReturn(orderSummaryFixture);
		
		List<OrderSummary> result = classUnderTest.getOrderSummary(CUSTOMER_ID);
		
		//Verification example
		Mockito.verify(mockOrderDao).findOrdersByCustomer(CUSTOMER_ID);
		Mockito.verify(mockTransformer).transform(orderEntityFixture);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertSame(orderSummaryFixture, result.get(0));
	}

}
