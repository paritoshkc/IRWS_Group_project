package com.irws.tcd.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

public class FinancialTimesIndexing {
	
	//Path where indexed document is stored
		private static String INDEX_DIRECTORY = "./index/ft";
	
	public static void main(String[] args) throws IOException
	{
		//Path where all documents are stored
				String docName = "src/main/resources/ft";
						
				//Path where the indexed document will be stored
				Directory dir = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
						
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
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream,StandardCharsets.UTF_8));
		
		String currentLine = br.readLine();
		String docLine = "";
	    Document document = null;
	    String docType = "";
		while(currentLine != null){
			 while(!currentLine.contains("</DOC>"))
			 {
				 document = new Document();
				 currentLine = br.readLine();
				 
				 if(currentLine.contains("<DOCNO>")) {
				 docType = "documentNo";
				 docLine = docLine.concat(" "+currentLine);
				 while(!currentLine.contains("</DOCNO>")) {
	            	  currentLine = br.readLine();
	            	  if(!currentLine.contains("</DOCNO>"))
	            		  docLine = docLine.concat(" "+currentLine);
	              }	
				 if(docLine==null || docLine.equalsIgnoreCase("")) {
					 docLine=currentLine;
				 }
				 
				 if(docLine.contains("<DOCNO>"))
					 docLine = docLine.replaceAll("<DOCNO>", "");
				 if(docLine.contains("</DOCNO>"))
					 docLine = docLine.replaceAll("</DOCNO>", "");
				 }
				 
				 else if(currentLine.contains("<HEADLINE>")) {
					 docType = "headline";
					 docLine = docLine.concat(" "+currentLine);
					 while(!currentLine.contains("</HEADLINE>")) {
		            	  currentLine = br.readLine();
		            	  if(!currentLine.contains("</HEADLINE>"))
		            		  docLine = docLine.concat(" "+currentLine);
		              }	
					 if(docLine==null || docLine.equalsIgnoreCase("")) {
						 docLine=currentLine;
					 }
					 
					 if(docLine.contains("<HEADLINE>"))
						 docLine = docLine.replaceAll("<HEADLINE>", "");
					 if(docLine.contains("</HEADLINE>"))
						 docLine = docLine.replaceAll("</HEADLINE>", "");
					 
				 }
				 
				 else if(currentLine.contains("<BYLINE>")) {
					 docType = "byline";
					 docLine = docLine.concat(" "+currentLine);
					 while(!currentLine.contains("</BYLINE>")) {
		            	  currentLine = br.readLine();
		            	  if(!currentLine.contains("</BYLINE>"))
		            		  docLine = docLine.concat(" "+currentLine);
		              }	
					 if(docLine==null || docLine.equalsIgnoreCase("")) {
						 docLine=currentLine;
					 }
					 
					 if(docLine.contains("<BYLINE>"))
						 docLine = docLine.replaceAll("<BYLINE>", "");
					 if(docLine.contains("</BYLINE>"))
						 docLine = docLine.replaceAll("</BYLINE>", "");
					 
				 }
				 
				 else if(currentLine.contains("<TEXT>")) {
					 docType = "text";
					 docLine = docLine.concat(" "+currentLine);
					 while(!currentLine.contains("</TEXT>")) {
		            	  currentLine = br.readLine();
		            	  if(!currentLine.contains("</TEXT>"))
		            		  docLine = docLine.concat(" "+currentLine);
		              }	
					 if(docLine==null || docLine.equalsIgnoreCase("")) {
						 docLine=currentLine;
					 }
					 
					 if(docLine.contains("<TEXT>"))
						 docLine = docLine.replaceAll("<TEXT>", "");
					 if(docLine.contains("</TEXT>"))
						 docLine = docLine.replaceAll("</TEXT>", "");
					 
				 }
				 
				 
				 document.add(new TextField(docType, docLine.trim(), Field.Store.YES));
		         docLine="";
		         writer.addDocument(document); 
			 }
			 
			 currentLine = br.readLine();
			 if(currentLine == null)
				 break;
			 
		}
		
		br.close();
		fstream.close();
		
	}
	

}
