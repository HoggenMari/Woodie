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
import core.GcodeSender.Status;
import core.LightEvent.LightEventObject;
import processing.core.*;
import java.util.EventObject;
import java.lang.Math;


public class Particle{
	  
//	double accel = 0.96; //gravity
//	 double grav = 0.04; //gravity
//	 double velX = (Math.random() * 20 + 1) -10;
//	 double  velY = (Math.random() * 20 + 1) -10;
//	 double  pcolorB;
//	 double  pcolorG;
//	 double  pcolorR;
//	 double  locX;
//	 double  locY;
//	 double  r = 10.0;
//	 double  life = 255;
//	  
//	  Particle(float x, float y, float r, float g, float b)
//	  {
//	    pcolorB = b+ (Math.random() * 30 + 1) -15; //add color vary
//	    pcolorG = g+ (Math.random() * 30 + 1) -15;
//	    pcolorR = r+ (Math.random() * 30 + 1) -15;
//	    locX = x;
//	    locY = y;
//	  }
//	  
//	    void updateP() 
//	    {
//	     velY*= accel;
//	     velY-= grav;
//	     velX*= accel;
//	     locX -=velX;
//	     locY -= velY;
//	     life -= 3;
//	   }
//	  
//	    void renderP(PGraphics pgOff)
//	    {
//	      pgOff.beginDraw();
//	      pgOff.noStroke();
//	      pgOff.pushMatrix();
//	      pgOff.fill((int)pcolorR, (int)pcolorG, (int)pcolorB,(int)life);
//	      pgOff.translate(locX, locY);
//	      pgOff.ellipse(0,0,r,r);
//	      pgOff.fill(255,life/10);
//	      pgOff.ellipse(0,0,r+6,r+6);
//	      // star(0,0,r,5);
//	      pgOff.popMatrix();
//	  }
	
	float accel = 0.96f; //gravity
	  float grav = 0.04f; //gravity
	  double velX = (Math.random() * 10) -5;
	  double velY = (Math.random() * 10) -5;
	  double pcolorB;
	  double pcolorG;
	  double pcolorR;
	  float locX;
	  float locY;
	  double r = 30;
	  double life = 255;
	  
	  Particle(float x, float y, double r, double g, double b)
	  {
	    pcolorB = b+ (Math.random() * 30) -15; //add color vary
	    pcolorG = b+ (Math.random() * 30) -15;
	    pcolorR = b+ (Math.random() * 30) -15;
	    locX = x;
	    locY = y;
	  }
	  
	    void updateP() 
	    {
	     velY*= accel;
	     velY-= grav;
	     velX*= accel;
	     locX -=velX;
	     locY -= velY;
	     life -= 3;
	   }
	  
	    void renderP(PGraphics pgOff)
	    {
	      pgOff.beginDraw();
	      pgOff.noStroke();
	      pgOff.pushMatrix();
	      pgOff.fill((float)pcolorR, (float)pcolorG, (float)pcolorB,(float)life);
	      pgOff.translate(locX, locY);
	      pgOff.ellipse(0,0,(float)r,(float)r);
	      pgOff.fill(255,(float)life/10);
	      pgOff.ellipse(0,0,(float)r+6,(float)r+6);
	      // star(0,0,r,5);
	      pgOff.popMatrix();
	  }
	}