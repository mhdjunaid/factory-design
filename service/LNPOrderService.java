package com.twc.gw.tpgw.connector.service;

import java.io.StringReader;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.xml.sax.InputSource;
import com.twc.gw.mgmt.defines.LogDefines;
import com.twc.gw.mgmt.log.GWLogger;
import com.twc.gw.tpgw.connector.factory.BandwidthOrder;
import com.twc.gw.tpgw.connector.util.PRUtil;
import toolkit.xml.XmlObject;

public class LNPOrderService extends BandwidthService implements BandwidthOrder {

	public static final String CREATE_URL = "LNP_ORDER_CREATE_URI";

	public String processOrder(String host, String txAuth, String accountId, String orderType, String strPayLoad, String orderId, String httpType) {
		logger = new GWLogger(LogDefines.LOGGER_AREA_CONNECTOR);
		logger.DEBUG("LNPOrderService processOrder(): OrderType:" + orderType + "; accountId:" + accountId + "; orderId:" + orderId + "; httpType=" + httpType);
		if (HttpMethod.POST.equals(httpType)) {
			return createLNPOrder(host, accountId, strPayLoad);
		}
		else if (HttpMethod.GET.equals(httpType)) {
			return retrieveLNPOrder(host, accountId, orderId);
		}
		else if (HttpMethod.DELETE.equals(httpType)) {
			return deleteLNPOrder(host, accountId, orderId);
		} 
		else if (HttpMethod.PUT.equals(httpType)) {
			return supplementLNPOrder(host, accountId, orderId, strPayLoad);
		}
		else
			return retrieveLNPOrder(host, accountId, orderId);
	}

	/*
	 * This method is used to post the request XML to Bandwidth for creating LNP Order.
	 */
	protected String createLNPOrder(String host, String accountId, String strPayLoad) {
		String createURL = pm.getProperty(CREATE_URL);
		logger.DEBUG("createURL = " + createURL);
		String actualPath = PRUtil.populatePathParams(createURL, accountId);
		logger.DEBUG("Actual URI Path is " + host.concat(actualPath));
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		
		Response response = getClient(host.concat(actualPath)).request().buildPost(Entity.entity(strPayLoad, MediaType.APPLICATION_XML)).invoke();
		logger.DEBUG("API response code  is " + response.getStatus());

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

	protected String supplementLNPOrder(String host, String accountId, String orderId, String strPayLoad) {
		String updateURL = pm.getProperty(CREATE_URL) + "/{orderid}";
		String actualPath = PRUtil.populatePathParamsWithOrderId(updateURL, accountId, orderId);
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		Response response = getClient(host).path(actualPath).request(MediaType.APPLICATION_XML).put(Entity.xml(strPayLoad));
		logger.DEBUG("supplementLNPOrder() API response code  is " + response.getStatus());
		String responseStr = response.readEntity(String.class);
		
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse(responseStr);
		} 
		else if (Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(responseStr);
		} 
		else {
			XmlObject xmlObjOriginalPayload = new XmlObject(new InputSource(new StringReader(responseStr)));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}

	/*
	 * This method is to delete the LNP Order
	 */
	protected String deleteLNPOrder(String host, String accountId, String orderId) {
		String deleteURL = pm.getProperty(CREATE_URL) + "/{orderid}";
		String actualPath = PRUtil.populatePathParamsWithOrderId(deleteURL, accountId, orderId);
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		Response response = getClient(host).path(actualPath).request(MediaType.APPLICATION_XML).delete(Response.class);
		logger.DEBUG("deleteLNPOrder() API response code  is " + response.getStatus());
		String responseStr = response.readEntity(String.class);
		/*
		 * Ack Code
		 * Response.Status.OK
		 * Response.Status.NO_CONTENT
		 * Response.Status.ACCEPTED
		 * Response.Status.NOT_FOUND - Not found
		 * Nack Codes
		 * Response.Status.BAD_REQUEST
		 */
		
		if (Response.Status.OK.getStatusCode() == response.getStatus() 
				|| Response.Status.ACCEPTED.getStatusCode() == response.getStatus() 
				|| Response.Status.NOT_FOUND.getStatusCode() == response.getStatus() 
				|| Response.Status.NO_CONTENT.getStatusCode() == response.getStatus() ) {
			return util.getSuccessResponse(responseStr);
		} 
		else if (Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(responseStr);
		}
		else {
			XmlObject xmlObjOriginalPayload = new XmlObject(new InputSource(new StringReader(responseStr)));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}

	/*
	 * This method is to retrive the LNP Order
	 */
	protected String retrieveLNPOrder(String host, String accountId, String orderId) {
		String getURL = pm.getProperty(CREATE_URL) + "/{orderid}";
		String actualPath = PRUtil.populatePathParamsWithOrderId(getURL, accountId, orderId);
		logger.DEBUG("Actual URI Path is " + host.concat(actualPath));
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		Response response = getClient(host).path(actualPath).request().get(Response.class);
		String responseStr = response.readEntity(String.class);
		logger.DEBUG("API response code  is " + response.getStatus());
		
		/*
		 * Ack Code
		 * Response.Status.OK
		 * Nack Codes
		 * Response.Status.NOT_FOUND - Not found
		 */
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse(responseStr);
		}
		else if ( Response.Status.NOT_FOUND.getStatusCode() == response.getStatus() || Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(responseStr);
		} 
		else {
			XmlObject xmlObjOriginalPayload = new XmlObject(new InputSource(new StringReader(responseStr)));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}
}
