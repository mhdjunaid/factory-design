package com.twc.gw.tpgw.connector.service;

import com.twc.gw.mgmt.defines.LogDefines;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import com.twc.gw.mgmt.log.GWLogger;
import com.twc.gw.mgmt.manager.PropertyManager;
import com.twc.gw.tpgw.connector.util.ResponseUtil;

public class BandwidthService {
	protected GWLogger logger = null;
	public PropertyManager pm = new PropertyManager();
	ResponseUtil util = new ResponseUtil();
	String uname = null;
	String pwd = null;
	Integer connTimeOut = null;
	Integer readTimeOut = null;
	public static final Integer BW_DEF_CONN_TIMEOUT = 45000;
	public static final Integer BW_DEF_READ_TIMEOUT = 45000;
	
	public BandwidthService() {
		this.logger = new GWLogger(LogDefines.LOGGER_AREA_CONNECTOR);
		this.uname = pm.getProperty("BW_UNAME");
		this.pwd = pm.getProperty("BW_PASSWORD");
		try {
			String connTimeOutStr = pm.getProperty("BW_CONN_TIMEOUT");
			if( connTimeOutStr!=null && connTimeOutStr.trim().length()>0 )
				this.connTimeOut = Integer.parseInt(connTimeOutStr);
			
			String readTimeOutStr = pm.getProperty("BW_READ_TIMEOUT");
			if( readTimeOutStr!=null && readTimeOutStr.trim().length()>0 )
				this.readTimeOut = Integer.parseInt(connTimeOutStr);
			
			this.logger.INFO("Connection TimeOut="+connTimeOutStr + this.connTimeOut + "/Read Time Out="+this.readTimeOut);
		}
		catch(NumberFormatException nfe) {
			this.logger.ERROR("BandwidthService() Failed to convert TImeout to long");
		}
	}
	
	protected WebTarget getClient(String hostName) {
		Client client = ClientBuilder.newClient();
		this.logger.INFO("getClient CONN_TIMEOUT value :"+ this.connTimeOut + "; READ_TIMEOUT value :"+ this.readTimeOut);
		
		if( this.connTimeOut!=null )
			client.property(ClientProperties.CONNECT_TIMEOUT, this.connTimeOut);
		else
			client.property(ClientProperties.CONNECT_TIMEOUT, BW_DEF_CONN_TIMEOUT);
		
		if( this.readTimeOut!=null )
			client.property(ClientProperties.READ_TIMEOUT, this.readTimeOut);
		else
			client.property(ClientProperties.READ_TIMEOUT, BW_DEF_READ_TIMEOUT);
		
		this.logger.INFO("BandwidthService()- getClient() Setting basic Auth, Credentials=" + this.uname + "/" + this.pwd.substring(0, 1) + "****" + this.pwd.substring(this.pwd.length()-1));
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().credentials(this.uname, this.pwd)
				.build();

		client.register(feature);
		return client.target(hostName);
	}
	
	protected WebTarget getClient(String hostName, String txAuth) {
		Client client = ClientBuilder.newClient();
		this.logger.INFO("getClient CONN_TIMEOUT value :"+ this.connTimeOut + "; READ_TIMEOUT value :"+ this.readTimeOut);
		if( this.connTimeOut!=null )
			client.property(ClientProperties.CONNECT_TIMEOUT, this.connTimeOut);
		else
			client.property(ClientProperties.CONNECT_TIMEOUT, BW_DEF_CONN_TIMEOUT);
		
		if( this.readTimeOut!=null )
			client.property(ClientProperties.READ_TIMEOUT, this.readTimeOut);
		else
			client.property(ClientProperties.READ_TIMEOUT, BW_DEF_READ_TIMEOUT);
		
		String[] parts = txAuth.split(";");
		this.uname = parts[0];
		this.pwd = parts[1];
		
		this.logger.INFO("BandwidthService()- getClient(hostName, Auth) Setting basic Auth, Credentials=" + this.uname + "/" + this.pwd.substring(0, 1) + "****" + this.pwd.substring(this.pwd.length()-1));
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().credentials(this.uname, this.pwd)
				.build();

		client.register(feature);
		return client.target(hostName);
	}
}
