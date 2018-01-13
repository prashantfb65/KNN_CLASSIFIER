/**
Created By:               P R A S H A N T   G A R  G  |  STUDENT ID : 16201447
Created Date:             03-Dec-2016
Copyright:                University College Dublin
Subject:				  ADVANCE MACHINE LEARNING (Programming Assignment)
Description:              Starting File implementing k - N N   Weighted and Un-weighted classifiers for Text Documents represented by Document Term Matrix.
This source code can be used for Documents classification by preprocessing and representing the documents by Document Term Matrix

Version:                  00.00.00.01

Modification history:
----------------------------------------------------------------------------------------------------------------------------
Modified By         Modified Date (dd/mm/yyyy)                Version               Description
---------------------------------------------------------------------------------------------------------------------------
 **/

/*
 * Inclusion of Header Files
 */ 
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

public class MasterClass {
	/*
	 * Variables
	 */
	private BufferedReader br;		//Reader for Document
	private static List<Map<String, List<KeyValue>>> totalDocumentsInfo = new ArrayList<Map<String, List<KeyValue>>>(); //Total Information about all the documents
	private static Set<String> listOfLabels = new HashSet<String>(); //List of Unique Elements
	private static int kNN; 		//User specified value for k ( Number of Nearest Neighbors)
	private static Map<String,String> listOfDocsLabels; //holds the Document and label mapping
	private static Map<Object,Object> matrixFileLocation = new HashMap<Object,Object>();
	private static Scanner reader;

