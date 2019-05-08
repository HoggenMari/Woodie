package core;

import hypermedia.net.*;
import processing.core.PApplet;
import processing.core.PGraphics;

public class LEDController {
	
	public static final LEDController instance = new LEDController();

	private LEDController () {}
	
	String IP_ADDRESS = "127.0.0.1";  // the remote IP address
	int PORT = 5154;  // the destination port

	int NUM_LEDS = 64;

	byte[] HEADER = new byte[] { (byte)0x41, (byte)0x72, (byte)0x74,
	    (byte)0x2d, (byte)0x4e, (byte)0x65, (byte)0x74, (byte)0x30, (byte)0x50, (byte)0x50, (byte)0xff, (byte)0xff, (byte)0xff,
	    (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x0f, (byte)0x00 };
	    
	byte[] message;
	
	private UDP udp;
	private PApplet pApplet;

	public void setupConnection(PApplet p) {
		// create a new datagram connection
		pApplet = p;
		udp = new UDP(this);
		message = new byte[HEADER.length + NUM_LEDS * 3];
	}
	
	public void send(PGraphics pg) {
		//write the header into the message
		for (int i=0; i < HEADER.length; i++) {
		  message[i] = HEADER[i];  
		}
		
		// write the data (actual color information) into the message
		int dataPointer = HEADER.length;
		
		for(int ix=0; ix<pg.width; ix++) {
			for(int iy=0; iy<pg.height; iy++) {
				int rgb = pg.get(ix, iy);
				
				message[dataPointer++] = (byte) (rgb >> 8 & 0xff); //(rgb & 0xff);
				message[dataPointer++] = (byte) (rgb >> 16 & 0xff);
				message[dataPointer++] = (byte) (rgb & 0xff); //(rgb >> 16 & 0xff);
				
			}
		}
		
		udp.send(message, IP_ADDRESS, PORT);
	}
}
