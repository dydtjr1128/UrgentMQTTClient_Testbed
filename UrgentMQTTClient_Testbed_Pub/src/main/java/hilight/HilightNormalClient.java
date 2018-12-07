package hilight;

import org.eclipse.paho.client.mqttv3.*;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.time.LocalTime;

public class HilightNormalClient extends MqttClient implements Runnable {
    private MqttMessage message;
    private String topic;
    private MqttConnectOptions connOpts = new MqttConnectOptions();
    private String clientId;
    private StringBuilder builder;
    private BufferedWriter writer;

    public HilightNormalClient(String serverURI, String clientId, MqttClientPersistence persistence, String topic, BufferedWriter writer)
            throws MqttException {
        super(serverURI, clientId, persistence);
        builder = new StringBuilder();
        this.clientId = clientId;
        this.topic = topic;
        this.writer = writer;
    }

    public void run() {
        try {
            connect(connOpts);
            builder.append("NormalMessage").append(",").append(clientId).append(",").append(LocalTime.now().toString()).append(",N0");
            message = new MqttMessage(builder.toString().getBytes());//client id:time:N
            publish(topic, message);
            disconnect();
            close();

            writer.write(builder.delete(builder.length()-1, builder.length()).append("\n").toString());
            writer.flush();
            builder.setLength(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}