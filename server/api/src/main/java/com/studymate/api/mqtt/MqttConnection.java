package com.studymate.api.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.crt.CRT;
import software.amazon.awssdk.crt.CrtRuntimeException;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttClientConnectionEvents;
import software.amazon.awssdk.iot.AwsIotMqttConnectionBuilder;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Configuration
public class MqttConnection {
    @Value("${aws.client-id}")
    private String clientId;
    @Value("${aws.cert-path}")
    private String certPath;
    @Value("${aws.key-path}")
    private String keyPath;
    @Value("${aws.cert-auth-path}")
    private String certAuthPath;
    @Value("${aws.endpoint}")
    private String endpoint;

    @Bean
    public MqttClientConnection mqttClient() {
        MqttClientConnectionEvents events = new MqttClientConnectionEvents() {
            @Override
            public void onConnectionInterrupted(int errorCode) {
                if (errorCode != 0) {
                    log.info("Connection interrupted: " + errorCode + ": " + CRT.awsErrorString(errorCode));
                }
            }

            @Override
            public void onConnectionResumed(boolean sessionPresent) {
                log.info("Connection resumed: " + (sessionPresent ? "existing session" : "clean session"));
            }
        };

        try {
            MqttClientConnection conn =  AwsIotMqttConnectionBuilder.newMtlsBuilderFromPath(certPath, keyPath)
                    .withCertificateAuthorityFromPath(null, certAuthPath)
                    .withClientId(clientId)
                    .withConnectionEventCallbacks(events)
                    .withEndpoint(endpoint)
                    .withPort((short)8883)
                    .withCleanSession(true)
                    .withProtocolOperationTimeoutMs(6000)
                    .build();
            log.info("AWS MQTT Client successfully created");

            CompletableFuture<Boolean> connected = conn.connect();
            boolean sessionPresent = connected.get();
            log.info("Connected to " + (!sessionPresent ? "new" : "existing") + " session!");

            return conn;
        } catch (CrtRuntimeException ex) {
            log.warn("AWS MQTT Client creation failed");
            return null;
        } catch (Exception ex) {
            throw new RuntimeException("Exception occurred during AWS Iot connect", ex);
        }
    }


}
