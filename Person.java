/**
 * "Information Holder" for single search analysis result
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class Person {

	private final String name;
	private final String link;
	private final String movie;
	private final String movieLink;

	/**
	 * Constructor
	 * 
	 * @param name
	 *           Name of result (Actor, Actress, Director, etc.)
	 * @param link
	 *           Link for page corresponding to this.name
	 * @param movie
	 *           Movie corresponding to result
	 * @param movieLink
	 *           Link for page corresponding to this.movie
	 */
	public Person(String name, String link, String movie, String movieLink) {
		this.name = name;
		this.link = link;
		this.movie = movie;
		this.movieLink = movieLink;
	}

	/**
	 * Returns String representation of Object in format Key:Value;Key:Value...
	 */
	public String toString() {
		return "Name: " + this.name + ";  Link: " + this.link + ";  Movie: "
				+ this.movie + ";  MovieLink: " + this.movieLink;
	}

	/**
	 * Accessor for Link (corresponding to name)
	 * 
	 * @return this.link (corresponds to name)
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * Accessor for Name of result
	 * 
	 * @return this.name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Accessor for Movie corresponding to result
	 * 
	 * @return this.movie
	 */
	public String getMovie() {
		return this.movie;
	}

	/**
	 * Accessor for link corresponding to Movie
	 * 
	 * @return this.movieLink
	 */
	public String getMovieLink() {
		return this.movieLink;
	}
}
