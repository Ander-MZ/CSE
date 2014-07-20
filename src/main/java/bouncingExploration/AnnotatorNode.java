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
	private static int ID_ = 0;
	
	public AnnotatorNode(String annotator, int phase,int option, SecureRandom rand){
		ID_++;
		this.ID=ID_;
		this.annotator=annotator;
		this.phase=phase;
		this.option=option;
		configs=0;
		childs = new LinkedList();
		if(phase==0){
			this.cost=0;
		}
		else{
			this.cost = Math.exp(-rand.nextDouble());
		}
		
	}
	
	public void addChild(String annotator, int phase, int option, SecureRandom rand){
		childs.add(new AnnotatorNode(annotator,phase,option,rand));
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
	
	public AnnotatorNode getChildWithID(int ID){
		AnnotatorNode child = null;
		for(AnnotatorNode aux : this.childs){
				if(aux.ID==ID){
					child = aux;
					break;
				}
		}
		
		return child;
		
	}
	
	public void removeChildsWithoutIDS(LinkedList<Integer> ids){
		int size = this.childs.size();
		int nulls = 0;
		
		for(int i=0; i<size;i++){
			if(!ids.contains(childs.get(i).ID)){
				childs.set(i, null);
				configs--;
				nulls++;
			}
		}
		
		for(int i = 0;i<nulls;i++){
			childs.remove(null);
		}
		
		

		
	}

}
