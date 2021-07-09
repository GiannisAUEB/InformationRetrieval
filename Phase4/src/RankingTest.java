import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import txtparsing.MyDoc;
import txtparsing.TXTParsing;
import utils.LuceneTokenizerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Ranking tests evaluating different {@link Similarity} implementations against {@link WordEmbeddingsSimilarity}
 */
public class RankingTest {

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

  public RankingTest() throws Exception{

    ArrayList<String> txtfile =  new ArrayList<>(); //txt file to be parsed and indexed, it contains one document per line
    txtfile = readFiles();
    String indexLocation = ("index"); //define were to store the index

    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + indexLocation + "'...");

      Directory dir = FSDirectory.open(Paths.get(indexLocation));
      // define which analyzer to use for the normalization of documents
      Analyzer analyzer = new EnglishAnalyzer();

      // configure IndexWriter
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      //iwc.setSimilarity(similarity);

      // Create a new index in the directory, removing any
      // previously indexed documents:
      iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

      // create the IndexWriter with the configuration as above
      IndexWriter indexWriter = new IndexWriter(dir, iwc);
      List<List<MyDoc>> docList = new ArrayList();
      // parse txt document using TXT parser and index it
      for (int i=0; i< 14; i++) {
        docList.add(TXTParsing.parse(txtfile.get(i), "d"));
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

  private void indexDoc(IndexWriter indexWriter, MyDoc mydoc){

    try {
      FieldType type = new FieldType(TextField.TYPE_STORED);
      type.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
      type.setTokenized(true);
      type.setStored(true);
      type.setStoreTermVectors(true);
      type.setStoreTermVectorOffsets(true);
      type.setStoreTermVectorPositions(true);
      // make a new, empty document
      Document doc = new Document();

      // create the fields of the document and add them to the document
      Field id = new Field("id", mydoc.getID(), type);
      doc.add(id);
      //Field caption = new Field("title", mydoc.getTitle(), type);
      //doc.add(caption);
      //StoredField mesh = new StoredField("text", mydoc.getText());
      //doc.add(mesh);
      Field contents = new Field("contents", mydoc.getTitle() + " " + mydoc.getText(), type);
      doc.add(contents);

      if (indexWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
        // New index, so we just add the document (no old document can be there):
        indexWriter.addDocument(doc);
      }
    } catch(Exception e){
      e.printStackTrace();
    }

  }

  public void testRanking() throws Exception {
    String indexLocation = ("index");

    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
    String fieldName = "contents";

    /**Comment to run with the pretrained model*/
    /*FieldValuesSentenceIterator iterator = new FieldValuesSentenceIterator(reader, fieldName);
    Word2Vec vec = new Word2Vec.Builder()
            .layerSize(50)
            .windowSize(6)
            .tokenizerFactory(new LuceneTokenizerFactory(new EnglishAnalyzer()))
            .iterate(iterator)
            .seed(12345)
            .build();
    vec.fit();*/


    /**Comment the next line to run without the pretrained model*/
    Word2Vec vec = WordVectorSerializer.readWord2VecModel("model\\model.txt");

    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new WordEmbeddingsSimilarity(vec, fieldName, WordEmbeddingsSimilarity.Smoothing.MEAN));

    QueryParser qp = new QueryParser(fieldName, new EnglishAnalyzer());

    String queryTxtFile = "queries\\LISA.QUE";
    List<MyDoc> queries = TXTParsing.parse(queryTxtFile, "q");

    int k = 20;
    for(int j=0; j<3; j++) {
      System.out.println("Writing for k=" + k);
      FileWriter trecWriter = new FileWriter("trec_eval\\LISARESULTS_" + k + ".test");
      for (MyDoc query : queries) {
        Query queParsed = qp.parse(query.getText().toLowerCase());

        TopDocs hits = searcher.search(queParsed, k);

        for (int i = 0; i < hits.scoreDocs.length; i++) {
          ScoreDoc hitDoc = hits.scoreDocs[i];
          Document doc = searcher.doc(hitDoc.doc);

          /*Sometimes the process exits before it finishes writing the last sentence, didn't know how to fix this so either I rerun the program
           * or directly write the result in the generated file */
          trecWriter.write(query.getID() + "\t" + "0" + "\t" + doc.get("id") + "\t" + "0" + "\t" + hitDoc.score + "\t" + "myIRmethod" + "\n");
        }
      }
      if(j==0) k=30;
      if(j==1) k=50;
      trecWriter.close();
    }
  }

  public static void main(String[] args) throws Exception {
    RankingTest t = new RankingTest();
    try {
      t.testRanking();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
