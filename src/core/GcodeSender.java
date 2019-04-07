package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;

public class GcodeSender {
	
	private static GcodeSender instance;

	private GcodeSender () {}
	
	static SerialPort port = null;
	static String ARDUINO = "ttyUSB";
	static int BAUDRATE = 115200;
	
	static SerialPort portChalk = null;

		
	static String grbl_start = "Grbl 1.1f ['$' for help]";
	static boolean grblStarted = false;
	
	boolean send = false;
	
	static ArrayList<String> gcodeCommands = new ArrayList<String>();

	public static synchronized GcodeSender getInstance() {
		if (GcodeSender.instance == null) {
			GcodeSender.instance = new GcodeSender();
			//setupConnection();
		}
		return GcodeSender.instance;
    }

	public static void setupConnection(String portname) {
		
		if(!portname.equals("")) {
			ARDUINO = portname;
		}
		// open serial port
		SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++) {
			System.out.println(portNames[i].getSystemPortName());
			if (portNames[i].getSystemPortName().contains("ttyUSB1")) {
				System.out.println(portNames[i].getSystemPortName());
				port = portNames[i];
			}
			if (portNames[i].getSystemPortName().contains("ttyUSB0")) {
				System.out.println(portNames[i].getSystemPortName());
				portChalk = portNames[i];
			}
			
		}
				
		if (port != null) {
			port.setBaudRate(BAUDRATE);
			if (port.openPort()) {
				System.out.print("port open");
			}
		}
		
		if (portChalk != null) {
			portChalk.setBaudRate(BAUDRATE);
			if (portChalk.openPort()) {
				System.out.print("port 2 open");
			}
		}
		
		while (!grblStarted) {
			requestData();
			requestData2();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void requestData() {
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
	
	public static void requestData2() {
		if (portChalk.openPort()) {
		portChalk.clearDTR();
		//delay(100);
    	Scanner scanner = new Scanner(portChalk.getInputStream());
		while(scanner.hasNextLine()) {
			try {
				String line = scanner.nextLine();
				System.out.println(line);
				
			} catch(Exception e) {}
		}
		scanner.close();
		}
    }
	
	public void send(String str) {
		if (portChalk.openPort()) {
			OutputStream outputStream = portChalk.getOutputStream();
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
	
	public void writeReset() {
		/*OutputStream outputStream = port.getOutputStream();
		String str = "!\n";
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
		}*/
		//byte[] sendData = new byte[]{(byte) 0x18};
		//port.writeBytes(sendData, 1);
		try {
			wait(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setupConnection(ARDUINO);
	}
	
	public static boolean sendData(String s) {
		boolean ok = false;
		if (port.openPort()) {
			OutputStream outputStream = port.getOutputStream();
	    	Scanner scanner = new Scanner(port.getInputStream());
			String str = s + "\n";
			try {
				outputStream.write(str.getBytes());
				outputStream.close();
				Thread.sleep(1000);
				if (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					System.out.println(line);
					if (line.equals("ok")) {
						ok = true;
					} else {
						ok = false;
					}
				}
				scanner.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ok = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		return ok;
    }
	
	public static boolean isBusy() {
		boolean busy = false;
		if (port.openPort()) {
			OutputStream outputStream = port.getOutputStream();
	    	Scanner scanner = new Scanner(port.getInputStream());
			String str = "?\n";
			try {
				outputStream.write(str.getBytes());
				outputStream.close();
				Thread.sleep(1000);
				if (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					System.out.println(line);
					if (line.contains("Run")) {
						busy = true;
					} else {
						busy = false;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				busy = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
		
		return busy;
		
		
	}
	
	public static void readFile(String filename) {
		BufferedReader br = null;
		FileReader fr = null;

		try {

			fr = new FileReader(filename);
			br = new BufferedReader(fr);

			String sCurrentLine;
			
			gcodeCommands = new ArrayList<String>();

			while ((sCurrentLine = br.readLine()) != null) {
				gcodeCommands.add(sCurrentLine);
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
	}
	
	public static void printCommands() {
		Thread thread = new Thread(){
		   public void run(){
		     System.out.println("Thread Running");
		     for (int i = 0; i < gcodeCommands.size(); i++) {
			     System.out.println(gcodeCommands.get(i));
		    	 while (!sendData(gcodeCommands.get(i)));
		    	 while (isBusy());
		     }    
		   }
		};
		thread.start();
	}
}
