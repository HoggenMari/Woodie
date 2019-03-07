package core;

import processing.core.*;

public class Main extends PApplet {
	
	public static void main(String[] args) {
        PApplet.main("core.Main");
		PGraphics pg = new PGraphics();
		System.out.println("main");
		
    }

    public void setup(){
		System.out.println("setup");
		frameRate(1);
    }

    public void draw(){
		System.out.println("draw");

    }
    
}