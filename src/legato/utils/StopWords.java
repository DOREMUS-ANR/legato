package legato.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import legato.LEGATO;

public class StopWords
{	
	private static final String sw = "stopWords.txt";
	private StopWords(){}

	public static String clean(String content) throws IOException
	{
		String newContent = "";
		HashSet<String> stopWords = getStopWords();
		String words[] = content.split(" ");
		for (String word : words)
		{
			if (!stopWords.contains(word)) 
				newContent = newContent + word + " ";
		}
		newContent = newContent.trim();
		return newContent.replaceAll("\\\\n", "");
	}
	
	public static HashSet<String> getStopWords() throws IOException
	{
		HashSet<String> stopWords = new HashSet<String>();
			BufferedReader in = new BufferedReader(new FileReader(LEGATO.getInstance().getPath() +"store"+ File.separator + sw));
			String word;
			while((word = in.readLine()) != null)
			{
				stopWords.add(word);
			}
			in.close();
		return stopWords;
	}
}
