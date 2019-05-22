package core;

import processing.core.*;

/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/43543*@* */
/* !do not delete the line above, required for linking your tweak if you upload again */
/* Processing Water Simulation 
* adapted by: Rodrigo Amaya
* 
* Based on "Java Water Simulation", by: Neil Wallis
* For more information visit the original article here: 
* http://neilwallis.com/projects/java/water/index.php
*
* How does it work? "2D Water"
* http://freespace.virgin.net/hugo.elias/graphics/x_water.htm
*
*/

//import processing.opengl.*;


public class Water {

PApplet p;
PImage img;
PGraphics pg_water, pgOff, pgOff2;

int size;
int hwidth,hheight;
int riprad;

int ripplemap[];
int ripple[];
int texture[];

int oldind,newind, mapind;

int i,a,b; 

int counter = 0;
int maxVal = 0;
boolean counterBol;

int upCounter = 0;
boolean counterUp;

public void setupWater(PApplet p, PGraphics pgOff){
  this.p = p;
  this.pgOff = pgOff;
  img = p.loadImage("/home/pi/woodie/images/water2.png");
  img.resize(pgOff.width, pgOff.height);
  
  int c2 = p.color(255,60,21,255);
  int c1 = p.color(0,0,0,255);

  pgOff.beginDraw();
  //pgOff.image(img, 0, 0);
  pgOff.colorMode = PConstants.RGB;

  pgOff.noStroke();
  //for(int iy=0; iy<pgOff.height; iy++) {
  //	  int color = p.lerpColor(c1, c2, (float) (iy/pgOff.height));
	  pgOff.fill(c1);
	  pgOff.rect(0,0,pgOff.width,pgOff.height);
  //}
  pgOff.image(img, 0, 0);
  pgOff.endDraw();
  
  pgOff2 = p.createGraphics(160, 40);
  pgOff2.beginDraw();
  pgOff2.noStroke();
  for(int iy=0; iy<pgOff2.height; iy++) {
	  int color = p.lerpColor(c1, c2, (float) (iy/40.0));
	  pgOff2.fill(color);
	  pgOff2.rect(0,iy,pgOff.width,1);
  }
  pgOff2.image(img, 0, 0);
  pgOff2.endDraw();

  //width = img.width;
  //height = img.height;
  //size(width, height);
  //size(300, 300);
  //frameRate(40);
  
  hwidth = pgOff.width>>1;
  hheight = pgOff.height>>1;
  riprad=5; //test with 3
  
  size = pgOff.width * (pgOff.height+2) * 2;
  
  ripplemap = new int[size];
  ripple = new int[pgOff.width*pgOff.height];
  texture = new int[pgOff.width*pgOff.height];
  
  oldind = pgOff.width;
  newind = pgOff.width * (pgOff.height+3);
  
  p.image(pgOff, 0, 0); 
  p.loadPixels();
  
  p.smooth();
}

public void drawWater(PGraphics pgOff) {
  //image(img, 0, 0);
  
  if(p.frameCount%1==0){
  
  if(p.frameCount%100==0)  {
	  counterBol = true;
  }
  
  if (p.frameCount%5==0) {
  if(counterUp) {
	  upCounter++;
	  if(upCounter>=250) {
		  counterUp = false;
	  }
  } else {
	  upCounter --;
	  if(upCounter<=0) {
		  counterUp = true;
	  }
  }
  }
    
  if(counterBol && counter<maxVal){
    counter++;
  }else{
    counter=0;
    maxVal = (int) p.random(15, 35);
    counterBol=false;
  }
  
  if(counterBol){
  //disturb(pgOff.width-(counter*(pgOff.width/25)), pgOff.height/2);
  disturb(counter%pgOff.width + 0, pgOff.height/2);

  }
  }
  
  int c2 = p.color(255,60,21,255);//p.color(21,60,255,255);//p.color(255,60,21,255);
  int c1 = p.color(upCounter,upCounter,upCounter,255);
  
  pgOff2 = p.createGraphics(160, 40);
  pgOff2.beginDraw();
  pgOff2.noStroke();
  for(int iy=0; iy<pgOff2.height; iy++) {
	  int color = p.lerpColor(c1, c2, (float) (iy/40.0));
	  pgOff2.fill(color);
	  pgOff2.rect(0,iy,pgOff.width,1);
  }
  pgOff2.image(img, 0, 0);
  pgOff2.endDraw();
  
  pgOff.beginDraw();
  pgOff.fill(p.frameCount%360,100,70);
  pgOff.noStroke();
  pgOff.rect(0,0,pgOff.width,pgOff.height);
  pgOff.image(pgOff2, 0, 0);
  //pgOff.image(img, 0, 0);

  pgOff.loadPixels();
  texture = pgOff.pixels;
  
  newframe();
    
  for (int i = 0; i < pgOff.pixels.length; i++) {
    pgOff.pixels[i] = ripple[i];
  }
   
  pgOff.updatePixels();
  pgOff.endDraw();
  
}

public void disturb(int dx, int dy) {
  for (int j=dy-riprad;j<dy+riprad;j++) {
    for (int k=dx-riprad;k<dx+riprad;k++) {
      if (j>=0 && j<pgOff.height && k>=0 && k<pgOff.width) {
      ripplemap[oldind+(j*pgOff.width)+k] += 512;   //test with 512         
      } 
    }
  }
}

public void newframe() {
  //Toggle maps each frame
  i=oldind;
  oldind=newind;
  newind=i;

  i=0;
  mapind=oldind;
  for (int y=0;y<pgOff.height;y++) {
    for (int x=0;x<pgOff.width;x++) {
    short data = (short)((ripplemap[mapind-pgOff.width]+ripplemap[mapind+pgOff.width]+ripplemap[mapind-1]+ripplemap[mapind+1])>>1);
      data -= ripplemap[newind+i];
      data -= data >> 5;
      ripplemap[newind+i]=data;

    //where data=0 then still, where data>0 then wave
    data = (short)(1024-data);

      //offsets
    a=((x-hwidth)*data/1024)+hwidth;
      b=((y-hheight)*data/1024)+hheight;

     //bounds check
      if (a>=pgOff.width) a=pgOff.width-1;
      if (a<0) a=0;
      if (b>=pgOff.height) b=pgOff.height-1;
      if (b<0) b=0;

      ripple[i]=texture[a+(b*pgOff.width)];
      mapind++;
    i++;
    }
  }
}

/*void mouseMoved()
{
  disturb(mouseX, mouseY);
}

void mouseReleased()
{
}*/

}