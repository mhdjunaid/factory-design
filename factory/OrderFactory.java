package com.twc.gw.tpgw.connector.factory;

import com.twc.gw.tpgw.connector.defines.BandwidthOrderType;
import com.twc.gw.tpgw.connector.service.ActivationStatusService;
import com.twc.gw.tpgw.connector.service.DLDAOrderService;
import com.twc.gw.tpgw.connector.service.DisconnectOrderService;
import com.twc.gw.tpgw.connector.service.E911OrderService;
import com.twc.gw.tpgw.connector.service.LIDBOrderService;
import com.twc.gw.tpgw.connector.service.LNPNotesService;
import com.twc.gw.tpgw.connector.service.LNPOrderService;
import com.twc.gw.tpgw.connector.service.TnQueryOrderService;
import com.twc.gw.tpgw.connector.service.ValidateLocationService;

public class OrderFactory {
	
    public static BandwidthOrder getBandwidthOrder(String type) {
    	BandwidthOrderType orderType = BandwidthOrderType.get(type);
    	switch (orderType) {
        case LNP:
          return new LNPOrderService();
        case TN:
            return new TnQueryOrderService();
        case E911:
            return new E911OrderService();
        case DISCONNECT:
            return new DisconnectOrderService();
        case LIDB:
            return new LIDBOrderService();
        case DLDA:
            return new DLDAOrderService();
        case ACTIVESTATUS:
            return new ActivationStatusService();
        case NOTE:
            return new LNPNotesService();
        case VALIDATELOCATION:
            return new ValidateLocationService();
        case LNPSUPP:
            return new LNPOrderService();
        default:
          throw new IllegalArgumentException("Order Type not supported.");
      }
    }

}