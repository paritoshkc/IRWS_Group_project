package com.irws.tcd.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class FBISIndexer {
	
	//Path where indexed document is stored
	private static String INDEXER = "src/main/index/fbis";

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//Path where all documents are stored
		String docName = "src/main/resources/fbis";
				
		//Path where the indexed document will be stored
		Directory dir = FSDirectory.open(Paths.get(INDEXER));
				
		//Custom Analyzer used
		Analyzer analyzer = new StandardAnalyzer();
		
		//Create objects to write the indexed document
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		//Create mode used so that indexed documents will be written to avoid inaccurate querying
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter writer = new IndexWriter(dir, config);
		
		indexDocs(writer,docName);
		System.out.println("Indexing done");
		
		writer.close();

	}

private static void indexDocs(IndexWriter writer, String docFolder) throws IOException {
		
		File doc = new File(docFolder);
		if (doc.exists() && doc.isDirectory()) {
			File[] files = doc.listFiles();
			for (int j = 0; j < files.length; j++) {
				if (files[j].isFile() && !files[j].getName().startsWith("read")) {
					readFile(writer,files[j]);
				}
			}
		}
	}

private static void readFile(IndexWriter writer, File file) throws IOException {
	
	org.jsoup.nodes.Document document = Jsoup.parse(file,"UTF-8");
	Elements link = document.select("DOC");
	
	Document doc = null;
	
	for(Element e:link)
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

    	} 
    
	}
}

