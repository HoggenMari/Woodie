package core;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SimpleMqttCallBack implements MqttCallback {	

  public void connectionLost(Throwable throwable) {
    System.out.println("Connection to MQTT broker lost!");
  }

  public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

	String payload = new String(mqttMessage.getPayload());
	switch(s){
		case "rpi":
			switch(payload) {
				case "reboot":
					System.out.println("Message received:\t" + new String(mqttMessage.getPayload()));
					try {
						Runtime.getRuntime().exec("sudo reboot -h now");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case "shutdown":
					System.out.println("Message received:\t" + new String(mqttMessage.getPayload()));
					try {
						Runtime.getRuntime().exec("sudo shutdown -h now");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					System.out.println("Invalid topic");
			}
		case "gcode":
		    System.out.println("Message received:\t" + payload);
		    GcodeSender.getInstance().sendData(payload);
		    break;
		case "chalk":
			System.out.println("Message received:\t" + payload);
			if (payload.contentEquals("up")) {
				System.out.println("up");
				GcodeSender.getInstance().send("UP\n");
			} else if (payload.contentEquals("down")) {
				System.out.println("down");
				GcodeSender.getInstance().send("DOWN\n");
			}
			break;
		case "control":
			System.out.println("Message received:\t" + payload);
			if (payload.contentEquals("pause")) {
				System.out.println("pause");
				GcodeSender.getInstance().writeReset();
			} else if (payload.contentEquals("stop")) {
				System.out.println("stop");
			}
			break;
		case "draw":
			System.out.println("Message received:\t" + payload);
			if (payload.contentEquals("flower")) {
				System.out.println("flower");
			}
			break;
		default:
			System.out.println("Invalid topic");

	}
  }

  public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
  }
  
}