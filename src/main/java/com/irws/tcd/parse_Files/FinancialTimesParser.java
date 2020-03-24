package com.irws.tcd.parse_Files;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FinancialTimesParser {
    private List<Document> document_list;

    public List<Document> readFiles(File folder, List<Document> document_list) {
        this.document_list = document_list;
        parseFiles(folder);
        System.out.println("file length "+ Integer.toString(document_list.size()));
        return document_list;
    }

    public void parseFiles(File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
//                System.out.println("Folder name  " + fileEntry.getName());
                parseFiles(fileEntry);
            } else {
                if (!fileEntry.getName().contains("read")) {
                    try {
                        org.jsoup.nodes.Document document = Jsoup.parse(fileEntry, "UTF-8");
                        Elements link = document.select("DOC");
                        Document doc;
                        for (Element e : link) {
                                doc = new Document();
                                doc.add(new TextField("documentNo", e.getElementsByTag("DOCNO").text(), Field.Store.YES));
                                doc.add(new TextField("headline", e.getElementsByTag("HEADLINE").text(), Field.Store.YES));
                                doc.add(new TextField("byline", e.getElementsByTag("BYLINE").text(), Field.Store.YES));
                                doc.add(new TextField("text", e.getElementsByTag("TEXT").text(), Field.Store.YES));
//                                writer.addDocument(doc);
                                document_list.add(doc);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
    public static void main(String[] args) {
        FinancialTimesParser financialTimesParser= new FinancialTimesParser();
        List<Document> document = new ArrayList<>();
        //            FSDirectory dir = FSDirectory.open(Paths.get(path));
        document = financialTimesParser.readFiles(new File("src/main/resources/ft"), document);
        System.out.println(document.size());
        System.out.println("done");

    }
}
