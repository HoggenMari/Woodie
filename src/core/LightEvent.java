package core;
import java.util.EventObject;
import core.GcodeSender.GCodeStatus;


public class LightEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum LightEventObject {
		toggleLight, toggleGuidance
	}
	
    protected LightEventObject object;

	public LightEvent(Object source, LightEventObject object) {
		super(source);
		this.object = object;
		// TODO Auto-generated constructor stub
	}
	
}