package core;
import java.util.EventListener;

public interface GCodeStatusListener extends EventListener {
	
	public void statusChanged(GCodeStatusEvent e);
	
	public void drawingStatusChanged(double percent);

	public void chalk(boolean up);
}
