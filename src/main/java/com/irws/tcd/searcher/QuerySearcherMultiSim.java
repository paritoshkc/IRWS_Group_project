package com.irws.tcd.searcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.irws.tcd.Beans.QueryBean;
import com.irws.tcd.index.Common_Indexer;
import com.irws.tcd.index.CustomAnalyzer;
import com.irws.tcd.parse_Files.ParseQueryFile;
import com.irws.tcd.parser.FBISPrivateBM25;
import com.irws.tcd.parser.FBISPrivateStopAnalyser;
import org.apache.lucene.search.similarities.LMSimilarity;
import org.apache.lucene.search.similarities.LMSimilarity.CollectionModel;
import org.apache.lucene.search.similarities.MultiSimilarity;

public class QuerySearcherMultiSim {
	
    private static  String path="src/main/index/multisim";
    private static String[] replaceWordsList = {"relevant","document","focus","describing","a relevant document", "documents","to be relevant","will","may include","must","identifies","discussed","could","include","mentioning"};
    private static int MAX_RESULTS = 1000;

	public static void main(String[] args) throws IOException, ParseException {

		//Analyzer analyzer = new CustomAnalyzer();
		Analyzer analyzer = new FBISPrivateStopAnalyser();
		Similarity similarity1 = new FBISPrivateBM25();
		Similarity similarity2 = new LMJelinekMercerSimilarity(0.5f);
		//Similarity similarity3 = new ClassicSimilarity();
		//Similarity similarity4 = new LMDirichletSimilarity();
		Similarity sims[] = new Similarity[2];
		sims[0] = similarity1;
		sims[1] = similarity2;
		//sims[2] = similarity3;
		//sims[3] = similarity4;
		MultiSimilarity similarity = new MultiSimilarity(sims);
		
		delIndexFolder(path);		
		
		Common_Indexer.indexFiles(analyzer,similarity,path);
		
		Directory directory = FSDirectory.open(Paths.get(path));
		
		
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		

		HashMap<String, Float> boostedScores = new HashMap<String, Float>();
		boostedScores.put("headline", 0.1f);
		boostedScores.put("text", 0.9f);
		MultiFieldQueryParser parser = new MultiFieldQueryParser (new String[]{"headline", "text"},analyzer,boostedScores);
		
		String queryWriteDoc = "output/multisim/queryResultMultisim";
		isearcher.setSimilarity(similarity);
		querySearch(isearcher, parser,queryWriteDoc);
		System.out.println("Query search completed MultiSimilarity");
		
		queryWriteDoc = "output/multisim/queryResultBM25";
		isearcher.setSimilarity(similarity1);
		querySearch(isearcher, parser,queryWriteDoc);
		System.out.println("Query search completed BM25");
		
		queryWriteDoc = "output/multisim/queryResultLMJe";
		isearcher.setSimilarity(similarity2);
		querySearch(isearcher, parser,queryWriteDoc);
		System.out.println("Query search completed LMJelinekMercerSimilarity");
		
//		queryWriteDoc = "target/queryResultClassic";
//		isearcher.setSimilarity(similarity3);
//		querySearch(isearcher, parser,queryWriteDoc);
//		System.out.println("Query search completed ClassicSimilarity");
//		
//		queryWriteDoc = "target/queryResultLMD";
//		isearcher.setSimilarity(similarity4);
//		querySearch(isearcher, parser,queryWriteDoc);
//		System.out.println("Query search completed LMDirichletSimilarity");
//		
		ireader.close();
		directory.close();
		System.out.println("Search Finished !!!");
	}

	private static void querySearch(IndexSearcher isearcher, MultiFieldQueryParser parser, String queryWriteDoc) throws ParseException, IOException {

		List<QueryBean> queryFiles = ParseQueryFile.parseQueryFile();
		//Write file at given path
		BufferedWriter resultWriter = new BufferedWriter(new FileWriter(queryWriteDoc));
		
		for(int i=0; i<queryFiles.size(); i++)
		{
			BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
			Query titleQuery = parser.parse(QueryParser.escape(queryFiles.get(i).getTitle().trim()));
			Query descriptionQuery = parser.parse(QueryParser.escape(queryFiles.get(i).getDescription().trim()));
			Query narrativeQuery = null;
			String querySentence = "";
			if(queryFiles.get(i).getNarrative().contains(";"))
			{
				String[] sentences = queryFiles.get(i).getNarrative().split(";");
				for(String sentence: sentences)
				{
					
					if(!sentence.toLowerCase().contains("not relevant"))
					{
						if(sentence.contains("."))
						{
							String[] relevantSentences = sentence.split("\\.\\s");
							for(String relevantSentence: relevantSentences )
							{
								if(!relevantSentence.toLowerCase().contains("not relevant"))
								{
									for(String stopWords: replaceWordsList)
									{
										relevantSentence = relevantSentence.toLowerCase().replace(stopWords, "");
									}
									querySentence = querySentence.concat(" "+relevantSentence);
								}
							}
						
						}
						else
						{
							for(String stopWords: replaceWordsList)
							{
								sentence = sentence.toLowerCase().replace(stopWords, "");
							}
							querySentence = querySentence.concat(" "+sentence);
						}
							
					}
				}
			}
			else
			{
				String[] relevantSentences = queryFiles.get(i).getNarrative().split("\\.\\s");
				for(String relevantSentence: relevantSentences )
				{
					if(!relevantSentence.toLowerCase().contains("not relevant"))
					{
						for(String stopWords: replaceWordsList)
						{
							relevantSentence = relevantSentence.toLowerCase().replace(stopWords, "");
						}
						querySentence = querySentence.concat(" "+relevantSentence);
					}
				}
			}
			if(querySentence.length()>0)
				narrativeQuery = parser.parse(QueryParser.escape(querySentence.trim()));
			booleanQuery.add(new BoostQuery(titleQuery, (float) 4), BooleanClause.Occur.SHOULD);
			booleanQuery.add(new BoostQuery(descriptionQuery, (float) 1.7), BooleanClause.Occur.SHOULD);
			if (narrativeQuery != null) {
				booleanQuery.add(new BoostQuery(narrativeQuery, (float) 1.2), BooleanClause.Occur.SHOULD);
			}
			ScoreDoc[] hits = isearcher.search(booleanQuery.build(), MAX_RESULTS).scoreDocs;
			for (int j = 0; j < hits.length; j++)
			{
				Document hitDoc = isearcher.doc(hits[j].doc);
				String fileContent = queryFiles.get(i).getNum() + " 0 " + hitDoc.get("documentNo") + " 0 " + (hits[j].score)+" EXP\n";
				resultWriter.write(fileContent);
			}
		}
		
		resultWriter.close();
		
	}
		public static void delIndexFolder(String folder_path)
	        {
			File file = new File(folder_path);
			File[] dirFiles=file.listFiles();
			if (dirFiles!=null)
			{
			    for (File doc: dirFiles
			    ) {
				doc.delete();
			    }
			}
			file.delete();
	        }

}
