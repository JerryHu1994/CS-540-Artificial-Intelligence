import java.util.*;
import java.util.stream.Collectors;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a
 *  description of default methods.
 */
public class DecisionTreeImpl{
	private DecTreeNode root;
	//ordered list of attributes
	private ArrayList<String> mTrainAttributes;
	//
	private ArrayList<ArrayList<Double>> mTrainDataSet;
	//Min number of instances per leaf.
	private int minLeafNumber = 10;

	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary this is void purposefully
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning set.
	 * 
	 * @param train: the training set
	 * @param tune: the tuning set
	 */
	DecisionTreeImpl(ArrayList<ArrayList<Double>> trainDataSet, ArrayList<String> trainAttributeNames, int minLeafNumber) {
		this.mTrainAttributes = trainAttributeNames;
		this.mTrainDataSet = trainDataSet;
		this.minLeafNumber = minLeafNumber;
		this.root = buildTree(mTrainDataSet);
	}
	
	private DecTreeNode buildTree(ArrayList<ArrayList<Double>> dataSet){
		// TODO: add code here
		
		if (dataSet.size() == 0) {
			return new DecTreeNode(1, null,0);
		}
		//if the node less or equal to minLeafNumber, find the majority return the root
		if(dataSet.size() <= minLeafNumber)	{
			// create leaves for all of the data in the set
			int maj = calMajority(dataSet);
			return new DecTreeNode(maj, null, 0);
		}

		//if all the data has the same label, return the root
		int attriSize = dataSet.get(0).size() - 1;
		int check =dataSet.get(0).get(attriSize).intValue();
		boolean sameLabel = true;
		for(int i = 1; i< dataSet.size(); i++) {
			if (dataSet.get(i).get(attriSize).intValue() != check) {
				sameLabel = false;
			}
		}
		if (sameLabel) {
			// create leaves for all of the data in the set
			return new DecTreeNode(check, null, 0);
		}

		//find the best attribute, create node for it
		DecTreeNode localNode = rootInfoGain(dataSet,mTrainAttributes,minLeafNumber,1);

		//split the dataset into two parts based on the bestThres
		int attriPos = mTrainAttributes.indexOf(localNode.attribute);
		ArrayList<ArrayList<Double>> leftData = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> rightData = new ArrayList<ArrayList<Double>>();
		for(ArrayList<Double> eachEntry : dataSet){
			if(eachEntry.get(attriPos) <= localNode.threshold){
				leftData.add(eachEntry);
			}else{
				rightData.add(eachEntry);
			}
		}
		DecTreeNode leftNode, rightNode;
		leftNode = buildTree(leftData);
		rightNode = buildTree(rightData);
		localNode.left = leftNode;
		localNode.right = rightNode;


		//for each attribute, build a subtree
		return localNode;
	}

	public void testData(ArrayList<ArrayList<Double>> dataSet) {
		int labelIdx = dataSet.get(0).size()-1;
		int cnt = 0;
		for (int i = 0; i< dataSet.size(); i++) {
			if (classify(dataSet.get(i)) == dataSet.get(i).get(labelIdx)) {
				cnt++;
			}
			System.out.println( classify(dataSet.get(i)) );
		}
		System.out.println((double)cnt / (double)dataSet.size());
	}

	public int classify(List<Double> instance) {
		  // TODO: add code here
		  DecTreeNode curr = root;
		  //proceeed if we have not reach the leaf node
		  while(!curr.isLeaf()){
		   int attributeIndex = mTrainAttributes.indexOf(curr.attribute);
		   double thres = curr.threshold;
		   //determine going to the left tree or the right tree
		   if(instance.get(attributeIndex) <= thres){
		    curr = curr.left;
		   }else{
		    curr = curr.right;
		   }
		   
		  }
		  return curr.classLabel;

	}
	
