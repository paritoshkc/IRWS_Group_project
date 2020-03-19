package com.irws.tcd.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class jsoup_sample {
    public void listFilesForFolder(final File folder) throws IOException {
        System.out.println("function 2");
        int i=0;
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("Folder name  " + fileEntry.getName());
                listFilesForFolder(fileEntry);
            }
            else
            {
                System.out.println(fileEntry);
                Document doc = Jsoup.parse(fileEntry,"UTF-8");
                Elements link = doc.select("DOC");
                for (Element e: link) {

                    System.out.println(e.getElementsByTag("DOCNO"));
                }


            }
        }
    }

    public static void main(String[] args) {
        jsoup_sample sample= new jsoup_sample();
        File folder = new File("src/main/resources/testing/");
        try {
            sample.listFilesForFolder(folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
