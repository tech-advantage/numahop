package fr.progilone.pgcn.web.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.web.websocket.dto.NotificationDTO;
import fr.progilone.pgcn.web.websocket.dto.NotificationDTO.NotificationCode;
import fr.progilone.pgcn.web.websocket.dto.NotificationDTO.NotificationLevel;

@Service
public class WebsocketService  {

    private static final Logger LOG = LoggerFactory.getLogger(WebsocketService.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;


    public void sendNotification(final String userLogin, final NotificationCode notificationCode, final NotificationLevel notificationLevel) {
        final NotificationDTO notificationDTO = new NotificationDTO(userLogin, notificationCode, notificationLevel);
        LOG.trace("sendNotification : {}", notificationDTO);
        messagingTemplate.convertAndSend("/topic/notification", notificationDTO);
    }

    public void sendObject(final String identifier, final Object object) {
        LOG.trace("sendObject : {}", identifier);
        messagingTemplate.convertAndSend("/topic/object/" + identifier, object);
    }
}
