package myLuceneApp;

// tested for lucene 7.7.2 and jdk13
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import txtparsing.*;

/**
 * Creates a lucene's inverted index from an xml file.
 *
 * @author Tonia Kyriakopoulou
 */
public class Indexer {

    /**
     * Configures IndexWriter.
     * Creates a lucene's inverted index.
     *
     */
    public ArrayList<String> readFiles() {
        ArrayList<String> txtFile = new ArrayList<>();
        try {
            File myObj = new File("docs//");
            File[] listofFiles = myObj.listFiles();
            assert listofFiles != null;

            for (File file : listofFiles) {
                if (file.isFile()) {
                    txtFile.add("docs//" + file.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txtFile;
    }

    public Indexer() throws Exception{

        ArrayList<String> txtfile =  new ArrayList<>(); //txt file to be parsed and indexed, it contains one document per line
        txtfile = readFiles();
        String indexLocation = ("index"); //define were to store the index

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexLocation + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexLocation));
            // define which analyzer to use for the normalization of documents
            Analyzer analyzer = new EnglishAnalyzer();
            // define retrieval model
            Similarity similarity = new ClassicSimilarity();
            // configure IndexWriter
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            // Create a new index in the directory, removing any
            // previously indexed documents:
            iwc.setOpenMode(OpenMode.CREATE);

            // create the IndexWriter with the configuration as above
            IndexWriter indexWriter = new IndexWriter(dir, iwc);
            List<List<MyDoc>> docList = new ArrayList();
            // parse txt document using TXT parser and index it
            for (int i=0; i< 14; i++) {
                docList.add(TXTParsing.parse(txtfile.get(i)));
            }

            for(List<MyDoc> list : docList){
                for (MyDoc doc : list){
                    indexDoc(indexWriter, doc);
                }
            }


            indexWriter.close();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }


    }

    /**
     * Creates a Document by adding Fields in it and
     * indexes the Document with the IndexWriter
     *
     * @param indexWriter the indexWriter that will index Documents
     * @param mydoc the document to be indexed
     *
     */
    private void indexDoc(IndexWriter indexWriter, MyDoc mydoc){

        try {

            // make a new, empty document
            Document doc = new Document();

            // create the fields of the document and add them to the document
            StoredField title = new StoredField("id", mydoc.getID());
            doc.add(title);
            StoredField caption = new StoredField("title", mydoc.getTitle());
            doc.add(caption);
            StoredField mesh = new StoredField("text", mydoc.getText());
            doc.add(mesh);
            String fullSearchableText = mydoc.getID() + " " + mydoc.getTitle() + " " + mydoc.getText();
            TextField contents = new TextField("contents", fullSearchableText, Field.Store.NO);
            doc.add(contents);

            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                indexWriter.addDocument(doc);
            }
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Initializes an IndexerDemo
     */
    public static void main(String[] args) {
        try {
            Indexer indexerDemo = new Indexer();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}