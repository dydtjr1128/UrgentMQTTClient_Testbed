package hilight;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class App extends Thread {
    private ArrayList<MqttClient> hilightMqttClients;
    private int qos = 2;
    private String broker = "tcp://192.168.1.86:1883";
    private MemoryPersistence persistence;
    private MqttClient client;
    private String topic = "test";


    public App(int subs) {
        persistence = new MemoryPersistence();
        hilightMqttClients = new ArrayList<MqttClient>();
        connectClient(subs);
        this.start();
    }

    public void connectClient(int subs) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("sub_log.csv"), Charset.forName("EUC-KR"), StandardOpenOption.CREATE);
            writer.write("메시지구분, Pub ID, Pub 시간, Flag, Sub 시간\n");
            writer.close();
            writer = Files.newBufferedWriter(Paths.get("sub_log.csv"), Charset.forName("EUC-KR"), StandardOpenOption.APPEND);

            for (int i = 0; i < subs; i++) {
                try {
                    client = new HilightSubscribers(broker, "Sub" + i, persistence, topic, writer);
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
            System.out.println(subs + " subscribers is created!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (MqttClient client : hilightMqttClients) {
            try {
                new Thread((HilightSubscribers) client).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