	public DecTreeNode rootInfoGain(ArrayList<ArrayList<Double>> dataSet,
									ArrayList<String> trainAttributeNames,
									int minLeafNumber,
									int modeNum ) {
		this.mTrainAttributes = trainAttributeNames;
		this.mTrainDataSet = dataSet;
		this.minLeafNumber = minLeafNumber;
		// TODO: add code here
		int attributeLen = trainAttributeNames.size();

		//calculate the entropy of the root
		double rootzero = 0.0, rootone = 0.0;
		for(int i = 0; i<dataSet.size();i++){
			if(dataSet.get(i).get(attributeLen)==0){
				rootzero++;
			}else{
				rootone++;
			}
		}

		double rootsum = rootone+rootzero;
		double rootEntropy = calculateEntropy((rootzero/rootsum) , (rootone/rootsum));



		double[] bestSplitPointList = new double[attributeLen];
		//split on each attribute
		
		double bestEntropy = 1.0;
		String bestAttribute = null;
		double bestThres = 0.0;
		for(int i = 0; i < attributeLen;i++){
			//sort on each attribute, and find consecutive instances
			String currAttribute = trainAttributeNames.get(i);
			ArrayList<DataBinder> binderList = new ArrayList<DataBinder>();
			//read the data into the binderList
			for(int j=0;j < dataSet.size();j++){
				binderList.add(new DataBinder(i, dataSet.get(j)));
			}
			//sort the binderList based on the comparator function
			Collections.sort(binderList);
			
			//create an ArrayList of thresholds
			ArrayList<Double> thresList = new ArrayList<Double>();
			for(int j = 0;j < binderList.size(); j++){
				if(j != binderList.size()-1){
					//compare if the consecutive element has the same class
					
					if(Double.compare(binderList.get(j).getData().get(attributeLen),binderList.get(j+1).getData().get(attributeLen))!=0){
						//add the average if not
						thresList.add((binderList.get(j).getArgItem()+binderList.get(j+1).getArgItem())/((double)2));
					}
				}
			}
			//for each candidate thresholds, calculate the entropy
			double localEntropy = 1.0;
			double localThres = 0.0;
			
			for(int j = 0; j < thresList.size();j++){
				double currThres = thresList.get(j);
				
				double lnum_zero,lnum_one, rnum_zero, rnum_one;
				lnum_zero = lnum_one = rnum_zero = rnum_one = 0.0;
				for(int k = 0; k < binderList.size();k++){
					DataBinder currBinder = binderList.get(k);
					//when the attribute is < current threshold
					if(currBinder.getArgItem() <= currThres){
						if(currBinder.getData().get(attributeLen) == 0.0){
							lnum_zero++;
						}else{
							lnum_one++;
						}
					}
					
					//when the attribute is > current threshold
					if(currBinder.getArgItem() > currThres){
						if(currBinder.getData().get(attributeLen) == 0.0){
							rnum_zero++;
						}else{
							rnum_one++;
						}
					}
				}
				//calculate the entropy for the current threshold and update the local minimum and threshold
				double leftsum = lnum_one + lnum_zero;
				double rightsum = rnum_one + rnum_zero;
				double totalsize = (double)(binderList.size());
				double currEntropy = leftsum/totalsize*calculateEntropy(lnum_one, lnum_zero) + rightsum/totalsize*calculateEntropy(rnum_zero,rnum_one);
				
				//the current entropy less or equal to the local, update it
				if(currEntropy <= localEntropy){
					localEntropy = currEntropy;
					localThres = currThres;
				}
				
			}
			bestSplitPointList[i] = rootEntropy - localEntropy;
			
			//compare with the global minimum, update the attribute and threshold
			if(localEntropy <= bestEntropy){
				bestEntropy = localEntropy;
				bestAttribute = currAttribute;
				bestThres = localThres;
			}
		}

		// TODO: modify this example print statement to work with your code to output attribute names and info gain. Note the %.6f output format.
		if (modeNum == 0) {
			for (int i = 0; i < bestSplitPointList.length; i++) {
				System.out.println(this.mTrainAttributes.get(i) + " " + String.format("%.6f", bestSplitPointList[i]));
			}
		}
		DecTreeNode treeNode = new DecTreeNode(0, bestAttribute, bestThres);
		return treeNode;
	}

	
	/**
	 *	Calculate the entropy for a subset
	 */
	public double calculateEntropy(double zero,double one){
		if(zero == 0.0 || one==0.0)	return 0.0;
		return -zero/(zero+one)*(Math.log(zero/(zero+one))/Math.log(2)) - one/(zero+one)*(Math.log(one/(one+zero))/Math.log(2));
	}
	
	/**
	 * given a dataset, calculate the majority class label
	 * @param dataSet
	 * @return
	 */
	private int calMajority(ArrayList<ArrayList<Double>> dataSet){
		int countZero = 0, countOne = 0;
		int attributeSize = dataSet.get(0).size()-1;
		for(ArrayList<Double> curr : dataSet){
			if(curr.get(attributeSize) == 0){
				countZero++;
			}else{
				countOne++;
			}
		}
		return (countOne>=countZero) ? 1:0;
	}

	/**
	 * Print the decision tree in the specified format
	 */
	public void print() {
		printTreeNode("", this.root);
	}

	/**
	 * Recursively prints the tree structure, left subtree first, then right subtree.
	 */
	public void printTreeNode(String prefixStr, DecTreeNode node) {
		String printStr = prefixStr + node.attribute;
			
		System.out.print(printStr + " <= " + String.format("%.6f", node.threshold));
		if(node.left.isLeaf()){
			System.out.println(": " + String.valueOf(node.left.classLabel));
		}else{
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.left);
		}
		System.out.print(printStr + " > " + String.format("%.6f", node.threshold));
		if(node.right.isLeaf()){
			System.out.println(": " + String.valueOf(node.right.classLabel));
		}else{
			System.out.println();
			printTreeNode(prefixStr + "|\t", node.right);
		}
		
		
	}
	
	public double printAccuracy(int numEqual, int numTotal){
		double accuracy = numEqual/(double)numTotal;
		System.out.println(accuracy);
		return accuracy;
	}

	/**
	 * Private class to facilitate instance sorting by argument position since java doesn't like passing variables to comparators through
	 * nested variable scopes.
	 * */
	private class DataBinder implements Comparable<DataBinder>{
		
		public ArrayList<Double> mData;
		public int i;
		public DataBinder(int i, ArrayList<Double> mData){
			this.mData = mData;
			this.i = i;
		}
		public double getArgItem()
		{
			return mData.get(i);
		}

		public ArrayList<Double> getData()
		{
			return mData;
		}

		@Override
		public int compareTo(DataBinder compareBinder){
			
			int ret = Double.compare(this.getArgItem(), ((DataBinder)compareBinder).getArgItem());
			if(ret == 0){
				//if the attribute value is equal, compare the class number
				int len = this.getData().size();
				return Double.compare(this.getData().get(len-1),((DataBinder)compareBinder).getData().get(len-1));
			}else{
				return ret;
			}
		
		}


		
	}

}
