package fr.progilone.pgcn.config;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration extends AbstractWebSocketMessageBrokerConfigurer {

    private final Logger LOG = LoggerFactory.getLogger(WebsocketConfiguration.class);

    public static final String IP_ADDRESS = "IP_ADDRESS";

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOrigins("*").withSockJS().setInterceptors(httpSessionHandshakeInterceptor());
    }

    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HandshakeInterceptor() {

            @Override
            public boolean beforeHandshake(final ServerHttpRequest request,
                                           final ServerHttpResponse response,
                                           final WebSocketHandler wsHandler,
                                           final Map<String, Object> attributes) throws Exception {
                if (request instanceof ServletServerHttpRequest) {
                    final ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                    final List<String> ips = servletRequest.getHeaders().get("X-Forwarded-For");
                    if (ips != null && !ips.isEmpty()) {
                        attributes.put(IP_ADDRESS, ips.get(0));
                    } else {
                        attributes.put(IP_ADDRESS, servletRequest.getRemoteAddress());
                    }
                }
                return true;
            }

            @Override
            public void afterHandshake(final ServerHttpRequest request,
                                       final ServerHttpResponse response,
                                       final WebSocketHandler wsHandler,
                                       final Exception exception) {

            }
        };
    }
    
}
