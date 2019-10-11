package com.twc.gw.tpgw.connector.service;


import java.io.StringReader;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.xml.sax.InputSource;

import com.twc.gw.tpgw.connector.BWOutboundAccessPoint;
import com.twc.gw.tpgw.connector.factory.BandwidthOrder;

import toolkit.xml.XmlObject;

public class TnQueryOrderService extends BandwidthService implements BandwidthOrder {

	public static final String TN_QUERY = "TN_QUERY_URI";

	public String processOrder(String host, String txAuth, String accountId, String orderType, String strPayLoad, String orderId, String httpType) {
		return createTnQueryOrder(host, strPayLoad);
	}

	protected String createTnQueryOrder(String host, String strPayLoad) {
		String actualPath = host.concat(pm.getProperty(TN_QUERY));
	    logger.DEBUG("Actual URI Path is " + actualPath);
	    util.setApDetails("Transport URL=" + actualPath);
	    
		Response response = getClient(actualPath).request()
				.buildPost(Entity.entity(strPayLoad, MediaType.APPLICATION_XML)).invoke();
		logger.DEBUG("API response code  is " + response.getStatus());
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
