import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Parser that interacts with Wikipedia Portal for Academy Awards to answer
 * given search queries
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class WikipediaParser {

	// Regexes and Strings used throughout any instantiation of the WP object
	private static final String baseURL = "http://en.wikipedia.org/wiki/Portal:Academy_Award";
	private static final String wikiRootURL = "http://en.wikipedia.org";

	private static final Pattern trDefine = Pattern.compile("\\s*<tr(.*)>\\s*");
	private static final Pattern tableLinkExtract = Pattern
			.compile("\\s*.*<a href=\"(.*?)\".*>(.*)</a>.*");
	private static final String lastTableSplitRegex = ".*<h2>.*<span class=\"mw-headline\".*</span>.*</h2>.*";
	private static final Pattern yearExtract = Pattern
			.compile(".*<a href=\"(.*)\".*title.*>.*?(\\d+).*</a>.*");
	private static final Pattern yearExtractCell = Pattern
			.compile("\\s*<td.*<a href=\"(.*?)\".*>(\\d{4})</a>.*");
	private static final Pattern attributeHeader = Pattern
			.compile("\\s*<th.*>(.*)</th>\\s*");
	private static final Pattern infoCellA = Pattern
			.compile("\\s*<td>(.*)</td>\\s*");
	private static final Pattern rowEnd = Pattern.compile("\\s*</tr>\\s*");
	private static final Pattern generalCatHeader = Pattern
			.compile("\\s*<th.*title=.*>(.*)</a></th>\\s*");
	private static final Pattern unorderedListBegin = Pattern
			.compile("\\s*<ul>\\s*");
	private static final Pattern unorderedListCellEnd = Pattern
			.compile("\\s*</ul>\\s*</td>\\s*");
	private static final String listItemBegin = "\\s*<li.*";
	private static final String spaceDashSpaceRegex = "\\s[^\\w\\s\\d]\\s";
	private static final String sortableWikiTableRegex = "\\s*<table class=\"sortable wikitable\">\\s*";
	private static final Pattern tableHeader = Pattern
			.compile("\\s*<th.*>(.*)</th>\\s*");
	private static final Pattern infoCell = Pattern
			.compile("\\s*<td.*><a href.*>(.*)</a>.*");
	private static final Pattern infoCell2 = Pattern
			.compile("\\s*<td.*>(.*)<.*");
	private static final Pattern infoCellRef = Pattern
			.compile("\\s*<td.*><a href.*>(.*)</a>.*<sup class=\"reference\".*"); // cells
																											// with
																											// footnotes
	private static final Pattern rowBegin = Pattern.compile("\\s*<tr.*>\\s*");
	private static final Pattern cellBegin = Pattern.compile("\\s*<td.*");
	private static final String tableDefine = "\\s*<table class=\"wikitable\">\\s*";

	/**
	 * Empty Constructor
	 */
	public WikipediaParser() {
	}

	/**
	 * Searches baseURL (here, Academy Awards Portal) for links containing search
	 * term
	 * 
	 * @param search
	 *           Search query
	 * @return ArrayList of links containing search query
	 */
	public ArrayList<String> getBaseURLContents(String search) {
		return this.getURLContentsSearch(WikipediaParser.baseURL, search);
	}

	/**
	 * Returns URLs on page at searchURL that contain search
	 * 
	 * @param searchURL
	 *           HTML to be loaded and searched
	 * @param search
	 *           Search query
	 * @return ArrayList of matching URLs
	 */
	public ArrayList<String> getURLContentsSearch(String searchURL, String search) {
		ArrayList<String> wikiURLs = new ArrayList<String>();

		try {
			Document doc = Jsoup.connect(searchURL).get();
			String url = "";
			Elements urls = doc.select("[href]");
			Pattern absPattern = Pattern
					.compile("\\s*http://en.wikipedia.org(.*)");
			Matcher absMatcher;

			// adds link to ArrayList for each url matching search query
			for (Element e : urls) {
				String absHref = e.attr("abs:href");
				if ((absHref.contains(search))) {
					url = absHref;
					absMatcher = absPattern.matcher(url);

					// converts absolute url to relative
					if (absMatcher.find()) {
						url = absMatcher.group(1);
					}

					if (!wikiURLs.contains(url)) {
						wikiURLs.add(url);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return wikiURLs;
	}

	/**
	 * Searches specified page for specified search term displayed in <i>text</i>
	 * of URL
	 * 
	 * @param searchURL
	 *           URL to be searched
	 * @param search
	 *           Search query to be looked for in text of hyperlinks
	 * @return HashMap<String,String> where the key corresponds to text and value
	 *         to link in hyperlink
	 */
	public HashMap<String, String> getURLTextSearch(String searchURL,
			String search) {
		HashMap<String, String> hasher = new HashMap<String, String>();
		try {
			Document doc = Jsoup.connect(searchURL).get();
			Elements urls = doc.select("[href]");

			String tempString;
			Pattern alink = Pattern
					.compile("\\s*<a href=\"(.*)\".*title.*>(.*)</a>\\s*");
			Matcher alinkMatcher;

			for (Element e : urls) {
				tempString = e.toString();
				alinkMatcher = alink.matcher(tempString);

				if (alinkMatcher.find()) {
					if (alinkMatcher.group(2).matches(".*" + search + ".*")) {
						// uses the link display text : link hyperlink as a K:V pair
						// in a Hash
						if (alinkMatcher.group(1).matches(".*Academy.*Awards.*")) { // EXPERIMENTAL
							hasher.put(alinkMatcher.group(2), alinkMatcher.group(1));
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hasher;
	}

	/**
	 * Gets arrayList of all movies that have been nominated for best picture.
	 * Attributes (accessible through HashMap) include Title, Producer, Producing
	 * Company, Link to whole-year award page, calendar year of win, and winner
	 * indicator
	 * 
	 * @return ArrayList of Movies nominated for Best Picture
	 */
	public ArrayList<HashedInfo> getDecadeTableInfo(String category,
			int indicator) {
		ArrayList<String> links = this.getBaseURLContents(category);
		ArrayList<HashedInfo> allNoms = new ArrayList<HashedInfo>();

		Document doc = null;
		try {
			// because of specificity of search params, all links should redirect
			// to same page
			doc = Jsoup.connect(WikipediaParser.wikiRootURL + links.get(0)).get();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String URLContents = doc.toString();

		Scanner reader = new Scanner(URLContents);

		// award years are separated by this delimiter
		if (indicator == 0) {
			reader.useDelimiter("<table class=\"wikitable\" style=\"width:100%;\">");
		} else if (indicator == 1) {
			reader.useDelimiter(WikipediaParser.tableDefine);
		}

		String tempString = "";
		ArrayList<HashedInfo> tempNoms;

		reader.next(); // gets rid of pre-table data

		while (reader.hasNext()) {
			tempString = reader.next();

			// gets rid of last "chunk", after last table
			if (!reader.hasNext()) {
				tempString = tempString.split(WikipediaParser.lastTableSplitRegex)[0];
			}

			tempNoms = this.analyzeTable(tempString, indicator);
			for (HashedInfo m : tempNoms) {
				allNoms.add(m);
			}

		}
		return allNoms;
	}

	/**
	 * Analyzes general table in format of table on Best Picture or Original
	 * Screenplay/Best Actor Wikipedia page
	 * 
	 * @param table
	 *           String representing table
	 * @param indicator
	 *           0 for table similar to Best Picture; 1 for table similar to
	 *           Original Screenplay/Best Actor; other values throw exceptions
	 * @return ArrayList of movies analyzed from table
	 */
	private ArrayList<HashedInfo> analyzeTable(String table, int indicator) {
		ArrayList<HashedInfo> fromTable = new ArrayList<HashedInfo>();
		Scanner tableReader = new Scanner(table);
		tableReader.useDelimiter(System.getProperty("line.separator"));
		String awardLink = "";
		int awardYear = 0, calendarYear = 0;
		ArrayList<String> attributes = new ArrayList<String>();
		Matcher attributeMatcher, infoCellMatcher;

		boolean action = false;

		if (indicator == 0) { // BEST PICTURE
			// skips extra front lines
			while (!tableReader.hasNext(WikipediaParser.yearExtract)) {
				tableReader.next();
			}

			// grabs calendar year
			String calendarYearLine = tableReader.next();
			Matcher m = WikipediaParser.yearExtract.matcher(calendarYearLine);
			if (m.find()) {
				calendarYear = Integer.parseInt(m.group(2));
				if (String.valueOf(calendarYear).length() == 2) {
					calendarYear += 1900;
				}
			}

			// grabs award year
			String firstLine = tableReader.next(WikipediaParser.yearExtract);
			m = WikipediaParser.yearExtract.matcher(firstLine);

			// save link and year info from header
			if (m.find()) {
				awardLink = m.group(1);
				awardYear = Integer.parseInt(m.group(2));
			}

			action = false;
			// get attributes in table
			while (!tableReader.hasNext(WikipediaParser.rowEnd)) {
				action = false;

				// uses above-defined regex to see if this line defines row header
				if (tableReader.hasNext(WikipediaParser.attributeHeader)) {

					String tempRow = tableReader.next();
					attributeMatcher = WikipediaParser.attributeHeader
							.matcher(tempRow);
					action = true;

					if (attributeMatcher.find()) {
						attributes.add(attributeMatcher.group(1));
					}
				}

				if (!action) {
					tableReader.next();
				}
			}
		} else if (indicator == 1) { // ORIG SCREENPLAY
			// skips extra front lines
			while (!tableReader.hasNext(WikipediaParser.trDefine)) {
				tableReader.next();
			}

			tableReader.next(); // gets rid of year info line, so can grab award
										// number info

			action = false;
			// get attributes in table
			while (!tableReader.hasNext(WikipediaParser.rowEnd)) {
				action = false;

				// uses above-defined regex to see if this line defines row header
				if (tableReader.hasNext(WikipediaParser.attributeHeader)) {

					String tempRow = tableReader.next();
					attributeMatcher = WikipediaParser.attributeHeader
							.matcher(tempRow);
					action = true;

					if (attributeMatcher.find()) {
						attributes.add(attributeMatcher.group(1));
					}
				}

				if (!action) {
					tableReader.next();
				}
			}

			// continue "eating" lines until specified pattern is next
			while (!tableReader.hasNext(WikipediaParser.yearExtract)) {
				tableReader.next();
			}

			String firstLine = tableReader.next(WikipediaParser.yearExtract);
			Matcher m = WikipediaParser.yearExtract.matcher(firstLine);

			// save link and year info from header
			if (m.find()) {
				awardLink = m.group(1);
				awardYear = Integer.parseInt(m.group(2));
			}

		}

		// begin content parse
		Document tempRowDoc = Document.createShell("");
		int counter;
		String value = "";
		ArrayList<String> valueHolder;
		String winner = "1"; // 1 for winner; 0 otherwise
		HashMap<String, String> movieAttributes = new HashMap<String, String>();
		String rowContents;
		Elements links;
		while (tableReader.hasNext()) {
			action = false;

			if (tableReader.hasNext(WikipediaParser.trDefine)) {
				tableReader.next();
			} else if (tableReader.hasNext("\\s*<h3>.*</h3>\\s*")) {
				tableReader.next();
			} else if (tableReader.hasNext("\\s*<\tbody>\\s*")) {
				tableReader.next();
			} else if (tableReader.hasNext("\\s*<\table>\\s*")) {
				tableReader.next();
			} else {

				// below loop is for each movie
				counter = 0;
				eachMovie: while (!tableReader.hasNext(WikipediaParser.rowEnd)) {

					// make sure next line is in correct format for cell
					if (tableReader.hasNext(WikipediaParser.infoCellA)) {
						action = true;
						rowContents = tableReader.next();
						tempRowDoc = Jsoup.parse(rowContents);
						links = tempRowDoc.select("[href]");
						valueHolder = new ArrayList<String>();

						// add hyperlinks to valueHolder
						if (links.size() != 0) {
							for (Element e : links) {
								valueHolder.add(e.text().trim());
							}
						}

						// match plaintext left over
						infoCellMatcher = WikipediaParser.infoCellA
								.matcher(rowContents);
						if (infoCellMatcher.find()) {
							String[] tempValue = infoCellMatcher.group(1).split(", ");
							for (String s : tempValue) {
								if (!s.matches(".*href.*")) { // gets rid of hyperlinks
																		// already grabbed
									if (!valueHolder.contains(s.trim())) {
										valueHolder.add(s.trim());
									}
								}
							}
						}

						// create value String from valueHolder ArrayList
						for (String s : valueHolder) {
							if (!value.equals("")) {
								value += ", ";
							}
							value += s;
						}

						// add key--value pair to HashMap
						// if statement due to different structures of wiki tables
						if (indicator == 0) {
							movieAttributes.put(attributes.get(counter).trim(),
									value.trim());
						} else if (indicator == 1) {
							movieAttributes.put(attributes.get(counter + 1).trim(),
									value.trim());
						}

						counter++;
						value = "";
					}

					// safeguards
					if (!tableReader.hasNext()) {
						break eachMovie;
					}
					if (!action) {
						tableReader.next();
					}
				}

				// if movie item pre-assembled, add to list of noms
				if (action) {
					movieAttributes.put("winner", winner);
					movieAttributes.put("awardLink", awardLink.trim());
					movieAttributes.put("awardYear", String.valueOf(awardYear)
							.trim());
					movieAttributes
							.put("calendarYear", String.valueOf(calendarYear));

					fromTable.add(new HashedInfo(movieAttributes));

					winner = "0"; // from now on in this table, movies are not
										// winners

				} else if (tableReader.hasNext()) {
					tableReader.next();
				}

				// reset for next movie
				movieAttributes.clear();
			}
		}

		return fromTable;
	}

	/**
	 * Analyzes page for specific Academy Awards year and returns nominees for
	 * specified award Note the formatting of this wiki page is significantly
	 * different enough to not share an analysis method (these pages are for
	 * specific years).
	 * 
	 * @param category
	 *           Category to be searched
	 * @param year
	 *           Year to be searched
	 * @param indicator
	 *           0 for Person more important; 1 for Movie more important
	 */
	public ArrayList<Person> getCategoryYearInfo(String category, int year,
			int indicator) {
		HashMap<String, String> hasher = this.getURLTextSearch(
				WikipediaParser.baseURL, String.valueOf(year));

		Document doc = null;
		try {
			doc = Jsoup.connect(
					WikipediaParser.wikiRootURL + hasher.get(String.valueOf(year)))
					.get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		String CYIContentsTemp = doc.toString();

		Scanner tempScanner = new Scanner(CYIContentsTemp);
		tempScanner.useDelimiter(WikipediaParser.tableDefine);
		tempScanner.next(); // get rid of first block
		String CYIContents = tempScanner.next();

		Scanner docReader = new Scanner(CYIContents);
		docReader.useDelimiter(System.getProperty("line.separator"));

		Pattern catHeader = Pattern.compile("\\s*<th.*title=.*>" + category
				+ "</a></th>\\s*");

		while (!docReader.hasNext(catHeader)) {
			docReader.next();
		}

		docReader.next(); // eats category header

		// note position (left or right side of row) of the category header
		int position;
		if (docReader.hasNext(WikipediaParser.generalCatHeader)) {
			position = 1;
		} else {
			position = 2;
		}

		// get to correct spot
		while (!docReader.hasNext(WikipediaParser.unorderedListBegin)) {
			docReader.next();
		}

		// repeat if in second cell
		if (position == 2) {
			while (!docReader.hasNext(WikipediaParser.unorderedListCellEnd)) {
				docReader.next();
			}
			while (!docReader.hasNext(WikipediaParser.unorderedListBegin)) {
				docReader.next();
			}
		}

		String[] parsingString;
		String winner, film;
		ArrayList<Person> searchResults = new ArrayList<Person>();

		// continue until end of cell
		while (!docReader.hasNext(WikipediaParser.unorderedListCellEnd)
				&& docReader.hasNext()) {

			if (docReader.hasNext(WikipediaParser.listItemBegin)) {
				parsingString = docReader.next().trim().split(spaceDashSpaceRegex);
				winner = parsingString[0];

				// handles cases where both " - " exists and doesn't exist
				if (parsingString.length > 1) {
					film = parsingString[1].trim();
				} else {
					film = "";
				}

				Pattern urlExtract;
				if (indicator == 0) {// PERSON MORE IMPORTANT
					urlExtract = Pattern
							.compile(".*?<a href=\"(.*?)\".*?title.*?\">(.*?)</a.*");
				} else if (indicator == 1) {// MOVIE MORE IMPORTANT
					urlExtract = Pattern
							.compile(".*<a href=\"(.*)\".*title.*\">(.*)</a.*");
				} else {
					return null; // bad option; shouldn't happen
				}
				Matcher urlMatcher = urlExtract.matcher(winner);
				Matcher urlMatcher2 = urlExtract.matcher(film);

				// we do this twice because (as we see above) " - " pattern doesn't
				// always exist. This method finds where the movie data was stored
				if (film != "") {
					if (urlMatcher.find() && urlMatcher2.find()) {
						searchResults.add(new Person(urlMatcher.group(2), urlMatcher
								.group(1), urlMatcher2.group(2), urlMatcher2.group(1)));
					}
				} else {
					if (urlMatcher.find()) {
						searchResults.add(new Person(urlMatcher.group(2), urlMatcher
								.group(1), "", ""));
					}
				}

			} else {
				docReader.next();
			}
		}

		return searchResults;
	}

	/**
	 * Returns age of specified people in a certain year, based on Wikipedia page
	 * 
	 * @param people
	 *           ArrayList of Person where each Person is someone whose age is to
	 *           be calculated
	 * @param year
	 *           Reference year
	 * @return ArrayList of HashedInfo, where the "Age" key holds the age at the
	 *         given year for the "Name" key
	 */
	public ArrayList<HashedInfo> getAgeAtTime(ArrayList<Person> people, int year) {
		ArrayList<HashedInfo> toReturn = new ArrayList<HashedInfo>();
		HashMap<String, String> tempHash;
		String tempPersonPage, ageLine, age, ageAtTime;
		Scanner reader;
		Pattern bornHeader = Pattern.compile("\\s*<th.*>Born</th>\\s*");
		Pattern ageData = Pattern.compile(".*age&nbsp;(\\d+).*");
		Matcher ageMatcher;
		int currentYear;
		Document doc;

		for (Person p : people) {
			tempHash = new HashMap<String, String>();

			try {
				doc = Jsoup.connect(WikipediaParser.wikiRootURL + p.getLink())
						.get();
			} catch (IOException e) {
				e.printStackTrace();
				return null; // should only happen if link address changes between
									// initial query and now
			}

			tempPersonPage = doc.toString();
			reader = new Scanner(tempPersonPage);
			reader.useDelimiter(System.getProperty("line.separator"));

			// skip going until we have the bornHeader next
			while (!reader.hasNext(bornHeader)) {
				reader.next();
			}

			reader.next(); // skip "Born Header" line

			ageLine = reader.next(); // by pattern, the age is given in the line
												// immediately following the "born header"
			ageMatcher = ageData.matcher(ageLine);

			if (ageMatcher.find()) {
				age = ageMatcher.group(1);
			} else {
				age = "40"; // shouldn't ever happen - theoretically
			}

			// gets current year instead of hard-coding in
			currentYear = Calendar.getInstance().get(Calendar.YEAR);

			ageAtTime = String.valueOf(Integer.parseInt(age) + year - currentYear);

			tempHash.put("Age", ageAtTime);
			tempHash.put("Name", p.getName());
			tempHash.put("Movie", p.getMovie());

			toReturn.add(new HashedInfo(tempHash));

		}

		return toReturn;
	}

	/**
	 * Gets nominations for Foreign Language award
	 * 
	 * @return ArrayList of HashedInfo wrappers for each nominee and associated
	 *         info
	 */
	public ArrayList<HashedInfo> getForeignLanguageInfo() {
		ArrayList<String> FLLinks = this
				.getBaseURLContents("Best_Foreign_Language_Film");
		// needs to be long name
		ArrayList<HashedInfo> FLNoms = new ArrayList<HashedInfo>();

		Document doc = null;
		String linkToUse = "";

		// get the list page
		for (String s : FLLinks) {
			if (s.matches(".*[Ll]ist.*")) {
				linkToUse = s;
				break;
			}
		}

		try {
			// because of specificity of search params, all links should redirect
			// to same page
			doc = Jsoup.connect(WikipediaParser.wikiRootURL + linkToUse).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String FLURLContents = doc.toString();
		String FLTable = FLURLContents.split(sortableWikiTableRegex)[1]
				.split(lastTableSplitRegex)[0];
		// get table. In between the two regexes.

		Scanner reader = new Scanner(FLTable);
		reader.useDelimiter(System.getProperty("line.separator"));

		while (!reader.hasNext(WikipediaParser.trDefine)) {
			reader.next();
		}
		reader.next(); // skip trDefine pattern when it arrives

		// get attributes from top of table
		ArrayList<String> attributes = new ArrayList<String>();

		Matcher thMatcher;

		while (!reader.hasNext(WikipediaParser.rowEnd)) {
			thMatcher = WikipediaParser.tableHeader.matcher(reader.next());

			if (thMatcher.find()) {
				attributes.add(thMatcher.group(1));
			}
		}
		reader.next(); // to get to info cells from header

		HashMap<String, String> tempHash;

		Matcher infoCellMatcher, infoCell2Matcher, infoCellRefMatcher;
		String rowContents, value;
		int counter;

		while (reader.hasNext(WikipediaParser.rowBegin)) {
			tempHash = new HashMap<String, String>();
			counter = 0;

			// loop over all films
			while (!reader.hasNext(WikipediaParser.rowEnd)) {
				if (reader.hasNext(WikipediaParser.cellBegin)) {
					rowContents = reader.next();
					infoCellMatcher = WikipediaParser.infoCell.matcher(rowContents);
					infoCell2Matcher = WikipediaParser.infoCell2
							.matcher(rowContents);
					infoCellRefMatcher = WikipediaParser.infoCellRef
							.matcher(rowContents);

					// here we find the correct format
					if (infoCellRefMatcher.find()) {
						value = infoCellRefMatcher.group(1);
					} else if (infoCellMatcher.find()) {
						value = infoCellMatcher.group(1);
					} else if (infoCell2Matcher.find()) {
						value = infoCell2Matcher.group(1);
					} else {
						value = "";
					}

					tempHash.put(attributes.get(counter), value);
					counter++;

				} else {
					reader.next();
				}

			}

			FLNoms.add(new HashedInfo(tempHash));
			tempHash.clear();
			reader.next(); // eat </tr>
		}

		return FLNoms;
	}

	/**
	 * Gets the number of wins for a given movie (in the corresponding year).
	 * This uses the pattern that winners are in boldface type while noms are
	 * not.
	 * 
	 * @param movie
	 *           Movie whose win count is to be calculated
	 * @param year
	 *           Year movie won award
	 * @return Number of wins
	 */
	public int getNumberOfWins(String movie, String year) {
		int winCount = 0;
		String yearLink = this.getURLTextSearch(WikipediaParser.baseURL, year)
				.get(year);
		String document = "";

		try {
			document = Jsoup.connect(WikipediaParser.wikiRootURL + yearLink).get()
					.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// gets rid of pre-table data
		document = document.split(WikipediaParser.tableDefine)[1];
		Scanner reader = new Scanner(document);
		reader.useDelimiter(System.getProperty("line.separator"));

		// takes advantage of winners being formatted in bold
		Pattern winnerFind = Pattern.compile("\\s*<li>.*<b>.*" + movie
				+ ".*</b>.*");

		while (reader.hasNext()) {
			if (reader.hasNext(winnerFind)) {
				winCount++;
			}
			reader.next();
		}

		return winCount;
	}

	/**
	 * Gets information for any category whose tables are in the year-win-noms
	 * format, separated by decade. (See Best Director for example)
	 * 
	 * @param category
	 *           Category to be scraped
	 * @return ArrayList of HashedInfo wrapper for nominations in this category
	 *         and associated info for each movie
	 */
	public ArrayList<HashedInfo> getCategoryInfoWinNom(String category) {

		ArrayList<String> BDLinks = this.getBaseURLContents(category);
		ArrayList<HashedInfo> allNoms = new ArrayList<HashedInfo>();

		String BDfullText = null;

		try {
			BDfullText = Jsoup
					.connect(WikipediaParser.wikiRootURL + BDLinks.get(0)).get()
					.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// breaks page into table
		Scanner fullReader = new Scanner(BDfullText);
		fullReader.useDelimiter(WikipediaParser.tableDefine);
		String table;
		ArrayList<HashedInfo> analysisResults;

		fullReader.next(); // gets rid of chunk before table

		// continue until no more "table chunks"
		while (fullReader.hasNext()) {
			table = fullReader.next();

			// if last table, drops off post-tables data (denoted by <h2> tag)
			if (!fullReader.hasNext()) {
				table = table.split(lastTableSplitRegex)[0];
			}
			// here we have the whole table in "table" variable

			analysisResults = this.analyzeWinNomTable(table);

			// add results from this table to allNoms running List
			for (HashedInfo h : analysisResults) {
				allNoms.add(h);
			}

		}

		return allNoms;
	}

	/**
	 * This method parses a table in year-win-nom format. See Best Director for
	 * example
	 * 
	 * @param table
	 *           String representation of table to be analyzed
	 * @return ArrayList of HashedInfo wrappers for nominated movies and
	 *         associated information
	 */
	private ArrayList<HashedInfo> analyzeWinNomTable(String table) {
		ArrayList<HashedInfo> analyzedResults = new ArrayList<HashedInfo>();
		HashMap<String, String> tempHasher = new HashMap<String, String>();
		Scanner reader = new Scanner(table);
		reader.useDelimiter(System.getProperty("line.separator"));

		Matcher yearMatcher;

		// skips header data
		while (!reader.hasNext(WikipediaParser.rowEnd)) {
			reader.next();
		}
		reader.next(); // eat <\tr> tag
		// here reader is at <tr> tag defining first row in table

		String rowContents;
		String awardLink, awardCalendarYear;
		String[] noms, content;
		while (reader.hasNext(WikipediaParser.trDefine)) {
			reader.next(); // read <tr>

			// gets year information out of first cell
			rowContents = reader.next();
			yearMatcher = WikipediaParser.yearExtractCell.matcher(rowContents);

			if (yearMatcher.find()) {
				awardLink = yearMatcher.group(1);
				awardCalendarYear = yearMatcher.group(2);
			} else {
				awardLink = "";
				awardCalendarYear = "";
			}

			// gets winner information
			rowContents = reader.next();

			content = rowContents.split("<br />");
			tempHasher = this.analyzeLink(content, "\\s*<td>(.*)<\td>\\s*",
					tempHasher);

			tempHasher.put("winner", "1");
			tempHasher.put("awardYear", awardCalendarYear);
			tempHasher.put("awardLink", awardLink);
			analyzedResults.add(new HashedInfo(tempHasher));

			// now time to extract non-winning nominees
			rowContents = reader.next();

			if (rowContents.matches("\\s*<td.*</td>.*")) { // in this case, lines
																			// are split by line
																			// break tags
				noms = rowContents.split("<br />");
				analyzedResults = this.analyzeBreakedNoms(noms, analyzedResults,
						awardCalendarYear, awardLink);
			} else {
				if (!rowContents.matches(WikipediaParser.listItemBegin)) {
					while (!reader.hasNext(WikipediaParser.listItemBegin)) {
						reader.next();
					}
				} // get reader to correct point in file
				while (!reader.hasNext(WikipediaParser.rowEnd)) {
					if (reader.hasNext(WikipediaParser.listItemBegin)) {
						rowContents = reader.next();

						content = rowContents
								.split(WikipediaParser.spaceDashSpaceRegex);
						tempHasher = this.analyzeLink(content, "\\s?(.*)</li>\\s*",
								tempHasher);

						// add extra data
						tempHasher.put("winner", "0");
						tempHasher.put("awardYear", awardCalendarYear);
						tempHasher.put("awardLink", awardLink);

						analyzedResults.add(new HashedInfo(tempHasher));
						tempHasher.clear();
					} else {
						reader.next();
					}
				}
			}

			reader.next(); // eats trailing </tr> tag
		}
		return analyzedResults;
	}

	/**
	 * Analyzes line-break separated nominations in single table cell. This means
	 * nominees are separated by <br />
	 * tag, not a list tag
	 * 
	 * @param noms
	 *           Raw nomination data
	 * @param analyzedResults
	 *           Where analyzed data ought be placed
	 * @param awardCalendarYear
	 *           Calendar year of this award
	 * @param awardLink
	 *           Link to award ceremony for this year
	 * @return ArrayList of HashedInfo wrapper for each movie extracted from the
	 *         noms input
	 */
	private ArrayList<HashedInfo> analyzeBreakedNoms(String[] noms,
			ArrayList<HashedInfo> analyzedResults, String awardCalendarYear,
			String awardLink) {
		HashMap<String, String> tempHasher = new HashMap<String, String>();
		String[] content;

		for (String s : noms) {

			if (s.matches("\\s*</td>\\s*")) {
				continue; // skip to next string s if current string is cell end
								// tag
			}

			content = s.split(spaceDashSpaceRegex);
			tempHasher = this.analyzeLink(content, "\\s*<td>(.*)</td>\\s*",
					tempHasher);

			// add extra data
			tempHasher.put("winner", "0");
			tempHasher.put("awardYear", awardCalendarYear);
			tempHasher.put("awardLink", awardLink);

			analyzedResults.add(new HashedInfo(tempHasher));
			tempHasher.clear();
		}

		return analyzedResults;
	}

	/**
	 * Finds stars of movie at given movieLink input
	 * 
	 * @param movieLink
	 *           Relative link to movie of which stars are to be extracted from
	 * @return ArrayList of Strings of stars of supplied movie
	 */
	public ArrayList<String> getStarring(String movieLink) {
		ArrayList<String> starring = new ArrayList<String>();
		String tempPersonPage;
		Scanner reader;
		Pattern starringHeader = Pattern.compile("\\s*<th.*>Starring</th>\\s*");
		String[] stars;
		Matcher linkMatcher;

		Document doc;

		try {
			doc = Jsoup.connect(WikipediaParser.wikiRootURL + movieLink).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null; // should only happen if link address changes between
								// initial query and now
		}

		tempPersonPage = doc.toString();
		reader = new Scanner(tempPersonPage);
		reader.useDelimiter(System.getProperty("line.separator"));

		while (!reader.hasNext(starringHeader)) {
			reader.next();
		}

		reader.next(); // skip "Starring Header" line

		stars = reader.next().split("<br />");

		// name is extracted from raw html for each star
		for (String s : stars) {
			linkMatcher = WikipediaParser.tableLinkExtract.matcher(s);

			if (linkMatcher.find()) {
				starring.add(linkMatcher.group(2).trim());
			}
		}
		return starring;
	}

	/**
	 * Sub-method to analyze an array of links (should be of length 2,
	 * corresponding to person and associated movie) and turn into links
	 * 
	 * @param content
	 *           Input to be analyzed
	 * @param defaultRegexPattern
	 *           Default regex pattern to be used to remove most obvious tags in
	 *           case internal algorithm fails to detect match
	 * @param tempHasher
	 *           HashMap where extracted information should be stored
	 * @return tempHasher input, after appropriate information stored
	 */
	private HashMap<String, String> analyzeLink(String[] content,
			String defaultRegexPattern, HashMap<String, String> tempHasher) {
		Matcher linkMatcher, tempMatcher;
		String link = "", text = "";
		String person = content[0];
		String film = content[1];

		// analyze person (director, actor, actress, etc.) link
		linkMatcher = WikipediaParser.tableLinkExtract.matcher(person);
		if (linkMatcher.find()) {
			link = linkMatcher.group(1);
			text = linkMatcher.group(2);

			tempHasher.put("person", text);
			tempHasher.put("personLink", link);
		} else {
			tempMatcher = Pattern.compile(defaultRegexPattern).matcher(person);
			if (tempMatcher.find()) {
				text = tempMatcher.group(1);
			}
		}

		// analyze Movie Link
		linkMatcher = WikipediaParser.tableLinkExtract.matcher(film);
		if (linkMatcher.find()) {
			link = linkMatcher.group(1);
			text = linkMatcher.group(2);

			tempHasher.put("movie", text);
			tempHasher.put("movieLink", link);
		} else {
			tempMatcher = Pattern.compile(defaultRegexPattern).matcher(film);
			if (tempMatcher.find()) {
				text = tempMatcher.group(1);
			}
		}

		return tempHasher;
	}
}