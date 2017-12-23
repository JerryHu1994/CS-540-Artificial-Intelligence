import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Do not modify.
 * 
 * This is the class with the main function
 */

public class HW4{
	/**
	 * Runs the tests for HW4
	*/
	public static void main(String[] args)
	{
		//Checking for correct number of arguments
		if (args.length < 5) 
		{
			System.out.println("usage: java HW4 <noHiddenNode> " +
					"<learningRate> <maxEpoch> <trainFile> <testFile>");
			System.exit(-1);
		}
		
		//Mapping form class index to actual digits
		Map<Integer, Integer> indexToDigitMap = new HashMap<Integer, Integer>();
		indexToDigitMap.put(0, 1);
		indexToDigitMap.put(1, 4);
		indexToDigitMap.put(2, 7);
		indexToDigitMap.put(3, 8);
		indexToDigitMap.put(4, 9);
		
		//Reading the training set 	
		ArrayList<Instance> trainingSet=getData(args[3]);
		
		
		//Reading the weights
		Double[][] hiddenWeights=new Double[Integer.parseInt(args[0])][];
		for(int i=0;i<hiddenWeights.length;i++)
		{
			hiddenWeights[i]=new Double[trainingSet.get(0).attributes.size()+1];
		}
		
		Double [][] outputWeights=new Double[trainingSet.get(0).classValues.size()][];
		for (int i=0; i<outputWeights.length; i++) 
		{
			outputWeights[i]=new Double[hiddenWeights.length+1];
		}
		
		readWeights(hiddenWeights,outputWeights);
		
		Double learningRate=Double.parseDouble(args[1]);
		
		if(learningRate>1 || learningRate<=0)
		{
			System.out.println("Incorrect value for learning rate\n");
			System.exit(-1);
		}
		
		NNImpl nn=new NNImpl(trainingSet,Integer.parseInt(args[0]),Double.parseDouble(args[1]),Integer.parseInt(args[2]), 
					hiddenWeights,outputWeights);
		nn.train();
			
		//Reading the test set 	
		ArrayList<Instance> testSet=getData(args[4]);
			
			
		int correct=0;
		/*Count the numbers for the confusion matrix*/
		int [][]confusion = new int[5][5];
		for(int i=0;i<testSet.size();i++)
		{
			//Getting output from network
			int predicted_idx = nn.calculateOutputForInstance(testSet.get(i));
			int actual_idx = -1;
			//Getting actual output from instance's classValues
			for (int j=0; j < testSet.get(i).classValues.size(); j++) {
				if (testSet.get(i).classValues.get(j) > 0.5)
					actual_idx=j;
			}
				
			//Map class index to actual digits
			int actual_output = indexToDigitMap.get(actual_idx);
			int predicted_output = indexToDigitMap.get(predicted_idx);

			if (actual_output == predicted_output) 
			{
				correct++;
				System.out.println("Instance " + (i+1) + ": Correct classification, actual: " + actual_output + ", predicted:" + predicted_output);
			}
			else 
			{
				System.out.println("Instance " + (i+1) + ": Misclassification, actual: " + actual_output + ", predicted:" + predicted_output);
			}
			/*
			if(actual_output == 1){
				if(predicted_output==1)	confusion[0][0]++;
				if(predicted_output==4)	confusion[0][1]++;
				if(predicted_output==7)	confusion[0][2]++;
				if(predicted_output==8)	confusion[0][3]++;
				if(predicted_output==9)	confusion[0][4]++;
			}
			if(actual_output == 4){
				if(predicted_output==1)	confusion[1][0]++;
				if(predicted_output==4)	confusion[1][1]++;
				if(predicted_output==7)	confusion[1][2]++;
				if(predicted_output==8)	confusion[1][3]++;
				if(predicted_output==9)	confusion[1][4]++;
			}
			if(actual_output == 7){
				if(predicted_output==1)	confusion[2][0]++;
				if(predicted_output==4)	confusion[2][1]++;
				if(predicted_output==7)	confusion[2][2]++;
				if(predicted_output==8)	confusion[2][3]++;
				if(predicted_output==9)	confusion[2][4]++;
			}
			if(actual_output == 8){
				if(predicted_output==1)	confusion[3][0]++;
				if(predicted_output==4)	confusion[3][1]++;
				if(predicted_output==7)	confusion[3][2]++;
				if(predicted_output==8)	confusion[3][3]++;
				if(predicted_output==9)	confusion[3][4]++;
			}
			if(actual_output == 9){
				if(predicted_output==1)	confusion[4][0]++;
				if(predicted_output==4)	confusion[4][1]++;
				if(predicted_output==7)	confusion[4][2]++;
				if(predicted_output==8)	confusion[4][3]++;
				if(predicted_output==9)	confusion[4][4]++;
			}*/
			
		}
			
		System.out.println("Total instances: " + testSet.size());
		System.out.println("Correctly classified: "+correct);
		System.out.println("Accuracy: " + (double)correct/(double)testSet.size());
		
		/*Print the confusion matrix*/
		/*
		System.out.println("Printing the confusion maxtrix...");
		for(int i=0;i<confusion.length;i++){
			for(int j=0;j<confusion[0].length;j++){
				System.out.print(confusion[i][j] + " ");
			}
			System.out.println();
		}*/
			
	}
		
	// Reads a file and gets the list of instances
	private static ArrayList<Instance> getData(String file)
	{
		ArrayList<Instance> data=new ArrayList<Instance>();
		BufferedReader in;
		int attributeCount=0;
		int outputCount=0;
		
		try
		{
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) { 
				String line = in.readLine(); 	
				String prefix = line.substring(0, 2);
				if (prefix.equals("//")) {
				} 
				else if (prefix.equals("##")) {
					attributeCount=Integer.parseInt(line.substring(2));
				} else if (prefix.equals("**")) {
					outputCount=Integer.parseInt(line.substring(2));
				}else {
					String[] vals=line.split(" ");
					Instance inst = new Instance();
					for (int i=0; i<attributeCount; i++)
						inst.attributes.add(Double.parseDouble(vals[i]));
					for (int i=attributeCount; i < vals.length; i++)
						inst.classValues.add(Integer.parseInt(vals[i]));	
					data.add(inst);	
				}
				
			}
			in.close();
			return data;
			
		}
		catch(Exception e)
		{
			System.out.println("Could not read instances: "+e);
		}
		
		return null;
	}

	// Gets weights randomly
	public static void readWeights(Double [][]hiddenWeights, Double[][]outputWeights)
	{
		Random r = new Random();
			
		for(int i=0;i<hiddenWeights.length;i++)
		{
			for(int j=0;j<hiddenWeights[i].length;j++)
			{
				hiddenWeights[i][j] = r.nextDouble()*0.01;
			}
		}
				
		for(int i=0;i<outputWeights.length;i++)
		{
			for (int j=0; j<outputWeights[i].length; j++)
			{
				outputWeights[i][j] = r.nextDouble()*0.01;
			}
		}	
	}
}
