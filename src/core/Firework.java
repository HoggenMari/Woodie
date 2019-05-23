package core;
import java.util.ArrayList;

import processing.core.*;

public class Firework {
	
	ArrayList<Fire> hanabi = new ArrayList();

	final int FIRE_COUNT = 1000;
	final float X = 200;
	final float Y = 50;

	final float G = (float) 0.04;

	private PApplet p;
	
	public Firework(PApplet p) {
		this.p = p;
	}
	
	public void drawFirework(PGraphics pgOff){
		  
		  pgOff.beginDraw();
		  pgOff.colorMode(PConstants.HSB, 360, 100, 100);
		  pgOff.noStroke();
		  pgOff.fill(0,0,0,30);
		  pgOff.rect(0,0,pgOff.width,pgOff.height);
		  
		  pgOff.fill(200);
		  pgOff.text("click anywhere" , 10,380); 
		  
		  for(Fire fire : hanabi){ 
		    fire.vx += 0;
		    fire.vy += G;
		    
		    fire.x += fire.vx;
		    fire.y += fire.vy;
		    
		    if(fire.lifetime-50>0){
		      pgOff.noStroke();
		      int c = fire.col;
		      float hue = c >> 16 & 0xFF; 
		      float saturation = c >> 8 & 0xFF; 
		      float brightness = c & 0xFF; 
		      pgOff.fill(hue, saturation, brightness, // RGB
		         fire.lifetime-50); //Alpha
		        
		      pgOff.ellipse(fire.x,fire.y,6,6); // draw the fire
		      fire.lifetime -= 0.5; // decrease lifetime
		    }else{
		    }
		  }
		  //pgOff.fill(255,100,100);
		  //pgOff.rect(0,0,50,frameCount%pgOff.height);
		  pgOff.endDraw();
		  
		}

	public void mousePressed(){
		    hanabi.clear();
		    
		    int c = p.color(0,0,0);
		    if(p.random(0, 1)<=0.5){
		      c = p.color(p.random(0,0),p.random(0,0),p.random(0,0));
		    }else{
		      c = p.color(p.random(0,0),p.random(0,0),p.random(0,0));

		    }
		    for(int i=0; i<FIRE_COUNT; i++){
		     float r = p.random(0,PConstants.TWO_PI);
		     float R = p.random(0,2);
		     
		     //hanabi.add(new Fire((int)random(30,40),(int)(0.8*envZMaxUnits*3*2),R*sin(r),R*cos(r),c));
		     hanabi.add(new Fire((int)(160),(int)(40),R*PApplet.sin(r),R*PApplet.cos(r),c));
		     hanabi.add(new Fire((int)(0),(int)(40),R*PApplet.sin(r),R*PApplet.cos(r),c));

		   }
		   
		   //counterBol = true;
	}

}
