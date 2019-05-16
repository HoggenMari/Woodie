package core;
import java.util.EventObject;
import core.GcodeSender.GCodeStatus;


public class LightEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum LightEventObject {
		toggleLight, toggleGuidance, brightnessChanged
	}
	
    protected LightEventObject object;
    
    float brightness;

	public LightEvent(Object source, LightEventObject object, float brightness) {
		super(source);
		this.object = object;
		this.brightness = brightness;
		// TODO Auto-generated constructor stub
	}
	
	public float getBrightness() {
		return brightness;
	}
	
}