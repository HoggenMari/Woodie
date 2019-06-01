package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;

import core.ShockEvent.ShockEventObject;
import processing.core.PApplet;
import processing.core.PVector;

public class GcodeSender {
	
	private static GcodeSender instance;

	private GcodeSender () {}
	
	SerialPort port = null;
	String ARDUINO = "ttyUSB";
	int BAUDRATE = 115200;
	
	SerialPort portChalk = null;

		
	String grbl_start = "Grbl 1.1f ['$' for help]";
	boolean grblStarted = false;
	
	boolean send = false;
	boolean isDrawing = false;
	boolean chalkUp = false;
	boolean hold = false;
	
	public GCodeStatus status = GCodeStatus.IDLE;
	
	EventListenerList listenerList = new EventListenerList();
	EventListenerList shockListenerList = new EventListenerList();

    //private volatile boolean exit = false;
	
	ArrayList<String> gcodeCommands = new ArrayList<String>();
	
    ArrayList<PVector> vectorList = new ArrayList<PVector>();
    
	private PApplet pApplet;
	
	double lastRunX, lastRunY;
	double lastIdleX, lastIdleY;
	
	boolean detection = true;

	public enum Direction {
	    UP, DOWN, LEFT, RIGHT 
	}
	
	public enum Status {
		RUN, JOG, END, IDLE
	}
	
	public enum GCodeStatus {
		DRAWING, JOGGING, IDLE
	}

	public static synchronized GcodeSender getInstance() {
		if (GcodeSender.instance == null) {
			GcodeSender.instance = new GcodeSender();
			//setupConnection();
		}
		return GcodeSender.instance;
    }
	
	public void addGCodeStatusListener(GCodeStatusListener l) {
		listenerList.add(GCodeStatusListener.class, l);
	}

	public void removeSensorListener(GCodeStatusListener l) {
		listenerList.remove(GCodeStatusListener.class, l);
	}
	
	public void addShockEventListener(ShockEventListener l) {
		shockListenerList.add(ShockEventListener.class, l);
	}

	public void removeShockEventListener(ShockEventListener l) {
		shockListenerList.remove(ShockEventListener.class, l);
	}

