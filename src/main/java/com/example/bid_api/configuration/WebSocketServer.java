//package com.example.bid_api.configuration;
//
//import jakarta.websocket.*;
//import jakarta.websocket.server.ServerEndpoint;
//import lombok.extern.slf4j.Slf4j;
//import org.example.authentication.model.entity.User;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.CopyOnWriteArraySet;
//import java.util.stream.Collectors;
//
//@Component
//@Slf4j
//@Service
//@ServerEndpoint("/websocket")
//public class WebSocketServer {
//    private static int onlineCount = 0;
//    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
//
//    private Session session;
//    private String userId = "";
//
//
//    @OnOpen
//    public void onOpen(Session session) {
//        try {
//            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                    (UsernamePasswordAuthenticationToken) session.getUserPrincipal();
//            User user = (User) usernamePasswordAuthenticationToken.getPrincipal();
//            this.session = session;
//            webSocketSet.add(this);
//            this.userId = user.getUserId();
//            addOnlineCount();
//            log.info("userId: " + userId + " open connect websocket");
//        } catch (Exception e) {
//            log.error(userId + ": " + e.getMessage());
//        }
//    }
//
//
//    @OnClose
//    public void onClose() {
//        webSocketSet.remove(this);
//        subOnlineCount();
//        log.info("user : " + userId + " close connect websocket");
//    }
//
//
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        for (WebSocketServer item : webSocketSet) {
//            try {
//                if (Objects.equals(item.userId, this.userId)) {
//                    item.sendMessage(message);
//                }
//            } catch (IOException e) {
//                log.error(e.getMessage());
//            }
//        }
//    }
//
//
//    @OnError
//    public void onError(Session session, Throwable error) {
//        subOnlineCount();
//        webSocketSet.remove(this);
//
//    }
//
//
//    public void sendMessage(String message) throws IOException {
//        this.session.getBasicRemote().sendText(message);
//    }
//
//    public static void sendInfo(String message, String userId) {
//        List<WebSocketServer> webSocketServers = webSocketSet.stream().filter(webSocket -> Objects.equals(webSocket.userId, userId)).toList();
//
//        if (!webSocketServers.isEmpty()) {
//            webSocketServers.forEach(webSocketServer -> {
//                try {
//                    webSocketServer.sendMessage(message);
//                    log.info(String.format("pushed message %s to %s", message, webSocketServer.userId));
//                } catch (IOException e) {
//                    log.error(e.getMessage());
//                }
//            });
//        }
//    }
//
//    public static synchronized int getOnlineCount() {
//        return onlineCount;
//    }
//
//    public static synchronized void addOnlineCount() {
//        WebSocketServer.onlineCount++;
//    }
//
//    public static synchronized void subOnlineCount() {
//        WebSocketServer.onlineCount--;
//    }
//
//    public static List<String> getWebSocketSet() {
//        return webSocketSet.stream().map(s -> s.userId).collect(Collectors.toList());
//    }
//}
//
