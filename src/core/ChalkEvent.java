package core;
import java.sql.Timestamp;
import java.util.EventObject;


public class ChalkEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum ChalkEventObject {
		chalkUp, chalkDown
	}
	
    protected ChalkEventObject object;
    
    long time;

	public ChalkEvent(Object source, ChalkEventObject object) {
		super(source);
		this.object = object;
		
		//this.time = System.currentTimeMillis();
		
		// TODO Auto-generated constructor stub
	}
	
	public ChalkEventObject getChalkEventObject() {
		return object;
	}
	
}