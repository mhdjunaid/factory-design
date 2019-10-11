package com.twc.gw.tpgw.connector.service;

import java.io.StringReader;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.xml.sax.InputSource;

import com.twc.gw.mgmt.defines.LogDefines;
import com.twc.gw.mgmt.log.GWLogger;
import com.twc.gw.tpgw.connector.defines.BandwidthOrderType;
import com.twc.gw.tpgw.connector.factory.BandwidthOrder;
import com.twc.gw.tpgw.connector.util.PRUtil;

import toolkit.xml.XmlObject;

public class LNPNotesService extends BandwidthService implements BandwidthOrder {

	public static final String CREATE_URL = "LNP_NOTES_CREATE_URI";

	public String processOrder(String host, String txAuth, String accountId, String orderType, String strPayLoad, String orderId, String httpType) {
		logger = new GWLogger(LogDefines.LOGGER_AREA_CONNECTOR);
		logger.DEBUG("LNPNotesService : OrderType " + orderType);
		if (BandwidthOrderType.get(orderType).equals(BandwidthOrderType.NOTE)) {
			return createLNPNotes(host, accountId, strPayLoad, orderId);
		}  else
			return retrieveLNPNotes(host, accountId, orderId);
	}

	protected String createLNPNotes(String host, String accountId, String strPayLoad, String orderId ) {

		String actualPath = PRUtil.populatePathParamsWithOrderId(pm.getProperty(CREATE_URL), accountId, orderId);
		logger.DEBUG("Actual URI Path is " + host.concat(actualPath));
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		
		Response response = getClient(host.concat(actualPath)).request().buildPost(Entity.entity(strPayLoad, MediaType.APPLICATION_XML))
				.invoke();
		logger.DEBUG("API response code  is " + response.getStatus());
		//logger.DEBUG("API response message  is " + response.readEntity(String.class));
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

	protected String retrieveLNPNotes(String host, String accountId, String orderId) {
		String getNotesURL = pm.getProperty(CREATE_URL) + "/{orderid}";
		String actualPath = PRUtil.populatePathParamsWithOrderId(pm.getProperty(CREATE_URL), accountId, orderId);
		util.setApDetails("Transport URL=" + host.concat(actualPath));
		
		Response response = getClient(host).path(actualPath).request(MediaType.APPLICATION_XML).get(Response.class);
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse(response.toString());
		} else {
			XmlObject xmlObjOriginalPayload = new XmlObject(
					new InputSource(new StringReader(response.readEntity(String.class))));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}
}
