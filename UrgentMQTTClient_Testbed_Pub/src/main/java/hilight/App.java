package hilight;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class App extends Thread {
    private ArrayList<MqttClient> hilightMqttClients;
    private String broker = "tcp://192.168.1.86:1883";
    private String normalClientId = "MQTT_Testbed_N";
    private String urengtClientId = "MQTT_Testbed_U";
    private MemoryPersistence persistence;
    private MqttClient client;
    private String normalTopic = "test";
    private String urgentTopic = "test";

    public App(int normalPubs, int urgentPubs) {
        persistence = new MemoryPersistence();
        hilightMqttClients = new ArrayList<MqttClient>();
        connectClient(normalPubs, urgentPubs);
        this.start();

    }

    public void connectClient(int normalPubs, int urgentPubs) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("pub_log.csv"), Charset.forName("EUC-KR"), StandardOpenOption.CREATE);
            writer.write("메시지구분, ClientID, Pub 시간, Flag\n");
            writer.close();
            writer = Files.newBufferedWriter(Paths.get("pub_log.csv"), Charset.forName("EUC-KR"), StandardOpenOption.APPEND);

            for (int i = 0; i < normalPubs; i++) {
                try {
                    client = new HilightNormalClient(broker, normalClientId + i, persistence, normalTopic, writer);
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
                    client = new HilightUrgentClient(broker, urengtClientId + i, persistence, urgentTopic, writer);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
