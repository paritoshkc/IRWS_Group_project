package com.irws.tcd.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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

public class FinancialTimesIndexer {
	
	//Path where indexed document is stored
	private static String INDEXER = "src/main/index/ft";

	public static void main(String[] args) throws IOException {

		//Path where all documents are stored
				String docName = "src/main/resources/ft";
						
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
			File[] subDocs = doc.listFiles();
			for (int i = 0; i < subDocs.length; i++) {
				if (subDocs[i].isDirectory() && !subDocs[i].getName().startsWith("read")) {
					File[] files = subDocs[i].listFiles();
					for (int j = 0; j < files.length; j++) {
						if (files[j].isFile() && !files[j].getName().startsWith("read")) {
							readFile(writer,files[j]);
						}
					}
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
			doc = new Document();
//			System.out.println(e.getElementsByTag("DOCNO").text());
			doc.add(new TextField("documentNo", e.getElementsByTag("DOCNO").text(), Field.Store.YES));
			doc.add(new TextField("headline", e.getElementsByTag("HEADLINE").text(), Field.Store.YES));
			doc.add(new TextField("byline", e.getElementsByTag("BYLINE").text(), Field.Store.YES));
			doc.add(new TextField("text", e.getElementsByTag("TEXT").text(), Field.Store.YES));
			writer.addDocument(doc);
		}
	}

}
