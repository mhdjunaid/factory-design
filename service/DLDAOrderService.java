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

public class DLDAOrderService extends BandwidthService implements BandwidthOrder {

	private static final String CREATE_URL = "DLDA_CREATE_URI";

	/*
	 * This method processes GET. POST and DELETE of a DLDA order
	 */
	public String processOrder(String host, String txAuth, String accountId, String orderType, String strPayLoad, String orderId, String httpType) {
		logger.DEBUG("DLDAOrderService processOrder(): OrderType:" + orderType + "; accountId:" + accountId + "; orderId:" + orderId + "; httpType=" + httpType);
		if (HttpMethod.POST.equals(httpType)) {
			return createDLDAOrder(host, accountId, strPayLoad);
		} else if (HttpMethod.GET.equals(httpType)) {
			return retrieveDLDAOrder(host, accountId, orderId);
		} else if (HttpMethod.DELETE.equals(httpType)) {
			return deleteDLDAOrder(host, accountId, orderId);
		} else
			return retrieveDLDAOrder(host, accountId, orderId);
	}
	
	/*
	 * This method is to create DLDA order
	 */
	protected String createDLDAOrder(String host, String accountId, String strPayLoad) {
		String actualPath = PRUtil.populatePathParams(pm.getProperty(CREATE_URL), accountId);
		logger.DEBUG("Invoking Bandwidth API with URL " + host.concat(actualPath));
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		Response response = getClient(host).path(actualPath).request(MediaType.APPLICATION_XML)
				.post(Entity.xml(strPayLoad));
		logger.DEBUG("DLDAOrderService : response status : " + response.getStatus());
		if (Response.Status.CREATED.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse(response.readEntity(String.class));
		}  else if ( Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(response.readEntity(String.class));
		} else {
			XmlObject xmlObjOriginalPayload = new XmlObject(
				new InputSource(new StringReader(response.readEntity(String.class))));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}
	
	/*
	 * This method is to delete DLDA Order
	 */
	protected String deleteDLDAOrder(String host, String accountId, String orderId) {
		String deleteURL = pm.getProperty(CREATE_URL) + "/{orderid}";
		String actualPath = PRUtil.populatePathParamsWithOrderId(deleteURL, accountId, orderId);
		util.setApDetails("Transport URL=" + host.concat(actualPath));
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
	 * This method is used to Retrieve DLDA Order
	 */
	protected String retrieveDLDAOrder(String host, String accountId, String orderId) {
		String getURL = pm.getProperty(CREATE_URL) + "/{orderid}";
		String actualPath = PRUtil.populatePathParamsWithOrderId(getURL, accountId, orderId);
		logger.DEBUG("DLDAOrderService:retrieveDLDAOrder()->Actual URI Path is " + host.concat(actualPath));
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		Response response = getClient(host).path(actualPath).request().get(Response.class);
		logger.DEBUG("DLDAOrderService:retrieveDLDAOrder()->API response code  is " + response.getStatus());
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
