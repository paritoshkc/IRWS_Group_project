package com.irws.tcd.parse_Files;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FBISParser {
    private List<Document> document_list;
    public void readFiles(File folder, IndexWriter writer)
    {
        parseFiles(folder,writer);
        System.out.println("Indexing for FBIS done");
    }
    private void parseFiles(File folder, IndexWriter writer) {
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
                            String docNumber = e.getElementsByTag("DOCNO").text().trim();
                            String date = e.getElementsByTag("DATE1").text().trim();
                            String title = e.getElementsByTag("TI").text().trim();
                            String textContent = e.getElementsByTag("TEXT").text().trim();

                            DateFormat df = new SimpleDateFormat("MMMM dd yyyy");
                            DateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");

                            try {
                                date = sdf.format(df.parse(date));
                            } catch (ParseException e0) {
                                DateFormat df1 = new SimpleDateFormat("dd MMM yyyy");
                                try {
                                    date = sdf.format(df1.parse(date));
                                } catch (ParseException e1) {
                                    DateFormat df2 = new SimpleDateFormat("dd MMMM");
                                    try {
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(df2.parse(date));
                                        cal.set(Calendar.YEAR, 1994);
                                        date = sdf.format(cal.getTime());
                                    } catch (ParseException e2) {
                                        date = date.substring(date.length() - 4);
                                    }
                                }
                            }

                            if (!textContent.contains("[Text]")) {

                                if (textContent.contains("[Passage omitted]")) {
                                    textContent = textContent.substring(textContent.indexOf("[Passage omitted]")).replace("[Passage omitted]", "").trim();
                                } else if (textContent.contains("[Excerpts]")) {
                                    textContent = textContent.substring(textContent.indexOf("[Excerpts]")).replace("[Excerpts]", "").trim();
                                } else {
//                System.out.println(textContent);
//                break;
                                }
                            } else {
                                textContent = textContent.substring(textContent.indexOf("[Text]")).replace("[Text]", "").trim();
                            }

                            doc = new Document();
                            doc.add(new TextField("documentNo", docNumber, Field.Store.YES));
                            doc.add(new TextField("date", date, Field.Store.YES));
                            doc.add(new TextField("headline", title, Field.Store.YES));
                            doc.add(new TextField("text", textContent, Field.Store.YES));
                            writer.addDocument(doc);

//                            writer.addDocument(doc);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

//    public static void main(String[] args) {
//       FBISParser FBISParser = new FBISParser();
//        List<Document> document = new ArrayList<>();
//        document = FBISParser.readFiles(new File("src/main/resources/fbis"), document);
//        System.out.println(document.size());
//        document = FBISParser.readFiles(new File("src/main/resources/fbis"), document);
//        System.out.println(document.size());
//
//        System.out.println("done");
//    }
}
