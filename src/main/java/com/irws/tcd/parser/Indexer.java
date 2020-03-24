package com.irws.tcd.parser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Indexer {
    final private Analyzer analyzer;
    final private Similarity similarity;
    final private String path="src/main/index/fr";
    public IndexWriter writer;

    public Indexer() {
        this.analyzer = new StandardAnalyzer();
        this.similarity =  new BM25Similarity();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = null;
        try {
            dir = FSDirectory.open(Paths.get(path));
            writer =  new IndexWriter(dir, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public Similarity getSimilarity() {
        return similarity;
    }
    public IndexWriter getIndexWriterObject()
    {
//        checkIndexFolder();
        return this.writer;

    }
    public void closeWriter()
    {
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkIndexFolder()
    {
        File file = new File(path);
        File[] dirFiles=file.listFiles();
        if (dirFiles!=null)
            for (File doc: dirFiles)
                doc.delete();
        file.delete();
    }
}
