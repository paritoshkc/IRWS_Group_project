package com.irws.tcd.parser;


import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.apache.lucene.analysis.TokenStream;

/* 
* Templete: Copy of Lucene English Stop Analyser
*/

//Novice Java Developer code. Please consider.

public class FBISPrivateStopAnalyser extends StopwordAnalyzerBase{
	  /** An unmodifiable set containing some common English words that are not usually useful
	   for searching.*/
	  public static final CharArraySet ENGLISH_STOP_WORDS_SET;

	  static {
	    final List<String> stopWords = Arrays.asList(
	        "a", "an", "and", "are", "as", "at", "be", "but", "by",
	        "for", "if", "in", "into", "is", "it",
	        "no", "not", "of", "on", "or", "such",
	        "that", "the", "their", "then", "there", "these",
	        "they", "this", "to", "was", "will", "with", 
	        "because","you'll","it's",
	        "a", "about", "above", "after", "again", "against", "all", 
	        "am", "an", "and", "any", "are", "as", "at", "be", "because", 
	        "been", "before", "being", "below", "between", "both", "but", 
	        "by", "could", "did", "do", "does", "doing", "down", "during", 
	        "each", "few", "for", "from", "further", "had", "has", "have", 
	        "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", 
	        "hers", "herself", "him", "himself", "his", "how", "how's", "i", 
	        "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "it", "it's", 
	        "its", "itself", "let's", "me", "more", "most", "my", "myself", 
	        "nor", "of", "on", "once", "only", "or", "other", "ought", "our", 
	        "ours", "ourselves", "out", "over", "own", "same", "she", "she'd", 
	        "she'll", "she's", "should", "so", "some", "such", "than", "that", 
	        "that's", "the", "their", "theirs", "them", "themselves", "then", 
	        "there", "there's", "these", "they", "they'd", "they'll", "they're", 
	        "they've", "this", "those", "through", "to", "too", "under", "until", 
	        "up", "very", "was", "we", "we'd", "we'll", "we're", "we've", "were", 
	        "what", "what's", "when", "when's", "where", "where's", "which", "while", 
	        "who", "who's", "whom", "why", "why's", "with", "would", "you", "you'd", 
	        "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves",
	        "'ll","'tis","'twas","'ve","10","39","a","a's","able","ableabout","about",
         "above","abroad","abst","accordance","according","accordingly","across","act","actually",
         "ad","added","adj","adopted","ae","af","affected","affecting","affects","after","afterwards",
         "ag","again","against","ago","ah","ahead","ai","ain't","aint","al","all","allow","allows","almost",
         "alone","along","alongside","already","also","although","always","am","amid","amidst","among","amongst",
         "amoungst","amount","an","and","announce","another","any","anybody","anyhow","anymore","anyone","anything",
         "anyway","anyways","anywhere","ao","apart","apparently","appear","appreciate","appropriate","approximately",
         "aq","ar","are","area","areas","aren","aren't","arent","arise","around","arpa","as","aside","ask","asked","asking",
         "asks","associated","at","au","auth","available","aw","away","awfully","az","b","ba","back","backed","backing","backs",
         "backward","backwards","bb","bd","be","became","because","become","becomes","becoming","been","before","beforehand","began",
         "begin","beginning","beginnings","begins","behind","being","beings","believe","below","beside","besides","best",
         "better","between","beyond","bf","bg","bh","bi","big","bill","billion","biol","bj","bm","bn","bo","both","bottom",
         "br","brief","briefly","bs","bt","but","buy","bv","bw","by","bz","c","c'mon","c's","ca","call","came","can","can't",
         "cannot","cant","caption","case","cases","cause","causes","cc","cd","certain","certainly","cf","cg","ch","changes","ci",
         "ck","cl","clear","clearly","click","cm","cmon","cn","co","co.","com","come","comes","computer","con","concerning",
         "consequently","consider","considering","contain","containing","contains","copy","corresponding","could","could've",
         "couldn","couldn't","couldnt","course","cr","cry","cs","cu","currently","cv","cx","cy","cz","d","dare","daren't","darent",
         "date","de","dear","definitely","describe","described","despite","detail","did","didn","didn't","didnt","differ",
         "different","differently","directly","dj","dk","dm","do","does","doesn","doesn't","doesnt","doing","don","don't",
         "done","dont","doubtful","down","downed","downing","downs","downwards","due","during","dz","e","each","early","ec",
         "ed","edu","ee","effect","eg","eh","eight","eighty","either","eleven","else","elsewhere","empty","end","ended",
         "ending","ends","enough","entirely","er","es","especially","et","et-al","etc","even","evenly","ever","evermore",
         "every","everybody","everyone","everything","everywhere","ex","exactly","example","except","f","face","faces",
         "fact","facts","fairly","far","farther","felt","few","fewer","ff","fi","fifteen","fifth","fifty","fify","fill",
         "find","finds","fire","first","five","fix","fj","fk","fm","fo","followed","following","follows","for","forever",
         "former","formerly","forth","forty","forward","found","four","fr","free","from","front","full","fully","further",
         "furthered","furthering","furthermore","furthers","fx","g","ga","gave","gb","gd","ge","general","generally","get",
         "gets","getting","gf","gg","gh","gi","give","given","gives","giving","gl","gm","gmt","gn","go","goes","going","gone",
         "good","goods","got","gotten","gov","gp","gq","gr","great","greater","greatest","greetings","group","grouped",
         "grouping","groups","gs","gt","gu","gw","gy","h","had","hadn't","hadnt","half","happens","hardly","has","hasn",
         "hasn't","hasnt","have","haven","haven't","havent","having","he","he'd","he'll","he's","hed","hell","hello","help",
         "hence","her","here","here's","hereafter","hereby","herein","heres","hereupon","hers","herself","herse�","hes","hi",
         "hid","high","higher","highest","him","himself","himse�","his","hither","hk","hm","hn","home","homepage","hopefully",
         "how","how'd","how'll","how's","howbeit","however","hr","ht","htm","html","http","hu","hundred","i","i'd","i'll",
         "i'm","i've","i.e.","id","ie","if","ignored","ii","il","ill","im","immediate","immediately","importance",
         "important","in","inasmuch","inc","inc.","indeed","index","indicate","indicated","indicates","information",
         "inner","inside","insofar","instead","int","interest","interested","interesting","interests","into","invention",
         "inward","io","iq","ir","is","isn","isn't","isnt","it","it'd","it'll","it's","itd","itll","its","itself","itse�",
         "ive","j","je","jm","jo","join","jp","just","k","ke","keep","keeps","kept","keys","kg","kh","ki","kind","km","kn",
         "knew","know","known","knows","kp","kr","kw","ky","kz","l","la","large","largely","last","lately","later","latest",
         "latter","latterly","lb","lc","least","length","less","lest","let","let's","lets","li","like","liked","likely","likewise",
         "line","little","lk","ll","long","longer","longest","look","looking","looks","low","lower","lr","ls","lt","ltd","lu",
         "lv","ly","m","ma","made","mainly","make","makes","making","man","many","may","maybe","mayn't","maynt","mc",
         "md","me","mean","means","meantime","meanwhile","member","members","men","merely","mg","mh","microsoft",
         "might","might've","mightn't","mightnt","mil","mill","million","mine","minus","miss","mk","ml","mm","mn",
         "mo","more","moreover","most","mostly","move","mp","mq","mr","mrs","ms","msie","mt","mu","much","mug",
         "must","must've","mustn't","mustnt","mv","mw","mx","my","myself","myse�","mz","n","na","name",
         "namely","nay","nc","nd","ne","near","nearly","necessarily","necessary","need","needed","needing",
         "needn't","neednt","needs","neither","net","netscape","never","neverf","neverless","nevertheless","new","newer",
         "newest","next","nf","ng","ni","nine","ninety","nl","no","no-one","nobody","non","none","nonetheless","noone","nor",
         "normally","nos","not","noted","nothing","notwithstanding","novel","now","nowhere","np","nr","nu","null",
         "number","numbers","nz","o","obtain","obtained","obviously","of","off","often","oh","ok","okay","old","older",
         "oldest","om","omitted","on","once","one","one's","ones","only","onto","open","opened","opening","opens","opposite",
         "or","ord","order","ordered","ordering","orders","org","other","others","otherwise","ought","oughtn't","oughtnt",
         "our","ours","ourselves","out","outside","over","overall","owing","own","p","pa","page","pages","part","parted",
         "particular","particularly","parting","parts","past","pe","per","perhaps","pf","pg","ph","pk","pl","place",
         "placed","places","please","plus","pm","pmid","pn","point","pointed","pointing","points","poorly","possible",
         "possibly","potentially","pp","pr","predominantly","present","presented","presenting","presents","presumably",
         "previously","primarily","probably","problem","problems","promptly","proud","provided","provides","pt","put",
         "puts","pw","py","q","qa","que","quickly","quite","qv","r","ran","rather","rd","re","readily","really","reasonably",
         "recent","recently","ref","refs","regarding","regardless","regards","related","relatively","research","reserved",
         "respectively","resulted","resulting","results","right","ring","ro","room","rooms","round","ru","run","rw","s",
         "sa","said","same","saw","say","saying","says","sb","sc","sd","se","sec","second","secondly","seconds","section",
         "see","seeing","seem","seemed","seeming","seems","seen","sees","self","selves","sensible","sent","serious","seriously",
         "seven","seventy","several","sg","sh","shall","shan't","shant","she","she'd","she'll","she's","shed","shell","shes",
         "should","should've","shouldn","shouldn't","shouldnt","show","showed","showing","shown","showns","shows","si","side",
         "sides","significant","significantly","similar","similarly","since","sincere","site","six","sixty","sj","sk","sl",
         "slightly","sm","small","smaller","smallest","sn","so","some","somebody","someday","somehow","someone","somethan",
         "something","sometime","sometimes","somewhat","somewhere","soon","sorry","specifically","specified","specify",
         "specifying","sr","st","state","states","still","stop","strongly","su","sub","substantially","successfully",
         "such","sufficiently","suggest","sup","sure","sv","sy","system","sz","t","t's","take","taken","taking","tc","td",
         "tell","ten","tends","test","text","tf","tg","th","than","thank","thanks","thanx","that","that'll","that's",
         "that've","thatll","thats","thatve","the","their","theirs","them","themselves","then","thence","there",
         "there'd","there'll","there're","there's","there've","thereafter","thereby","thered","therefore",
         "therein","therell","thereof","therere","theres","thereto","thereupon","thereve","these","they","they'd",
         "they'll","they're","they've","theyd","theyll","theyre","theyve","thick","thin","thing","things","think",
         "thinks","third","thirty","this","thorough","thoroughly","those","thou","though","thoughh","thought","thoughts",
         "thousand","three","throug","through","throughout","thru","thus","til","till","tip","tis","tj","tk","tm","tn","to",
         "today","together","too","took","top","toward","towards","tp","tr","tried","tries","trillion","truly","try",
         "trying","ts","tt","turn","turned","turning","turns","tv","tw","twas","twelve","twenty","twice","two","tz",
         "u","ua","ug","uk","um","un","under","underneath","undoing","unfortunately","unless","unlike","unlikely","until",
         "unto","up","upon","ups","upwards","us","use","used","useful","usefully","usefulness","uses","using","usually",
         "uucp","uy","uz","v","va","value","various","vc","ve","versus","very","vg","vi","via","viz","vn","vol","vols","vs",
         "vu","w","want","wanted","wanting","wants","was","wasn","wasn't","wasnt","way","ways","we","we'd","we'll","we're",
         "we've","web","webpage","website","wed","welcome","well","wells","went","were","weren","weren't","werent","weve",
         "wf","what","what'd","what'll","what's","what've","whatever","whatll","whats","whatve","when","when'd","when'll",
         "when's","whence","whenever","where","where'd","where'll","where's","whereafter","whereas","whereby","wherein",
         "wheres","whereupon","wherever","whether","which","whichever","while","whilst","whim","whither","who","who'd",
         "who'll","who's","whod","whoever","whole","wholl","whom","whomever","whos","whose","why","why'd","why'll","why's",
         "widely","width","will","willing","wish","with","within","without","won","won't","wonder","wont","words","work",
         "worked","working","works","world","would","would've","wouldn","wouldn't","wouldnt","ws","www","x","y","ye","year",
         "years","yes","yet","you","you'd","you'll","you're","you've","youd","youll","young","younger","youngest",
         "your","youre","yours","yourself","yourselves","youve","yt","yu","z","za","zero","zm","zr"
	        
	    );
	    final CharArraySet stopSet = new CharArraySet(stopWords, false);
	    ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
	  }

