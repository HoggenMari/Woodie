package core;

public class Fire{
	  float x;
	  float y;
	  float vx;
	  float vy;
	  
	  int col;
	  
	  float lifetime = 300;
	  
	  Fire(float x, float y, float vx, float vy, int col){
	    this.x = x;
	    this.y = y;
	    this.vx = vx;
	    this.vy = vy;
	    this.col = col;
	  }
}