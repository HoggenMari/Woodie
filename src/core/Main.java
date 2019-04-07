package core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.fazecast.jSerialComm.SerialPort;

import processing.core.*;

public class Main extends PApplet {
	
	boolean send = false;
	
	String grbl_start = "Grbl 1.1f ['$' for help]";
	boolean grblStarted = false;
	
	static String portname = "";
	
	PGraphics pg;
		
	public static void main(String[] args) {
        PApplet.main("core.Main");	
        
        if(args.length > 0) {
        	portname = args[0];
        }
    }

    public void setup(){
		
    	size(100,100);
    	
		System.out.println("== START SUBSCRIBER ==");

	    MqttClient client;
		try {
			client = new MqttClient("tcp://localhost:6667", Long.toString(System.currentTimeMillis()));
			client.setCallback( new SimpleMqttCallBack() );
		    client.connect();

		    client.subscribe("rpi");
		    client.subscribe("gcode");
		    client.subscribe("chalk");
		    client.subscribe("control");
		    client.subscribe("draw");

		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GcodeSender.getInstance();
		GcodeSender.setupConnection(portname);
		
		LEDController.instance.setupConnection(this);
		
    	pg = createGraphics(12,4);

				
    }

    public void draw(){
    	
    	pg.beginDraw();
    	pg.background(frameCount%255,220,220);
    	pg.endDraw();
    	
    	LEDController.instance.send(pg);
    	
    	//GcodeSender.requestData();
    	//GcodeSender.printCommands();
    	if (GcodeSender.grblStarted && !GcodeSender.getInstance().send) {
    		delay(100);
    		//GcodeSender.getInstance().sendData();
    		GcodeSender.getInstance().send = true;
    		GcodeSender.readFile("/home/pi/woodie/gcode/output_0002.ngc");
    		GcodeSender.printCommands();
    	}
    }
    
}