	  private final CharArraySet stemExclusionSet;
	   
	  /**
	   * Returns an unmodifiable instance of the default stop words set.
	   * @return default stop words set.
	   */
	  public static CharArraySet getDefaultStopSet(){
	    return ENGLISH_STOP_WORDS_SET;
	  }

	  /**
	   * Builds an analyzer with the default stop words: {@link #getDefaultStopSet}.
	   */
	  public FBISPrivateStopAnalyser() {
	    this(ENGLISH_STOP_WORDS_SET);
	  }
	  
	  /**
	   * Builds an analyzer with the given stop words.
	   * 
	   * @param stopwords a stopword set
	   */
	  public FBISPrivateStopAnalyser(CharArraySet stopwords) {
	    this(stopwords, CharArraySet.EMPTY_SET);
	  }

	  /**
	   * Builds an analyzer with the given stop words. If a non-empty stem exclusion set is
	   * provided this analyzer will add a {@link SetKeywordMarkerFilter} before
	   * stemming.
	   * 
	   * @param stopwords a stopword set
	   * @param stemExclusionSet a set of terms not to be stemmed
	   */
	  public FBISPrivateStopAnalyser(CharArraySet stopwords, CharArraySet stemExclusionSet) {
	    super(stopwords);
	    this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
	  }

