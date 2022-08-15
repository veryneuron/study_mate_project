//package com.studymate.api.mqtt;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
//
//import javax.annotation.PostConstruct;
//import java.util.concurrent.CompletableFuture;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class MqttInit {
//    private final MqttClientConnection conn;
//    private Boolean isConnected = false;
//
//    @PostConstruct
//    public void init() {
//        CompletableFuture<Boolean> connected = conn.connect();
//        try {
//            isConnected = connected.get();
//            log.info("Connected to " + (!isConnected ? "new" : "existing") + " session!");
//            log.info("AWS MQTT Client successfully connected");
//        } catch (Exception ex) {
//            throw new RuntimeException("Exception occurred during AWS Iot connect", ex);
//        }
//    }
//    public Boolean isConnected() {
//        return isConnected;
//    }
//}
