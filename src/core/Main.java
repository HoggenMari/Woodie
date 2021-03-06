package core;

import java.awt.Event;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import com.fazecast.jSerialComm.SerialPort;

import core.ChalkEvent.ChalkEventObject;
import core.GcodeSender.GCodeStatus;
import core.LightEvent.LightEventObject;
import processing.core.*;

public class Main extends PApplet implements GCodeStatusListener, LightControlListener, ShockEventListener, ChalkEventListener {
	
	boolean send = false;
	
	String grbl_start = "Grbl 1.1f ['$' for help]";
	boolean grblStarted = false;
	
	static String portname = "";
	
	PGraphics pg;
	PGraphics pgScale;
	
	MqttClient client;
	
	private int qos = 2;
	
	boolean lightsOn = true;
	boolean guidanceOn = false;
	
	double counter;
	double counter2;


	float brightness = 1;
	
	CircularFifoQueue<ShockEvent> shockQueue;
	
	ArrayList<Powerfield> fields;
	
	Water water = new Water();
	
	Firework firework;
	
	float chalkUpCounter;
	
	ChalkEventObject chalkEvent = ChalkEventObject.chalkUp;
	
	int brightnessCounter = 0;
	
	boolean brCounterUp = true;

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
		mqttCallback.addChalkEventListener(this);
		
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
		GcodeSender.getInstance().addShockEventListener(this);

		
		LEDController.instance.setupConnection(this);
		
    	pg = createGraphics(16,4);
    	pgScale = createGraphics(160, 40);
    	
    	shockQueue = new CircularFifoQueue<>(3);
    	
		fields = new ArrayList<Powerfield>();
		
		water.setupWater(this, pgScale);
		
