package com.irws.tcd.parser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LATimesIndexer {

    public static String fileDir = "src/main/resources/latimes";
    public static String indexDir = "src/main/index/latimes";

    public static void main(String args[]) {

        try {
            System.out.println("Indexing Start");
            indexDocuments(fileDir, indexDir);
            System.out.println("Indexing Complete");
        } catch (Exception e) {

            System.out.println("Exception ::: " + e);

        }

    }

    public static void indexDocuments(String fDir, String iDir) throws Exception{

        //Lucene Documents for indexing
        List<Document> docs = fetchDocuments(fDir);

        //Path where the indexed document will be stored
        Directory dir = FSDirectory.open(Paths.get(iDir));

        //Initialize Standard Analyzer
        Analyzer analyzer = new StandardAnalyzer();

        //Initialize IndexWriterConfig object
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        //Initialize IndexWrite
        IndexWriter iWriter = new IndexWriter(dir, iwc);
        iWriter.addDocuments(docs);
        iWriter.close();

    }

    public static List<Document> fetchDocuments(String pathToFedRegister) throws IOException {

        List<org.apache.lucene.document.Document> parsedDocsList = new ArrayList<Document>();

        File[] files = new File(pathToFedRegister).listFiles();

        //Iterate on each file to fetch attributes and add in document
        for (File file : files) {

            System.out.println("Parsing File: " + file.getName());
            String sgml = FileUtils.readFileToString(file, "utf-8");
            org.jsoup.nodes.Document laTimesContent = Jsoup.parse(sgml);

            Elements docs = laTimesContent.select("DOC");

            for (Element doc : docs) {
                String docId, docNo, date, headline, section, text, byline;

                //Fetch different attributes from the document
                docNo = (doc.select("DOCNO").text());
                docId = doc.select("DOCID").text();
                date = (doc.select("DATE").select("P").text());
                headline = (doc.select("HEADLINE").select("P").text());
                section = (doc.select("SECTION").select("P").text());
                text = (doc.select("TEXT").select("P").text());
                byline = (doc.select("BYLINE").select("P").text());
                //appending doc in parsed documents list
                parsedDocsList.add(createDocument(docNo, docId, date, headline, section, text, byline));

            }
        }
        return parsedDocsList;


    }

    private static org.apache.lucene.document.Document createDocument(String docNo, String docId, String date, String headline, String section, String text, String byline) {

        //Initialize new Document
        org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();

        //Add fields in the document with appropriate field name and type
        document.add(new StringField("docNo", docNo, Field.Store.YES));
        document.add(new StringField("docId", docId, Field.Store.YES));
        document.add(new TextField("date", date, Field.Store.YES));
        document.add(new TextField("headline", headline, Field.Store.YES));
        document.add(new TextField("section", section, Field.Store.YES));
        document.add(new TextField("text", text, Field.Store.YES));
        document.add(new TextField("byline", byline, Field.Store.YES));

        return document;
    }

}
