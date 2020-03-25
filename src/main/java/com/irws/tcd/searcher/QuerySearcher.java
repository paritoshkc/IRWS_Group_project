package com.irws.tcd.searcher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.irws.tcd.Beans.QueryBean;
import com.irws.tcd.index.Common_Indexer;
import com.irws.tcd.index.CustomAnalyzer;
import com.irws.tcd.parse_Files.ParseQueryFile;

public class QuerySearcher {
	
    private static  String path="src/main/index";
    private static int MAX_RESULTS = 1000;

	public static void main(String[] args) throws IOException, ParseException {

		Analyzer analyzer = new CustomAnalyzer();
		Similarity similarity = new BM25Similarity();
		Common_Indexer.indexFiles(analyzer,similarity,path);
		
		Directory directory = FSDirectory.open(Paths.get(path));
		String queryWriteDoc = "src/main/queryResult";
		
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		
		isearcher.setSimilarity(new BM25Similarity());
		MultiFieldQueryParser parser = new MultiFieldQueryParser (new String[]{"headline", "documentTitle", "text"},analyzer);
		
		querySearch(isearcher, parser,queryWriteDoc);
		System.out.println("Query search completed");
		ireader.close();
		directory.close();
	}

	private static void querySearch(IndexSearcher isearcher, MultiFieldQueryParser parser, String queryWriteDoc) throws ParseException, IOException {

		List<QueryBean> queryFiles = ParseQueryFile.parseQueryFile();
		//Write file at given path
		BufferedWriter resultWriter = new BufferedWriter(new FileWriter(queryWriteDoc));
		
		for(int i=0; i<queryFiles.size(); i++)
		{
			Query query = parser.parse(QueryParser.escape( queryFiles.get(i).getDescription().trim()));
			ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;
			for (int j = 0; j < hits.length; j++)
			{
				Document hitDoc = isearcher.doc(hits[j].doc);
				String fileContent = queryFiles.get(i).getNum() + " 0 " + hitDoc.get("documentNo") + " 0 " + (hits[j].score)+" EXP\n";
				resultWriter.write(fileContent);
			}
		}
		
		resultWriter.close();
		
	}

}
