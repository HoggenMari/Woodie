package core;
import java.util.EventObject;
import core.GcodeSender.GCodeStatus;


public class GCodeStatusEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    protected GCodeStatus status;

	public GCodeStatusEvent(Object source, GCodeStatus status) {
		super(source);
		this.status = status;
		// TODO Auto-generated constructor stub
	}
	
}