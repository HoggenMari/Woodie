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
	
	SerialPort port = null;
	String ARDUINO = "ttyUSB";
	int BAUDRATE = 115200;
	
	boolean send = false;
	
	String grbl_start = "Grbl 1.1f ['$' for help]";
	boolean grblStarted = false;
		
	public static void main(String[] args) {
        PApplet.main("core.Main");		
    }

    public void setup(){
		
		System.out.println("== START SUBSCRIBER ==");

	    MqttClient client;
		try {
			client = new MqttClient("tcp://192.168.0.102:6667", Long.toString(System.currentTimeMillis()));
			client.setCallback( new SimpleMqttCallBack() );
		    client.connect();

		    client.subscribe("rpi");
		    client.subscribe("gcode");
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GcodeSender.getInstance();
	    
		// open serial port
		/*SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++) {
			System.out.println(portNames[i].getSystemPortName());
			if (portNames[i].getSystemPortName().contains(ARDUINO)) {
				System.out.println(portNames[i].getSystemPortName());
				port = portNames[i];
			}
		}
		
		if (port != null) {
			port.setBaudRate(BAUDRATE);
			if (port.openPort()) {
				System.out.print("port open");
			}
		}*/
				
    }

    public void draw(){
    	GcodeSender.getInstance().requestData();
    	if (GcodeSender.getInstance().grblStarted && !GcodeSender.getInstance().send) {
    		delay(100);
    		GcodeSender.getInstance().sendData();
    		GcodeSender.getInstance().send = true;
    	}
    }
    
    /*public void requestData() {
		if (port.openPort()) {
		port.clearDTR();
		delay(100);
    	Scanner scanner = new Scanner(port.getInputStream());
		while(scanner.hasNextLine()) {
			try {
				String line = scanner.nextLine();
				if (line.equals(grbl_start)) {
					grblStarted = true;
				}
				System.out.println(line);
				
			} catch(Exception e) {}
		}
		scanner.close();
		}
    }
    
    public void sendData() {
		if (port.openPort()) {
			OutputStream outputStream = port.getOutputStream();
			String str = "X-20\n";
			try {
				outputStream.write(str.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
    }*/
    
}