	/**
	 * Method Name: [readMatrixCreateDocumentsList]
	 * Reads the news_articles.mtx file and creates documents formation
	 * @param  delta   Two documents at a Time
	 * @return         true if the matrix is read and processed successfully
	 */
	public boolean readMatrixCreateDocumentsList(Map<Object,Object> matrixFileLocation){
		String locationOfFile = (String)matrixFileLocation.get("locationOfFile"); //Stores the location from where the file  is to be read
		try{
			br = new BufferedReader(new FileReader(locationOfFile));
			String lineRead = null;									//Represents the Line Being Read from Buffered Reader
			int unnecessaryLines = 0;								//In the matrix there are two lines which are unwanted, this is variable is used to 
			List<String> totalLinesInMatrix = new ArrayList<>();  	//Stores total lines in list
			String previousWord = null;								//Previous Document Name
			List<KeyValue> listWordFreq = new ArrayList<KeyValue>();//List of All words in a Document	

			/** 
			 * Reading the Complete Matrix and Storing Each Line in a List of String
			 */
			while((lineRead = br.readLine())!=null){
				if(unnecessaryLines>1)totalLinesInMatrix.add(lineRead);	//Removing unnecessary lines from Matrix File
				unnecessaryLines++;
			}
			totalLinesInMatrix.add("end end end");			//To mark end of String in an interactive way

			/**
			 * Structuring the List of Strings in Matrix to Documents which include words and their frequency in the
			 */
			for(String singleLine :totalLinesInMatrix ){
				String[] lineSplitter = singleLine.split(" ");
				KeyValue wordFreqInEachLine = new KeyValue(lineSplitter[1], lineSplitter[2]);
				if(previousWord == null){
					previousWord = lineSplitter[0];
					listWordFreq.add(wordFreqInEachLine);
				}else if(lineSplitter[0].equalsIgnoreCase(previousWord)){
					listWordFreq.add(wordFreqInEachLine);
				}else if(!lineSplitter[0].equalsIgnoreCase(previousWord)){
					/**
					 * As soon as the description of a new document arrives in the 
					 * matrix, the details of previous document are mapped to 
					 * a map using insertDocumentDetails
					 */

					insertDocumentDetails(listWordFreq, previousWord); //Method Call to insertDocumentDetails

					listWordFreq = new ArrayList<KeyValue>();
					listWordFreq.add(wordFreqInEachLine);
					previousWord = lineSplitter[0];
				}
			}
			/**
			 * If everything goes well the matrix is formatted and processed Successfully. The status true is returned
			 */
			return true;
		}catch(IOException e){
			System.out.println("Problem while reading the file");
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * End of Method [readMatrixCreateDocumentsList]
	 */


	/**
	 * Method Name: [insertDocumentDetails]
	 * Inserts every document details in a list, This list contains no. of words in the document along with the
	 * @param  delta   The document name , its words and frequency details
	 * @return         nothing
	 */
	private void insertDocumentDetails(List<KeyValue> listWordFreq, String documentName) {
		try{
			Map<String, List<KeyValue>> documentInformation = new HashMap<String, List<KeyValue>>(); 	//Single Document
			documentInformation.put(documentName, listWordFreq);
			totalDocumentsInfo.add(documentInformation);												//Inserting Each Document in the static list of documents
		}catch(Exception e){
			System.out.println("Error while Structuring Document Details");
		}
	}
	/**
	 * End of Method [insertDocumentDetails]
	 */


	/**
	 * Method Name: [mapLabelsToDocuments]
	 * Reads the news_articles.labels file and maps the labels to the documents
	 * @param  delta   Location where the label file is stored
	 * @return         nothing
	 */
	public Map<String,String> mapLabelsToDocuments(Map<Object,Object> labelFileLocation) throws IOException{
		Map<String,String> labelTagsForDocs = new HashMap<String,String>();		//Stores the label information for the 
		String locationOfFile = (String)labelFileLocation.get("labellLoc");		//Stores the location from where the file  is to be read
		String lineRead = null;													//Represents the Line Being Read from Buffered Reader
		try {
			br = new BufferedReader(new FileReader(locationOfFile));	

			/** 
			 * Reading the Complete Label File, Find Unique labels and Maps Label to the documents
			 */
			while((lineRead = br.readLine())!=null){
				String[] splitDocsLabelInfo = lineRead.split(",");

				/**
				 * Tags the Documents with Labels
				 */
				labelTagsForDocs.put(splitDocsLabelInfo[0], splitDocsLabelInfo[1]);

				/**
				 * Finds Unique list of Labels to listOfLabels
				 */
				listOfLabels.add(splitDocsLabelInfo[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return labelTagsForDocs;
	}
	/**
	 * End of Method [mapLabelsToDocuments]
	 */


	/**
	 * Method Name: [findAccuracyKNN]
	 * 
	 * This is most important method of the project implemented
	 * It involves computation of kNN for Weighted and Un-weighted conventions
	 * It tags the testing documents with labels based on their closeness to the training documents
	 * 
	 * @param  delta   List of Training Documents and their similarity with the Testing Document
	 * @return         label for a test document in reference to Weight and Un-Weighted Criteria
	 */

	public String[] findAccuracyKNN(Entry<String, List<KeyValueDouble>> similarityDetails){

		List<KeyValueDouble> compTrainingDataInstanceSim = similarityDetails.getValue();	//Instances of Training Doc with their similarities 
		String[] correctLabelWtdUnWtd = new String[2];		//Contains Label Predictions from the Two
		int kNNCheck = 1;									//Variable to check for the limit of k in k-NN

		List<String> labelsCollected = new ArrayList<String>(); //Labels of Training documents similar to Testing Documents
		/** 
		 * Predicting the label for  the test documents using weighted criteria
		 * We have iterated the training sets on the basis k which is User specified
		 */
		for(KeyValueDouble trainingDocNameAndSimil : compTrainingDataInstanceSim){
			if(!(kNNCheck>kNN)){
				String eachLabel = listOfDocsLabels.get(trainingDocNameAndSimil.getKey());
				labelsCollected.add(eachLabel);
				kNNCheck++;
			}

			/**
			 * skipping the iterations when k-nearest labels are found and added to a list
			 */
			if(kNNCheck>kNN)continue;
		}

		/**
		 * Finding the frequency of unique label 
		 *  and predicted the best label for a particular
		 *  testing class on the basis of the label of training class
		 *  
		 *  This is the block for predicting the label for Un-weighted Criteria
		 */
		Set<String> uniqueLabels = new HashSet<String>(labelsCollected);		//All unique similar label to a document
		int numOfOccurance = 0;													//Number of time a label occurred in most similar records search
		String bestLabelUnweighted = "";
		for (String unqieLabel : uniqueLabels) {
			int freqOfLab = Collections.frequency(uniqueLabels, unqieLabel);
			if(numOfOccurance == 0){
				numOfOccurance = freqOfLab;
				bestLabelUnweighted = unqieLabel;
			}else if(freqOfLab>numOfOccurance){
				numOfOccurance = freqOfLab;
				bestLabelUnweighted = unqieLabel;
			}
		}
		/**
		 * At the end of the above block we get a predicted label for our testing document
		 */


		/**
		 * This part involves finding the label of testing document 
		 * in accordance to its weighted criteria
		 */
		double weightForTotal = 0.00; 		//Stores weight for Each Label
		int kNNCheckForWeighted = 1;		//Variable to check for the limit of k in k-NN

		Map<String,Double> storeLabelWeights = new HashMap<String,Double>();

		/** 
		 * Iterating unique labels across Training Docs labels for the Testing Docs
		 */
		try{
			for(String generalLabel : listOfLabels ){
				double weight = 0.00;
				for(KeyValueDouble trainingDocNameAndSimil: compTrainingDataInstanceSim){
					String labelTrainingDoc = listOfDocsLabels.get(trainingDocNameAndSimil.getKey());		//Getting the Label from the training document
					if(generalLabel.equalsIgnoreCase(labelTrainingDoc) && kNNCheckForWeighted<=kNN){
						/**
						 * Distance can be calculated as 1-similarity
						 * So, in our case it would be (1-cosineSimilarity)
						 */
						double distance = 1 - trainingDocNameAndSimil.getValue();
						/**
						 * The weight then can be calculated as 1/distance
						 * We now normalize it to prevent weight jumping to infinity
						 * i.e we calculate weight as 1/( distance + 1)
						 */
						weight = 1/(1+distance);
						weightForTotal += weight;
					}
					kNNCheckForWeighted++;
					/**
					 * The above block evaluates the distance and weight for our implementation
					 */
				}
				/**
				 * Storing Labels weights in correspondence to a Unique Label
				 */
				storeLabelWeights.put(generalLabel, weightForTotal);
				kNNCheckForWeighted =1;
				weightForTotal = 0.00;
			}
		}catch(Exception e){
			System.out.println("Could Not Calculate the Wieghted Nearest Neighbor");
			System.out.println("Please Try Again");
		}


		/**
		 * Iterating through the Collections of labels weight to evaluate best 
		 * predicted label for the Testing 
		 */
		Iterator<Entry<String, Double>> winnerPrp = storeLabelWeights.entrySet().iterator();			//Iterator to iterate label with their total weight
		double maxWeightForWinner = 0;
		while (winnerPrp.hasNext()) {
			Entry<String, Double> findWinner = winnerPrp.next();
			if(findWinner.getValue()>maxWeightForWinner){
				maxWeightForWinner = findWinner.getValue();
				/**
				 * Setting best label for the Weighted k-NN Classifier
				 */
				correctLabelWtdUnWtd[1] = findWinner.getKey();
			}
		}
		/**
		 * Setting best label for the Un-Weighted k-NN Classifier
		 */
		correctLabelWtdUnWtd[0] = bestLabelUnweighted;
		return correctLabelWtdUnWtd;
	}
	/**
	 * End of Method [findAccuracyKNN]
	 */


	/**
	 * The flow of execution for our programs starts from here
	 * The user is prompted asking for the value of k in Nearest Neighbor
	 * @param args
	 */
	public static void main(String[] args) { 

		/**
		 * The first step in our implementation is to read the matrix file news_articles.mtx
		 * and structure the documents.
		 * After the user is Prompted for the value of k
		 * After that there is a split of documents to testing and training documents
		 * The cosine similarity is found on that basis
		 * After the cosine similarity is found the main methods approaches to find the 
		 * accuracy for the model using weighted and Un-Weighted Criteria's
		 * 
		 */
		MasterClass rm = new MasterClass();

		/**
		 * Referencing the file location
		 */
		matrixFileLocation.put("locationOfFile","news_articles.mtx");
		matrixFileLocation.put("labellLoc", "news_articles.labels");


		/**
		 *Reading the Matrix file and forming documents structures through it
		 */
		boolean matrixReadSuccessfully = rm.readMatrixCreateDocumentsList(matrixFileLocation);


		/**
		 * If the matrix is read properly the following code is executed
		 */
		if(matrixReadSuccessfully){
			System.out.println("[Matrix Read Successfully]\n");

			/**
			 * Defining constant split of documents in 80% (TrainingSet) - 20% (TestSet)
			 */

			float splitCriteria = 80;			//Defining constant split criteria
			float documentsSplit = (float) totalDocumentsInfo.size()*(splitCriteria/100); //Defines the document split boundary
			int trainingSet =  Math.round(documentsSplit);
			int testingSet = totalDocumentsInfo.size() - trainingSet;

			/**
			 * We now will take the input of the k from the user now
			 */
			System.out.println("This is the Implementation of kNN (non-weighted and weighted) using % [Training Set -->" +splitCriteria +  " % , Testing Set -->" + (100-splitCriteria) +  "%]split");
			System.out.println("Please enter the value of k [kNN] within the Range [ 0 -  " + (testingSet-1) + "]");
			reader = new Scanner(System.in);
			System.out.print("Enter a number:(k) = ");
			try{
				kNN = reader.nextInt(); // Scans the next token of the input as an int.
				if(kNN<1 || kNN>(testingSet-1)){
					System.out.println("The value of k is incorrect. You have one more try");
					System.out.print("Enter a number:(k) = ");
					kNN = reader.nextInt(); // Scans the next token of the input as an int.
				}
			}catch(Exception e){
				System.out.println("Incorrect format for k, it should a Positive Integer");
				System.out.println("Restart the application again");
				return;
			}
			System.out.println("[Total Documents ==> "+ totalDocumentsInfo.size() + "]\n"
					+ "[Training Documents ==> "+ trainingSet + "]\n"
					+ "[Testing Documents ==> "+ testingSet + "]\n"
					+"[Calculating Cosine Similarity]\n"+ "Please Wait.............\n");
			/**
			 * Till here all the preparation work is finished, now it' for finding the cosine similarity
			 */

			/**
			 * Before finding the cosine similarity we have this opportunity to shuffle the dataset to reduce un-fair distribution
			 * of training and testing dataset
			 */
			CalcuateCosineSimilarities findCosineSimilarity = new CalcuateCosineSimilarities();
			Collections.shuffle(totalDocumentsInfo);
			Collections.shuffle(totalDocumentsInfo);
			Collections.shuffle(totalDocumentsInfo);
			long startTime = System.currentTimeMillis();

			/**
			 * Cosine similarity between the training and testing document is found using computeSimilarity method
			 */
			Map<String,List<KeyValueDouble>> calculatedCosineSim =  findCosineSimilarity.computeSimilarity(totalDocumentsInfo, trainingSet);

			/** 
			 * After the similarities are found, weighted and unweighted class label 
			 * predictions for the testing documents is made
			 */
			if(calculatedCosineSim!=null){
				System.out.println("[Calculating Accuracy for k=" + kNN + " (kNN-unweighted, unweighted)] \n\n\nPlease Wait.............\n");
				int totalElements = calculatedCosineSim.size();

				/**
				 * The label file is read and the labels are assigned to documents
				 */
				try {
					listOfDocsLabels = rm.mapLabelsToDocuments(matrixFileLocation);
				} catch (IOException e) {
					System.out.println("Could not read the label file");
					e.printStackTrace();
					return;
				}


				/**
				 * Iterator to run over the similarities of testing documents 
				 * across the training documents
				 */
				Iterator<Entry<String, List<KeyValueDouble>>> readingTestDocSim = calculatedCosineSim.entrySet().iterator();

				int numOfCorrectPredictionsUnWtd = 0;				//Stores number of label predicted correctly using unweighted criteria
				int numOfCorrectPredictionWtd = 0;					//Stores number of label predicted correctly using weighted criteria

				while (readingTestDocSim.hasNext()) {
					Entry<String, List<KeyValueDouble>> listOfSimils = readingTestDocSim.next();


					/**
					 * Finding the Predicted labels for the testing document 
					 * labels predicted and stored in labelPrediction[0] for unweighted and labelPrediction[1] for weighted
					 */
					//System.out.println("For the document :: " + listOfSimils.getKey());
					String[] labelPrediction = rm.findAccuracyKNN(listOfSimils);


					/**
					 * We now compare the original label of the Testing Document with the
					 * predicted label and find accuracy
					 */
					String originalLabel = listOfDocsLabels.get(listOfSimils.getKey());
					if(labelPrediction[0].equalsIgnoreCase(originalLabel)){
						numOfCorrectPredictionsUnWtd++;
					}
					if(labelPrediction[1].equalsIgnoreCase(originalLabel)){
						numOfCorrectPredictionWtd++;
					}
				}

				double accurracyWithUnWtdCriteria = (numOfCorrectPredictionsUnWtd * 100 )/totalElements;
				double accurracyWithWtdCriteria = (numOfCorrectPredictionWtd * 100 )/totalElements;

				System.out.println(" ------------- Unweighted kNN ---------");
				System.out.println("| Total Documents Classified " + totalElements);
				System.out.println("| Correctly Classified " + numOfCorrectPredictionsUnWtd);
				int incorrectInstancesUnWtd = totalElements-numOfCorrectPredictionsUnWtd;
				System.out.println("| InCorrectly Classified = " + incorrectInstancesUnWtd);
				System.out.println("| Accuracy ("+kNN+"-NN unweighted) ::: " + accurracyWithUnWtdCriteria + "%");
				System.out.println(" ---------------------------------------\n\n");


				System.out.println(" ------------- Weighted kNN ---------");
				System.out.println("| Total Documents Classified " + totalElements);
				System.out.println("| Correctly Classified " + numOfCorrectPredictionWtd);
				int incorrectInstancesWtd = totalElements-numOfCorrectPredictionWtd;
				System.out.println("| InCorrectly Classified = " + incorrectInstancesWtd);
				System.out.println("| Accuracy ("+kNN+"-NN weighted) ::: " + accurracyWithWtdCriteria + "%");
				System.out.println(" ---------------------------------------\n\n");
				/**
				 * Accuracy is found and  displayed to the user from the above code
				 */


				long endTime = System.currentTimeMillis();
				System.out.println("That took " + (endTime - startTime)/1000 + " seconds");
			}else{
				System.out.println("Some error while calculating the cosine similarity");
				System.out.println("Please Run the Application Again\n");
				return;
			}
		}
		else {
			System.out.println("Some error while reading the matrix ::: ");
			System.out.println("Please Run the Application Again\n");
			return;
		}
	}
	/**
	 * End of Method [findAccuracyKNN]
	 */


}


/**
 * End of File
 */
