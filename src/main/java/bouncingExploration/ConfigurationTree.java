package bouncingExploration;

import java.util.LinkedList;
import java.security.SecureRandom;

public class ConfigurationTree {
	
	private int phases;
	private int nodes;
	private LinkedList<AnnotatorNode> tree;
	private AnnotatorNode root;
	private int currentPhase;
    private SecureRandom random;
    private int totalPaths;

	private double improvFact;
	
	public ConfigurationTree(int currentPhase){
		root = new AnnotatorNode("ROOT",0,0);
		this.currentPhase=currentPhase;
		random = new SecureRandom();
		totalPaths=0;
		nodes=0;
	}
	
    public int getTotalPaths() {
    	countPaths(root);
		return totalPaths;
	}
	
	public void setImprovFact(double improvFact){
		this.improvFact=improvFact;
	}
	
	public int getTotalNodes(){
		return nodes;
	}
	
	public void addAnnotator(String name, int phase, int option){
		if(phase==1){//If we need to start from the root
			root.addChild(name,phase,option);
		}
		else{
			addAnnotatorP(root,name,(phase-1),option);
		}
		nodes++;
	}
	
	private void addAnnotatorP(AnnotatorNode node ,String name, int phase,int option){
		if(node.phase==phase){//If he have reached the desired phase to add an annotator (father's phase)
			node.addChild(name, phase+1,option);
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
	public LinkedList<LinkedList<Integer>> generatePaths(double expFact){
		
		countPaths(root);
		LinkedList<LinkedList<Integer>> paths = new LinkedList<LinkedList<Integer>>();
		
		for(int i=0;i<(int)(Math.ceil(expFact*totalPaths));i++){
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
			path.add(index);
			sampleTree(path,node.childs.get(index));
		}
	}
	
	public void printTree(){
		String indent = "\t";
		printTree(root,indent);
	}
	
	private void printTree(AnnotatorNode node, String indent){
		System.out.println(indent + node.getAnnotator() + " (Config [" + node.option + "])");
		if(node.childs.size()>0){
			for(AnnotatorNode aux : node.childs){
				printTree(aux,(indent+"\t"));
			}
		}
		
	}

}
