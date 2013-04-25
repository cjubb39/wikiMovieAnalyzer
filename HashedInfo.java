import java.util.HashMap;

/**
 * Wrapper for HashMap<String,String> Object
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class HashedInfo {

	private final HashMap<String, String> infoHash;

	/**
	 * Constructor. Creates object identical to parameter and stores as instance
	 * variable of this object
	 * 
	 * @param hash
	 *           the HashMap<String,String> to be wrapped
	 */
	public HashedInfo(HashMap<String, String> hash) {
		this.infoHash = new HashMap<String, String>();
		this.infoHash.putAll(hash);
	}

	/**
	 * Accessor for HashMap Object
	 * 
	 * @return HashMap Object wrapped by this class
	 */
	public HashMap<String, String> getHasher() {
		return this.infoHash;
	}

	/**
	 * Accessor for String representation of HashMap
	 * 
	 * @return String representation of HashMap Object wrapped by this class
	 */
	public String toString() {
		return this.infoHash.toString();
	}

}
