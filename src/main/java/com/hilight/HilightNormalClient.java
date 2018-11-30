package com.hilight;

import org.eclipse.paho.client.mqttv3.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalTime;

public class HilightNormalClient extends MqttClient implements Runnable {
    private MqttMessage message;
    private String topic;
    private MqttConnectOptions connOpts = new MqttConnectOptions();
    private String clientId;
    private String content;
    private StringBuilder builder;
    private FileWriter writer;
    public HilightNormalClient(String serverURI, String clientId, MqttClientPersistence persistence, String topic, String content)
            throws MqttException {
        super(serverURI, clientId, persistence);
        builder = new StringBuilder();
        this.clientId = clientId;
        this.topic = topic;
        this.content = content;
    }

    public void run() {
        try {
            connect(connOpts);
            builder.append(content).append(",").append(clientId).append(",").append(LocalTime.now().toString()).append(",N0");
            message = new MqttMessage(builder.toString().getBytes());//client id:time:N
            publish(topic, message);
            disconnect();
            close();

            try {
                writer = new FileWriter("log.csv",true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            writer.write(builder.delete(builder.length()-1, builder.length()).append("\n").toString());
            writer.flush();
            writer.close();
            builder.setLength(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}