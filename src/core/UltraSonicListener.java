package core;
import java.util.EventListener;

public interface UltraSonicListener extends EventListener {
	
	public void newMeasurement(UltraSonicEvent e);
	
}