		firework = new Firework(this);
				
    }

    public void draw(){
    	
    	//frameRate(20);
    	
    	pgScale.beginDraw();
    	pgScale.noStroke();
    	pgScale.clear();
    	//pgScale.fill(255,0,0,255);
    	//pgScale.rect(0, 0, pgScale.width, pgScale.height);
    	//pgScale.fill(30, 80, 255, 255);
    	//pgScale.rect(0, 0, pgScale.width, pgScale.height);
    	
    	if(GcodeSender.getInstance().status == GCodeStatus.IDLE) {
    		
    		//if (frameCount%1000 < 500) {
    		//	water.drawWater(pgScale);
    		//} else {
    			//firework.drawFirework(pgScale);	
    			//if (frameCount % 500 == 0) {
    			//	firework.mousePressed();
    			//}
    		//}
    	}
    	
    	//firework.drawFirework(pgScale);
    	
    	if (this.millis() - chalkUpCounter < 7000) {
    		if (frameCount%120==0) {
			//fields.add(new Powerfield(this, pgScale, color(179,23,25,255)));
    			if (chalkEvent == ChalkEventObject.chalkUp) {
    				fields.add(new Powerfield(this, pgScale, color(10,10,10,250), true));
    			} else {
    				fields.add(new Powerfield(this, pgScale, color(255,255,255,255), false));
    			}
			
			//firework.mousePressed();
    		}
    	}
    	
    	for (int f = 0; f < fields.size(); f++) {
			if (fields.get(f).dead()) {
				fields.remove(f);
			}
		}

		for (int f = 0; f < fields.size(); f++) {
			fields.get(f).display();
		}
		
    	pgScale.endDraw();
    	
    	
    	pg.beginDraw();
    	pg.colorMode = PConstants.RGB;
    	pg.noStroke();
    	pg.fill(frameCount%255,255,0);
    	//for(int i=0; i<pg.width; i++) {
        //	pg.fill(255-(frameCount%10)*10,i*10,10);
        //	pg.rect(i,0,1,pg.height);
    	//}
    	
    	//pg.loadPixels();
    	if(frameCount%1==0) {
    		counter+=0.1;
    		counter2+=1;
    		if(brCounterUp) {
    			brightnessCounter++;
    			  if(brightnessCounter>=255) {
    				  brCounterUp = false;
    			  }
    		  } else {
    			  brightnessCounter--;
    			  if(brightnessCounter<=0) {
    				  brCounterUp = true;
    			  }
    		  }
    	}
    	
    	if(GcodeSender.getInstance().status == GCodeStatus.DRAWING || GcodeSender.getInstance().status == GCodeStatus.JOGGING) {
    		colorCycle2(counter2);
    	} else {
    		colorCycle(counter);
    	}
    	
    	//pg.tint(255, 0, 0, 255);
    	//pg.updatePixels();
    	
    	//if(GcodeSender.getInstance().status == GCodeStatus.IDLE) {
    		PImage img = downscale(pgScale, 1);    	
    		pg.image(img, 0, 0);
    	//}
    	
		//colorCycle(counter);
		//rainbowCycle(counter);

    	
    	pg.fill(0,(int)(255.0-brightness*255.0));
    	pg.rect(0, 0, pg.width, pg.height);
    	
    	if (!lightsOn) {
    		pg.background(0);
    	}
    	if (guidanceOn) {
    		pg.fill(255,255,255,255);
    		pg.rect(0,0,2,pg.height);
    	}
    	
    	
    	pg.endDraw();

    	LEDController.instance.send2(pg);

    	
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

    PImage downscale(PGraphics pg, int intensity) {
		PImage in = pg.get();
		in.filter(BLUR, intensity);
		in.resize(16, 4);
		//PGraphics out = createGraphics(16, 4, PConstants.P2D);
		//out.image(in, 0, 0);
		return in;
	}
    
	@Override
	public void statusChanged(GCodeStatusEvent event) {
		// TODO Auto-generated method stub
		System.out.println("Received Event: " + event.status);
		
		String perString = event.status.toString();
		MqttMessage message = new MqttMessage(perString.getBytes());
        message.setQos(qos);
        try {
			client.publish("status", message);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		} else if (e.object == LightEventObject.brightnessChanged) {
			System.out.println("brightnessChanged: "+e.getBrightness());
			brightness = e.getBrightness();
		}
	}
	
	private int wheel(int pos) {
		//System.out.println(pos);
		if (pos < 85) {
			return this.color(pos * 3, 255 - pos * 3, 0);
		} else if( pos < 170) {
			pos -= 85;
			return this.color(255 - pos * 3, 0, pos * 3);
		} else {
			pos -= 170;
			return this.color(0, pos * 3, 255 - pos * 3);
		}
	}
	
	private int lerpC(double pos) {
		int lc1 = this.color(73,21,125);//this.color(255,186,0);//this.color(255,212,12);//this.color(255,186,0);//this.color(180,0,255);//this.color(255,255,0);
		int lc2 = this.color(255,167,0);//this.color(0,0,0);////this.color(255,212,12);
		
		/*if (pos < 32) {
			return this.lerpColor(lc1, lc2, (float) (pos/32.0));
		} else {
			//pos -= 32;
			return this.lerpColor(lc1, lc2, (float) ((64.0-pos)/32.0));

		}*/
				
		if(pos < 16) {
			return this.lerpColor(lc1, lc2, (float) (pos/16.0));
		} else if(pos<32) {
			//pos -= 16;
			return this.lerpColor(lc2, lc1, (float) ((pos-16.0)/16.0));
		} else if(pos<48) {
			//pos -= 32;
			return this.lerpColor(lc1, lc2, (float) ((pos-32.0)/16.0));
		} else {
			//pos -= 48;
			return this.lerpColor(lc2, lc1, (float) ((pos-48.0)/16.0));
		}
		
		/*if(pos < 8) {
			return this.lerpColor(lc1, lc2, (float) (pos/8.0));
		} else if(pos<32) {
			//pos -= 16;
			return this.lerpColor(lc2, lc1, (float) ((pos-8.0)/12.0));
		} else if(pos<40) {
			//pos -= 32;
			return this.lerpColor(lc1, lc2, (float) ((pos-32.0)/8.0));
		} else {
			//pos -= 48;
			return this.lerpColor(lc2, lc1, (float) ((pos-40.0)/12.0));
		}*/
		
		
		
	}
	
	private int lerpC2(double pos) {
		int lc1 = this.color(150,5,5);//this.color(255,186,0);//this.color(255,212,12);//this.color(255,186,0);//this.color(180,0,255);//this.color(255,255,0);
		int lc2 = this.color(146,42,240);//this.color(0,0,0);////this.color(255,212,12);
		
				
		if(pos < 16) {
			return this.lerpColor(lc1, lc2, (float) (pos/16.0));
		} else if(pos<32) {
			//pos -= 16;
			return this.lerpColor(lc2, lc1, (float) ((pos-16.0)/16.0));
		} else if(pos<48) {
			//pos -= 32;
			return this.lerpColor(lc1, lc2, (float) ((pos-32.0)/16.0));
		} else {
			//pos -= 48;
			return this.lerpColor(lc2, lc1, (float) ((pos-48.0)/16.0));
		}
		
		
		
	}
	
	private void rainbowCycle(int j) {
		
		//for(int j=0; j<(256*5); j++) {
			for(int x=0; x<pg.width; x++) {
				for(int y=0; y<pg.height; y++) {
					//if (pg.pixels != null) {
						//System.out.print(pg.pixels);
						//pg.pixels[x*pg.height+y] = this.color((x*pg.height+y)*4,0,255);//wheel(((int)(i * 256 / 64) + j) & 255);
						int c1 = wheel(((int)((x*pg.height+y) * 256 / 64) + j) & 255);
						//c1 = lerpC((((x*pg.height+y) * 256 / 64) + j) & 255);
						//c1 = lerpC(((x*pg.height+y)+j)%64);
						pg.set(x, y, c1);
						//pg.pixels[0] = this.color(255,0,0);
						//pg.pixels[1] = this.color(0,255,0);
						//pg.pixels[2] = this.color(0,0,255);
					//}	
				}
			}
		//}
    }
	
	private void colorCycle(double j) {
		for(int x=0; x<pg.width; x++) {
			for(int y=0; y<pg.height; y++) {
				int c1 = lerpC(((x*pg.height+y)+j)%64);
				pg.set(x, y, c1);
			}
		}
	}
	
	private void colorCycle2(double j) {
		for(int x=0; x<pg.width; x++) {
			for(int y=0; y<pg.height; y++) {
				int c1 = lerpC2(((x*pg.height+y)+j)%64);
				pg.set(x, y, c1);
			}
		}
	}

	@Override
	public void shockEvent(ShockEvent event) {
		// TODO Auto-generated method stub
		System.out.println("NEW SHOICK EVENT:"+event.getTimeStamp());
		shockQueue.add(event);
		
		String perString = ""+event.getTimeStamp();
		MqttMessage message = new MqttMessage(perString.getBytes());
        message.setQos(qos);
        try {
			client.publish("shock", message);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if (shockQueue.size()>=3) {
        	long timeStamp1 = shockQueue.get(0).getTimeStamp();
        	long timeStamp2 = shockQueue.get(2).getTimeStamp();
        	
        	if (timeStamp2 - timeStamp1 < 2000 && GcodeSender.getInstance().detection) {
        		
        		System.out.println("ALARM");
        		GcodeSender.getInstance().pause();
        		
        		perString = "alarm";
        		message = new MqttMessage(perString.getBytes());
                message.setQos(qos);
        		
                try {
        			client.publish("shock", message);
        		} catch (MqttPersistenceException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} catch (MqttException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}

        }
	}

	@Override
	public void chalkEvent(ChalkEvent e) {
		// TODO Auto-generated method stub
		
		System.out.println("New Chalk Event");
		
		chalkUpCounter = this.millis();
		
		chalkEvent = e.object;
		
		if (chalkEvent == ChalkEventObject.chalkUp) {
			fields.add(new Powerfield(this, pgScale, color(10,10,10,230), true));
		} else {
			fields.add(new Powerfield(this, pgScale, color(255,255,255,255), false));
		}
		
	}

    
}