	  /**
	   * Creates a
	   * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}
	   * which tokenizes all the text in the provided {@link Reader}.
	   * 
	   * @return A
	   *         {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}
	   *         built from an {@link StandardTokenizer} filtered with
	   *         {@link EnglishPossessiveFilter},
	   *         {@link LowerCaseFilter}, {@link StopFilter}
	   *         , {@link SetKeywordMarkerFilter} if a stem exclusion set is
	   *         provided and {@link PorterStemFilter}.
	   */
	  @Override
	  protected TokenStreamComponents createComponents(String fieldName) {
		Analyzer  whitespaceanalyzer = new WhitespaceAnalyzer();
	    final Tokenizer source = new StandardTokenizer();
	    TokenStream result = new EnglishPossessiveFilter(source);
	    result = new LowerCaseFilter(result);
	    result = new StopFilter(result, stopwords);
//
	    result = new LowerCaseFilter(result);
	    result = new TrimFilter(result);
	    result = new FlattenGraphFilter(new SynonymGraphFilter(result, createSynMap(), true));
	    result = new SnowballFilter(result, new EnglishStemmer());
	    result = new PorterStemFilter(result);
	    result = new ClassicFilter(result);
//     
	    if(!stemExclusionSet.isEmpty())
	      result = new SetKeywordMarkerFilter(result, stemExclusionSet);
	    result = new PorterStemFilter(result);
	    return new TokenStreamComponents(source, result);
	  }

