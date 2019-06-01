package core;
import java.util.EventListener;

public interface GCodeStatusListener extends EventListener {
	
	public void statusChanged(GCodeStatusEvent e);
	
	public void statusChanged(StatusEvent e);
	
	public void drawingStatusChanged(double percent);

	public void chalk(boolean up);
    
    public void newAngle(float angle);
}
