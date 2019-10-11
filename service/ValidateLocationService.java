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

import toolkit.xml.XmlObject;

public class ValidateLocationService extends BandwidthService implements BandwidthOrder {

	public static final String CREATE_URL = "VALIDATE_LOC_URI";

	public String processOrder(String host, String txAuth, String accountId, String orderType, String strPayLoad, String orderId, String httpType) {
		logger = new GWLogger(LogDefines.LOGGER_AREA_CONNECTOR);
		logger.INFO("ValidateLocationService processOrder(): OrderType:" + orderType + "; accountId:" + accountId + "; orderId:" + orderId);
		if (BandwidthOrderType.get(orderType).equals(BandwidthOrderType.VALIDATELOCATION)) {
			return createValidateLocationOrder(host, txAuth, accountId, strPayLoad);
		} else
			return util.getFailureResponse(null);
	}

	protected String createValidateLocationOrder(String host, String txAuth, String accountId, String strPayLoad) {
		String validateUri = pm.getProperty(CREATE_URL);
		logger.DEBUG("createValidateLocationOrder() -> Actual URI Path is " + host.concat(validateUri));
		util.setApDetails("Transport URL=" + host.concat(validateUri));
		Response response = getClient(host.concat(validateUri), txAuth).request().buildPost(Entity.entity(strPayLoad, MediaType.APPLICATION_XML)).invoke();
		String responseStr = response.readEntity(String.class);
		logger.DEBUG("createValidateLocationOrder -> API response code  is " + response.getStatus() + "; API Response =" + responseStr);
		
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse(responseStr);
		}  else if ( Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(responseStr);
		} else {
			XmlObject xmlObjOriginalPayload = new XmlObject(
				new InputSource(new StringReader(responseStr)));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}

	}
}
