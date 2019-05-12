package core;
import java.util.EventListener;

public interface LightControlListener extends EventListener {
	
	public void lightEvent(LightEvent e);
	

}
