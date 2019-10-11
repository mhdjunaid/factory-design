package com.twc.gw.tpgw.connector.service;

import java.io.StringReader;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.xml.sax.InputSource;

import com.twc.gw.mgmt.defines.LogDefines;
import com.twc.gw.mgmt.log.GWLogger;
import com.twc.gw.tpgw.connector.BWOutboundAccessPoint;
import com.twc.gw.tpgw.connector.defines.BandwidthOrderType;
import com.twc.gw.tpgw.connector.factory.BandwidthOrder;
import com.twc.gw.tpgw.connector.util.PRUtil;

import toolkit.xml.XmlObject;

public class ActivationStatusService extends BandwidthService implements BandwidthOrder {
	

	public String processOrder(String host, String txAuth, String accountId, String orderType, String strPayLoad, String orderId, String httpType) {
		logger = new GWLogger(LogDefines.LOGGER_AREA_CONNECTOR);
		logger.DEBUG("ActivationStatusService : OrderType " + orderType);
		logger.DEBUG("ActivationStatusService : OrderId " + orderId);
		if (BandwidthOrderType.get(orderType).equals(BandwidthOrderType.ACTIVESTATUS)) {
			return createActivationService(host, accountId, strPayLoad,orderId);
		} 
		else 
			return null;
	}

	private String createActivationService(String host, String accountId, String strPayLoad, String orderId) {
		String activationStatusURL = pm.getProperty(LNPOrderService.CREATE_URL) + "/{orderid}/activationStatus";
		String actualPath = PRUtil.populatePathParamsWithOrderId(activationStatusURL, accountId, orderId);
		logger.DEBUG("Actual URI Path is " + host.concat(actualPath));
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		Response response = getClient(host.concat(actualPath)).request(MediaType.APPLICATION_XML).put(Entity.xml(strPayLoad));
		/*Response response = getClient(host.concat(actualPath)).request().put(Entity.entity(strPayLoad, MediaType.APPLICATION_XML))
				.invoke();*/
		String apiResponse = response.readEntity(String.class);
		logger.DEBUG("API response code  is " + response.getStatus());
		logger.DEBUG("API response message  is " + apiResponse);
		
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse("<ActivationStatusResponse>UPDATED</ActivationStatusResponse>");
		} else if ( Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus() || Response.Status.NOT_FOUND.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(apiResponse);
		} else {
			XmlObject xmlObjOriginalPayload = new XmlObject(
				new InputSource(new StringReader(apiResponse)));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}

}
