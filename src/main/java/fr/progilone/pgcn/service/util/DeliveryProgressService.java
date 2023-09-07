package fr.progilone.pgcn.service.util;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.web.websocket.WebsocketService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryProgressService {

    private final WebsocketService websocketService;

    private static final Map<String, Map<String, String>> PROGRESS_VALUES = Collections.synchronizedMap(new HashMap<>());

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
    public void deliveryProgress(final Delivery delivery,
                                 final String idDoc,
                                 final String typeMsg,
                                 final String status,
                                 final int progress,
                                 final String stage,
                                 final boolean reload) {

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
        Map<String, String> deliveryProgressMap = getOrCreateDeliveryProgress(delivery.getIdentifier());
        if (StringUtils.isNotBlank(idDoc)) {
            statusMap.put("idDoc", idDoc);
            deliveryProgressMap.put(idDoc, String.valueOf(progress));
        } else if (progress > 0) {
            deliveryProgressMap.keySet().forEach(k -> deliveryProgressMap.put(k, String.valueOf(progress)));
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

    public Map<String, String> getDeliveryProgress(final String id, final String digitalId) {
        if (StringUtils.isNotBlank(id) && PROGRESS_VALUES.containsKey(id)) {
            final Map<String, String> values = PROGRESS_VALUES.get(id);
            if (StringUtils.isNotBlank(digitalId) && values.containsKey(digitalId)) {
                final Map<String, String> result = new HashMap<>();
                result.put(digitalId, values.get(digitalId));
                return result;
            } else {
                return values;
            }
        }
        return Collections.emptyMap();
    }

    public void removeDeliveryProgress(final String id) {
        if (StringUtils.isNotBlank(id) && PROGRESS_VALUES.containsKey(id)) {
            PROGRESS_VALUES.remove(id);
        }
    }

    public Map<String, String> getOrCreateDeliveryProgress(final String id) {
        return PROGRESS_VALUES.computeIfAbsent(id, k -> Collections.synchronizedMap(new HashMap<>()));
    }

}
