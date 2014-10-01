/**
 * This class provides source code to fetch the most popular phrase over time
 * @author Pratiksha Pai
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import edu.smu.tspell.wordnet.*;

public class wordFrequency {

	final static String OUTPUT_FILE_NAME = "/Users/pk/Desktop/inputdata/239Tuned/quotes_2008-08_op.txt";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		System.setProperty("wordnet.database.dir", "/Users/pk/Documents/JAWS/WordNet-3.0/dict");

		System.out.println("Finding most popular phrase: ");
//								final File textFile = new File("test.txt"); 
		//						final File textFile = new File("text_file.txt");
		//						final File textFile = new File("September2008.txt");
//								final File textFile = new File(OUTPUT_FILE_NAME);  
								final File textFile = new File("ExampleFile.txt"); 
		//						final File textFile = new File("quotes_2008-08_op.txt");

		final BufferedReader in = new BufferedReader(new FileReader(textFile));

		Set<String> stopWords = new LinkedHashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader("stopwords.txt"));
		for(String line;(line = br.readLine()) != null;)
			stopWords.add(line.trim());
		br.close();

		// Mapping of String->Integer (word -> frequency) 
		final TreeMap<String, Word> frequencyMap = new TreeMap<String, Word>();
		final int cLimit = 15;

		// Iterate through each line of the file 
		String currentLine;
		int lineCount = 0;
		float testLineCount;
		while ((currentLine = in.readLine()) != null) {
			lineCount++;
			testLineCount = lineCount;
			if (testLineCount%10000 == 0) {
				System.out.println("LineNum"+lineCount );
			}
			// Remove this line if you want words to be case sensitive 
			currentLine = currentLine.toLowerCase();

			// Iterate through each word of the current line 
			// Delimit words based on whitespace, punctuation, and quotes 
			final StringTokenizer parser = new StringTokenizer(currentLine, " \t\n\r\f.,;:!?'");			
			if (parser.countTokens() < cLimit) {
				while (parser.hasMoreTokens()) {
					final String currentWord = parser.nextToken(); 

					//				TODO: put in frequencyMap only if its not a stop word
					if (!stopWords.contains(currentWord)){
						// If no synsets then invalid word and neglect it
						Word myWord = frequencyMap.get(currentWord);

						// Add the word if it doesn't already exist, otherwise increment the 
						// frequency counter. 
						if (myWord == null) { 
							myWord = new Word();
						} 
						myWord.setFrequency(myWord.getFrequency()+1);
						myWord.putLine(lineCount, currentLine);
						frequencyMap.put(currentWord, myWord);
					}
				}
			}
		}

		// Iterate for synomyns
		final BufferedReader bTextFile = new BufferedReader(new FileReader(textFile));
		lineCount = 0;
		while ((currentLine = bTextFile.readLine()) != null) {
			lineCount++;
			testLineCount = lineCount;
			if (testLineCount%10000 == 0) {
				System.out.println("LineNum"+lineCount );
			}

			// Remove this line if you want words to be case sensitive 
			currentLine = currentLine.toLowerCase(); 

			// Iterate through each word of the current line 
			// Delimit words based on whitespace, punctuation, and quotes 
			final StringTokenizer parser = new StringTokenizer(currentLine, " \t\n\r\f.,;:!?'");
			//			int wordCount = 0;
			if (parser.countTokens() < cLimit) {
				while (parser.hasMoreTokens()) {
					//					wordCount++;
					final String currentWord = parser.nextToken(); 

					//				TODO: put in frequencyMap only if its not a stop word
					// Do new code around here
					//				1. if not a stop word - Done
					if (!stopWords.contains(currentWord)){
						//					2. then get all similarWord(array) for the currentWord (Do it till here)
						// If no synsets then invalid word and neglect it
						List<String> syns = getSynonyms(currentWord);
						if (syns == null){
							continue;
						}

						//					3. while/for loop through each of the similarWord array
						for(String syn : syns) {
							//						4. get each SimilarWord and find it in frequencyMap and do the check
							//						5. if you find the similarWord in frequencyMap then append the new line in the current word line
							if(syn.compareTo(currentWord) != 0) {
								Word myWord = frequencyMap.get(syn);
								if (myWord != null) {
									myWord.setFrequency(myWord.getFrequency()+1);
									myWord.putLine(lineCount, currentLine);
									frequencyMap.put(syn, myWord);
								}
							}
						}					
					}
				}
			}
		}



		// Finally
		in.close();
		bTextFile.close();

		// Just print here
		//		printTreeMap(frequencyMap);

		//		Finally,  1. Inverse frequency analysis from the above frequency map to quotes frequency
		// Mapping of Integer->Integer (lineNumber -> frequency) 
		//		final TreeMap<Integer, Integer> quoteFrequencyMap = new TreeMap<Integer, Integer>();
		TreeMap<String, Word> tempFrequencyMap = new TreeMap<String, Word>(frequencyMap);

		// While iterator frequencyMap's each line and then update quoteFrequencyMap
		// Find word(s) with highest number of quotes
		String mostCommonWord = findHighestFrequencyWord(tempFrequencyMap);
		System.out.println("\n\nCommon Word:" + mostCommonWord + " with " + frequencyMap.get(mostCommonWord).getFrequency() + " frequency.");
		//		System.out.println("\n" + mostCommonWord + "  " +" has best jaccard similarity with quotes contating: " + frequencyMap.get(mostCommonWord).getFrequency() + " frequency.");
		//**************************************************************************************
		// Clear mostCommonWord synonyms from the map
		System.out.println("\nBefore:");
		printTreeMap(tempFrequencyMap);
		tempFrequencyMap = clearMostCommonWordSyns(tempFrequencyMap, mostCommonWord);
		System.out.print("\n***After:");
		printTreeMap(tempFrequencyMap);

		// Iterate through frequencyMap and get Jaccard similarity with every other word 
		String jaccardWord = jaccardSimilarQuoteWord(tempFrequencyMap, mostCommonWord);
		//		System.out.println("\n\nJaccard Word:" + jaccardWord + " with " + frequencyMap.get(jaccardWord).getFrequency() + " frequency.");
		//		System.out.println("\n\nJaccard Word:" + jaccardWord);
		printWordQuotes(frequencyMap.get(jaccardWord).getMap());

		// Enhancement:
		// Remove mostCommonWord from tempFrequencyMap
		// Repeat form line 139
	}

	static private String jaccardSimilarQuoteWord(TreeMap<String, Word> tempMap, String commonWord) {
		System.out.println("\n\n****");
		HashMap<Integer, String> commonMap = tempMap.get(commonWord).getMap();

		int highestJaccardSimilarity = 0;
		String jaccardWord = commonWord;

		// Find highest similarity word from the list
		for (Entry<String, Word> entry : tempMap.entrySet())
		{
			if (entry.getKey().compareTo(commonWord) != 0) {
				//				System.out.print("\nNew Word:" + entry.getKey() + " - ");
				int similarity = 0;

				// Find highest number of common quotes between every word and commonWord
				for (Entry<Integer, String> quote : entry.getValue().getMap().entrySet())
				{
					//			    	System.out.print(quote.getKey() + ", ");
					// The quotes intersection between the highest frequency word and above word are most famous quotes
					for (Entry<Integer, String> commonQuote : commonMap.entrySet())
					{
						if(commonQuote.getKey() == quote.getKey()){
							similarity++;
							break;
						}
					}
				}

				if(similarity > highestJaccardSimilarity) {
					highestJaccardSimilarity = similarity;
					jaccardWord = entry.getKey();
					//					System.out.println("  ##Current Jaccard Word:"+ jaccardWord + " with similarity " + highestJaccardSimilarity);
					System.out.println("Best jaccard similarity with quotes contating "+ " : "+ jaccardWord + " with similarity :" + highestJaccardSimilarity);
				}
			}
		}
		//		System.out.println("  ##Current Jaccard Word:"+ jaccardWord + " with similarity " + highestJaccardSimilarity);
		return jaccardWord;
	}

	static private TreeMap<String, Word> clearMostCommonWordSyns(TreeMap<String, Word> tempMap, String commonWord) {
		List<String> syns = getSynonyms(commonWord);

		for(String syn : syns) {
			if(syn.compareTo(commonWord) != 0) {
				tempMap.remove(syn);
			}
		}

		return tempMap;
	}

	static private String findHighestFrequencyWord(TreeMap<String, Word> fMap) {
		int highestValue = 0;
		String highestFreqWord = null;

		for (Entry<String, Word> entry : fMap.entrySet())
		{
			//		    System.out.print(entry.getKey() + "["+ entry.getValue().getFrequency() +"]: ");

			if(entry.getValue().getFrequency() > highestValue) {
				highestValue = entry.getValue().getFrequency();
				highestFreqWord = entry.getKey();
			}
		}
		return highestFreqWord;
	}

	static private List<String> getSynonyms(String word) {
		final List<String> synomyns = new ArrayList<String>();

		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(word);
		//  Display the word forms and definitions for synsets retrieved
		if (synsets.length > 0)
		{
			//			System.out.println("The following synsets contain '" + word + "' or a possible base form " + "of that text:");
			for (int i = 0; i < synsets.length; i++)
			{
				String[] wordForms = synsets[i].getWordForms();
				for (int j = 0; j < wordForms.length; j++)
				{
					if(!synomyns.contains(wordForms[j]) && !wordForms[j].contains(" ")) {
						synomyns.add(wordForms[j]);
					}
				}
			}

			//			for (String syn : synomyns) {
			//				System.out.print(syn);
			//				System.out.print(",");
			//			}
		}
		else
		{
			//			System.err.println("No synsets exist that contain " + "the word form '" + word + "'");
			return null;
		}

		return synomyns;
	}

	static private void printTreeMap(TreeMap<String, Word> tMap) {
		for (Entry<String, Word> entry : tMap.entrySet())
		{
			System.out.println();
			System.out.print(entry.getKey() + "["+ entry.getValue().getFrequency() +"]: ");
			for (Entry<Integer, String> quote : entry.getValue().getMap().entrySet())
			{
				System.out.print(quote.getKey() + ", ");
				//		    	System.out.print(quote.getKey() + ", " + quote.getValue() + newLine );
			}
		}
	}

	static private void printWordQuotes(HashMap<Integer, String> wordMap) {
		System.out.println("\nMost famous Quotes:");
		int count =0;
		for (Entry<Integer, String> quote : wordMap.entrySet())
		{
			System.out.print(quote.getValue() + "\n");
			//	    	System.out.println(" "+quote.getKey() + " : " + quote.getValue() );
			count++;
			if (count <=10 ){
				//				System.out.print(quote.getValue() + "\n");

			}
		}
		System.out.println("\nTotal Occurance: "+ count);
	}
}
