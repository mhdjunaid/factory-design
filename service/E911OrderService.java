package com.twc.gw.tpgw.connector.service;

import java.io.StringReader;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.xml.sax.InputSource;
import com.twc.gw.tpgw.connector.BWOutboundAccessPoint;
import com.twc.gw.tpgw.connector.factory.BandwidthOrder;
import com.twc.gw.tpgw.connector.util.PRUtil;
import toolkit.xml.XmlObject;

public class E911OrderService extends BandwidthService implements BandwidthOrder {

	public static final String CREATE_URL = "E911_CREATE_URI";

	/*
	 * This method processes GET. POST and DELETE of a E911 order
	 */
	public String processOrder(String host, String txAuth, String accountId, String orderType, String strPayLoad, String orderId, String httpType) {
		logger.DEBUG("E911OrderService:processOrder(): OrderType:" + orderType + "; accountId:" + accountId + "; orderId:" + orderId + "; httpType=" + httpType);
		if (HttpMethod.POST.equals(httpType)) {
			return createE911Order(host, accountId, strPayLoad);
		} else if (HttpMethod.GET.equals(httpType)) {
			return retrieveE911Order(host, accountId, orderId);
		} else if (HttpMethod.DELETE.equals(httpType)) {
			return deleteE911Order(host, accountId, orderId);
		} else
			return retrieveE911Order(host, accountId, orderId);
	}

	protected String createE911Order(String host, String accountId, String strPayLoad) {
		String actualPath = PRUtil.populatePathParams(pm.getProperty(CREATE_URL), accountId);
	    logger.DEBUG("E911OrderService:createE911Order()->Invoking Bandwidth API with URL " + host.concat(actualPath));
	    util.setApDetails("Transport URL=" + host.concat(actualPath));
		Response response = getClient(host).path(actualPath).request(MediaType.APPLICATION_XML).post(Entity.xml(strPayLoad));
		logger.DEBUG("E911OrderService:createE911Order()->response status : " + response.getStatus());
		if (Response.Status.CREATED.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse(response.readEntity(String.class));
		} else if ( Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(response.readEntity(String.class));
		} else {
			XmlObject xmlObjOriginalPayload = new XmlObject(
				new InputSource(new StringReader(response.readEntity(String.class))));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}

	/*
	 * This method is to delete E911 Order
	 */
	protected String deleteE911Order(String host, String accountId, String orderId) {
		String deleteURL = pm.getProperty(CREATE_URL) + "/{orderid}";
		String actualPath = PRUtil.populatePathParamsWithOrderId(deleteURL, accountId, orderId);
		Response response = getClient(host).path(actualPath).request(MediaType.APPLICATION_XML).delete(Response.class);
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse(response.toString());
		} else {
			XmlObject xmlObjOriginalPayload = new XmlObject(
					new InputSource(new StringReader(response.readEntity(String.class))));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}

	/*
	 * This method is used to Retrieve E911 Order
	 */
	protected String retrieveE911Order(String host, String accountId, String orderId) {
		String getURL = pm.getProperty(CREATE_URL) + "/{orderid}";
		String actualPath = PRUtil.populatePathParamsWithOrderId(getURL, accountId, orderId);
		logger.DEBUG("E911OrderService:retrieveE911Order()->Actual URI Path is " + host.concat(actualPath));
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		Response response = getClient(host).path(actualPath).request().get(Response.class);
		logger.DEBUG("E911OrderService:retrieveE911Order()->API response code  is " + response.getStatus());
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse(response.readEntity(String.class));
		}  else if ( Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(response.readEntity(String.class));
		} else {
			XmlObject xmlObjOriginalPayload = new XmlObject(
				new InputSource(new StringReader(response.readEntity(String.class))));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}
}
