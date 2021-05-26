package myLuceneApp;

//tested for lucene 7.7.3 and jdk13
import org.apache.lucene.document.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.classification.utils.DocToDoubleVectorUtils;
import txtparsing.MyDoc;
import txtparsing.TXTParsing;

public class CreateTermDocMatrix {

	public static void writeVector(String[][] vector) {
		try{
			// Create new file
			System.out.println("Writing vector to .csv file");
			String path="src\\svd\\exercise2\\luceneDocVector.csv";
			File file = new File(path);

			// If file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			// Write in file
			for (int i = 0; i<vector.length; i++){
				for(int j = 0; j<vector[i].length; j++){
					bw.write(vector[i][j]);
					if(j!=vector[i].length-1) bw.write("\t");
				}
				if(i!= vector.length-1) bw.write("\n");
			}
			// Close connection
			bw.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}

	public static ArrayList<String> readFiles() {
		ArrayList<String> docTxtFile = new ArrayList<>();
		try {
			File myObj = new File("docs//");
			File[] listofFiles = myObj.listFiles();
			assert listofFiles != null;

			for (File file : listofFiles) {
				if (file.isFile()) {
					docTxtFile.add("docs//" + file.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return docTxtFile;
	}

	public static void main(String[] args) throws IOException, ParseException {
		try {
			ArrayList<String> docTxtFile =  new ArrayList<>(); //txt file to be parsed and indexed, it contains one document per line
			docTxtFile = readFiles();

			String queryTxtFile = "src\\myLuceneApp\\LISA.QUE.test";

			String indexLocation = ("index");
			//   Specify the analyzer for tokenizing text.
			//   The same analyzer should be used for indexing and searching
			//   Specify retrieval model (Vector Space Model)
			EnglishAnalyzer analyzer = new EnglishAnalyzer();
			Similarity similarity = new ClassicSimilarity();

			//   create the index
			Directory index = FSDirectory.open(Paths.get(indexLocation));

			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			config.setSimilarity(similarity);
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

			FieldType type = new FieldType();
			type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
			type.setTokenized(true);
			type.setStored(true);
			type.setStoreTermVectors(true);

			IndexWriter writer = new IndexWriter(index, config);

			List<List<MyDoc>> docList = new ArrayList();
			// parse txt document using TXT parser and index it
			for (int i=0; i< 14; i++) {
				docList.add(TXTParsing.parse(docTxtFile.get(i), "d"));
			}

			for(List<MyDoc> list : docList){
				for (MyDoc doc : list){
					addDocWithTermVector(writer, doc, type);
				}
			}
			writer.close();

			IndexReader reader = DirectoryReader.open(index);
			testSparseFreqDoubleArrayConversion(reader);
			reader.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private static ArrayList<String> docID = new ArrayList<String>();
	private static void addDocWithTermVector(IndexWriter writer, MyDoc mydoc, FieldType type) throws IOException {
		Document doc = new Document();

		// create the fields of the document and add them to the document
		StoredField title = new StoredField("id", mydoc.getID());
		doc.add(title);
		StoredField caption = new StoredField("title", mydoc.getTitle());
		doc.add(caption);
		StoredField mesh = new StoredField("text", mydoc.getText());
		doc.add(mesh);
		String fullSearchableText = mydoc.getTitle() + " " + mydoc.getText();
		//TextField title = new TextField("title", value, Field.Store.YES);
		Field field = new Field("contents", fullSearchableText, type);
		doc.add(field);  //this field has term Vector enabled.
		writer.addDocument(doc);
		docID.add(mydoc.getID());
	}

	private static void testSparseFreqDoubleArrayConversion(IndexReader reader) throws Exception {
		Terms fieldTerms = MultiFields.getTerms(reader, "contents");   //the number of terms in the lexicon after analysis of the Field "contents"
		System.out.println("Terms:" + fieldTerms.size());
		System.out.println("Creating vector...");

		if (fieldTerms != null && fieldTerms.size() != -1) {
			IndexSearcher indexSearcher = new IndexSearcher(reader);
			int c = 0;
			String[][] svdVector = new String[(int) fieldTerms.size()][6004];
			NumberFormat nf = new DecimalFormat("0.#");
			for (ScoreDoc scoreDoc : indexSearcher.search(new MatchAllDocsQuery(), Integer.MAX_VALUE).scoreDocs) {   //retrieves all documents
				Terms docTerms = reader.getTermVector(scoreDoc.doc, "contents");
				while (scoreDoc.doc + c >1992 && scoreDoc.doc + c <1998) {
					for (int i = 0; i < fieldTerms.size(); i++) {
						svdVector[i][scoreDoc.doc + c] = "0";
					}
					c++;
				}
				Double[] vector = DocToDoubleVectorUtils.toSparseLocalFreqDoubleArray(docTerms, fieldTerms); //creates document's vector
				for (int i = 0; i < vector.length; i++) {
					svdVector[i][scoreDoc.doc + c] = nf.format(vector[i]);
				}
			}
			writeVector(svdVector);
		}
	}

}

