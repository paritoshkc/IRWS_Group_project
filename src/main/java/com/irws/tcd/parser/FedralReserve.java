package com.irws.tcd.parser;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FedralReserve {
    List<String> filenames = new ArrayList<String>();
    String path="src/main/resources/fr94_NoComments";
    File newfolder= new File(path);

    public void listFilesForFolder(final File folder) throws IOException {
        System.out.println("function 2");
        int i=0;
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                System.out.println("Folder name  "+fileEntry.getName());
                listFilesForFolder(fileEntry);
            } else {

                if (!(fileEntry.getName().equals("readchg") || fileEntry.getName().equals("readmefr"))) {
                    File temp= new File(path+"/"+fileEntry.getName());
                    System.out.println("writing to "+path+"/"+fileEntry.getName());
                    Scanner scanner = new Scanner(fileEntry);
                    scanner.useDelimiter("\n");

                    BufferedWriter bufferedWriter= new BufferedWriter(new FileWriter(temp));
                    while (scanner.hasNext()) {
                        String temp1=scanner.next();
//                        System.out.println(temp1);
//                        if (scanner.nextLine().contains("PJG")) {
//                            i++;
                        if (temp1.contains("<DOC>"))
                        {
                            i++;
                            bufferedWriter.write("DOC-"+Integer.toString(i));
                            bufferedWriter.newLine();
                            while(!temp1.contains("</DOC>"))
                            {
                                temp1= scanner.next();
                                if (temp1.contains("<DOCNO>"))
                                {
                                    bufferedWriter.write("DOCNO-");

                                    temp1=temp1.replace("<DOCNO>", "").replace("</DOCNO>", "");
                                    bufferedWriter.write(temp1);
                                }
                                else if (temp1.contains("<PARENT>"))
                                {
                                    bufferedWriter.write("PARENT-");

                                    temp1=temp1.replace("<PARENT>", "").replace("</PARENT>", "");
                                    bufferedWriter.write(temp1);
                                }
                                else if (temp1.contains("<TEXT>"))
                                {
                                    String line="";
                                    while(!temp1.contains("</TEXT>")){
                                        temp1=scanner.next();
                                        if (!temp1.contains("PJG")) {

                                            if (temp1.contains("USDEPT>"))
                                            {temp1 = "USDEPT-" + temp1.replace("<USDEPT>", ""); temp1 = temp1.replace("</USDEPT>", "");}
                                            else if (temp1.contains("USBUREAU>"))
                                            {temp1 = "USBUREAU-" + temp1.replace("<USBUREAU>", ""); temp1 = temp1.replace("</USBUREAU>", "");}
                                            else if (temp1.contains("CFRNO>"))
                                            {   temp1 = "CFRNO-" + temp1.replace("<CFRNO>", ""); temp1 = temp1.replace("</CFRNO>", "");}
                                            else if (temp1.contains("RINDOCK>"))
                                            {   temp1 = "RINDOCK-" + temp1.replace("<RINDOCK>", ""); temp1 = temp1.replace("</RINDOCK>", "");}
                                            else  if (temp1.contains("AGENCY>"))
                                            {   temp1 = "AGENCY-" + temp1.replace("<AGENCY>", ""); temp1 = temp1.replace("</AGENCY>", "");}
                                            else if (temp1.contains("ACTION>"))
                                            {   temp1 = "ACTION-" + temp1.replace("<ACTION>", ""); temp1 = temp1.replace("</ACTION>", "");}
                                            else if (temp1.contains("SUMMARY>"))
                                            {   temp1 = temp1.replace("<SUMMARY>", ""); temp1 = temp1.replace("</SUMMARY>", "");}
                                            else if (temp1.contains("DATE>"))
                                            {   temp1 = "DATE-" + temp1.replace("<DATE>", ""); temp1 = temp1.replace("</DATE>", "");}
                                            else if (temp1.contains("FURTHER>"))
                                            {   temp1 = "FURTHER-" + temp1.replace("<FURTHER>", ""); temp1 = temp1.replace("</FURTHER>", "");}
                                            else  if (temp1.contains("SIGNER>"))
                                            {   temp1 = "SIGNER-" + temp1.replace("<SIGNER>", ""); temp1 = temp1.replace("</SIGNER>", "");}
                                            else if (temp1.contains("SIGNJOB>"))
                                            {   temp1 = "SIGNJOB-" + temp1.replace("<SIGNJOB>", ""); temp1 = temp1.replace("</SIGNJOB>", "");}
                                            else if (temp1.contains("FRFILING>"))
                                            {   temp1 = "FRFILING-" + temp1.replace("<FRFILING>", ""); temp1 = temp1.replace("</FRFILING>", "");}
                                            else if (temp1.contains("BILLING>"))
                                            {   temp1 = "BILLING-" + temp1.replace("<BILLING>", ""); temp1 = temp1.replace("</BILLING>", "");}
                                            else if (temp1.contains("FOOTCITE>"))
                                            {   temp1 = "FOOTCITE-" + temp1.replace("<FOOTCITE>", ""); temp1 = temp1.replace("</FOOTCITE>", "");}
                                            else  if (temp1.contains("FOOTNAME>"))
                                            {   temp1 = "FOOTNAME-" + temp1.replace("<FOOTNAME>", ""); temp1 = temp1.replace("</FOOTNAME>", "");}
                                            else  if (temp1.contains("FOOTNOTE>"))
                                            {   temp1 = "FOOTNOTE-" + temp1.replace("<FOOTNOTE>", ""); temp1 = temp1.replace("</FOOTNOTE>", "");}
                                            else if (temp1.contains("SUPPLEM>"))
                                            {   temp1 = "SUPPLEM-" + temp1.replace("<SUPPLEM>", ""); temp1 = temp1.replace("</SUPPLEM>", "");}
                                            else if (temp1.contains("TABLE>"))
                                            {   temp1 = "TABLE-" + temp1.replace("<TABLE>", ""); temp1 = temp1.replace("</TABLE>", "");}
                                            else if (temp1.contains("IMPORT>"))
                                            {   temp1 = "IMPORT-" + temp1.replace("<IMPORT>", ""); temp1 = temp1.replace("</IMPORT>", "");}
                                            else if (temp1.contains("ADDRESS>"))
                                            {   temp1 = temp1.replace("<ADDRESS>", ""); temp1 = temp1.replace("</ADDRESS>", "");}
                                            else if (temp1.contains("DOCTITLE>"))
                                            {   temp1 = "DOCTITLE-"+temp1.replace("<DOCTITLE>", ""); temp1 = temp1.replace("</DOCTITLE>", "");}


                                            temp1=temp1.replace("&hyph","-").replace("&blank","").replace("&sect","").replace("&amp","&").replace("&para","\n").replace("&rsquo","'");
                                            line = line.concat(temp1 + " ");
                                        }
                                    }
                                    line=line.replace("</TEXT>", "");
                                    bufferedWriter.write("TEXT-"+ line);
                                }
                                bufferedWriter.newLine();
                            }
                        }
                    }
                    scanner.close();
                    bufferedWriter.close();
                }
            }
        }
        System.out.println("PJG Total changed "+ i);
    }

    //removing PJG tag from the files
    protected List<String> removeComments() throws FileNotFoundException {
        final File folder = new File("src/main/resources/fr94");
        try {
            listFilesForFolder(folder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filenames;
    }

    public static void main(String[] args) {
        FedralReserve fedralReserve= new FedralReserve();

        try {
            fedralReserve.removeComments();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
