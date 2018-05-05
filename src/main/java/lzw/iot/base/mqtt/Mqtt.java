package lzw.iot.base.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mqtt
 *
 * @author lzw
 * @date 2018/5/5 3:20
 **/
@Component
public class Mqtt {


    private String host = "tcp://127.0.0.1:61613";

    private MqttClient client;
    private String clientId = UUID.randomUUID().toString();
    private MqttConnectOptions options;
    private String userName = "admin";    //非必须
    private String passWord = "password";  //非必须



    public Mqtt() throws MqttException {
        // host为主机名，clientid即连接MQTT的客户端ID，一般以唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
        client = new MqttClient(host, clientId, new MemoryPersistence());
        // MQTT的连接设置
        options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置连接的用户名
        options.setUserName(userName);
        // 设置连接的密码
        options.setPassword(passWord.toCharArray());
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        // 设置回调
        client.setCallback(new PushCallback());
        //连接
        client.connect(options);
    }

    public MqttClient getClient() {
        return client;
    }
}
