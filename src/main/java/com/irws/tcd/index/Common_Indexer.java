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

    private static IndexWriter writer;

//    public Common_Indexer(Analyzer analyzer, Similarity similarity) {
//        this.analyzer = analyzer;
//        this.similarity = similarity;
//    }

//    private void checkIndexFolder()
//    {
//        File file = new File("src/main/index");
//        File[] dirFiles=file.listFiles();
//        if (dirFiles!=null)
//            for (File doc: dirFiles)
//                doc.delete();
//        file.delete();
//    }
    public static void indexFiles(Analyzer analyzer, Similarity similarity, String path) throws IOException {

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setSimilarity(similarity);
        Directory dir = null;
        dir = FSDirectory.open(Paths.get(path));
        writer =  new IndexWriter(dir, config);

//        FR94
        FedralReserveParser fedralReserveParser= new FedralReserveParser();
        fedralReserveParser.readFiles(new File("src/main/resources/fr94"), writer);

//       FBIS
        FBISParser FBISParser = new FBISParser();
        FBISParser.readFiles(new File("src/main/resources/fbis"), writer);

//        LATIMES
        LATimesParser laTimesParser= new LATimesParser();
        laTimesParser.readFiles(new File("src/main/resources/latimes"), writer);

//        FinancialTimes
        FinancialTimesParser financialTimesParser = new FinancialTimesParser();
        financialTimesParser.readFiles(new File("src/main/resources/ft"), writer);

        writer.close();
        dir.close();


//        try {
//
//            for(Document doc : document)
//            {
//                //there is another function in IndexWriter which is addDocuments
//                writer.addDocument(doc);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                writer.close();
//                dir.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
