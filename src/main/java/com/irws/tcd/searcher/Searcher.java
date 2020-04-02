package com.irws.tcd.searcher;

import com.irws.tcd.Beans.QueryBean;
import com.irws.tcd.index.Common_Indexer;
import com.irws.tcd.index.CustomAnalyzer;
import com.irws.tcd.parse_Files.ParseQueryFile;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Searcher {

    private static  String path="src/main/index";
    private static int MAX_RESULTS = 1000;

//    public static void main(String[] args) throws IOException, ParseException {
//
//        Analyzer analyzer = new CustomAnalyzer();
//        Similarity similarity = new BM25Similarity();
//
//        // Indexing Files
//        Common_Indexer.indexFiles(analyzer,similarity,path);
//
//        Directory directory = FSDirectory.open(Paths.get(path));
//        String queryWriteDoc = "src/main/queryResult";
//
//        DirectoryReader ireader = DirectoryReader.open(directory);
//        IndexSearcher isearcher = new IndexSearcher(ireader);
//
//        // Setting Similarity for index searcher
//        isearcher.setSimilarity(new BM25Similarity());
//
//        querySearch(isearcher, analyzer, queryWriteDoc);
//        System.out.println("Query search completed");
//        ireader.close();
//        directory.close();
//    }

    private static void querySearch(IndexSearcher isearcher, Analyzer analyzer, String queryWriteDoc) throws ParseException, IOException {

        List<QueryBean> queryFiles = ParseQueryFile.parseQueryFile();

        // Write file at given path
        BufferedWriter resultWriter = new BufferedWriter(new FileWriter(queryWriteDoc));

        // Querying on each query from queryFiles
        for(int i=0; i<queryFiles.size(); i++)
        {

            QueryBuilder builder = new QueryBuilder(analyzer);

            BooleanQuery.Builder bq = new BooleanQuery.Builder();
            String title = queryFiles.get(i).getTitle().trim();
            String desc = queryFiles.get(i).getDescription().trim();
//            String nar = queryFiles.get(i).getNarrative().trim();

            // Building boolean queries for each element in query text
            Query a = builder.createBooleanQuery("headline", title);
            Query b = builder.createBooleanQuery("text", desc);
//            Query c = builder.createBooleanQuery("text", nar);

            // Adding all queries to builder
            bq.add(a, BooleanClause.Occur.SHOULD);
            bq.add(b, BooleanClause.Occur.SHOULD);
//            bq.add(c, BooleanClause.Occur.SHOULD);

            // Building the final query
            Query query = bq.build();

            // Executing the query
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
