/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 * 
 */

import java.util.*;


public class NNImpl{
	public ArrayList<Node> inputNodes=null;//list of the input layer nodes.
	public ArrayList<Node> hiddenNodes=null;//list of the hidden layer nodes
	public ArrayList<Node> outputNodes=null;// list of the output layer nodes
	
	public ArrayList<Instance> trainingSet=null;//the training set
	
	Double learningRate=1.0; // variable to store the learning rate
	int maxEpoch=1; // variable to store the maximum number of epochs
	
	/**
 	* This constructor creates the nodes necessary for the neural network
 	* Also connects the nodes of different layers
 	* After calling the constructor the last node of both inputNodes and  
 	* hiddenNodes will be bias nodes. 
 	*/
	
	public NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Double [][]hiddenWeights, Double[][] outputWeights)
	{
		this.trainingSet=trainingSet;
		this.learningRate=learningRate;
		this.maxEpoch=maxEpoch;
		
		//input layer nodes
		inputNodes=new ArrayList<Node>();
		int inputNodeCount=trainingSet.get(0).attributes.size();
		int outputNodeCount=trainingSet.get(0).classValues.size();
		for(int i=0;i<inputNodeCount;i++)
		{
			Node node=new Node(0);
			inputNodes.add(node);
		}
		
		//bias node from input layer to hidden
		Node biasToHidden=new Node(1);
		inputNodes.add(biasToHidden);
		
		//hidden layer nodes
		hiddenNodes=new ArrayList<Node> ();
		for(int i=0;i<hiddenNodeCount;i++)
		{
			Node node=new Node(2);
			//Connecting hidden layer nodes with input layer nodes
			for(int j=0;j<inputNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(inputNodes.get(j),hiddenWeights[i][j]);
				node.parents.add(nwp);
			}
			hiddenNodes.add(node);
		}
		
		//bias node from hidden layer to output
		Node biasToOutput=new Node(3);
		hiddenNodes.add(biasToOutput);
			
		//Output node layer
		outputNodes=new ArrayList<Node> ();
		for(int i=0;i<outputNodeCount;i++)
		{
			Node node=new Node(4);
			//Connecting output layer nodes with hidden layer nodes
			for(int j=0;j<hiddenNodes.size();j++)
			{
				NodeWeightPair nwp=new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
				node.parents.add(nwp);
			}	
			outputNodes.add(node);
		}	
	}
	
	/**
	 * Get the output from the neural network for a single instance
	 * Return the idx with highest output values. For example if the outputs
	 * of the outputNodes are [0.1, 0.5, 0.2, 0.1, 0.1], it should return 1. If outputs
	 * of the outputNodes are [0.1, 0.5, 0.1, 0.5, 0.2], it should return 3. 
	 * The parameter is a single instance. 
	 */
	
	public int calculateOutputForInstance(Instance inst)
	{
		// set all the input nodes
		for(int i = 0;i<inst.attributes.size();i++){
			this.inputNodes.get(i).setInput(inst.attributes.get(i));
		}
		// calculate all the hidden nodes
		for(Node hd : this.hiddenNodes){
			hd.calculateOutput();
		}
		// calculate all the output nodes
		for(Node out : this.outputNodes){
			out.calculateOutput();
		}
		double currMax = 0;
		int maxIdx = 0;
		// find the index of the output nodes with largest value 
		for(int i=0; i<this.outputNodes.size();i++){
			Node currOut = this.outputNodes.get(i);
			if(currOut.getOutput() > currMax){
				currMax = currOut.getOutput();
				maxIdx = i;
			}
		}
		
		return maxIdx;
	}
	

	
	
	
	/**
	 * Train the neural networks with the given parameters
	 * 
	 * The parameters are stored as attributes of this class
	 */
	
	public void train()
	{
		// update on each epoch iteration
		for(int i =0; i<this.maxEpoch;i++){
			//update on each Instance iteration
			for(Instance currSample : this.trainingSet){
				// set the input values
				for(int j= 0; j<currSample.attributes.size();j++){
					this.inputNodes.get(j).setInput(currSample.attributes.get(j));
				}
				
				//create a map to store all the pairs and update deltas
				Map<NodeWeightPair,Double> deltaMap = new HashMap<NodeWeightPair, Double>();
				// forward pass 
				this.calculateOutputForInstance(currSample);
				
				// back-propagation from k to j 
				for(Node outNode : this.outputNodes){
					double derivative = this.calDerivative(outNode.getOutput());
					// for each pair coming into the outNode
					if(outNode.parents == null)	continue;
					for(NodeWeightPair hiddenPair : outNode.parents){
						// calculate the error based on the teaching value
						double error = currSample.classValues.get(this.outputNodes.indexOf(outNode))-outNode.getOutput();
						Node hidNode = hiddenPair.node;
						double currdelta = this.learningRate*hidNode.getOutput()*error*derivative;
						deltaMap.put(hiddenPair, currdelta);
					}
					
				}
				
				// back-propagation from k to j
				for(Node hidNode : this.hiddenNodes){
					double derivative = this.calDerivative(hidNode.getOutput());
					if(hidNode.parents == null)	continue;
					
					for(NodeWeightPair inputPair : hidNode.parents){
						double localSum = 0;
						for(Node outNode : this.outputNodes){
							double error = currSample.classValues.get(this.outputNodes.indexOf(outNode)) - outNode.getOutput();
							NodeWeightPair pair = this.searchPair(hidNode, outNode);
							localSum += pair.weight*error*this.calDerivative(outNode.getOutput());
						}
						
						Node input = inputPair.node;
						double currdelta = this.learningRate*input.getOutput()*derivative*localSum;
						deltaMap.put(inputPair,currdelta);
						
					}
				}
				
				// After all the deltas calculated, update the weights
				for(NodeWeightPair p: deltaMap.keySet()){
					p.weight += deltaMap.get(p);
				}
				
			}
			
		}
	}
	
	
	
	// Calculate the derivative term of the sigmoid function
	private double calDerivative(double gx){
		return gx*(1-gx);
	}
	// find the NodeWeightPair from the hidden to output
	private NodeWeightPair searchPair(Node hidden, Node output){
		for(NodeWeightPair pair : output.parents){
			if(pair.node.equals(hidden))	return pair;
		}
		return null;
	}
	}

