import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Outside point of interaction with WikipediaParser class. Methods return in
 * types that make sense for information set requested. Thus, a class that
 * interacts with this will have to convert the output format.
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class MovieInterpreter {

	private WikipediaParser parser;

	/**
	 * Constructor. Creates association with WikipediaParser class.
	 */
	public MovieInterpreter() {
		parser = new WikipediaParser();
	}

	/**
	 * Searches Best Picture Nominee through WikipediaParser class and returns
	 * those films which have a specified key (columns on Best Picture Wikipedia
	 * Page) matching a specified value.
	 * 
	 * @param key
	 *           Column on Wikipedia Best Picture page to be search. e.g.
	 *           Production company(s)
	 * @param value
	 *           Value returned films will have associated with given key
	 * @return ArrayList of HashedInfo wrappers for information associated with
	 *         movie. Title located at "Film".
	 */
	public ArrayList<HashedInfo> bestPictureSearch(String key, String value) {
		ArrayList<HashedInfo> allNoms = this.parser.getDecadeTableInfo(
				"Best_Picture", 0);
		ArrayList<HashedInfo> searchResult = new ArrayList<HashedInfo>();

		for (HashedInfo m : allNoms) {

			String tempKey = m.getHasher().get(key);
			if (tempKey != null) {
				if (tempKey.matches(".*" + value + ".*")) {
					searchResult.add(m);
				}
			}
		}

		return searchResult;
	}

	/**
	 * Searches for given film to see if it won an award for Original Screenplay.
	 * If so, returns writers of the film
	 * 
	 * @param title
	 *           Name of film to be searched for
	 * @return String array of writers. Each writer is entry in array.
	 */
	public String[] originalScreenplayWriters(String title) {
		ArrayList<HashedInfo> allNoms = this.parser.getDecadeTableInfo(
				"Original_Screenplay", 1);
		title = title.trim();
		String tempWriters;
		for (HashedInfo m : allNoms) {
			HashMap<String, String> tempHash = m.getHasher();
			if (tempHash.get("Film") != null) {
				if (tempHash.get("Film").matches(".*" + title + ".*")) {
					tempWriters = tempHash.get("Screenwriter(s)");
					return tempWriters.split(", ");
				}
			}
		}

		String[] toReturn = { "Film Not Found" };
		return toReturn;
	}

	/**
	 * Searches for actors and returns nominated for Best Leading Actor playing a
	 * specified role. This input is searched for anywhere in the role given in
	 * the "Role" or "Role(s)" column on the Wikipedia Page
	 * 
	 * @param playing
	 *           Search key. Program will search for actors playing this role.
	 * @return ArrayList of Strings of actors playing specified role.
	 */
	public ArrayList<String> bestLeadingActor(String playing) {
		ArrayList<HashedInfo> allNoms = this.parser.getDecadeTableInfo(
				"Best_Actor", 1);
		ArrayList<String> searchResult = new ArrayList<String>();
		String toAdd;

		for (HashedInfo m : allNoms) {
			HashMap<String, String> tempHash = m.getHasher();
			toAdd = tempHash.get("Actor");

			if (tempHash.get("Role") != null) {

				if (tempHash.get("Role").matches(".*" + playing + ".*")) {
					if (!searchResult.contains(toAdd)) {
						searchResult.add(toAdd);
					}
				}
			}

			// check alternate formatting of role column
			if (tempHash.get("Role(s)") != null) {

				if (tempHash.get("Role(s)").matches(".*" + playing + ".*")) {
					if (!searchResult.contains(toAdd)) {
						searchResult.add(toAdd);
					}
				}
			}
		}

		return searchResult;
	}

	/**
	 * Looks at specified category in a specified year and returns age of those
	 * nominated for the award.
	 * 
	 * @param category
	 *           Category to be searched in conjunction with year
	 * @param year
	 *           Year to be search in conjunction with category
	 * @return ArrayList of HashedInfo wrappers for people, their age, and other
	 *         misc. information
	 */
	public ArrayList<HashedInfo> getCategoryYearInfo(String category, int year) {
		ArrayList<Person> allNoms = this.parser.getCategoryYearInfo(
				category.trim(), year, 0);
		ArrayList<HashedInfo> searchResults = this.parser.getAgeAtTime(allNoms,
				year);

		return searchResults;
	}

	/**
	 * Calculates Country with the most nominations in the Foreign Film award
	 * 
	 * @return Object array containing, in order, String of winning country,
	 *         integer of number of films by those countries, ArrayList of
	 *         Strings of those movies nomimated
	 */
	public Object[] getMaxForeignWins() {
		ArrayList<HashedInfo> allNoms = this.parser.getForeignLanguageInfo();
		HashMap<String, Integer> winCount = new HashMap<String, Integer>();
		String country;
		int currentNumber;

		// populate winCount Hash
		for (HashedInfo h : allNoms) {
			country = h.getHasher().get("Submitting country").trim();

			if (winCount.containsKey(country)) {
				currentNumber = winCount.get(country);
				currentNumber++;

				winCount.remove(country);
				winCount.put(country, currentNumber);
			} else {
				winCount.put(country, 1);
			}
		}

		// find winner
		int currentMax = 0;
		int count;
		String key;
		String maxCountry = "";
		Iterator<String> it = winCount.keySet().iterator();
		while (it.hasNext()) {
			key = it.next();

			count = winCount.get(key);

			if (count > currentMax) {
				maxCountry = key;
				currentMax = count;
			}
		}

		// search list of noms for country that matches max country
		ArrayList<String> maxMovies = new ArrayList<String>();

		for (HashedInfo h : allNoms) {

			if (h.getHasher().get("Submitting country").equals(maxCountry)) {
				maxMovies.add(h.getHasher().get("Film title used in nomination"));
			}
		}

		Object[] toReturn = { maxCountry, currentMax, maxMovies };

		return toReturn;
	}

	/**
	 * Searches for movies nominated for Best Picture, Best Director, Best
	 * Leading Actor, and Best Leading Actress along with the total number of
	 * wins for that movie
	 * 
	 * @return HashMap of movies where the name of the movie is a key for the
	 *         number of awards it won
	 */
	public HashMap<String, Integer> getQuadThreat() {
		HashMap<String, Integer> toReturn = new HashMap<String, Integer>();
		String tempMovie = "";

		int winCount;
		ArrayList<Person> match = new ArrayList<Person>();
		ArrayList<Person> temp;

		for (int i = 1934; i < 2013; i++) {

			match = this.parser.getCategoryYearInfo(this.getProperBestPicture(i),
					i, 1);

			String[] categories = { "Best Actor", "Best Actress", "Best Director" };
			Person p1;
			int size;

			for (String s : categories) {
				temp = this.parser.getCategoryYearInfo(s, i, 1);

				size = match.size();

				BPMatchLoop: for (int j = 0; j < size; j++) {
					p1 = match.get(j);

					for (Person p2 : temp) {

						// find correct placement of movie
						if (p2.getMovie().trim() != "") {
							tempMovie = p2.getMovie();
						} else {
							tempMovie = p2.getName();
						}

						if (tempMovie.equals(p1.getName())) {
							continue BPMatchLoop;
						}

					}

					// get rid of those movies not still in contention
					match.remove(p1);
					size--;
					j--;

				}

			}
			// only to this point with match containing those nom'ed for all four
			// categories
			for (Person p : match) {
				winCount = this.parser.getNumberOfWins(p.getName(),
						String.valueOf(i));
				toReturn.put(tempMovie, winCount);
			}
		}

		return toReturn;
	}

	/**
	 * Searches for directors nominated for at least the given number of movies
	 * 
	 * @param count
	 *           Minimum threshold of nominations for directors to be returned
	 * @return ArrayList of Person, each corresponding to director with:
	 *         name=director, movie=csv string of movies nominated
	 */
	public ArrayList<Person> bestDirectorThreshold(int count) {
		ArrayList<HashedInfo> allNoms = this.parser
				.getCategoryInfoWinNom("Best_Director");

		ArrayList<Person> searchResults = new ArrayList<Person>();
		HashMap<String, Integer> runningCount = new HashMap<String, Integer>();
		int tempCount;
		String personName;

		// create HashMap of directors and their nomCounts
		for (HashedInfo h : allNoms) {
			personName = h.getHasher().get("person").trim();

			if (runningCount.containsKey(personName)) {
				tempCount = runningCount.get(personName);
				runningCount.remove(personName);

				tempCount++;
				runningCount.put(personName, tempCount);

			} else {
				runningCount.put(personName, 1);
			}
		}

		Iterator<String> directors = runningCount.keySet().iterator();
		int nomCount;
		String tempDirector;
		String movies = "";

		// creates ArrayList<Person> from HashMap created above
		while (directors.hasNext()) {
			tempDirector = directors.next();
			nomCount = runningCount.get(tempDirector);

			if (nomCount >= count) {
				for (HashedInfo h : allNoms) {

					// if director directed this movie, add it to a movies string
					if (h.getHasher().get("person").equals(tempDirector)) {
						// make list of movies comma-separated
						if (!movies.equals("")) {
							movies += ", ";
						}
						movies += h.getHasher().get("movie");
					}

				}

				searchResults.add(new Person(tempDirector, "", movies, ""));
				movies = "";
			}
		}

		return searchResults;
	}

	/**
	 * Searches for movies nominated in specified category that stared a
	 * specified actor or actress
	 * 
	 * @param category
	 *           Category to be searched
	 * @param person
	 *           Person to be searched for
	 * @return ArrayList of Strings of the movies meeting requirements
	 */
	public ArrayList<String> getCategoryStarring(String category, String person) {
		ArrayList<HashedInfo> allNoms = this.parser
				.getCategoryInfoWinNom(category);
		ArrayList<String> queryResponse = new ArrayList<String>();

		if (category.matches(".*Best_Picture.*")
				|| category.matches(".*Best_Animated_Feature.*")) {
			for (HashedInfo h : allNoms) {
				// here, I use "person" information to match convention of the
				// pre-dash entity being person, post-dash as movie (on wiki page)
				if (this.parser.getStarring(h.getHasher().get("personLink"))
						.contains(person.trim())) {
					queryResponse.add(h.getHasher().get("person"));
				}
			}
		} else {
			for (HashedInfo h : allNoms) {
				if (this.parser.getStarring(h.getHasher().get("movieLink"))
						.contains(person.trim())) {
					queryResponse.add(h.getHasher().get("movie"));
				}
			}
		}

		return queryResponse;
	}

	/**
	 * Returns proper string representation of Best Picture award according at
	 * input year
	 * 
	 * @param year
	 *           Year for which Best-Picture--equivalent desired
	 * @return String corresponding to Best-Picture--equivalent for input year
	 */
	public String getProperBestPicture(int year) {
		if (year >= 1962) {
			return "Best Picture";
		} else if (year >= 1944) {
			return "Best Motion Picture";
		} else if (year >= 1941) {
			return "Outstanding Motion Picture";
		} else if (year >= 1930) {
			return "Outstanding Production";
		} else if (year >= 1928) {
			return "Outstanding Picture";
		} else {
			return null; // safeguard
		}
	}
}