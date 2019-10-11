package com.twc.gw.tpgw.connector.factory;

public interface BandwidthOrder {
	String processOrder(String host, String txAuth, String accountId, String orderType, String strPayLoad, String orderId, String httpType);
}
