package com.practice.mockito_examples.order.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.practice.mockito_examples.common.DataAccessException;
import com.practice.mockito_examples.common.ServiceException;
import com.practice.mockito_examples.order.dao.OrderDao;
import com.practice.mockito_examples.order.model.domain.OrderSummary;
import com.practice.mockito_examples.order.model.entity.OrderEntity;
import com.practice.mockito_examples.order.model.transformer.OrderEntityToOrderSummaryTransformer;

public class OrderServiceImplTest {
	
	private OrderServiceImpl classUnderTest;
	private final static long CUSTOMER_ID = 1L;
	
	@Mock OrderDao mockOrderDao;
	@Mock OrderEntityToOrderSummaryTransformer mockTransformer;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		classUnderTest = new OrderServiceImpl();
		classUnderTest.setTransformer(mockTransformer);
		classUnderTest.setOrderDao(mockOrderDao);
	}

	@Test
	public void test_getOrderSummary_success() throws ServiceException, DataAccessException {
		//Mocking example could be done like this or like above starting on line 27
//		OrderDao mockOrderDao = Mockito.mock(OrderDao.class);
//		OrderEntityToOrderSummaryTransformer mockTransformer = Mockito.mock(OrderEntityToOrderSummaryTransformer.class);
		
		OrderEntity orderEntityFixture = new OrderEntity();
		List<OrderEntity> orderEntityFixtures = new LinkedList<OrderEntity>();
		orderEntityFixtures.add(orderEntityFixture);
		
		//Tell the mock to return something specific
		Mockito.when(mockOrderDao.findByCustomerId(CUSTOMER_ID)).thenReturn(orderEntityFixtures);
		
		OrderSummary orderSummaryFixture = new OrderSummary();
		Mockito.when(mockTransformer.transform(orderEntityFixture)).thenReturn(orderSummaryFixture);
		
		List<OrderSummary> result = classUnderTest.getOrderSummary(CUSTOMER_ID);
		
		//Verification example
		Mockito.verify(mockOrderDao).findByCustomerId(CUSTOMER_ID);
		Mockito.verify(mockTransformer).transform(orderEntityFixture);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertSame(orderSummaryFixture, result.get(0));
	}
	
	@Test
	public void test_OpenNewOrder_successfullyRetriesDataInsert() throws DataAccessException, ServiceException {
		//Testing that the first call throws an exception and the second call returns a successful value
		OrderEntity orderEntityFixture = new OrderEntity();
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
			.thenThrow(new DataAccessException("First Ex")).thenReturn(orderEntityFixture);
		
		classUnderTest.openNewOrder(CUSTOMER_ID);
		
		//make sure the mock was called twice
		Mockito.verify(mockOrderDao, Mockito.times(2)).insert(Mockito.any(OrderEntity.class));
	}
	
	@Test(expected=ServiceException.class)
	public void test_OpenNewOrder_failedDataInsert() throws DataAccessException, ServiceException {
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class)))
			.thenThrow(new DataAccessException("First Ex"))
			.thenThrow(new DataAccessException("Second Ex"));
		
		//IMPORTANT!! because of the exceptions above, verify won't be called unless in a try/finally
		try {
			classUnderTest.openNewOrder(CUSTOMER_ID);
		} finally {
			Mockito.verify(mockOrderDao, Mockito.times(2)).insert(Mockito.any(OrderEntity.class));
		}
	}
	
	@Test
	public void test_OpenNewOrder_success() throws DataAccessException, ServiceException {
		OrderEntity orderEntityFixture = new OrderEntity();
		Mockito.when(mockOrderDao.insert(Mockito.any(OrderEntity.class))).thenReturn(orderEntityFixture);
		
		String orderNumber = classUnderTest.openNewOrder(CUSTOMER_ID);
		
		//Used when an object is created in the method and test doesn't explicitly get access to it.
		//this order entity that we are capturing gets passed into the mockOrderDao for processing
		//in this test we want to make sure that everything was set up correctly on that object.
		ArgumentCaptor<OrderEntity> orderEntityCaptor = ArgumentCaptor.forClass(OrderEntity.class);
		Mockito.verify(mockOrderDao).insert(orderEntityCaptor.capture());
		
		OrderEntity capturedOrderEntity = orderEntityCaptor.getValue();
		
		assertNotNull(capturedOrderEntity);
		assertEquals(CUSTOMER_ID, capturedOrderEntity.getCustomerId());
		assertEquals(orderNumber, capturedOrderEntity.getOrderNumber());
	}

}
