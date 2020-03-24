package com.irws.tcd.index;

import com.irws.tcd.parse_Files.FBISParser;
import com.irws.tcd.parse_Files.FedralReserveParser;
import com.irws.tcd.parse_Files.FinancialTimesParser;
import com.irws.tcd.parse_Files.LATimesParser;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Common_Indexer {
    private static List<Document> document= new ArrayList<>();
    private static Analyzer analyzer;
    private static Similarity similarity;
    private static  String path="src/main/index";
    private static IndexWriter writer;

    public Common_Indexer(Analyzer analyzer, Similarity similarity) {
        this.analyzer = analyzer;
        this.similarity = similarity;
    }

    private void checkIndexFolder()
    {
        File file = new File("src/main/index");
        File[] dirFiles=file.listFiles();
        if (dirFiles!=null)
            for (File doc: dirFiles)
                doc.delete();
        file.delete();
    }
    public static void main(String[] args) {



//        FR94
        FedralReserveParser fedralReserveParser= new FedralReserveParser();
        document =fedralReserveParser.readFiles(new File("src/main/resources/fr94"), document);
        System.out.println(document.size());

//       FBIS
        FBISParser FBISParser = new FBISParser();
        document = FBISParser.readFiles(new File("src/main/resources/fbis"), document);
        System.out.println(document.size());

//        LATIMES
        LATimesParser laTimesParser= new LATimesParser();
        document=laTimesParser.readFiles(new File("src/main/resources/latimes"), document);
        System.out.println(document.size());

//        FinancialTimes
        FinancialTimesParser financialTimesParser = new FinancialTimesParser();
        financialTimesParser.readFiles(new File("src/main/resources/ft"), document);
        System.out.println(document.size());


        analyzer = new StandardAnalyzer();
        similarity =  new BM25Similarity();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setSimilarity(similarity);
        Directory dir = null;
        try {
            dir = FSDirectory.open(Paths.get(path));
            writer =  new IndexWriter(dir, config);

            for(Document doc : document)
            {
                //there is another function in IndexWriter which is addDocuments
                writer.addDocument(doc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                writer.close();
                dir.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
