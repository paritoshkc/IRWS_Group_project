package com.irws.tcd.parser;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class FedralReserveIndexer {
    private static IndexWriter writer;

    public void readFiles(File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("Folder name  " + fileEntry.getName());
                readFiles(fileEntry);
            } else {
                if (!(fileEntry.getName().equals("readchg") || fileEntry.getName().equals("readmefr"))) {
                    try {
                        Document document= Jsoup.parse(fileEntry,"UTF-8");
                        Elements link = document.select("DOC");
                        org.apache.lucene.document.Document doc;
                        for (Element e: link)
                        {
                            doc = new org.apache.lucene.document.Document();
                            doc.add(new TextField("documentNo", e.getElementsByTag("DOCNO").text(), Field.Store.YES));
                            doc.add(new TextField("parent", e.getElementsByTag("PARENT").text(), Field.Store.YES));
                            doc.add(new TextField("documentTitle", e.getElementsByTag("DOCTITLE").text(), Field.Store.YES));
                            doc.add(new TextField("usDepartment", e.getElementsByTag("USDEPT").text(), Field.Store.YES));
                            doc.add(new TextField("usBureau", e.getElementsByTag("USBUREAU").text(), Field.Store.YES));
                            doc.add(new TextField("cfrNumber", e.getElementsByTag("CFRNO").text(), Field.Store.YES));
                            doc.add(new TextField("rindock", e.getElementsByTag("RINDOCK").text(), Field.Store.YES));
                            doc.add(new TextField("agency", e.getElementsByTag("AGENCY").text(), Field.Store.YES));
                            doc.add(new TextField("action", e.getElementsByTag("ACTION").text(), Field.Store.YES));
                            doc.add(new TextField("summary", e.getElementsByTag("SUMMARY").text(), Field.Store.YES));
                            doc.add(new TextField("date", e.getElementsByTag("DATE").text(), Field.Store.YES));
                            doc.add(new TextField("further", e.getElementsByTag("FURTHER").text(), Field.Store.YES));
                            doc.add(new TextField("signer", e.getElementsByTag("SIGNER").text(), Field.Store.YES));
                            doc.add(new TextField("signJob", e.getElementsByTag("SIGNJOB").text(), Field.Store.YES));
                            doc.add(new TextField("frFiling", e.getElementsByTag("FRFILING").text(), Field.Store.YES));
                            doc.add(new TextField("billing", e.getElementsByTag("BILLING").text(), Field.Store.YES));
                            doc.add(new TextField("footcite", e.getElementsByTag("FOOTCITE").text(), Field.Store.YES));
                            doc.add(new TextField("footname", e.getElementsByTag("FOOTNAME").text(), Field.Store.YES));
                            doc.add(new TextField("footnote", e.getElementsByTag("FOOTNOTE").text(), Field.Store.YES));
                            doc.add(new TextField("supplem", e.getElementsByTag("SUPPLEM").text(), Field.Store.YES));
                            doc.add(new TextField("table", e.getElementsByTag("TABLE").text(), Field.Store.YES));
                            doc.add(new TextField("import", e.getElementsByTag("IMPORT").text(), Field.Store.YES));
                            doc.add(new TextField("address", e.getElementsByTag("ADDRESS").text(), Field.Store.YES));
                            doc.add(new TextField("text", e.getElementsByTag("TEXT").text(), Field.Store.YES));
                            writer.addDocument(doc);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        final String path="src/main/index/fr";
        FedralReserveIndexer fedralReserveIndexer = new FedralReserveIndexer();
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = null;
        try {
            dir = FSDirectory.open(Paths.get(path));
            writer =  new IndexWriter(dir, config);
            fedralReserveIndexer.readFiles(new File("src/main/resources/fr94"));
            writer.close();
            System.out.println("Indexing done");
        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }

}
