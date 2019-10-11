package com.twc.gw.tpgw.connector;

import java.io.StringReader;
import org.xml.sax.InputSource;
import com.twc.gw.connector.defines.ConnectorDefines;
import com.twc.gw.connector.httpconnector.HttpOutboundAccessPoint;
import com.twc.gw.core.defines.MessageDefines;
import com.twc.gw.mgmt.defines.LogDefines;
import com.twc.gw.mgmt.log.GWLogger;
import com.twc.gw.mgmt.manager.PropertyManager;
import com.twc.gw.tpgw.connector.defines.BandwidthOrderType;
import com.twc.gw.tpgw.connector.factory.BandwidthOrder;
import com.twc.gw.tpgw.connector.factory.OrderFactory;
import com.twc.gw.tpgw.connector.util.PRUtil;
import com.twc.gw.tpgw.connector.util.ResponseUtil;
import toolkit.xml.XmlObject;

public class BWOutboundAccessPoint extends HttpOutboundAccessPoint {
	protected GWLogger logger = null;
	ResponseUtil util = new ResponseUtil();
	PRUtil prUtil = new PRUtil();
	public PropertyManager pm = new PropertyManager();
	String orderType = null;
	String orderId = null;
	String httpType = "POST";
	String accountId = null;
	
	public BWOutboundAccessPoint() {
		super();
		this.accountId = pm.getProperty("BW_ACCOUNT_ID");
		this.logger = new GWLogger(LogDefines.LOGGER_AREA_CONNECTOR);
	}

	public BWOutboundAccessPoint(String url, String txAuth) {
		super(url, txAuth);
		this.txAuth = txAuth;
		this.accountId = pm.getProperty("BW_ACCOUNT_ID");
		this.logger = new GWLogger(LogDefines.LOGGER_AREA_CONNECTOR);
	}

	@Override
	public String sendMessage(String msgPayload) {
		try {
			logger.INFO("BWOutboundAccessPoint::sendMessage => full msgPayload size => " + msgPayload.length());
			XmlObject xmlObjOriginalPayload = new XmlObject(new InputSource(new StringReader(msgPayload)));

			String strOriginalPayLoad = xmlObjOriginalPayload.getValue(ConnectorDefines.ELEMENT_PAYLOAD);
			String strMessageId = xmlObjOriginalPayload.getValue(ConnectorDefines.ELEMENT_MESSAGE_ID);
			logger.INFO(strMessageId, "BWOutboundAccessPoint::sendMessage =>MessageId=> " + strMessageId
					+ "   payload=" + strOriginalPayLoad + "  url=" + url + "; txAuth=" + txAuth);
			
			XmlObject xmlObjPayload = new XmlObject(new InputSource(new StringReader(strOriginalPayLoad)));
			XmlObject headerXml = xmlObjPayload.getXmlObject(MessageDefines.OUTBOUND_MESSAGE_WRAPPER_HEADER_NODE);
				
			if(headerXml == null)
			{
				logger.DEBUG(strMessageId, "BWOutboundAccessPoint::sendMessage =>MessageId=> " + strMessageId
						+ "   payload=" + strOriginalPayLoad + "  url=" + url + "ORDERTYPE=" + xmlObjPayload.getRootTag());
			
				this.orderType =  xmlObjPayload.getRootTag();
				logger.DEBUG(strMessageId, "BWOutboundAccessPoint::sendMessage Get the OrderInfo"+this.orderType);

			}
			else
			{
				logger.DEBUG(strMessageId, "BWOutboundAccessPoint::sendMessage =>headerXml=> " + headerXml);
				this.httpType = headerXml.getValue("method");
				this.orderType =  headerXml.getValue("OrderType");
				this.orderId = headerXml.getValue("OrderId");
			}
			
			XmlObject messageXml = xmlObjPayload.getXmlObject(MessageDefines.OUTBOUND_MESSAGE_WRAPPER_PAYLOAD_NODE);
			if( messageXml!=null ) {
				String outBoundPayload = xmlObjPayload.getValue(MessageDefines.OUTBOUND_MESSAGE_WRAPPER_PAYLOAD_NODE);
				if( outBoundPayload!=null && outBoundPayload.trim().length()>0 ) {
					strOriginalPayLoad = outBoundPayload;
				}
				logger.DEBUG(strMessageId, "BWOutboundAccessPoint::sendMessage =>outBoundPayload=> " + outBoundPayload);
			}
			logger.INFO("BWOutboundAccessPoint::sendMessage => BW ORDER TYPE => " + this.orderType + "; BW HTTP METHOD  => " + this.httpType);
			return sendBandwithRequest(strOriginalPayLoad);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.ERROR("BWOutboundAccessPoint", "sendMessage()", ex);
		}
		return util.getFailureResponse(null);
	}
	
	private String sendBandwithRequest(String strPayLoad) {
		logger.DEBUG(orderType + " Order Type ");
		BandwidthOrder bwOrder = OrderFactory.getBandwidthOrder(this.orderType);
		return bwOrder.processOrder(this.url, this.txAuth, this.accountId, this.orderType, strPayLoad, this.orderId, this.httpType);
	}
}
