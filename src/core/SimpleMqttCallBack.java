package core;

import java.io.IOException;

import javax.swing.event.EventListenerList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import core.ChalkEvent.ChalkEventObject;
import core.GcodeSender.GCodeStatus;
import core.LightEvent.LightEventObject;

public class SimpleMqttCallBack implements MqttCallback {	

	
	EventListenerList listenerList = new EventListenerList();
	EventListenerList chalkListenerList = new EventListenerList();

	
	float brightness;
	
	public void addLightEventListener(LightControlListener l) {
		listenerList.add(LightControlListener.class, l);
	}

	public void removeLightEventListener(LightControlListener l) {
		listenerList.remove(LightControlListener.class, l);
	}	
	
	public void addChalkEventListener(ChalkEventListener l) {
		chalkListenerList.add(ChalkEventListener.class, l);
	}

	public void removeChalkEventListener(ChalkEventListener l) {
		chalkListenerList.remove(ChalkEventListener.class, l);
	}

	
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
				//sendChalkEvent(true);
			} else if (payload.contentEquals("down")) {
				System.out.println("down");
				GcodeSender.getInstance().send("DOWN\n");
				//sendChalkEvent(false);
			} else if (payload.contentEquals("turboup")) {
				System.out.println("turboup");
				GcodeSender.getInstance().send("TURBOUP\n");
				sendChalkEvent(true);
			} else if (payload.contentEquals("turbodown")) {
				System.out.println("turbodown");
				GcodeSender.getInstance().send("TURBODOWN\n");
				sendChalkEvent(false);
			}
			break;
		case "control":
			System.out.println("Message received:\t" + payload);
			if (payload.contentEquals("pause")) {
				System.out.println("pause");
				GcodeSender.getInstance().pause();
				//GcodeSender.getInstance().writeReset();
			} else if (payload.contentEquals("resume")) {
				System.out.println("resume");
				GcodeSender.getInstance().resume();
			} else if (payload.contentEquals("stop")) {
				System.out.println("stop");
				GcodeSender.getInstance().stop();
			} else if (payload.contains("detection")) {
				String[] detectionString = payload.split(" ");
				if(detectionString[1].contains("on")) {
					System.out.println("detection On");
					GcodeSender.getInstance().setDetection(true);
				} else if (detectionString[1].contains("off")) {
					System.out.println("detection Off");
					GcodeSender.getInstance().setDetection(false);
				}
			}
			break;
		case "draw":
			System.out.println("Message received:\t" + payload);
			int number = Integer.parseInt(payload);
			if (number>0) {
				GcodeSender.getInstance().draw(number);
			}
			if (payload.contentEquals("flower")) {
				System.out.println("flower");
			}
			break;
		case "move":
			System.out.println("Message received:\t" + payload);
			String[] parts = payload.split(" ");
			int distance = Integer.parseInt(parts[1]);
			if (parts[0].contentEquals("up")) {
				System.out.println(distance);
				GcodeSender.getInstance().move(GcodeSender.Direction.UP, distance);
			} else if (parts[0].contentEquals("down")) {
				System.out.println(distance);
				GcodeSender.getInstance().move(GcodeSender.Direction.DOWN, distance);
			} else if (parts[0].contentEquals("left")) {
				System.out.println(distance);
				GcodeSender.getInstance().move(GcodeSender.Direction.LEFT, distance);
			} else if (parts[0].contentEquals("right")) {
				System.out.println(distance);
				GcodeSender.getInstance().move(GcodeSender.Direction.RIGHT, distance);
			}
			break;
		case "lightcontrol":
			System.out.println("Message received:\t" + payload);
			if (payload.contentEquals("togglelight")) {
				triggerLightEvent(LightEventObject.toggleLight);
				//System.out.println("pause");
				//GcodeSender.getInstance().pause();
			} else if (payload.contentEquals("toggleguidance")) {
				triggerLightEvent(LightEventObject.toggleGuidance);
				//System.out.println("resume");
				//GcodeSender.getInstance().resume();
			} else if (payload.contains("brightness")) {
				//
				String[] brightnessStrings = payload.split(" ");
				brightness = Float.parseFloat(brightnessStrings[1]);
				triggerLightEvent(LightEventObject.brightnessChanged);
			}
			break;
		default:
			System.out.println("Invalid topic");

	}
  }
  
  private void sendChalkEvent(boolean up) {
	  
	  System.out.println("CHALK UP");
	  
	  ChalkEvent event;
	  if (up) {
		  event = new ChalkEvent(this, ChalkEventObject.chalkUp);
	  } else {
		  event = new ChalkEvent(this, ChalkEventObject.chalkDown);
	  }
	  
	  Object[] listeners = chalkListenerList.getListenerList();
		
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == ChalkEventListener.class) {
				((ChalkEventListener) listeners[i + 1])
						.chalkEvent(event);
			}
		}	
  }

  private void triggerLightEvent(LightEventObject event) {
		Object[] listeners = listenerList.getListenerList();
		
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == LightControlListener.class) {
				((LightControlListener) listeners[i + 1])
						.lightEvent(new LightEvent(this, event, brightness));
			}
		}		
	}
  
  public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
  }
  
}