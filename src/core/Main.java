package core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.fazecast.jSerialComm.SerialPort;

import core.LightEvent.LightEventObject;
import processing.core.*;

public class Main extends PApplet implements GCodeStatusListener, LightControlListener {
	
	boolean send = false;
	
	String grbl_start = "Grbl 1.1f ['$' for help]";
	boolean grblStarted = false;
	
	static String portname = "";
	
	PGraphics pg;
		
	MqttClient client;
	
	private int qos = 2;
	
	boolean lightsOn = true;
	boolean guidanceOn = false;

	
	public static void main(String[] args) {
        PApplet.main("core.Main");	
        
        if(args.length > 0) {
        	portname = args[0];
        }
    }

    public void setup(){
		
    	size(100,100);
    	colorMode(RGB);
    	frameRate(30);
    	
		System.out.println("== START SUBSCRIBER ==");

		SimpleMqttCallBack mqttCallback = new SimpleMqttCallBack();
		mqttCallback.addLightEventListener(this);
		
		try {
			client = new MqttClient("tcp://localhost:6667", Long.toString(System.currentTimeMillis()));
			client.setCallback( mqttCallback );
		    client.connect();

		    client.subscribe("rpi");
		    client.subscribe("gcode");
		    client.subscribe("chalk");
		    client.subscribe("control");
		    client.subscribe("draw");
		    client.subscribe("move");
		    client.subscribe("lightcontrol");

		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GcodeSender.getInstance();
		GcodeSender.getInstance().setupConnection(portname, this);
		
		GcodeSender.getInstance().addGCodeStatusListener(this);
		
		LEDController.instance.setupConnection(this);
		
    	pg = createGraphics(16,4);
    	
				
    }

    public void draw(){
    	
    	pg.beginDraw();
    	pg.colorMode = PConstants.RGB;
    	pg.noStroke();
    	pg.fill(frameCount%255,255,0);
    	//for(int i=0; i<pg.width; i++) {
        //	pg.fill(255-(frameCount%10)*10,i*10,10);
        //	pg.rect(i,0,1,pg.height);
    	//}
    	pg.rect(0,0,pg.width,pg.height);
    	if (!lightsOn) {
    		pg.background(0);
    	}
    	if (guidanceOn) {
    		pg.fill(255,255,255,255);
    		pg.rect(0,0,1,pg.height);
    	}
    	pg.endDraw();

    	LEDController.instance.send(pg);

    	
    	if (this.frameCount % 100 == 0) {
    		System.out.println(this.frameRate);
    	}
    	
    	//GcodeSender.requestData();
    	//GcodeSender.printCommands();
    	/*if (GcodeSender.grblStarted && !GcodeSender.getInstance().send) {
    		delay(100);
    		//GcodeSender.getInstance().sendData();
    		GcodeSender.getInstance().send = true;
    		GcodeSender.readFile("/home/pi/woodie/gcode/output_0003.ngc");
    		GcodeSender.printCommands();
    	}*/
    }

	@Override
	public void statusChanged(GCodeStatusEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Received Event: " + e.status);
		
	}

	@Override
	public void drawingStatusChanged(double percent) {
		// TODO Auto-generated method stub
		System.out.println("DrawingStatusChanged: " + percent);
		
		String perString = Double.toString(percent);
		MqttMessage message = new MqttMessage(perString.getBytes());
        message.setQos(qos);
        try {
			client.publish("drawingstatus", message);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

	@Override
	public void lightEvent(LightEvent e) {
		// TODO Auto-generated method stub
		if (e.object == LightEventObject.toggleLight) {
			System.out.println("toogleLight");
			lightsOn = !lightsOn;
		} else if (e.object == LightEventObject.toggleGuidance) {
			guidanceOn = !guidanceOn;
			System.out.println("toogleGuidance");
		}
	}
    
}