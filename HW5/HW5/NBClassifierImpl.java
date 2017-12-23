/***************************************************************************************
  CS540 - Section 2
  Homework Assignment 5: Naive Bayes

  NBClassifierImpl.java
  This is the main class that implements functions for Naive Bayes Algorithm!
  ---------
  	*Free to modify anything in this file, except the class name 
  	You are required:
  		- To keep the class name as NBClassifierImpl for testing
  		- Not to import any external libraries
  		- Not to include any packages 
	*Notice: To use this file, you should implement 2 methods below.

	@author: TA 
	@date: April 2017
*****************************************************************************************/

import java.util.ArrayList;
import java.util.List;


public class NBClassifierImpl implements NBClassifier {
	
	private int nFeatures; 		// The number of features including the class 
	private int[] featureSize;	// Size of each features
	private List<List<Double[]>> logPosProbs;	// parameters of Naive Bayes
	private Double pProb;		//probability of positive labels in trainset
	private Double nProb;		//probability of negative labels in trainset
	
	
	/**
     * Constructs a new classifier without any trained knowledge.
     */
	public NBClassifierImpl() {
		
	}

	/**
	 * Construct a new classifier 
	 * 
	 * @param int[] sizes of all attributes
	 */
	public NBClassifierImpl(int[] features) {
		this.nFeatures = features.length;
		
		// initialize feature size
		this.featureSize = features.clone();

		this.logPosProbs = new ArrayList<List<Double[]>>(this.nFeatures);
	}


	/**
	 * Read training data and learn parameters
	 * 
	 * @param int[][] training data
	 */
	@Override
	public void fit(int[][] data) {

		//	TO DO
		double countP = 0.0;
		double countN = 0.0;
		for(int i=0;i<data.length;i++){
			if(data[i][nFeatures-1] == 1){
				countP++;
			}else{
				countN++;
			}
		}
		//System.out.println(countP + " " + countN);
		this.pProb = Math.log((countP+1)/((double)(data.length+2)));
		this.nProb = Math.log((countN+1)/((double)(data.length+2)));
		for(int i=0; i<nFeatures-1;i++){
			//for each feature, construct a list of parameters with log values
			//pList:P(F1|P) P(F2|P)... P(Fn|P)
			//nList:P(F1|N) P(F2|N)... P(Fn|N)
			int currFeatureSize = featureSize[i];
			//int[] pList = new int[(currFeatureSize+1)];
			//int[] nList = new int[(currFeatureSize+1)];
			double[] pList = new double[currFeatureSize];
			double[] nList = new double[currFeatureSize];
			for(int j=0;j<data.length;j++){
				if(data[j][nFeatures-1] == 1){
					//positive label
					//pList[currFeatureSize]++;
					//countP++;
					//increment the count of feature value based on train data j's ith feature  
					pList[data[j][i]]++;
				}else{
					//negative label
					 //nList[currFeatureSize]++;
					//countN++; 
					//increment the count of feature value based on train data j's ith feature
					 nList[data[j][i]]++;
				}
			}
			List<Double[]> currFeatures = new ArrayList<Double[]>();

			Double[] pDList = new Double[currFeatureSize];
			Double[] nDList = new Double[currFeatureSize];
			for(int k=0;k<pList.length;k++){
				pDList[k] = new Double(Math.log((pList[k]+1)/(countP+currFeatureSize)));
				//System.out.println(pDList[k]);
			}
			for(int k=0;k<nList.length;k++){
				nDList[k] = new Double(Math.log((nList[k]+1)/(countN+currFeatureSize)));
				//System.out.println(nDList[k]);
			}
			currFeatures.add(pDList);
			currFeatures.add(nDList);
			this.logPosProbs.add(currFeatures);
		}
		
	}

	/**
	 * Classify new dataset
	 * 
	 * @param int[][] test data
	 * @return Label[] classified labels
	 */
	@Override
	public Label[] classify(int[][] instances) {
		
		int nrows = instances.length;
		Label[] yPred = new Label[nrows]; // predicted data

		//	TO DO
		//calculate the log(P(T = pos | X))
		for(int i=0;i<nrows;i++){
			int [] currIns = instances[i];
			Double posSum = this.pProb;
			Double negSum = this.nProb;
			for(int j=0;j<currIns.length-1;j++){
				//for each feature, add log(P(Xi|P)) to posSum, add log(P(Xi|N)) to negSum
				//System.out.println(currIns.length);
				posSum += this.logPosProbs.get(j).get(0)[currIns[j]];
				negSum += this.logPosProbs.get(j).get(1)[currIns[j]];
			}
			yPred[i] = (Double.compare(posSum, negSum)>=0) ? Label.Positive : Label.Negative;
		}
		return yPred;
	}
}