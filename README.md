# Information Retrieval

The purpose of the assignment is to practice classical methods and models of information retrieval, and to apply techniques to improve the results of a retrieval system on real data.

<h3>Description</h3>
The project consists of four different phases using the same collection of texts known as <a href="http://ir.dcs.gla.ac.uk/resources/test_collections/lisa/">LISA</a>, for the purpose of testing our results on different methods.

<h4>Made with</h4>
<ul>
  <li>JAVA
  <li>Python
  <li>Lucene
  <li>DeepLearning4Java
</ul>

<h4>Phase 1</h4>

<ol>
  <li>Preprocess the text collection in a form suitable to be used by the Lucene search engine. 
  <li>Create an index for the collection.
  <li>Run the queries (queries.txt file) on the index and collect the machine's answers, the k first retrieved texts, for k = 20, 30, 50. 
  <li>Evaluate the answers by comparing them with the correct answers (file qrels.txt) using the trec_eval assessment tool and assessment measures MAP (mean average precision) and avgPre@k (average precision on the first k retrieved  texts) for k = 5, 10, 15, 20.
  <li>Save the results on the "trec_eval" folder
</ol>

<h5>How to</h5>
<ul>
  <li>Clone the project
  <li>Open with a code editor (produced and tested with INTELLIJ IDEA)
  <li>Run
  <pre>Indexer.java</pre>
  <li>Results will be written in the "trec_eval" folder
</ul>

<h4>Phase 2</h4>

The representation of the collection as a table was done using JAVA, while the rest was done using Python.
<ol>
  <li>Represent the collection of texts as a table, with dimensions TxD (TermxDocument). In the rows are the terms of the collection and in the columns are the texts in the collection.
  <li>Factorize this sparse matrix A by applying SVD analysis and calculate a approximation of low degree (order = 50).
  <li>Calculate the cosine similarity of the query vectors with the vectors of the texts in the new space.  Sort the texts in descending order similarity and collect the k texts with the highest similarity score, for k = 20, 30, 50. 
  <li>Evaluate the answers by comparing them with the correct answers (file qrels.txt) using the trec_eval assessment tool and assessment measures MAP (mean average precision) and avgPre@k (average precision on the first k retrieved  texts) for k = 5, 10, 15, 20.
  <li>Save the results on the "trec_eval" folder
</ol>

<h5>How to</h5>
<ul>
  <li>Clone the project
  <li>Open with a code editor (produced and tested with INTELLIJ IDEA)
  <li>Run
  <pre>CreateTermDocQueMatrix.java</pre>
  <li>Open the following using Python IDE or a Jupyter notebook
  <pre>svdEx4.py</pre>
  <li>Results will be written in the "trec_eval" folder
</ul>

<h4>Phase 3</h4>
<ol>
  <li>Preprocess the text collection in a form suitable to be used by the Lucene search engine, this time using the following similarity functions.
  <ul>
    <li>BM25Similarity
    <li>LMJelinekMercerSimilarity
  </ul>
  <li>Create an index for the collection.
  <li>Run the queries (queries.txt file) on the index and collect the machine's answers, the k first retrieved texts, for k = 20, 30, 50. 
  <li>Evaluate the answers by comparing them with the correct answers (file qrels.txt) using the trec_eval assessment tool and assessment measures MAP (mean average precision) and avgPre@k (average precision on the first k retrieved  texts) for k = 5, 10, 15, 20.
  <li>Save the results on the "trec_eval" folder
</ol>


<h5>How to</h5>
<ul>
  <li>Clone the project
  <li>Open with a code editor (produced and tested with INTELLIJ IDEA)
  <li>Run
  <pre>Indexer.java</pre>
  <li>Results will be written in the "trec_eval" folder
</ul>

<h4>Phase 4</h4>

In this phase I build a retrieval system, in which the representations of texts and queries will be implemented using word2vec.
<ol>
  <li>Train a word2vec model using the text collection as input and the DeepLearningForJava (DL4J) library.
  <li>From the word vectors created by the word2vec model, create the vectors of texts and queries by averaging the vectors of terms.
  <li>Calculate the cosine similarity of the questions to the texts in the new space. Sort the texts in descending order of similarity and collect the k texts with the highest similarity score, for k = 20, 30, 50.
  <li>Evaluate the answers by comparing them with the correct answers (file qrels.txt) using the trec_eval assessment tool and assessment measures MAP (mean average precision) and avgPre@k (average precision on the first k retrieved  texts) for k = 5, 10, 15, 20.
  <li>Save the results on the "trec_eval" folder
</ol>
