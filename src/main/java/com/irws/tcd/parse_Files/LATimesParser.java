package com.irws.tcd.parse_Files;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LATimesParser {
    private List<Document> document_list;

    public void readFiles(File folder, IndexWriter writer) {
        parseFiles(folder,writer);
        System.out.println("Indexing for LATTimes done");
    }

    public void parseFiles(File folder, IndexWriter writer) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
//                System.out.println("Folder name  " + fileEntry.getName());
                parseFiles(fileEntry,writer);
            } else {
                if (!fileEntry.getName().contains("read")) {
                    try {
                        org.jsoup.nodes.Document document = Jsoup.parse(fileEntry, "UTF-8");
                        Elements link = document.select("DOC");
                        for (Element e : link) {
                            Document doc= new Document();
                            doc.add(new TextField("documentNo", e.getElementsByTag("DOCNO").text(), Field.Store.YES));
                            doc.add(new TextField("documentId", e.getElementsByTag("DOCID").text(), Field.Store.YES));
                            doc.add(new TextField("date", e.getElementsByTag("DATE").text(), Field.Store.YES));
                            doc.add(new TextField("headline", e.getElementsByTag("HEADLINE").text(), Field.Store.YES));
                            doc.add(new TextField("section", e.getElementsByTag("SECTION").text(), Field.Store.YES));
                            doc.add(new TextField("text", e.getElementsByTag("TEXT").text(), Field.Store.YES));
                            doc.add(new TextField("byline", e.getElementsByTag("BYLINE").text(), Field.Store.YES));
                            writer.addDocument(doc);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
//    public static void main(String[] args) {
//        LATimesParser laTimesParser= new LATimesParser();
//        List<Document> document = new ArrayList<>();
//        //            FSDirectory dir = FSDirectory.open(Paths.get(path));
//        document = laTimesParser.readFiles(new File("src/main/resources/latimes"), document);
//        System.out.println(document.size());
//        System.out.println("done");
//
//    }
}
