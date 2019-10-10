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


public class Fireworks{

	  ArrayList<Particle> particles;
	  float life = 300f;
	  boolean dead = false;
	  double r;
	  double g;
	  double b;
	  int a = (int) Math.random() * 5; 
	  
	  Fireworks(float sX, float sY, int num )
	  {
	    if(a==0){r=255;g=203;b=7;}
	    else if(a==1){r=251;g=83;b=4;}
	    else if(a==2){r=255;g=39;b=93;}
	    else if(a==3){r=253;g=148;b=6;}
	    else if(a==4){r=255;g=97;b=201;}
	    else if(a==5){r=203;g=202;b=6;}
//		    if(a==0){r=255;g=0;b=0;}
//		    else if(a==1){r=0;g=255;b=0;}
//		    else if(a==2){r=0;g=0;b=255;}
//		    else if(a==3){r=255;g=255;b=0;}
//		    else if(a==4){r=0;g=255;b=255;}
//		    else if(a==5){r=255;g=0;b=255;}
	
	    particles = new ArrayList<Particle>();
	    for (int i = 0; i < num; i++) {
	    particles.add(new Particle(sX, sY,r,g,b));
	    }
	  }
	   
	   void run(PGraphics pgOff){
	    for (int i = 0; i < particles.size(); i++) {
	      //update each particle per frame
	      particles.get(i).updateP();
	      particles.get(i).renderP(pgOff);
	      life-=.3;
	      if(life<=0){
	        dead = true;
	      }
	    }
	  }
	 }
	 