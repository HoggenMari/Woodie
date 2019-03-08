package core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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
		System.out.println("setup");
		
		// open serial port
		SerialPort[] portNames = SerialPort.getCommPorts();
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
		}
				
    }

    public void draw(){
    	requestData();
    	if (grblStarted && !send) {
    		delay(100);
    		sendData();
    		send = true;
    	}
    }
    
    public void requestData() {
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
			String str = "X50\n";
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
    }
    
}