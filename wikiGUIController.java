import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Interface between "Back End" Movie Interpreter--Wikipedia Parser and User
 * GUI. Essentially converts requested endpoint into String to be display in
 * results section of GUI
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class wikiGUIController {

	private MovieInterpreter info;

	/**
	 * Constructor. Associates a Movie Interpreter object with the instantiated
	 * GUI Controller
	 */
	public wikiGUIController() {
		info = new MovieInterpreter();
	}

	/**
	 * Best Picture Interpreter. Searches based on given Key:Value pair
	 * 
	 * @param option1
	 *           "Key" e.g. Production company(s)
	 * @param option2
	 *           "Value" e.g. Disney
	 * @return String representation of search result
	 */
	public String bestPictureSearch(String option1, String option2) {
		if (option1.equals("Production company")) {
			option1 = "Production company(s)";
		}
		ArrayList<HashedInfo> response = this.info.bestPictureSearch(option1,
				option2);
		String strResponse = "";

		if (response.size() == 0) {
			throw new BadArgumentException();
		}

		for (HashedInfo h : response) {
			strResponse += h.getHasher().get("Film") + "\n";
		}

		return strResponse;
	}

	/**
	 * Best Original Screenplay Writers Interpreter. Searches for movie and
	 * returns writers
	 * 
	 * @param option1
	 *           Film to be searched for
	 * @return String representation of writers if award won. Else, error
	 *         message.
	 */
	public String bestOrigScreenplay(String option1) {
		String[] response = this.info.originalScreenplayWriters(option1);
		String strResponse = "";

		if (response[0].equals("Film Not Found")) {
			return "Film did not win nomination!";
		}

		for (String s : response) {
			strResponse += s + "\n";
		}

		return strResponse;
	}

	/**
	 * Best Actor in Roles interpreter
	 * 
	 * @param option1
	 *           The role to be searched for
	 * @return String representation of actors nominated for Best Actor playing
	 *         given role
	 */
	public String bestActorRole(String option1) {
		ArrayList<String> response = this.info.bestLeadingActor(option1);
		String strResponse = "";

		if (response.size() == 0) {
			throw new BadArgumentException();
		}

		for (String s : response) {
			strResponse += s + "\n";
		}

		return strResponse;
	}

	/**
	 * Given a year, finds the Best Actress nominees for a given year and returns
	 * their age at the time of the ceremony year
	 * 
	 * @param option1
	 *           Year to be searched for
	 * @return String representation of actresses and their ages and movies
	 */
	public String actressAge(String option1) {
		int op1;
		try {
			op1 = Integer.parseInt(option1);
		} catch (Exception e) {
			throw new BadArgumentException("Not an integer!");
		}

		ArrayList<HashedInfo> response = this.info.getCategoryYearInfo(
				"Best Actress", op1);
		String strResponse = "", tempString = "";

		for (HashedInfo h : response) {
			tempString = "";
			tempString += "Name:\t" + h.getHasher().get("Name");
			tempString += "; Age at Time:\t" + h.getHasher().get("Age");
			tempString += "; Movie:\t" + h.getHasher().get("Movie");

			strResponse += tempString + "\n";
		}

		return strResponse;
	}

	/**
	 * Interprets search result for directors being nominated for at least a
	 * given number of Best Director awards
	 * 
	 * @param option1
	 *           Threshold number of awards
	 * @return String representation of director name and associated movies
	 */
	public String directorThreshold(String option1) {
		int op1;
		try {
			op1 = Integer.parseInt(option1);
		} catch (Exception e) {
			throw new BadArgumentException("Not an integer!");
		}

		ArrayList<Person> response = this.info.bestDirectorThreshold(op1);
		String strResponse = "", tempString = "";

		for (Person p : response) {
			tempString = "";
			tempString += "Name:\t" + p.getName();
			tempString += "\t\tMovies:\t" + p.getMovie();

			strResponse += tempString + "\n";
		}

		return strResponse;
	}

	/**
	 * Interprets Top Foreign Country end point.
	 * 
	 * @return String representation of Country along with nom count and nom'ed
	 *         movies
	 */
	@SuppressWarnings("unchecked")
	public String topForeign() {
		Object[] response = this.info.getMaxForeignWins();
		String strResponse = "";
		ArrayList<String> movies = (ArrayList<String>) response[2];

		strResponse += "Winning Country: " + response[0] + ", with ";
		strResponse += response[1] + " nominations!\n\n";
		strResponse += "The movies are: \n";

		for (String s : movies) {
			strResponse += s + "\n";
		}

		return strResponse;
	}

	/**
	 * Interprets (category) starring (actor or actress) end point
	 * 
	 * @param option1
	 *           Category to be searched
	 * @param option2
	 *           Actor to be searched for among nominated movies in given
	 *           category
	 * @return String representation of movies nominated in given category and
	 *         starring given person
	 */
	public String nomStarring(String option1, String option2) {
		option1 = option1.trim().replace(' ', '_'); // converts spaces to
																	// underscores
		ArrayList<String> response;

		try {
			response = this.info.getCategoryStarring(option1, option2);
		} catch (Exception e) {
			throw new BadArgumentException();
		}

		String strResponse = "";

		for (String s : response) {
			strResponse += s + "\n";
		}

		return strResponse;
	}

	/**
	 * Interprets "quad threat" end point.
	 * 
	 * @return String representation of movies nominated for Best Picture, Best
	 *         Director, Best Actor, and Best Actress along with total win count
	 */
	public String quadThreat() {
		HashMap<String, Integer> response = this.info.getQuadThreat();
		String strResponse = "", curMovie;

		Iterator<String> movies = response.keySet().iterator();

		while (movies.hasNext()) {
			curMovie = movies.next();
			strResponse += curMovie + ", winning ";
			strResponse += response.get(curMovie) + " awards!\n";
		}

		return strResponse;
	}

	/**
	 * Interprets best picture count end point
	 * 
	 * @return Number of films nominated for Best Picture
	 */
	public String bestPictureCount() {
		String count = this.info.getBestPictureCount();
		String strResponse = count
				+ " different movies have been nominated for Best Picture. Wow!";

		return strResponse;
	}
	
	/**
	 * Interprets best actor count end point
	 * 
	 * @return Number of films nominated for Best Actor
	 */
	public String bestActorCount() {
		String count = this.info.getBestActorCount();
		String strResponse = count
				+ " different nominations have been handed out for Best Actor. Wow!";

		return strResponse;
	}
}
