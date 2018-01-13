/**
Created By:               P R A S H A N T   G A R  G  |  STUDENT ID : 16201447
Created Date:             03-Dec-2016
Copyright:                University College Dublin
Subject:				  ADVANCE MACHINE LEARNING (Programming Assignment)
Description:              Starting File implementing k - N N   Weighted and Un-weighed classifiers
Version:                  00.00.00.01

Modification history:
----------------------------------------------------------------------------------------------------------------------------
Modified By         Modified Date (dd/mm/yyyy)                Version               Description
---------------------------------------------------------------------------------------------------------------------------
 **/

/*
 * Inclusion of Header Files
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class CalcuateCosineSimilarities {
	
	
	/**
	 * Method Name: [computeCosineSimilarity]
	 * Computes Cosine Similarity between two Documents
	 * @param  delta   Two documents at a Time
	 * @return         1 if double value of o2 value is greater or -1 if its o1's, returns 0 in case of a tie
	 */
	
	
	public double computeCosineSimilarity(List<KeyValue> documentOne, List<KeyValue> documentTwo){
		
		/**
		 * cosine similarity ===> cos(A1.A2) = (A1.A2)/(||A1|| ||A2||)
		 * Numerator is dot product
		 * Denominator is the product of the vector sum
		 */
		
		//Variables Declaration
		int docProduct = 0;           	//Stores dotProduct of Vector 1 and Vector 2
		int vectorOneSquareSum = 0;		//Stores Square sum of Vector1
		int vectorTwoSquareSum = 0;		//Stores Square sum of Vector1
		double vector1 = 0.0;			//Vector 1 
		double vector2 = 0.0;			//Vector 2
		double vectorProduct = 0.0;		//Vector Product of Document Instance one and two
		double cosineSimilarity = 0.0;	//Cosine similarities

		/**
		 * The below code calculates dot product for two document and Product of the vector sum
		 */
		for(KeyValue wordFequencyPairD1: documentOne){
			vectorOneSquareSum = vectorOneSquareSum +  Integer.parseInt(wordFequencyPairD1.getValue())* Integer.parseInt(wordFequencyPairD1.getValue());
			for(KeyValue wordFequencyPairD2: documentTwo){
				if(wordFequencyPairD1.getKey().equals(wordFequencyPairD2.getKey())){ // Complexity Reduced by only includes matched words
					int numerator = Integer.parseInt(wordFequencyPairD1.getValue())*Integer.parseInt(wordFequencyPairD2.getValue());
					docProduct  = docProduct + numerator;
				}
			}
		}
		for(KeyValue wordFequencyPairD2: documentTwo){
			vectorTwoSquareSum = vectorTwoSquareSum +  Integer.parseInt(wordFequencyPairD2.getValue())* Integer.parseInt(wordFequencyPairD2.getValue());
		}
		vector1 = Math.sqrt(vectorOneSquareSum);
		vector2 = Math.sqrt(vectorTwoSquareSum);
		vectorProduct = vector1*vector2;
		
		/**
		 * The cosine similarity is calculated here
		 */
		cosineSimilarity = docProduct/vectorProduct;
		return cosineSimilarity;
	}
	
	/**
	 * End of Method [computeCosineSimilarity]
	 */
	
	
	/**
	 * Method Name: [computeSimilarity]
	 * Computes Similarity for Test Data Set Modeling the Training Set
	 * @param  delta   Complete Document List, Count of Items in Training set 
	 * @return         Test Documents similarity with the Training Data Using Cosine Similarity
	 */
	
	public Map<String,List<KeyValueDouble>> computeSimilarity(List<Map<String, List<KeyValue>>> totalDocuments, int numItemsTraining){
		
		CalcuateCosineSimilarities evaluateCosineSim = new CalcuateCosineSimilarities();		//Object instantiation for computing Cosine Similarity of Two documents
		
		/**
		 * Splitting the Document list into Training and Testing Set
		 */
		List<Map<String, List<KeyValue>>> trainingSet = totalDocuments.subList(0, numItemsTraining);
		List<Map<String, List<KeyValue>>> testingSet = totalDocuments.subList(numItemsTraining, totalDocuments.size());
		
		/**
		 * Total Comparisons and Initial Comparison Count
		 */
		int numberOfComparisons = 0;
		int totalComparisons = testingSet.size(); //Problem can be here
		
		String testingDocumentName = null; 											//Name of the Testing Document
		List<KeyValue> listOfWordsTestDoc = new ArrayList<KeyValue>();  			//Words in Testing Document with Frequency
		String testDocInitialName = null;											//Name of Previous Testing Document
		List<KeyValueDouble> similarityComputation = new ArrayList<KeyValueDouble>();//Entity to store all similarities of a Test Doc with Training Doc
		List<KeyValue> listOfWordsTrainDoc = new ArrayList<KeyValue>(); 			//Words in Training Document with Frequency
		String trainingDocumentName = null;											//Name of the Testing Document
		
		
		
		Map<String,List<KeyValueDouble>> repository = new HashMap<String,List<KeyValueDouble>>(); //Repository of similarities between test and training data
		
		/**
		 * Iterating the Testing Documents over
		 * Training Documents to Compute the Similarity between them
		 */
		
		/**
		 * Iteration of Testing Data Now - start
		 */
		for(Map<String, List<KeyValue>> testingDocument: testingSet){
			numberOfComparisons++;
			Iterator<Entry<String, List<KeyValue>>> testingDocumentIterator = testingDocument.entrySet().iterator();
			while (testingDocumentIterator.hasNext()) {
				Entry<String, List<KeyValue>> testingDocumentInstance = testingDocumentIterator.next();
				testingDocumentName = testingDocumentInstance.getKey();
				listOfWordsTestDoc = testingDocumentInstance.getValue();
				if(testDocInitialName==null)testDocInitialName = testingDocumentName;
				else if(testDocInitialName.equalsIgnoreCase(testingDocumentName)){
					if(numberOfComparisons==totalComparisons)similarityComputation = new ArrayList<KeyValueDouble>();
				}
				else if(!testDocInitialName.equalsIgnoreCase(testingDocumentName)){
					similarityComputation = new ArrayList<KeyValueDouble>();
				}
				/**
				 * Iteration of Training Data Now - start
				 */
				for(Map<String, List<KeyValue>> trainingDocument: trainingSet){
					Iterator<Entry<String, List<KeyValue>>> trainingDocumentIterator = trainingDocument.entrySet().iterator();
					while (trainingDocumentIterator.hasNext()) {
						Entry<String, List<KeyValue>> trainingDocumentInstance = trainingDocumentIterator.next();
						trainingDocumentName = trainingDocumentInstance.getKey();
						listOfWordsTrainDoc = trainingDocumentInstance.getValue();
						if(!testingDocumentName.equalsIgnoreCase(trainingDocumentName)){
							/**
							 * Fetching Cosine Similarity between each instance of Training and Testing Document
							 */
							double cosineSimilarity = evaluateCosineSim.computeCosineSimilarity(listOfWordsTestDoc, listOfWordsTrainDoc);
							
							/**
							 * Inserting the Document Similarity of Testing Document with Training data in Key Value Pair
							 */
							KeyValueDouble similarityEntity = new KeyValueDouble(trainingDocumentName, cosineSimilarity);
							
							/**
							 * Each Entity of Training Doc with its similarity added to the consolidated
							 * repository
							 */
							similarityComputation.add(similarityEntity);
							
						}
					}
				}
				/**
				 * Testing of One Testing document with all the Training Documents Completes Here
				 */
				
				/**
				 * Sorting the training docs with decreasing order of similarity with the testing doc
				 * and adding it to the repository
				 */
				Collections.sort(similarityComputation, new CompareSort().reversed());
				repository.put(testingDocumentName, similarityComputation);
			}
		}
		/**
		 * Iteration of Testing Docs Iteration Ends Here 
		 */
		return repository;
	}
	/**
	 * End of Method [computeCosineSimilarity]
	 */
}

/**
 * End of File
 */
