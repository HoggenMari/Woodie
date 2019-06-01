package core;
import java.util.EventObject;


public class UltraSonicEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    public float front, back, left, right;

	public UltraSonicEvent(Object source, float front, float back, float left, float right) {
		super(source);
		this.front = front;
		this.back = back;
		this.left = left;
		this.right = right;

		// TODO Auto-generated constructor stub
	}
	
}