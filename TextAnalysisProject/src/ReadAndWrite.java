/**
 * This class provides source code to clean up data and preprocess to MEMEMiners need
 * @author Pratiksha Pai
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
//import java.lang.Object;
//import java.util.LinkedHashSet;
//import java.util.Set;




public class ReadAndWrite {
	final static String FILE_NAME = "/Users/pk/Desktop/inputdata/239Raw/quotes_2009-03.txt";
	final static String OUTPUT_FILE_NAME = "/Users/pk/Desktop/inputdata/239Tuned/quotes_2009-03_op.txt";


	public static void main(String[] args) throws IOException {
		ReadAndWrite rw = new ReadAndWrite();
		rw.ReadWrite();

	}

	public void ReadWrite() throws IOException
	{
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try {
			int dataLimit = 100000;
			bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
			bufferedWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE_NAME));
			String s = "";
			final int cLimit = 100;
			StringTokenizer parser;

			//			Set<String> stopWords = new LinkedHashSet<String>();
			//			BufferedReader br = new BufferedReader(new FileReader("stopwords.txt"));
			//			for(String line;(line = br.readLine()) != null;)
			//				stopWords.add(line.trim());
			//			br.close();

			System.out.println("Reading...");
			int opLines = 0;
			while ((s = bufferedReader.readLine()) != null && opLines < dataLimit) {
				if(s.startsWith("Q")) { // || s.startsWith("T")) {
					parser = new StringTokenizer(s, " \t\n\r\f.,;:!?'");
					if (parser.countTokens() < cLimit) {
						opLines++;
						bufferedWriter.write(s.substring(2));
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// close without throwing exception
			bufferedReader.close();
			bufferedWriter.close();
		}
	}



}
