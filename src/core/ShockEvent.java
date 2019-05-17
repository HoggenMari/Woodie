package core;
import java.sql.Timestamp;
import java.util.EventObject;


public class ShockEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum ShockEventObject {
		shockDetected
	}
	
    protected ShockEventObject object;
    
    long time;

	public ShockEvent(Object source, ShockEventObject object) {
		super(source);
		this.object = object;
		
		this.time = System.currentTimeMillis();
		
		// TODO Auto-generated constructor stub
	}
	
	public long getTimeStamp() {
		return time;
	}
	
}