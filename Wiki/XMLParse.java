// javac -cp .;wikixmlj-r43.jar;bzip2.jar;joda-time.jar;stanford-corenlp-3.5.0.jar;stanford-corenlp-3.5.0-models.jar;xom.jar XMLParse.java
// java -Xmx3g -cp .;wikixmlj-r43.jar;bzip2.jar;joda-time.jar;stanford-corenlp-3.5.0.jar;stanford-corenlp-3.5.0-models.jar;xom.jar XMLParse

import edu.jhu.nlp.wikipedia.*;
import java.util.*;
import java.io.*;
//import org.tartarus.snowball.ext.englishStemmer; libstemmer.jar
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class XMLParse
{
	static int numDocs = 0;
	static Map<String, MutableInt>	doc;
	static StanfordLemmatizer slem;
	
	static String[] stopwords = { "aaa","aaron","abc","able","about","above","accessdate","according","accordingly","across","actually","adam","after","afterwards","again","against","aint","alan","albert","alexander","alice","align","all","allow","allows","almost","alone","along","already","also","although","always","am","amanda","amber","among","amongst","amy","an","and","andrea","andrew","angela","ann","anna","another","anthony","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","arent","around","arthur","as","ashley","aside","ask","asking","associated","at","austin","available","away","awfully","barbara","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","benjamin","beside","besides","best","better","betty","between","beverly","beyond","bgcolor","billy","bob","bobby","both","brandon","brenda","brian","brief","brittany","bruce","bryan","but","by","came","can","cannot","cant","carl","carol","carolyn","catherine","cause","causes","cccccc","ccffcc","cellpadding","cellspacing","center","certain","certainly","changes","charles","cheryl","christian","christina","christine","christopher","cite","clearly","cmon","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","couldnt","course","crystal","cs","currently","cynthia","daniel","danielle","date","david","deborah","debra","definitely","denise","dennis","described","despite","diana","diane","did","didnt","different","do","does","doesnt","doi","doing","donald","done","donna","dont","doris","dorothy","douglas","down","downwards","during","dylan","each","edu","edward","eg","eight","either","elizabeth","else","elsewhere","emily","emma","enough","entirely","eric","especially","et","etc","ethan","eugene","evelyn","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","far","few","ffc","fff","ffffff","fifth","file","first","five","followed","following","follows","footnote","for","former","formerly","forth","four","frances","frank","from","further","furthermore","gary","george","gerald","get","gets","getting","given","gives","gloria","go","goes","going","gone","got","gotten","grace","greetings","gregory","had","hadnt","hannah","happens","hardly","harold","harry","has","hasnt","have","havent","having","he","heather","height","helen","hello","help","hence","henry","her","here","hereafter","hereby","herein","heres","hereupon","hers","herself","hes","hi","him","himself","his","hither","hopefully","how","howard","howbeit","however","id","ie","if","ignored","ill","im","image","imagesize","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","infobox","inner","insofar","instead","into","inward","is","isnt","it","itd","itll","its","its","itself","ive","jack","jacob","jacqueline","james","jane","janet","janice","jason","jean","jeffrey","jennifer","jeremy","jerry","jesse","jessica","joan","joe","john","johnny","jonathan","jordan","jose","joseph","joshua","joyce","jpg","juan","judith","judy","julia","julie","just","justin","karen","katherine","kathleen","kathryn","kathy","keep","keeps","keith","kelly","kenneth","kept","kevin","kimberly","know","known","knows","kyle","larry","last","latd","lately","later","latm","latns","lats","latter","latterly","laura","lauren","lawrence","least","left","less","lest","let","lets","like","liked","likely","linda","lisa","little","location","longd","longEW","longm","look","looking","looks","lori","louis","ltd","madison","mainly","many","mapsize","margaret","maria","marie","marilyn","mark","martha","mary","matthew","may","maybe","me","mean","meanwhile","megan","melissa","merely","michael","michelle","might","mildred","more","moreover","most","mostly","much","must","my","myself","name","namely","nancy","nathan","nbsp","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nicholas","nicole","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","obviously","of","off","often","oh","ok","okay","old","olivia","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","page","pamela","particular","particularly","patricia","patrick","paul","per","perhaps","peter","philip","phillip","placed","please","plus","png","possible","presumably","probably","provides","publisher","que","quite","qv","rachel","ralph","randy","rather","raymond","rd","re","really","reasonably","rebecca","ref","reference","reflist","regarding","regardless","regards","relatively","respectively","richard","right","robert","roger","ronald","rose","roy","russell","ruth","ryan","said","samantha","same","samuel","sandra","sara","sarah","saw","say","saying","says","score","scott","sean","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","sharon","shawn","she","shirley","should","shouldnt","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","stephanie","stephen","steven","still","style","sub","such","sup","sure","susan","take","taken","tammy","tell","tends","teresa","terry","th","than","thank","thanks","thanx","that","thats","thats","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","theres","theres","theresa","thereupon","these","they","theyd","theyll","theyre","theyve","think","third","this","thomas","thorough","thoroughly","those","though","three","through","throughout","thru","thus","tiffany","time","timothy","title","title","to","together","too","took","toward","towards","tried","tries","truly","try","trying","ts","twice","two","tyler","un","under","unfortunately","unless","unlikely","until","unto","up","upon","url","us","use","used","useful","uses","using","usually","value","various","very","via","victoria","vincent","virginia","viz","vs","walter","want","wants","was","wasnt","way","wayne","we","wed","welcome","well","well","went","were","were","werent","weve","what","whatever","whats","when","whence","whenever","where","whereafter","whereas","whereby","wherein","wheres","whereupon","wherever","whether","which","while","whither","who","whoever","whole","whom","whos","whose","why","width","wiki","wikipedia","wikitable","will","william","willie","willing","wish","with","within","without","wonder","wont","would","wouldnt","year","yes","yet","you","youd","youll","your","youre","yours","yourself","yourselves","youve","zachary","zero" };
					//states = { "Alabama","Alaska","Arizona","Arkansas","California","Colorado","Connecticut","Delaware","Florida","Georgia","Hawaii","Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine","Maryland","Massachusetts","Michigan","Minnesota","Mississippi","Missouri","Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York","North Carolina","North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania","Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah","Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming" };
		// 543 stopwords used by MySQL + nbsp + infobox + aaa + abc + align + score + date + left + center + wiki + wikipedia + wikitable + cccccc + ccffcc cellpadding cellspacing
		//stopwords = { "a's","aaa","abc","able","about","above","according","accordingly","across","actually","after","afterwards","again","against","ain't","align","all","allow","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","aren't","around","as","aside","ask","asking","associated","at","available","away","awfully","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","c'mon","c's","came","can","can't","cannot","cant","cause","causes","cccccc","ccffcc","center","cellpadding","cellspacing""certain","certainly","changes","clearly","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","couldn't","course","currently","date","definitely","described","despite","did","didn't","different","do","does","doesn't","doing","don't","done","down","downwards","during","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","from","further","furthermore","get","gets","getting","given","gives","go","goes","going","gone","got","gotten","greetings","had","hadn't","happens","hardly","has","hasn't","have","haven't","having","he","he's","hello","help","hence","her","here","here's","hereafter","hereby","herein","hereupon","hers","herself","hi","him","himself","his","hither","hopefully","how","howbeit","however","i'd","i'll","i'm","i've","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","infobox","inner","insofar","instead","into","inward","is","isn't","it","it'd","it'll","it's","its","itself","just","keep","keeps","kept","know","known","knows","last","lately","later","latter","latterly","least","left","less","lest","let","let's","like","liked","likely","little","look","looking","looks","ltd","mainly","many","may","maybe","me","mean","meanwhile","merely","might","more","moreover","most","mostly","much","must","my","myself","name","namely","nbsp","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","particular","particularly","per","perhaps","placed","please","plus","possible","presumably","probably","provides","que","quite","qv","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","said","same","saw","say","saying","says","score","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","she","should","shouldn't","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t's","take","taken","tell","tends","th","than","thank","thanks","thanx","that","that's","thats","the","their","theirs","them","themselves","then","thence","there","there's","thereafter","thereby","therefore","therein","theres","thereupon","these","they","they'd","they'll","they're","they've","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","to","together","too","took","toward","towards","tried","tries","truly","try","trying","twice","two","un","under","unfortunately","unless","unlikely","until","unto","up","upon","us","use","used","useful","uses","using","usually","value","various","very","via","viz","vs","want","wants","was","wasn't","way","we","we'd","we'll","we're","we've","welcome","well","went","were","weren't","what","what's","whatever","when","whence","whenever","where","where's","whereafter","whereas","whereby","wherein","whereupon","wherever","whether","which","while","whither","who","who's","whoever","whole","whom","whose","why","wiki","wikipedia","wikitable","will","willing","wish","with","within","without","won't","wonder","would","wouldn't","yes","yet","you","you'd","you'll","you're","you've","your","yours","yourself","yourselves","zero" },
		//wikipedia = { "accessdate","cite","imagesize","location","mapsize","page","publisher","title","latd","latm","lats","latns","longd","longm","longEW","footnote","doi","ref","title","file","jpg","png","ffc","fff","ffffff","url","width","height","reflist","reference","image" },
		//commonNames = { "aaron","adam","alan","albert","alexander","alice","amanda","amber","amy","andrea","andrew","angela","ann","anna","anthony","arthur","ashley","austin","barbara","benjamin","betty","beverly","billy","bob","bobby","brandon","brenda","brian","brittany","bruce","bryan","carl","carol","carolyn","catherine","charles","cheryl","christian","christina","christine","christopher","crystal","cynthia","daniel","danielle","david","deborah","debra","denise","dennis","diana","diane","donald","donna","doris","dorothy","douglas","dylan","edward","elizabeth","emily","emma","eric","ethan","eugene","evelyn","frances","frank","gary","george","gerald","gloria","grace","gregory","hannah","harold","harry","heather","helen","henry","howard","jack","jacob","jacqueline","james","jane","janet","janice","jason","jean","jeffrey","jennifer","jeremy","jerry","jesse","jessica","joan","joe","john","johnny","jonathan","jordan","jose","joseph","joshua","joyce","juan","judith","judy","julia","julie","justin","karen","katherine","kathleen","kathryn","kathy","keith","kelly","kenneth","kevin","kimberly","kyle","larry","laura","lauren","lawrence","linda","lisa","lori","louis","madison","margaret","maria","marie","marilyn","mark","martha","mary","matthew","megan","melissa","michael","michelle","mildred","nancy","nathan","nicholas","nicole","olivia","pamela","patricia","patrick","paul","peter","philip","phillip","rachel","ralph","randy","raymond","rebecca","richard","robert","roger","ronald","rose","roy","russell","ruth","ryan","samantha","samuel","sandra","sara","sarah","scott","sean","sharon","shawn","shirley","stephanie","stephen","steven","susan","tammy","teresa","terry","theresa","thomas","tiffany","timothy","tyler","victoria","vincent","virginia","walter","wayne","william","willie","zachary" };
	static Random r = new Random();	
	
	public static void main(String[] args)
	{
		doc  = new HashMap<String, MutableInt>();
		PrintWriter out1, out2, out3, out4;
		slem = new StanfordLemmatizer();
		
		try
		{
			out1 = new PrintWriter("words.txt");
			out2 = new PrintWriter("numbers.txt");
			out3 = new PrintWriter("titles.txt");
			out4 = new PrintWriter("categories.txt");
		
			//englishStemmer stemmer = new englishStemmer();
			
			WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser("E:\\Current\\Neural Network\\Project\\enwiki-latest-pages-articles.xml\\enwiki-latest-pages-articles.xml");
			try
			{
				wxsp.setPageCallback(new PageCallbackHandler() { 
					public void process(WikiPage page)
					{
						boolean goodTitle = true;
						String title = page.getTitle();
						if(page.isRedirect())						goodTitle = false;
						else if(page.isStub())						goodTitle = false;
						else if(title.contains("(disambiguation)"))	goodTitle = false;
						else if(title.contains("File:"))			goodTitle = false;
						else if(title.contains("Wikipedia:"))		goodTitle = false;
						else if(title.contains("Template:"))		goodTitle = false;
						else if(title.contains("Book:"))			goodTitle = false;
						else if(title.contains("Portal:"))			goodTitle = false;
						else if(title.contains("Draft:"))			goodTitle = false;
						else if(title.contains("List of "))			goodTitle = false;
						/*else if
						{
							for(int i = 0; i < states.length; i++)
							{
								if(title.contains(states[i])
								{
									goodTitle = false;
									break;
								}
							}
						}*/
						
						//String words[] = page.getText().toLowerCase().split("[^a-zA-Z']+"); // Splits by all non-ascii chars and not on - or '
						
						if(goodTitle) // If page is not a stub or redirect
						{
							String d =  page.getText().replaceAll("\uFFFD", "").toLowerCase();
							String text[] = d.split("[^a-zA-Z']+"); // Splits by all non-ascii chars and not on - or '
							if(text.length > 250)
							{
								if(r.nextDouble() <= 0.01) // ~460 MB
								{
									lemmatize(d);
									//stem(text);
									for(Map.Entry<String, MutableInt> entry : doc.entrySet())
									{
										out1.print(entry.getKey()+ "\t");
										out2.print(entry.getValue().value + "\t");
									}
									out1.print("\n");
									out2.print("\n");
									out3.println(title);
									Vector<String> cats = page.getCategories();
									for(String s : cats)
									{
										out4.print(s + "\t");
									}
									out4.print("\n");
									
									doc.clear();
								}
							}
						}
					}
				});
			
			wxsp.parse();
			out1.close();
			out2.close();
			out3.close();
			out4.close();
			} catch(Exception e) {	e.printStackTrace(); }
			System.out.println("Docs: " + numDocs);
		}
		catch (Exception e) { e.printStackTrace(); }
	}
	
	public static void lemmatize(String d)
	{
		ArrayList<String> words = slem.lemmatize(d);
		numDocs++;
		for(String word : words)
		{
			if(!word.contains("-") && !word.contains("_")) // This is most likely going to be CoreNLP parenthesis or wikipedia tables.
			{
				String temp = word.replaceAll("[^a-zA-Z']+", "");
				if(temp.length() >= 3) // Lots of garbage below this threshold
				{
					if(Arrays.binarySearch(stopwords, temp) < 0)
					{
						//System.out.println(temp);
						MutableInt count = doc.get(temp);
						if (count == null)	doc.put(temp, new MutableInt());
						else				count.increment();
					}
				}
			}
		}
	}
	/*
	public static void stem(String[] words)
	{
		String words[] = page.getText().toLowerCase().split("[^a-zA-Z']+"); // Splits by all non-ascii chars and not on - or '
						
		for(int i = 0; i < words.length; i++)
		{
			String word = words[i];
			if(Arrays.binarySearch(stopwords, word) < 0)
			{
				stemmer.setCurrent(word);
				if(stemmer.stem())
				{
					word = stemmer.getCurrent();
					if(word.length() >= 3)
					{
						MutableInt count = doc.get(word);
						if (count == null)	doc.put(word, new MutableInt());
						else				count.increment();
					}
				}
			}
		}
	}*/
	
	static class MutableInt
	{
		int value = 1; // note that we start at 1 since we're counting
		public void increment () { ++value;      }
		public int  get ()       { return value; }
	}
	
	static class StanfordLemmatizer
	{
		protected StanfordCoreNLP pipeline;
		
		public StanfordLemmatizer() {
			// Create StanfordCoreNLP object properties, with POS tagging
			// (required for lemmatization), and lemmatization
			Properties props;
			props = new Properties();
			props.put("annotators", "tokenize, ssplit, pos, lemma");
			this.pipeline = new StanfordCoreNLP(props);
		}
		
		public ArrayList<String> lemmatize(String documentText)
		{
			ArrayList<String> lemmas = new ArrayList<String>();
			// Create an empty Annotation just with the given text
			Annotation document = new Annotation(documentText);
			// run all Annotators on this text
			this.pipeline.annotate(document);
			// Iterate over all of the sentences found
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			for(CoreMap sentence: sentences) {
				// Iterate over all tokens in a sentence
				for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
					// Retrieve and add the lemma for each word into the
					// list of lemmas
					lemmas.add(token.get(LemmaAnnotation.class));
				}
			}
			return lemmas;
		}
	}
}