	  @Override
	  protected TokenStream normalize(String fieldName, TokenStream in) {
	    return new LowerCaseFilter(in);
	  }
	  
	  private SynonymMap createSynMap() {
 SynonymMap synMap = new SynonymMap(null, null, 0);
 try {
 	final List<String> countries = Arrays.asList(
 			"Afghanistan","Albania","Algeria","Andorra","Angola","Antigua & Deps","Argentina",
 			"Armenia","Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados",
 			"Belarus","Belgium","Belize","Benin","Bhutan","Bolivia","Bosnia Herzegovina","Botswana",
 			"Brazil","Brunei","Bulgaria","Burkina","Burundi","Cambodia","Cameroon","Canada",
 			"Cape Verde","Central African Rep","Chad","Chile","China","Colombia","Comoros","Congo",
 			"Congo {Democratic Rep}","Costa Rica","Croatia","Cuba","Cyprus","Czech Republic","Denmark",
 			"Djibouti","Dominica","Dominican Republic","East Timor","Ecuador","Egypt","El Salvador",
 			"Equatorial Guinea","Eritrea","Estonia","Ethiopia","Fiji","Finland","France","Gabon","Gambia",
 			"Georgia","Germany","Ghana","Greece","Great Britain","Grenada","Guatemala","Guinea",
 			"Guinea-Bissau","Guyana","Haiti","Honduras","Hungary","Iceland","India","Indonesia","Iran",
 			"Iraq","Ireland {Republic}","Ireland","Israel","Italy","Ivory Coast","Jamaica","Japan",
 			"Jordan","Kazakhstan","Kenya","Kiribati","Korea North","Korea South","Kosovo","Kuwait",
 			"Kyrgyzstan","Laos","Latvia","Lebanon","Lesotho","Liberia","Libya","Liechtenstein","Lithuania",
 			"Luxembourg","Macedonia","Madagascar","Malawi","Malaysia","Maldives","Mali","Malta","Marshall Islands",
 			"Mauritania","Mauritius","Mexico","Micronesia","Moldova","Monaco","Mongolia","Montenegro","Morocco",
 			"Mozambique","Myanmar, {Burma}","Namibia","Nauru","Nepal","Netherlands","New Zealand","Nicaragua",
 			"Niger","Nigeria","Norway","Oman","Pakistan","Palau","Panama","Papua New Guinea","Paraguay","Peru",
 			"Philippines","Poland","Portugal","Qatar","Romania","Russian Federation","Rwanda","St Kitts & Nevis",
 			"St Lucia","Saint Vincent & the Grenadines","Samoa","San Marino","Sao Tome & Principe","Saudi Arabia",
 			"Senegal","Serbia","Seychelles","Sierra Leone","Singapore","Slovakia","Slovenia","Solomon Islands",
 			"Somalia","South Africa","South Sudan","Spain","Sri Lanka","Sudan","Suriname","Swaziland","Sweden",
 			"Switzerland","Syria","Taiwan","Tajikistan","Tanzania","Thailand","Togo","Tonga","Trinidad & Tobago",
 			"Tunisia","Turkey","Turkmenistan","Tuvalu","Uganda","Ukraine","United Arab Emirates","United Kingdom",
 			"United States","Uruguay","Uzbekistan","Vanuatu","Vatican City","Venezuela","Vietnam","Yemen","Zambia",
 			"Zimbabwe","Asia","Africa","North America","South America","Antarctica","Europe","Australia","America",
 			"U.S.","U.K.");

     final SynonymMap.Builder builder = new SynonymMap.Builder(true);
     ListIterator<String> countriesIterator = countries.listIterator();
     
     String country ;
     while(countriesIterator.hasNext()) {
     	country = countriesIterator.next();
         builder.add(new CharsRef("country"), new CharsRef(country), true);
         builder.add(new CharsRef("countries"), new CharsRef(country), true);
     }
     
     synMap = builder.build();
 } catch (Exception e) {
     e.getMessage();
 }
 return synMap;
}

}
