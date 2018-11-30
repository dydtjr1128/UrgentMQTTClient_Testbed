package com.hilight;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class App extends Thread {
    private ArrayList<MqttClient> hilightMqttClients;
    private int qos = 2;
    private String broker = "tcp://192.168.1.86:1883";
    private String normalClientId = "MQTT_Testbed_N";
    private String urengtClientId = "MQTT_Testbed_U";
    private MemoryPersistence persistence;
    private MqttClient client;
    private String normalTopic = "test";
    private String urgentTopic = "test";
    private String normalContent = "message";
    private String urgentContent = "message";

    public App(int normalPubs, int urgentPubs) {
        persistence = new MemoryPersistence();
        hilightMqttClients = new ArrayList<MqttClient>();
        connectClient(normalPubs, urgentPubs);
        this.start();
    }

    public void connectClient(int normalPubs, int urgentPubs) {
        for (int i = 0; i < normalPubs; i++) {
            try {
                client = new HilightNormalClient(broker, normalClientId + i, persistence, normalTopic, normalContent);
                hilightMqttClients.add(client);
            } catch (MqttException me) {
                System.out.println("reason " + me.getReasonCode());
                System.out.println("msg " + me.getMessage());
                System.out.println("loc " + me.getLocalizedMessage());
                System.out.println("cause " + me.getCause());
                System.out.println("excep " + me);
                me.printStackTrace();
            }
        }
        System.out.println("Normal clients is connected!");
        for (int i = 0; i < urgentPubs; i++) {
            try {
                client = new HilightUrgentClient(broker, urengtClientId + i, persistence, urgentTopic, urgentContent);
                hilightMqttClients.add(client);
            } catch (MqttException me) {
                System.out.println("reason " + me.getReasonCode());
                System.out.println("msg " + me.getMessage());
                System.out.println("loc " + me.getLocalizedMessage());
                System.out.println("cause " + me.getCause());
                System.out.println("excep " + me);
                me.printStackTrace();
            }
        }
        System.out.println("Urgent clients is connected!");
    }

    @Override
    public void run() {
        for (MqttClient client : hilightMqttClients) {
            try {
                if (client instanceof HilightNormalClient)
                    new Thread((HilightNormalClient) client).start();
                else
                    new Thread((HilightUrgentClient) client).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
