package core;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Powerfield {

	PApplet applet;
	PGraphics canvas;
	float rad;
	int color;
	
	boolean up;

	public Powerfield(PApplet applet, PGraphics canvas) {
		this.applet = applet;
		this.canvas = canvas;
		rad = 0;
		color = applet.color(255, 100);
	}
	
	public Powerfield(PApplet applet, PGraphics canvas, int color, boolean up) {
		this.applet = applet;
		this.canvas = canvas;
		this.up = up;
		if(up) {
			rad = -14;
		} else {
			rad = canvas.height + 14;
		}
		this.color = color;
	}

	void display() {
		if (up) {
			rad += 0.3f;
		} else {
			rad -= 0.3f;
		}
		canvas.beginDraw();
		//canvas.noFill();
		//canvas.stroke(color);
		//canvas.strokeWeight(7);
		canvas.noStroke();
		canvas.fill(color);
		//canvas.ellipse(canvas.width / 2, canvas.height / 2, rad, rad);
		canvas.rect(0, rad, canvas.width, 12);
		canvas.endDraw();
	}

	boolean dead() {
		
		if (up) {
		if (rad > canvas.height + 12) {
			return true;
		} else
			return false;
		} else {
			if (rad < -12) {
				return true;
			} else
				return false;	
		}
	}

}
