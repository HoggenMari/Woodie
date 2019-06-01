package core;
import java.util.EventObject;
import core.GcodeSender.Status;


public class StatusEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    protected Status status;

	public StatusEvent(Object source, Status status) {
		super(source);
		this.status = status;
		// TODO Auto-generated constructor stub
	}
	
}