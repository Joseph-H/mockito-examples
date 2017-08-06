package com.practice.mockito_examples.order.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.practice.mockito_examples.order.dao.OrderDao;
import com.practice.mockito_examples.order.integration.WarehouseManagementService;
import com.practice.mockito_examples.order.model.entity.OrderEntity;
import com.practice.mockito_examples.order.model.entity.OrderItemEntity;
import com.practice.mockito_examples.order.model.message.OrderMessage;
import com.practice.mockito_examples.order.model.transformer.OrderEntityToOrderSummaryTransformer;

@RunWith(PowerMockRunner.class)
@PrepareForTest(value={WarehouseManagementService.class})
public class OrderServiceImplWithPowerMockitoTest {

	private final static long ORDER_ID = 2L;
	private final static String ORDER_NUMBER = "1234";
	
	private OrderServiceImpl classUnderTest = null;

	protected @Mock OrderDao mockOrderDao;
	protected @Mock OrderEntityToOrderSummaryTransformer mockTransformer;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		classUnderTest = new OrderServiceImpl();
		classUnderTest.setOrderDao(mockOrderDao);
		classUnderTest.setTransformer(mockTransformer);
	}
	
	@Test
	public void test_completeOrder_success() throws Exception {
		OrderItemEntity oiFixture1 = new OrderItemEntity();
		oiFixture1.setSku("SKU1");
		oiFixture1.setQuantity(1);
		
		OrderItemEntity oiFixture2 = new OrderItemEntity();
		oiFixture2.setSku("SKU2");
		oiFixture2.setQuantity(2);
		
		OrderEntity orderFixture = new OrderEntity();
		orderFixture.setOrderNumber(ORDER_NUMBER);
		orderFixture.setOrderItemList(new LinkedList<OrderItemEntity>());
		orderFixture.getOrderItemList().add(oiFixture1);
		orderFixture.getOrderItemList().add(oiFixture2);
		
		Mockito.when(mockOrderDao.findById(ORDER_ID)).thenReturn(orderFixture);
		
		// Static mocking
		PowerMockito.mockStatic(WarehouseManagementService.class);
		PowerMockito.doNothing().when(WarehouseManagementService.class, "sendOrder", Matchers.any(OrderMessage.class));
		
		classUnderTest.completeOrder(ORDER_ID);
		
		Mockito.verify(mockOrderDao).findById(ORDER_ID);
		
		PowerMockito.verifyStatic();
		ArgumentCaptor<OrderMessage> orderMessageCaptor = ArgumentCaptor.forClass(OrderMessage.class);
		
		WarehouseManagementService.sendOrder(orderMessageCaptor.capture());
		OrderMessage capturedOrderMessage = orderMessageCaptor.getValue();
		
		assertNotNull(capturedOrderMessage);
		assertEquals(ORDER_NUMBER, capturedOrderMessage.getOrderNumber());
	}
}
