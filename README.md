#Wikipedia Movie Analyzer
Chae Jubb \\ ecj2122

##To use this software
$./java wikiAnalyzerGUI

Choose question from drop-down menu and enter option 1 and option 2 (if necessary) into appropriate
	boxes. The results will appear in the text area at the bottom of the window.  Note that options
	are case sensitive;
	
###Question specific Instructions:
1) List all movies nom'd for the Best Picture award for which one of the (OPTION1) was 
			(OPTION2).
		OPTION1 should be an attribute header on the Wikipedia page.  Currently, this is Production 
		company(s) or Producer(s).  OPTION2 should be _part_ of the cell contents.
		
2) For the Best Original Screenplay award, list the writers for the movie that was nominated/won 
			title (OPTION1).
		OPTION1 should be the title of the movie.  If the movie did not win, an error is thrown.

3) List all actors nominated for a Best Leading Actor award whose role was playing (a/an) 
			(OPTION1).
		OPTION1 will be searched for as _part_ of the Role/Role(s) column on the Best Actor page

4) For the year (OPTION1), list all actresses nominated for a Best Leading Actress award along with
			the movie and their age that year.
		OPTION1 should be an integer.

5) List all directors (with the corresponding movies) that have been nominated for at least 
			(OPTION1) Best Director awards.
		OPTION1 should be an integer.

6) List the country (with the corresponding movies) that has been nominated the most number of
			times for Best Foreign Language Film award.

7) List all movies nominated for the (OPTION1) award that starred (OPTION2).
		OPTION1 should be an award with tables in the same format as the "Best Animated Feature" page.
		OPTION2 should be an actor or actress

8) List all movies that were nominated for Best Picture, Best Director, Best Leading Actor, and 
			Best Leading Actress along with the number of awards won by each movie.
		Returns total number of awards won by each movie.

9) How many movies have been nominated for Best Picture (and similar, older awards)?

10)How many different nominations have been handed out for Best Actor?
		

##Design Choices
I chose to use this HashedInfo HashMap wrapper as a primary form of search result because of its 
	customization.  Using a Person-like class for search results would have been silly because each
	result would have many, many unused instance variable: it would have been wasteful and confusing.
	Thus, I created this HashedInfo class to contain only necessary values.  I created it as a 
	wrapper for the HashMap class rather than using multiple HashMaps for ease of transfer and 
	creation.  In order to pass, say 100 HashMaps, I would have had to create, and name 100 HashMaps;
	here, I can simply pass the HashMap to this class which will replicate and wrap it, as seen 
	throughout my code.
	
I also have the potentially confusing two-layer facade (one in front of the GUI and one in front 
	of the Parser). This choice was made so the Parser could interact with a wide variety of 
	"front-ends", like the GUI I created.  This is also why the results from the MovieInterpreter
	class are returned in the format best fitting the requested data rather than a standardized
	String.  The GUI Controller is essentially responsible for interpreting these results into
	something that can be displayed in the GUI result area. 
