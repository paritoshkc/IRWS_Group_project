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

public class FinancialTimesParser {
    private List<Document> document_list;

    public void readFiles(File folder, IndexWriter writer) {
        parseFiles(folder,writer);
        System.out.println("Indexing for Financial Times done");
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
                        Document doc;
                        for (Element e : link) {
                                doc = new Document();
                                doc.add(new TextField("documentNo", e.getElementsByTag("DOCNO").toString().replace("<docno>", "").replace("</docno>", "").replace("\n", "").trim(), Field.Store.YES));
                                doc.add(new TextField("headline", e.getElementsByTag("HEADLINE").toString().replace("<headline>", "").replace("</headline>", "").trim(), Field.Store.YES));
//                                doc.add(new TextField("byline", e.getElementsByTag("BYLINE").text().trim(), Field.Store.YES));
                                doc.add(new TextField("text", e.getElementsByTag("TEXT").toString().replace("<text>", "").replace("</text>", "").trim(), Field.Store.YES));
//                                writer.addDocument(doc);
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
//        FinancialTimesParser financialTimesParser= new FinancialTimesParser();
//        List<Document> document = new ArrayList<>();
//        //            FSDirectory dir = FSDirectory.open(Paths.get(path));
//        document = financialTimesParser.readFiles(new File("src/main/resources/ft"), document);
//        System.out.println(document.size());
//        System.out.println("done");
//
//    }
}
