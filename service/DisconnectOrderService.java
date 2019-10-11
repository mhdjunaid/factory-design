package com.twc.gw.tpgw.connector.service;

import java.io.StringReader;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.xml.sax.InputSource;

import com.twc.gw.tpgw.connector.BWOutboundAccessPoint;
import com.twc.gw.tpgw.connector.factory.BandwidthOrder;

import toolkit.xml.XmlObject;

public class DisconnectOrderService extends BandwidthService implements BandwidthOrder {

	public String processOrder(String host, String txAuth, String accountId, String orderType, String strPayLoad, String orderId, String httpType) {
		if (HttpMethod.POST.equals(orderType)) {
			return disconnectTN(host, orderType, strPayLoad);
		} else if (HttpMethod.GET.equals(orderType)) {
			return retrieveDisconnectTNs(host, orderType);
		} else
			return util.getFailureResponse(null);
	}

	protected String disconnectTN(String host, String path, String strPayLoad) {

		Response response = getClient(host).path(path).request(MediaType.APPLICATION_XML).post(Entity.xml(strPayLoad));
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			// Fixme check if few Tn has got error conditon on 200

			return util.getSuccessResponse(response.toString());
		}  else if ( Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(response.readEntity(String.class));
		} else {
			XmlObject xmlObjOriginalPayload = new XmlObject(
				new InputSource(new StringReader(response.readEntity(String.class))));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}

	private String retrieveDisconnectTNs(String host, String path) {
		Response response = getClient(host).path(path).request(MediaType.APPLICATION_XML).get(Response.class);
		if (Response.Status.OK.getStatusCode() == response.getStatus()) {
			return util.getSuccessResponse(response.toString());
		}  else if ( Response.Status.BAD_REQUEST.getStatusCode() == response.getStatus()) {
			return util.getNACKResponse(response.readEntity(String.class));
		} else {
			XmlObject xmlObjOriginalPayload = new XmlObject(
				new InputSource(new StringReader(response.readEntity(String.class))));
			return util.getFailureResponse(xmlObjOriginalPayload);
		}
	}

}
