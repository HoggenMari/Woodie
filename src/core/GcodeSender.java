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

import processing.core.PApplet;

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
	boolean isDrawing = false;
	boolean chalkUp = false;
	
    //private volatile boolean exit = false;
	
	static ArrayList<String> gcodeCommands = new ArrayList<String>();
	
	private static PApplet pApplet;
	
	public enum Direction {
	    UP, DOWN, LEFT, RIGHT 
	}
	
	public enum Status {
		BUSY, END, IDLE
	}

	public static synchronized GcodeSender getInstance() {
		if (GcodeSender.instance == null) {
			GcodeSender.instance = new GcodeSender();
			//setupConnection();
		}
		return GcodeSender.instance;
    }

	public static void setupConnection(String portname, PApplet p) {
		
		pApplet = p;

		if(!portname.equals("")) {
			ARDUINO = portname;
		}
		// open serial port
		SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++) {
			System.out.println(portNames[i].getSystemPortName());
			if (portNames[i].getSystemPortName().contains("ttyUSB0")) {
				System.out.println(portNames[i].getSystemPortName());
				port = portNames[i];
			}
			if (portNames[i].getSystemPortName().contains("ttyUSB1")) {
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
	
	public static void sendCommand(String str) {
		if (port.openPort()) {
			OutputStream outputStream = port.getOutputStream();
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
		}
		//byte[] sendData = new byte[]{(byte) 0x18};
		//port.writeBytes(sendData, 1);
		try {
			wait(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setupConnection(ARDUINO);*/
	}
	
	public static void move(Direction dir, int distance) {
		 switch (dir) {
		 case UP:
			 System.out.println("move up " + dir + distance);
			 String[] s1 = { "$J=G91 G21 X" + (int)(distance/2) + " F100", "G92 X0 Y0" };
			 sendLines(s1);
			 break;
		 case DOWN:
			 System.out.println("move down " + dir + distance);
			 String[] s2 = { "$J=G91 G21 X" + (int)(-distance/2) + " F100", "G92 X0 Y0" };
			 sendLines(s2);
			 break;
		 case LEFT:
			 System.out.println("move left " + dir + distance);
			 String[] s3 = { "$J=G91 G21 Y" + (int)(distance/2) + " F100", "G92 X0 Y0" };
			 sendLines(s3);
			 break;
		 case RIGHT:
			 System.out.println("move right " + dir + distance);
			 String[] s4 = { "$J=G91 G21 Y" + (int)(-distance/2) + " F100", "G92 X0 Y0" };
			 sendLines(s4);
			 break;
		 default:
			 System.out.println("Command invalid");
			 break;
		 }
	}
	
	public static boolean sendData(String s) {
		boolean ok = false;
		if (port.openPort()) {
			System.out.println(s);
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
					if (line.equals("ok") || line.contains("MSG:Pgm End")) {
						if(line.contains("MSG:Pgm End")) {
							GcodeSender.getInstance().isDrawing = false;
						}
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
					if (line.contains("MSG:Pgm End")) {
						GcodeSender.getInstance().isDrawing = false;
						//GcodeSender.getInstance().exit = true;
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
	
	
	
	public static Status getStatus() {
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
						return Status.BUSY;
					} else if (line.contains("MSG:Pgm End")) {
						return Status.END;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		
		return Status.IDLE;
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
			   //GcodeSender.getInstance().exit = false;
			   GcodeSender.getInstance().isDrawing = true;
			   //while(!GcodeSender.getInstance().exit){
				   System.out.println("Thread Running");
				   for (int i = 0; i < gcodeCommands.size(); i++) {
					   System.out.println(gcodeCommands.get(i));
					   if (gcodeCommands.get(i).contains("Z5.000000") && !GcodeSender.getInstance().chalkUp) {
						   System.out.println("TUP");
						   GcodeSender.getInstance().send("TURBOUP\n");
						   pApplet.delay(10000);
						   System.out.println("DELAY");
						   GcodeSender.getInstance().chalkUp = true;
					   } else if (gcodeCommands.get(i).contains("Z-1.000000") && GcodeSender.getInstance().chalkUp) {
						   System.out.println("TDOWN");
						   GcodeSender.getInstance().send("TURBODOWN\n");
						   pApplet.delay(10000);
						   System.out.println("DELAY");
						   GcodeSender.getInstance().chalkUp = false;
					   }
					   while (!sendData(gcodeCommands.get(i)));
					   while (getStatus()==Status.BUSY);
					   //if(getStatus()==Status.END) {
					   //   GcodeSender.getInstance().exit = true;
					   //}
					   /*if(i==gcodeCommands.size()-1 && !isBusy()) {
		    		 		GcodeSender.getInstance().isDrawing = false;
		    	 		}*/
				   }    
			   //}
		   }
		};
		thread.start();
	}
	
	public static void sendLines(String[] string) {
		Thread thread = new Thread(){
			public void run(){
				System.out.println("Thread Running");
				for (int i = 0; i < string.length; i++) {
					System.out.println(string[i]);
				    while (!sendData(string[i]));
				    while (isBusy());
				}
			}
		};
		thread.start();
	}
	
	public static void pause() {
		while (!sendData("!"));
	}
	
	public static void resume() {
		while (!sendData("~"));
	}
	
	public static void draw(int number) {
		String file = "";
		if (number==1) {
			file = "/home/pi/woodie/gcode/drawing1.ngc";
		} else if(number==2) {
			file = "/home/pi/woodie/gcode/drawing2.ngc";
		} else if(number==3) {
			file = "/home/pi/woodie/gcode/drawing3.ngc";
		}
		if (getStatus() != Status.BUSY) {
			if (GcodeSender.grblStarted && !GcodeSender.getInstance().isDrawing) {
    			//port.delay(100);
    			//GcodeSender.getInstance().sendData();
    			GcodeSender.getInstance().send = true;
    			GcodeSender.readFile(file);
    			GcodeSender.printCommands();
    		}
		}
	}
}
