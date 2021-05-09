package myLuceneApp;

// tested for lucene 7.7.2 and jdk13
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Tonia Kyriakopoulou
 */

public class Searcher {
    public Searcher(){
        try{
            String indexLocation = ("index"); //define where the index is stored
            String field = "contents"; //define which field will be searched

            //Access the index using indexReaderFSDirectory.open(Paths.get(index))
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation))); //IndexReader is an abstract class, providing an interface for accessing an index.
            IndexSearcher indexSearcher = new IndexSearcher(indexReader); //Creates a searcher searching the provided index, Implements search over a single IndexReader.
            indexSearcher.setSimilarity(new ClassicSimilarity());

            //Search the index using indexSearcher, for k=20, k=30, k=50
            search(indexSearcher, field, 20);
            counterResults = 1;
            search(indexSearcher, field, 30);
            counterResults = 1;
            search(indexSearcher, field, 50);

            //Close indexReader
            indexReader.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Searches the index given a specific user query.
     */
    int counterResults = 1;
    private void search(IndexSearcher indexSearcher, String field, int k){
        try{
            /*Cleaning previous if needed*/
            new FileWriter("trec_eval\\LISARESULTS_"+ k +".test", false).close();
            // define which analyzer to use for the normalization of user's query
            Analyzer analyzer = new EnglishAnalyzer();

            // create a query parser on the field "contents"
            QueryParser parser = new QueryParser(field, analyzer);

            // read user's query from stdin
            ArrayList<String> line = queryReader();
            for(int i=0; i<line.size(); i++){
                // parse the query according to QueryParser
                Query query = parser.parse(line.get(i));

                // search the index using the indexSearcher
                TopDocs results = indexSearcher.search(query, k);
                ScoreDoc[] hits = results.scoreDocs;

                //display results
                for(int j=0; j<hits.length; j++){
                    resultWriter(hits[j], indexSearcher, k);
                }
                counterResults++;
            }
            System.out.println("Wrote to file " + "LISARESULTS_"+ k +".test");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void resultWriter(ScoreDoc hit, IndexSearcher indexSearcher, int k) {
        try {
            Document hitDoc = indexSearcher.doc(hit.doc);
            FileWriter myWriter = new FileWriter("trec_eval\\LISARESULTS_"+ k +".test", true);
            myWriter.write(counterResults + "\t" + "0" + "\t" + hitDoc.get("id") + "\t" + "0" + "\t" + hit.score + "\t" + "myIRmethod" + "\n");
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private ArrayList<String> queryReader() {
        try {
            File myObj = new File("src\\myLuceneApp\\LISA.QUE");
            Scanner myReader = new Scanner(myObj);
            int refID = myReader.nextInt() + 1;
            ArrayList<String> refArray = new ArrayList<>();
            String ref = "";
            while (myReader.hasNextLine()) {
                String next = myReader.nextLine();
                if(!next.equals(Integer.toString(refID))){
                    ref = ref + " " + next;
                }else {
                    refArray.add(ref);
                    refID = Integer.parseInt(next) + 1;
                    ref = "";
                }
            }
            refArray.add(ref);
            myReader.close();
            return refArray;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Initialize a SearcherDemo
     */
    public static void main(String[] args){
        Searcher searcherDemo = new Searcher();
    }
}