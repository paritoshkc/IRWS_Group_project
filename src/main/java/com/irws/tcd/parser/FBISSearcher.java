package com.irws.tcd.parser;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.irws.tcd.Beans.QueryBean;

//Novice Java Developer code. Please consider.

public class FBISSearcher {

	
	public String searchOperations() throws IOException, ParseException {
		
		
		//=========================================================================================================//
//				Parse Query File 
		//=========================================================================================================//
		List<QueryBean> queryList= new ArrayList<>();
		File queryFile= new File("src/main/resources/query/topics");
				try {
				
		            Scanner scanner = new Scanner(queryFile);
		            QueryBean queryBean = null;
					
		            int queryNo = 0;
		            while (scanner.hasNext())
		            {

		                String next_line=scanner.nextLine();
		                if (!next_line.equals(""))
		                    if(next_line.contains("<top>"))
		                        next_line=scanner.nextLine();
	                        else if (next_line.contains("<num>")) {
	                            queryBean= new QueryBean();
	                            queryBean.setNum(next_line.replace("<num> Number: ",""));
	                            queryBean.setqueryNo(queryNo);
	                            queryNo++;
	                        }
	                        else if (next_line.contains("<title>"))
	                            queryBean.setTitle(next_line.replace("<title> ",""));
	                        else if (next_line.contains("<desc>"))
	                        {
	                            next_line=scanner.nextLine();
	                            String desc="";
	                            while(!next_line.contains("<narr>"))
	                            {
	                                desc=desc.concat(next_line);
	                                next_line=scanner.nextLine();
	                            }
	                            queryBean.setDescription(desc);
	                            String narr="";
	                            while(!next_line.contains("</top>"))
	                            {
	                                narr=narr.concat(next_line);
	                                next_line=scanner.nextLine();

	                            }
	                            queryBean.setNarrative(narr.replace("<narr> Narrative: ",""));
	                            queryList.add(queryBean);
	                            System.out.println(queryBean.getDescription());
	                        }
	                        else
	                            continue;

		            }
				}
				catch(IOException e) {
					
					e.printStackTrace();
					System.exit(1);
				}
				System.out.println("Query parsing Done!!! " + queryList.size() );	
		//=========================================================================================================//
//				Query Index Files 
		//=========================================================================================================//	

				Map<String, List<String>> resultDict = new HashMap<String, List<String>>();
				try {
					
					int hpp = 1200;
					BooleanQuery.setMaxClauseCount( Integer.MAX_VALUE );
					
					// Analyzer - - FBISPrivateStopAnalyser(), StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet()) , WhitespaceAnalyzer() , SimpleAnalyzer() , StopAnalyzer(EnglishAnalyzer.getDefaultStopSet())
					Analyzer analyzer = new FBISPrivateStopAnalyser();
					
					// Get index files
					Directory directory = FSDirectory.open(Paths.get("src/main/index/fbis"));
					DirectoryReader directoryreader = DirectoryReader.open(directory);
					
					// Index searcher
					IndexSearcher indexsearcher = new IndexSearcher(directoryreader);
					indexsearcher.setSimilarity(new FBISPrivateBM25()); //ClassicSimilarity() , LMDirichletSimilarity() , BM25Similarity()

					List<String> resFileContent = new ArrayList<String>();
					
					
					// Loop all the queries and retrieve docs
					for (int i = 0; i < queryList.size(); i++) {
						
						QueryBean cranQuery = queryList.get(i);
						HashMap<String,Float> boosts = new HashMap<String,Float>();
						//boosts.put("title",    (float) 5 );
						//boosts.put("description",     (float) 20 );
						MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
																new String[] {"headline", "text"},
																					 analyzer,
																					 boosts);
						Query query = queryParser.parse(cranQuery.getDescription());
						
						// Search
						TopDocs topDocs = indexsearcher.search(query, hpp);
						ScoreDoc[] hits = topDocs.scoreDocs;
						
						// Display results
						List<String> resultList = new ArrayList<String>();
//				        System.out.println("Found " + hits.length + " hits.");
				        for(int j = 0; j < hits.length; j++) {
				            
				        	int docId = hits[j].doc;
				            Document doc = indexsearcher.doc(docId);
				            resultList.add(doc.get("documentNo"));
				            resFileContent.add(cranQuery.getqueryNo() + " 0 " + doc.get("documentNo") + " 0 " + hits[j].score + " STANDARD\n");
				        }
				        resultDict.put(Integer.toString(i + 1), resultList);

					}
					
					// Create directory if it does not exist
					File outputDir = new File("src/main/output");
					if (!outputDir.exists()) outputDir.mkdir();
					
					Files.write(Paths.get("src/main/output/FBIS_Search_results.txt"), resFileContent, Charset.forName("UTF-8"));
					System.out.println("Search Done!!!");
				}
				catch (IOException e) {
					
					e.printStackTrace();
					System.exit(1);
				}
				
				return "Search Operation done successfully";
		
	}
	
	/**
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws IOException, ParseException {

		FBISSearcher mysearcher = new FBISSearcher();
		String return_phrase = mysearcher.searchOperations();
		System.out.println(return_phrase);
	}
	
}