	public void setupConnection(String portname, PApplet p) {
		
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
		
		Thread thread = new Thread(){
			   public void run(){
				   while(true) {
					   requestData2();
					   pApplet.delay(100);
				   }
			   }
		};
		thread.start();
		
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
	
	public void requestData2() {
		if (portChalk.openPort()) {
		portChalk.clearDTR();
		//delay(100);
    	Scanner scanner = new Scanner(portChalk.getInputStream());
		while(scanner.hasNextLine()) {
			try {
				String line = scanner.nextLine();
				System.out.println(line);
				if (line.contains("SHOCK")) {
					System.out.println("New Shock");
					Object[] listeners = shockListenerList.getListenerList();
					
					for (int i = 0; i < listeners.length; i++) {
						if (listeners[i] == ShockEventListener.class) {
							((ShockEventListener) listeners[i + 1])
									.shockEvent(new ShockEvent(this, ShockEventObject.shockDetected));
						}
					}
				}
				
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
	
	public void sendCommand(String str) {
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
		OutputStream outputStream = port.getOutputStream();
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
		//setupConnection(ARDUINO);
	}
	
	public void move(Direction dir, int distance) {
		changeStatus(GCodeStatus.JOGGING);
		pApplet.delay(500);
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
			 String[] s3 = { "$J=G91 G21 Y" + (int)(-distance/2) + " F100", "G92 X0 Y0" };
			 sendLines(s3);
			 break;
		 case RIGHT:
			 System.out.println("move right " + dir + distance);
			 String[] s4 = { "$J=G91 G21 Y" + (int)(distance/2) + " F100", "G92 X0 Y0" };
			 sendLines(s4);
			 break;
		 default:
			 System.out.println("Command invalid");
			 break;
		 }
	}
	
	public boolean sendData(String s) {
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
					System.out.println("RESPONSE" + line);
					if (line.equals("ok") || line.contains("MSG:Pgm End") || line.contains("Hold")) {
						if(line.contains("MSG:Pgm End")) {
							isDrawing = false;
							changeStatus(GCodeStatus.IDLE);
						}
						if(line.contains("Hold")) {
							hold = true;
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
	
	private void changeStatus(GCodeStatus status) {		
		
		if (status != this.status) {
		
			this.status = status;
			
			if (status == GCodeStatus.DRAWING || status == GCodeStatus.JOGGING) {
				System.out.println("power motor");
				send("POWERON\n");
			} else if(status == GCodeStatus.IDLE) {
				System.out.println("turn off motor");
				send("POWEROFF\n");
			}
			
			Object[] listeners = listenerList.getListenerList();
			
			for (int i = 0; i < listeners.length; i++) {
				if (listeners[i] == GCodeStatusListener.class) {
					((GCodeStatusListener) listeners[i + 1])
							.statusChanged(new GCodeStatusEvent(this, status));
				}
			}	
			
		}
			
	}
	
	private void changeDrawingStatus(int current, int total) {
		Object[] listeners = listenerList.getListenerList();
		
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == GCodeStatusListener.class) {
				((GCodeStatusListener) listeners[i + 1])
						.drawingStatusChanged((double)current/(double)(total - 1));
			}
		}		
		
	}
	
    private void sendNewAngle(float angle) {
        Object[] listeners = listenerList.getListenerList();
        
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == GCodeStatusListener.class) {
                ((GCodeStatusListener) listeners[i + 1])
                .newAngle(angle);
            }
        }
        
    }
    
	private void sendChalk(boolean up) {
		Object[] listeners = listenerList.getListenerList();
		
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == GCodeStatusListener.class) {
				((GCodeStatusListener) listeners[i + 1])
						.chalk(up);
			}
		}		
		
	}
	
	private void sendStatus(Status status) {
		Object[] listeners = listenerList.getListenerList();
		
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == GCodeStatusListener.class) {
				((GCodeStatusListener) listeners[i + 1])
						.statusChanged(new StatusEvent(this, status));;
			}
		}		
		
	}
	
	public Status getStatus() {
		if (port.openPort()) {
			OutputStream outputStream = port.getOutputStream();
	    	Scanner scanner = new Scanner(port.getInputStream());
			String str = "?\n";
			try {
				outputStream.write(str.getBytes());
				outputStream.close();
				Thread.sleep(750);
				if (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					System.out.println(line);
					if (line.contains("Run")) {
						String[] split1 = line.split(":");
						String[] split2 = split1[1].split(",");
						lastRunX = Double.parseDouble(split2[0]);
						lastRunY = Double.parseDouble(split2[1]);
						sendStatus(Status.RUN);
						return Status.RUN;
					} else if (line.contains("Idle")) {
						String[] split1 = line.split(":");
						String[] split2 = split1[1].split(",");
						lastIdleX = Double.parseDouble(split2[0]);
						lastIdleY = Double.parseDouble(split2[1]);
						if (!isDrawing) {
							changeStatus(GCodeStatus.IDLE);
						}
						sendStatus(Status.IDLE);
						return Status.IDLE;
					} else if (line.contains("Jog")) {
						changeStatus(GCodeStatus.JOGGING);
						sendStatus(Status.JOG);
						return Status.JOG;
					} else if (line.contains("MSG:Pgm End")) {
						sendStatus(Status.END);
						return Status.END;
					} else if (line.contains("!")) {
						System.out.println("!!!!!!!!");
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
	
	public void readFile(String filename) {
		BufferedReader br = null;
		FileReader fr = null;

		try {

			fr = new FileReader(filename);
			br = new BufferedReader(fr);

			String sCurrentLine;
			
			gcodeCommands = new ArrayList<String>();
            
            vectorList = new ArrayList<PVector>();

			while ((sCurrentLine = br.readLine()) != null) {
				gcodeCommands.add(sCurrentLine);
                
                
                if (sCurrentLine.contains("X") && sCurrentLine.contains("Y")) {
                    System.out.println("contains: "+sCurrentLine);
                    String[] coordinates = sCurrentLine.split(" ");
                    //float x = Float.parseFloat(coordinates[1].split("X"));
                    float x = Float.parseFloat(coordinates[1].split("X")[1]);
                    float y = Float.parseFloat(coordinates[2].split("Y")[1]);
                    vectorList.add(new PVector(y,x));
                } else {
                    vectorList.add(null);
                }

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
	
	public void printCommands() {
		Thread thread = new Thread(){
		   public void run(){
			   //GcodeSender.getInstance().exit = false;
			   isDrawing = true;
			   changeStatus(GCodeStatus.DRAWING);
			   pApplet.delay(500);
				   System.out.println("Thread Running");
                   PVector pVector = new PVector(0,0);
				   for (int i = 0; i < gcodeCommands.size(); i++) {
					   if(isDrawing) {
					   System.out.println(gcodeCommands.get(i));
					   changeDrawingStatus(i, gcodeCommands.size());
					   if (gcodeCommands.get(i).contains("Z5.000000") && !chalkUp) {
						   System.out.println("TUP");
						   String command = "G00 X"+lastRunX+" Y"+lastRunY;
						   System.out.println(command);
						   sendChalk(true);
						   sendData(command);
						   pApplet.delay(500);
						   send("TURBOUP\n");
						   pApplet.delay(10000);
						   command = "G00 X"+lastIdleX+" Y"+lastIdleY;
						   System.out.println(command);
						   sendData(command);
						   System.out.println("DELAY");
						   chalkUp = true;
					   } else if (gcodeCommands.get(i).contains("Z-1.000000") && chalkUp) {
						   System.out.println("TDOWN");
						   sendChalk(false);
						   send("TURBODOWN\n");
						   pApplet.delay(10000);
						   System.out.println("DELAY");
						   chalkUp = false;
					   }
					   //while (GcodeSender.getInstance().hold)
                       if (vectorList.get(i) != null) {
                           System.out.println("ALL: "+pVector+" "+vectorList.get(i));
                           PVector axis = new PVector(0,1);
                           PVector copy = new PVector(vectorList.get(i).x, vectorList.get(i).y);
                           PVector diff = copy.sub(pVector);
                           PVector norm = diff.normalize();
                           float deg = PVector.angleBetween(axis, norm);
                           System.out.println("VECTOR ANGLE: "+deg);
                           if (diff.x < 0) {
                        	   sendNewAngle(360-PApplet.degrees(deg));
                           } else {
                        	   sendNewAngle(PApplet.degrees(deg));
                           }
                           pVector = vectorList.get(i);
                       }
					   while (!sendData(gcodeCommands.get(i)));
					   while (getStatus()==Status.RUN);
					   while (hold);
					   }
					   //if(getStatus()==Status.END) {
					   //   GcodeSender.getInstance().exit = true;
					   //}
					   /*if(i==gcodeCommands.size()-1 && !isBusy()) {
		    		 		GcodeSender.getInstance().isDrawing = false;
		    	 		}*/
				   }    
			   }
		   
		};
		thread.start();
	}
	
	public void sendLines(String[] string) {
		Thread thread = new Thread(){
			public void run(){
				System.out.println("Thread Running");
				for (int i = 0; i < string.length; i++) {
					System.out.println(string[i]);
				    while (!sendData(string[i]));
				    while (getStatus()!=Status.IDLE);
				}
			}
		};
		thread.start();
	}
	
	public void pause() {
		if (!hold) {
			boolean val = sendData("!");
			hold = true;
		}
	}
	
	public void resume() {
		if (hold) {
			boolean val = sendData("~");
			hold = false;
		}
	}
	
	public void stop() {
		//if (hold) {
		isDrawing = false;

		byte[] sendData = new byte[]{(byte) 0x18};
		port.writeBytes(sendData, sendData.length);
		
		String[] s1 = { "$X", "G92 X0 Y0" };
		sendLines(s1);
		//}
	}
	
	public void draw(int number) {
		String file = "/home/pi/woodie/gcode/drawing" + number + ".ngc";
		if (getStatus() != Status.RUN) {
			if (grblStarted && !isDrawing) {
    			send = true;
    			readFile(file);
    			printCommands();
    		}
		}
	}
	
	public void setDetection(boolean detection) {
		this.detection = detection;
	}
}
