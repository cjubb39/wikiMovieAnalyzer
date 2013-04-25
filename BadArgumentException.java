/**
 * Custom Exception thrown when arguments may be invalid
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
@SuppressWarnings("serial")
public class BadArgumentException extends RuntimeException {

	public BadArgumentException() {
		super();
	}

	public BadArgumentException(String message) {
		super(message);
	}

}
