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
    	colorMode(RGB);
    	//frameRate(10);
    	
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
		    client.subscribe("move");

		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GcodeSender.getInstance();
		GcodeSender.setupConnection(portname, this);
		
		LEDController.instance.setupConnection(this);
		
    	pg = createGraphics(16,4);
    	
				
    }

    public void draw(){
    	
    	pg.beginDraw();
    	pg.colorMode = PConstants.RGB;
    	pg.noStroke();
    	pg.fill(frameRate%255,255,0);
    	//for(int i=0; i<pg.width; i++) {
        //	pg.fill(255-(frameCount%10)*10,i*10,10);
        //	pg.rect(i,0,1,pg.height);
    	//}
    	pg.rect(0,0,pg.width,pg.height);
    	pg.endDraw();

    	LEDController.instance.send(pg);

    	
    	/*if (this.frameCount % 100 == 0) {
    		System.out.println(this.frameRate);
    	}*/
    	
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
    
}