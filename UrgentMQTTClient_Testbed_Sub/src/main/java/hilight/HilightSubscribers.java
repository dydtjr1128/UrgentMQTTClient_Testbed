package hilight;

import org.eclipse.paho.client.mqttv3.*;

import java.io.BufferedWriter;
import java.time.LocalTime;

public class HilightSubscribers extends MqttClient implements MqttCallback,Runnable {
    private String topic;
    private MqttConnectOptions connOpts = new MqttConnectOptions();
    private StringBuilder builder;
    private BufferedWriter writer;

    public HilightSubscribers(String serverURI, String clientId, MqttClientPersistence persistence, String topic, BufferedWriter writer)
            throws MqttException {
        super(serverURI, clientId, persistence);
        builder = new StringBuilder();
        this.topic = topic;
        this.writer = writer;
    }

    public void run() {
        try {
            connect(connOpts);
            setCallback(this);
            subscribe(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String message = mqttMessage.toString();
        builder.append(message).append(",").append(LocalTime.now().toString());

        writer.write(builder.append("\n").toString());
        writer.flush();
        builder.setLength(0);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}