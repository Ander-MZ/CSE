package bouncingExploration;

import java.util.LinkedList;
import java.security.SecureRandom;

public class ConfigurationTree {
	
	private int phases;
	private static int nodes;
	private LinkedList<AnnotatorNode> tree;
	private AnnotatorNode root;
	private int currentPhase;
    private SecureRandom random;
    private int totalPaths;
    public double trueMin;
    public double trueMax;

	private double improvFact;
	
	public ConfigurationTree(int currentPhase){
		random = new SecureRandom();
		root = new AnnotatorNode("ROOT",0,0,random);
		this.currentPhase=currentPhase;
		totalPaths=0;
		nodes=0;
		trueMin = 2147483647;
		trueMax = -2147483646;
	}
	
    public int getTotalPaths() {
    	totalPaths=0;
    	countPaths(root);
		return totalPaths;
	}
    
    public void setPhases(int phases){
    	this.phases=phases;
    }
	
	public void setImprovFact(double improvFact){
		this.improvFact=improvFact;
	}
	
	public int getTotalNodes(){
		return nodes;
	}
	
	public void addAnnotator(String name, int phase, int option){
		if(phase==1){//If we need to start from the root
			root.addChild(name,phase,option,random);
			nodes++;
		}
		else{
			addAnnotatorP(root,name,(phase-1),option);
		}
	}
	
	private void addAnnotatorP(AnnotatorNode node ,String name, int phase,int option){
		if(node.phase==phase){//If he have reached the desired phase to add an annotator (father's phase)
			node.addChild(name, phase+1,option,random);
			nodes++;
		}
		else{
			for(AnnotatorNode aux : node.childs){
				addAnnotatorP(aux,name,phase,option);
			}
		}
	}
	
	private void countPaths(AnnotatorNode node){
		if(node.childs.size()==0){//Node is a leaf
			totalPaths++;
		}
		else{
			for(AnnotatorNode aux : node.childs){
				countPaths(aux);
			}
		}
	}
	
	/*
	 * This method returns a list containing the generated random paths,
	 * formated as lists containing the indexes of the annotator that has
	 * to be used at each phase.
	 */
	public void generateSampleTree(LinkedList<LinkedList<Integer>> paths){
		generateSampleTree(root,paths,0);
	}
	
	private void generateSampleTree(AnnotatorNode node, LinkedList<LinkedList<Integer>> paths, int level){
		if(level<this.phases){
			LinkedList<Integer> ids = new LinkedList();
			for(LinkedList<Integer> path : paths){
					ids.add(path.get(level));
			}
			
			node.removeChildsWithoutIDS(ids);
			
			for(AnnotatorNode aux : node.childs){
				generateSampleTree(aux,paths,level+1);
			}
		}
		
	}
	
	
	public LinkedList<LinkedList<Integer>> generatePaths(double expFact){
		LinkedList<LinkedList<Integer>> paths = new LinkedList();	
		int size = (int)(Math.ceil(expFact*this.getTotalPaths()));
		for(int i=0;i<size;i++){
			paths.add(sampleTree());
		}
		return paths;
	}
	
	private LinkedList<Integer> sampleTree(){
		LinkedList<Integer> path = new LinkedList<Integer>();
		sampleTree(path,root);
		return path;
	}
	
	private void sampleTree(LinkedList<Integer> path, AnnotatorNode node){
		if(node.childs.size()>0){//If node is not a leaf
			int index = node.randomChild(random);
			AnnotatorNode temp = node.childs.get(index);
			int id = temp.getID();
			path.add(id);
			AnnotatorNode aux = node.childs.get(index); 
			this.sampleTree(path,aux);
		}
	}
	
	public void printTree(){
		String indent = "\t";
		printTree(root,indent);
	}
	
	private void printTree(AnnotatorNode node, String indent){
		System.out.println(indent + node.getAnnotator() + " (Config [" + node.option + "])" + " <Costo= " + node.getCost() + " >");
		if(node.childs.size()>0){
			for(AnnotatorNode aux : node.childs){
				printTree(aux,(indent+"\t"));
			}
		}
		
	}
	
	public void calcTrueMin(){
		calcTrueMin(root,0);
	}
	
	private void calcTrueMin(AnnotatorNode node, double cost){
		if(node.childs.size()==0){
			if(cost<trueMin){
				trueMin = cost;
			}
			if(cost>trueMax){
				trueMax = cost;
			}
		}
		else{
			for(AnnotatorNode child : node.childs){
				calcTrueMin(child, cost + child.getCost());
			}
		}
	}
	
	
	public LinkedList<LinkedList<Integer>> bestLists(LinkedList<LinkedList<Integer>> list, double improv){
		LinkedList<LinkedList<Integer>> bestlists = new LinkedList();
		LinkedList<Double> costs = new LinkedList();
		double pathCost = 0;
		AnnotatorNode currentNode = root;
		
		double min = 2147483647;
		
		for(LinkedList<Integer> testList : list){
			currentNode = root;
			for(int ID : testList){
				currentNode=currentNode.getChildWithID(ID);
				pathCost+=currentNode.getCost();
			}
			costs.add(list.indexOf(testList), pathCost);
			if(pathCost<min){
				min = pathCost;
			}
			pathCost = 0;
		}
		
		System.out.println("Tree Min: (" + list.size() + " paths) " + min);
		
		int bestindex = 0;
		int size = list.size();
		
		
		for(int i=0; i<(int)Math.ceil(improv*size);i++){
			bestindex = bestPath(costs);
			bestlists.add(list.get(bestindex));
			list.remove(bestindex);
			costs.remove(bestindex);
		}
		return bestlists;
	}
	
	private int bestPath(LinkedList<Double> costs){
		int best = 0;
		double bestpathcost = 1;
		
		for(int i=0;i<costs.size();i++){
			if(costs.get(i)<bestpathcost){
				bestpathcost = costs.get(i);
				best = i;
			}
		}
				
		return best;
	}

}
