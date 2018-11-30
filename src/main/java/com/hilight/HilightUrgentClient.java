package com.hilight;

import org.eclipse.paho.client.mqttv3.*;

public class HilightUrgentClient extends MqttClient implements Runnable {
    private MqttMessage message;
    private String topic;
    private MqttConnectOptions connOpts = new MqttConnectOptions();

    public HilightUrgentClient(String serverURI, String clientId, MqttClientPersistence persistence, String topic, String content)
            throws MqttException {
        super(serverURI, clientId, persistence);
        this.topic = topic;
        message = new MqttMessage((content + "1").getBytes());
    }

    public void run() {
        try {
            connect(connOpts);
            publish(topic, message);
            disconnect();
            close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
