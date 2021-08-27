import java.io.*;
import java.util.*;

public class DocFrequency
{
	static double N = 1;
	
	public static void main(String[] args)
	{
		try
		{
			BufferedReader
				br1 = new BufferedReader(new FileReader("words.txt")),
				br2 = new BufferedReader(new FileReader("numbers.txt")),
				br3,
				br4;
			String
				line1 = br1.readLine(),
				line2 = br2.readLine(),
				line3 = "",
				line4 = "";
			ArrayList<dict> entries = new ArrayList<dict>();
			Map<String, MutableInt> potentialDictionary = new HashMap<String, MutableInt>();
			CustomCompare c = new CustomCompare();
			PrintWriter
				out1 = new PrintWriter("dictionary.txt"),
				out3 = new PrintWriter("training.txt"),
				out2 = new PrintWriter("testing.txt"),
				out5 = new PrintWriter("trainingTitles.txt"),
				out4 = new PrintWriter("testingTitles.txt"),
				out7 = new PrintWriter("trainingCats.txt"),
				out6 = new PrintWriter("testingCats.txt");
			
			while(line1 != null)
			{
				N++;
				String[] counts = line2.split("\t");
				String[] words = line1.split("\t");
				//int total = 0;
				
				for(int i = 0; i < counts.length; i++)
				{
					if(words[i].length() > 1)
					{
						int temp = Integer.parseInt(counts[i]);
						entries.add(new dict(words[i], temp));
						//total += temp;
					}
				}
				// Only consider the top 33% of words by frequency5
				Collections.sort(entries, c);
				int top33 = (int)((double)entries.size() * .33);
				for(int i = 0; i < top33; i++)
				{
					MutableInt count = potentialDictionary.get(entries.get(i).str);
					if (count == null)	potentialDictionary.put(entries.get(i).str, new MutableInt());
				}
				
				entries.clear();
				line1 = br1.readLine();
				line2 = br2.readLine();
			}
			br1.close();
			br2.close();
			
			
			// Loop back through, get true word count of everything in potential dictionary
			
			br1 = new BufferedReader(new FileReader("words.txt"));
			br2 = new BufferedReader(new FileReader("numbers.txt"));
			
			line1 = br1.readLine();
			line2 = br2.readLine();
			
			while(line1 != null)
			{
				String[] counts = line2.split("\t");
				String[] words = line1.split("\t");
				//int total = 0;
				
				for(int i = 0; i < counts.length; i++)
				{
					if(words[i].length() > 1)
					{
						int temp = Integer.parseInt(counts[i]);
						MutableInt count = potentialDictionary.get(words[i]);
						if (count != null)	count.increment();
					}
				}
				
				line1 = br1.readLine();
				line2 = br2.readLine();
			}
			br1.close();
			br2.close();
			
			// Calculate IDF, remove garbage words
			
			ArrayList<dict2> wordHolder = new ArrayList<dict2>();
			for(Map.Entry<String, MutableInt> entry : potentialDictionary.entrySet())
			{
				MutableInt i = entry.getValue();
				i.setIDF();
				wordHolder.add(new dict2(entry.getKey(), i));
			}
			potentialDictionary.clear();
			Collections.sort(wordHolder, new CustomCompare2());
			
			if(wordHolder.size() > 5000)
			{
				wordHolder.subList(5000, wordHolder.size()).clear();
			}
			String[] dictionary = new String[wordHolder.size()];
			for(int i = 0; i < wordHolder.size(); i++)
			{
				dictionary[i] = wordHolder.get(i).str;
			}
			Arrays.sort(dictionary + " blab " + wordHolder.size());
			wordHolder.clear();
			for(int i = 0; i < dictionary.length; i++)
			{
				out1.println(dictionary[i]);
			}
			
			out1.close();
			
			// Go back through all the documents, remove words that are not in the dictionary, find adjusted totals
			
			br1 = new BufferedReader(new FileReader("words.txt"));
			br2 = new BufferedReader(new FileReader("numbers.txt"));
			br3 = new BufferedReader(new FileReader("titles.txt"));
			br4 = new BufferedReader(new FileReader("categories.txt"));
			line1 = br1.readLine();
			line2 = br2.readLine();
			line3 = br3.readLine();
			line4 = br4.readLine();
			Random r = new Random();
			PrintWriter out, outT,outC;
			
			while(line1 != null)
			{
				out = out2;
				outT = out4;
				outC = out6;
				if(r.nextDouble() >= .2)
				{
					out = out3;
					outT = out5;
					outC = out7;
				}
				outT.println(line3);
				outC.println(line4);
				
				String[] counts = line2.split("\t");
				String[] words = line1.split("\t");
				int[] dictCount = new int[dictionary.length + 1];
				int total = 0;
				
				for(int i = 0; i < counts.length; i++)
				{
					String temp = words[i];
					int index = Arrays.binarySearch(dictionary, temp);
					if(index >= 0)
					{
						int j = Integer.parseInt(counts[i]);
						dictCount[index] = j;
						total += j;
					}
				}
				dictCount[dictionary.length] = total;
				
				for(int i = 0; i < dictCount.length; i++)
				{
					out.print(dictCount[i] + "\t");
				}
				out.print("\n");
				
				line1 = br1.readLine();
				line2 = br2.readLine();
				line3 = br3.readLine();
				line4 = br4.readLine();
			}
			br1.close();
			br2.close();
			br3.close();
			out3.close();
			out2.close();
			out4.close();
			out5.close();
			out6.close();
			out7.close();
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	static class CustomCompare implements Comparator<dict>
	{
		public int compare(dict d1, dict d2)
		{
			if(d1.freq < d2.freq)	return -1;
			if(d1.freq > d2.freq)	return 1;
			if(d1.freq == d2.freq)	return 0;
			
			return 0;
		}
	}
	static class CustomCompare2 implements Comparator<dict2>
	{
		public int compare(dict2 d1, dict2 d2)
		{
			if(d1.i.idf <  d2.i.idf)	return -1;
			if(d1.i.idf >  d2.i.idf)	return 1;
			if(d1.i.idf == d2.i.idf)	return 0;
			
			return 0;
		}
	}
	
	static class dict
	{
		String str;
		int freq;
		private dict(String s, int i)
		{
			str = s;
			freq = i;
		}
	}
	static class dict2
	{
		String str;
		MutableInt i;
		private dict2(String s, MutableInt in)
		{
			str = s;
			i = in;
		}
	}
	
	static class MutableInt
	{
		int value = 0; // note that we start at 1 since we're counting
		double idf = -1.0;
		public void increment()		{ ++value;      }
		public void add(int x)		{ value += x;   }
		public int  get()			{ return value; }
		public void setIDF()		{ idf = Math.log(N/(double) value); };
	}
}