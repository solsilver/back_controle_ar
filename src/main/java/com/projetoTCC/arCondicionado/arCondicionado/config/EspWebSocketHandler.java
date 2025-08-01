package com.projetoTCC.arCondicionado.arCondicionado.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EspWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> dispositivos = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        JsonNode msg = new ObjectMapper().readTree(message.getPayload());

        if ("register".equals(msg.get("type").asText())) {
            String deviceId = msg.get("deviceId").asText();
            dispositivos.put(deviceId, session);
            session.sendMessage(new TextMessage(msg.toString()));
            System.out.println("Dispositivo registrado: " + deviceId);
        }
    }

    public void enviarComandoParaDispositivo(String deviceId, String jsonComando) {
        WebSocketSession session = dispositivos.get(deviceId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(jsonComando));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Dispositivo " + deviceId + " desconectado ou não encontrado.");
        }
    }
}
