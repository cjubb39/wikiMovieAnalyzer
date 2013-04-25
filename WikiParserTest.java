import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiParserTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File o;
		PrintWriter out = null;

		// for (int selector = 0; selector < 14; selector++) {
		// System.out.println(selector);
		int selector = 11;

		switch (selector) {
		case 0:
			ArrayList<String> urls = new WikipediaParser()
					.getBaseURLContents("List_of_Academy_Award_winners_and_nominees_for_Best_Foreign_Language_Film");

			for (String s : urls) {
				System.out.println(s);
			}

			break;

		case 1:
			o = new File("output.txt");
			out = new PrintWriter(o);
			out.println(Jsoup
					.connect(
							"http://en.wikipedia.org//wiki/Academy_Award_for_Writing_Original_Screenplay")
					.get().toString());
			out.close();
			break;

		case 2:
			ArrayList<HashedInfo> temp = new WikipediaParser().getDecadeTableInfo(
					"Best_Actor", 1);
			System.out.println(temp.size());

			for (HashedInfo m : temp) {
				System.out.println(m.toString());
			}
			break;

		case 3:

			String tempString = "        <td><a href=\"/wiki/Michael_Powell_(director)\" title=\"Michael Powell (director)\">Michael Powell</a>, <a href=\"/wiki/Emeric_Pressburger\" title=\"Emeric Pressburger\">Emeric Pressburger</a></td> ";
			System.out.println(tempString);
			Document tempRowDoc = Jsoup.parse(tempString);
			// Element innerText = new Element(new Tag("text"), "");

			// tempRowDoc.text(tempString);
			System.out.println(tempRowDoc.body());
			Elements links = tempRowDoc.select("[href]");
			System.out.println(links.size());
			// System.out.println(Jsoup.connect("http://localhost:3000").get().body());
			break;

		case 4:
			HashMap<String, String> hasher = new HashMap<String, String>();
			hasher.put("key1", "value1");
			hasher.put("anotherValue", "anotherKey");

			HashedInfo testHashedInfo = new HashedInfo(hasher);

			System.out.println(testHashedInfo.toString());
			break;

		case 5:
			String rowContents = "        <td>Les Films du Losange, X Filme Creative Pool, Wega Film Production</td> ";
			tempRowDoc = Jsoup.parse(rowContents);
			links = tempRowDoc.select("[href]");
			System.out.println(links.size());
			String value = "";

			if (links.size() != 0) {
				for (Element e : links) {
					if (!value.equals("")) {
						value += ", ";
					}
					value += e.text();
				}
			} else {
				/*
				 * infoCellMatcher = infoCell.matcher(rowContents); if
				 * (infoCellMatcher.find()) { System.out.println(rowContents); value
				 * += infoCellMatcher.group(1); }
				 */
			}
			break;

		case 6:
			ArrayList<HashedInfo> tempHashedInfos = new MovieInterpreter()
					.bestPictureSearch("Production company(s)", "United Artists");

			if (tempHashedInfos.size() != 0)
				for (HashedInfo m : tempHashedInfos) {
					System.out.println(m.toString());
				}

			break;

		case 7:
			ArrayList<String> tempWriters = new MovieInterpreter()
					.bestLeadingActor("King");
			for (String s : tempWriters) {
				System.out.println(s);
			}
			break;

		case 8:
			// new WikipediaParser().getURLTextSearch("http://localhost:3000",
			// "");
			ArrayList<HashedInfo> results = new MovieInterpreter()
					.getCategoryYearInfo("Best Actress", 2006);
			for (HashedInfo p : results) {
				System.out.println(p.toString());
			}
			break;

		case 9:
			Object[] returnedInfo = new MovieInterpreter().getMaxForeignWins();
			System.out.println(returnedInfo[0]);
			System.out.println(returnedInfo[1]);
			for (String s : (ArrayList<String>) returnedInfo[2]) {
				System.out.println(s);
			}
			break;

		case 10:
			String[] result = new MovieInterpreter()
					.originalScreenplayWriters("Citizen Kane");
			for (String s : result) {
				System.out.println(s);
			}
			break;

		case 11:
			// HashMap<String,Integer> returnedResult = new
			// MovieInterpreter().getQuadThreat();
			System.out.println(new MovieInterpreter().getQuadThreat());
			break;

		case 12:
			/*
			 * ArrayList<HashedInfo> returnedAnswer = new
			 * WikipediaParser().getBestDirectorInfo(); for (HashedInfo h :
			 * returnedAnswer){System.out.println(h);}
			 */
			ArrayList<Person> returnedAnswer = new MovieInterpreter()
					.bestDirectorThreshold(7);
			for (Person p : returnedAnswer) {
				System.out.println(p.getName() + ":\t" + p.getMovie());
			}
			break;

		case 13:
			ArrayList<String> tempAnswer = new MovieInterpreter()
					.getCategoryStarring("Best_Director", "Jack Black");
			// ArrayList<String> tempAnswer = new
			// WikipediaParser().getStarring("/wiki/Lilo_%26_Stitch");
			for (String s : tempAnswer) {
				System.out.println(s);
			}

			break;

		default:
			System.out.println("Bad Option");
			break;
		}
	}
}
// }