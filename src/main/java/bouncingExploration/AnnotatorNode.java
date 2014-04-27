package bouncingExploration;

import java.util.LinkedList;
import java.security.SecureRandom;

public class AnnotatorNode {
	
	private String annotator;//The class of the annotator
	private int configs; //The configurations this annotator has
	private double cost;
	private boolean isLeaf;
	public LinkedList<AnnotatorNode> childs;
	public int phase;
	public int option;
	private int ID;
	private static int ID_;
	
	public AnnotatorNode(String annotator, int phase,int option){
		ID_++;
		this.ID=ID_;
		this.annotator=annotator;
		this.phase=phase;
		this.option=option;
		configs=0;
		childs = new LinkedList();
	}
	
	public void addChild(String annotator, int phase, int option){
		childs.add(new AnnotatorNode(annotator,phase,option));
		configs++;
	}
	
	public int totalChilds(){
		return childs.size();
	}

	public int getConfigs() {
		return configs;
	}

	public void setConfigs(int childs) {
		this.configs = childs;
	}

	public String getAnnotator() {
		return annotator;
	}

	public int getID() {
		return ID;
	}
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	/*
	 * Return the index of a random child
	 */
	public int randomChild(SecureRandom rand){
		return rand.nextInt(configs);
	}

}
