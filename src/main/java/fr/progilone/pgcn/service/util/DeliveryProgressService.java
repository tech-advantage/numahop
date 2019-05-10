package fr.progilone.pgcn.service.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.web.websocket.WebsocketService;

@Service
public class DeliveryProgressService {
    
    private final WebsocketService websocketService;
    
    @Autowired
    public DeliveryProgressService(final WebsocketService websocketService) {
        this.websocketService = websocketService;
    }

    
    /**
     * Informe le client sur l'avancement de la livraison.
     * 
     * @param delivery
     * @param idDoc
     * @param typeMsg
     * @param status
     * @param progress
     * @param stage
     * @param reload
     */
    public void deliveryProgress(final Delivery delivery, final String idDoc, final String typeMsg, final String status, final int progress, final String stage, final boolean reload) {
        
        if (delivery == null) {
            return;
        }
        
        // alimentation du websocket
        final Map<String, Object> statusMap = new HashMap<>();
        
        statusMap.put("status", status);
        statusMap.put("typeMsg", typeMsg);
        statusMap.put("progress", progress);
        statusMap.put("reload", reload);
        statusMap.put("stage", stage);
        if (StringUtils.isNotBlank(idDoc)){
            statusMap.put("idDoc", idDoc);
        }
        websocketService.sendObject(delivery.getIdentifier(), statusMap);
    }
    
    /**
     * Informe le client sur l'avancement de la livraison.
     * 
     * @param delivery
     * @param idDoc
     * @param typeMsg
     * @param status
     * @param progress
     * @param stage
     */
    public void deliveryProgress(final Delivery delivery, final String idDoc, final String typeMsg, final String status, final int progress, final String stage) {
        deliveryProgress(delivery, idDoc, typeMsg, status, progress, stage, false);
    }

    
}
