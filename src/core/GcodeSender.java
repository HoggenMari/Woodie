package core;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;

public class GcodeSender {
	
	private static GcodeSender instance;

	private GcodeSender () {}
	
	static SerialPort port = null;
	static String ARDUINO = "ttyUSB";
	static int BAUDRATE = 115200;
		
	String grbl_start = "Grbl 1.1f ['$' for help]";
	boolean grblStarted = false;
	
	boolean send = false;

	public static synchronized GcodeSender getInstance() {
		if (GcodeSender.instance == null) {
			GcodeSender.instance = new GcodeSender();
			setupConnection();
		}
		return GcodeSender.instance;
    }

	private static void setupConnection() {
		
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
	
	public void requestData() {
		if (port.openPort()) {
		port.clearDTR();
		//delay(100);
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
			String str = "X5\n";
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
	
	public void sendData(String s) {
		if (port.openPort()) {
			OutputStream outputStream = port.getOutputStream();
			String str = s + "\n";
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
