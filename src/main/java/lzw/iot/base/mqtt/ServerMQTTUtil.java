package lzw.iot.base.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * @author zzy
 * @date 2018/3/13 18:10
 **/
public class ServerMQTTUtil {

    //tcp://MQTT安装的服务器地址:MQTT定义的端口号
    public static final String HOST = "tcp://dev.****.com.cn:1883";
    //定义MQTT的ID，可以在MQTT服务配置中指定
    private static final String clientid = "server11";

    private MqttClient client;
    private MqttTopic mqttTopic;
    private String userName = "paho";
    private String passWord = "";

    /**
     * 构造函数
     * @throws MqttException
     */
    public ServerMQTTUtil(String topic) throws MqttException {
        // MemoryPersistence设置clientid的保存形式，默认为以内存保存
        client = new MqttClient(HOST, clientid, new MemoryPersistence());
        connect(topic);
    }

    /**
     *  用来连接服务器
     */
    private void connect(String topic) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(10);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
//          client.setCallback(new PushCallback());
            client.connect(options);
            mqttTopic = client.getTopic(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送消息并获取回执
    public void publish(MqttMessage message) throws MqttPersistenceException,
            MqttException, InterruptedException {
        MqttDeliveryToken token = mqttTopic.publish(message);
        token.waitForCompletion();
        System.out.println("message is published completely! "
                + token.isComplete());
        System.out.println("messageId:" + token.getMessageId());
        token.getResponse();
        if (client.isConnected()) {
            client.disconnect(10000);
        }
        System.out.println("Disconnected: delivery token \"" + token.hashCode()
                + "\" received: " + token.isComplete());
    }


    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassWord() {
        return passWord;
    }
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public MqttTopic getMqttTopic() {
        return mqttTopic;
    }

    public void setMqttTopic(MqttTopic mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

}
