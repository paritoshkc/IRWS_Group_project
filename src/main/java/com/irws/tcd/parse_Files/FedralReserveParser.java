package com.irws.tcd.parse_Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FedralReserveParser {
    private List<Document> document_list;

    public void readFiles(File folder,IndexWriter writer)
    {
        parseFiles(folder,writer);
        System.out.println("Indexing for Federal Reserve done");
    }
    public void parseFiles(File folder,IndexWriter writer) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
//                System.out.println("Folder name  " + fileEntry.getName());
                parseFiles(fileEntry,writer);
            } else {
                if (!fileEntry.getName().contains("read")) {
                    try {
                        org.jsoup.nodes.Document document= Jsoup.parse(fileEntry,"UTF-8");
                        Elements link = document.select("DOC");
                        Document doc;
                        for (Element e: link)
                        {
                            doc = new Document();
//                            doc.add(new TextField("documentNo_old", e.getElementsByTag("PARENT").text(), Field.Store.YES));
                            doc.add(new TextField("documentNo", e.getElementsByTag("DOCNO").text().trim(), Field.Store.YES));
                            doc.add(new TextField("headline", e.getElementsByTag("DOCTITLE").text().trim(), Field.Store.YES));
//                            doc.add(new TextField("usDepartment", e.getElementsByTag("USDEPT").text(), Field.Store.YES));
//                            doc.add(new TextField("usBureau", e.getElementsByTag("USBUREAU").text(), Field.Store.YES));
//                            doc.add(new TextField("cfrNumber", e.getElementsByTag("CFRNO").text(), Field.Store.YES));
//                            doc.add(new TextField("rindock", e.getElementsByTag("RINDOCK").text(), Field.Store.YES));
//                            doc.add(new TextField("agency", e.getElementsByTag("AGENCY").text(), Field.Store.YES));
//                            doc.add(new TextField("action", e.getElementsByTag("ACTION").text(), Field.Store.YES));
//                            doc.add(new TextField("summary", e.getElementsByTag("SUMMARY").text(), Field.Store.YES));
//                            doc.add(new TextField("date", e.getElementsByTag("DATE").text(), Field.Store.YES));
//                            doc.add(new TextField("further", e.getElementsByTag("FURTHER").text(), Field.Store.YES));
//                            doc.add(new TextField("signer", e.getElementsByTag("SIGNER").text(), Field.Store.YES));
//                            doc.add(new TextField("signJob", e.getElementsByTag("SIGNJOB").text(), Field.Store.YES));
//                            doc.add(new TextField("frFiling", e.getElementsByTag("FRFILING").text(), Field.Store.YES));
//                            doc.add(new TextField("billing", e.getElementsByTag("BILLING").text(), Field.Store.YES));
//                            doc.add(new TextField("footcite", e.getElementsByTag("FOOTCITE").text(), Field.Store.YES));
//                            doc.add(new TextField("footname", e.getElementsByTag("FOOTNAME").text(), Field.Store.YES));
//                            doc.add(new TextField("footnote", e.getElementsByTag("FOOTNOTE").text(), Field.Store.YES));
//                            doc.add(new TextField("supplem", e.getElementsByTag("SUPPLEM").text(), Field.Store.YES));
//                            doc.add(new TextField("table", e.getElementsByTag("TABLE").text(), Field.Store.YES));
//                            doc.add(new TextField("import", e.getElementsByTag("IMPORT").text(), Field.Store.YES));
//                            doc.add(new TextField("address", e.getElementsByTag("ADDRESS").text(), Field.Store.YES));
                            doc.add(new TextField("text", e.getElementsByTag("TEXT").text().trim(), Field.Store.YES));
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
//        FedralReserveParser fedralReserveParser = new FedralReserveParser();
//        List<Document> document = new ArrayList<>();
//        //            FSDirectory dir = FSDirectory.open(Paths.get(path));
//        document = fedralReserveParser.readFiles(new File("src/main/resources/fr94"), document);
//        System.out.println("done");
//    }



//        FedralReserveIndexer fedralReserveIndexer = new FedralReserveIndexer();
//        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
//        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
//        Directory dir = null;
//        try {
//            dir = FSDirectory.open(Paths.get(path));
////            writer =  new IndexWriter(dir, config);
////            fedralReserveIndexer.readFiles(new File("src/main/resources/fr94"),writer);
////            writer.close();
//            System.out.println("Indexing done");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
}
