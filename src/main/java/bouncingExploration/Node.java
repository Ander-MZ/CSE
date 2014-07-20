package bouncingExploration;

import java.util.LinkedList;

public class Node {
    
    private static int ID_Static;
    private int ID; 
    private int cost;
    private int level;
    private int totalChilds;
    private LinkedList<Node> childs;
    private Node father;
    
    public Node(){
        ID=ID_Static;
        ID_Static++;
        totalChilds=0;
        cost=0;
        childs = new LinkedList();
    }

    public int getID() {
        return ID;
    }
    
    public Node(int cost, int level, Node father){
        this();
        this.cost=cost;
        this.level=level;
        this.father=father;
    }
    
    public void addChild(int cost){
        Node c = new Node(cost,this.level+1,this);
        childs.add(c);
        totalChilds++;
    }
    
    public void removeChild(int i){
        childs.remove(i);
        totalChilds--;
    }

    public int getTotalChilds() {
        return totalChilds;
    }

    public void setTotalChilds(int totalChilds) {
        this.totalChilds = totalChilds;
    }
    
    public Node getChild(int i){
        return childs.get(i);
    }

    public LinkedList<Node> getChilds() {
        return childs;
    }

    public void setChilds(LinkedList<Node> childs) {
        this.childs = childs;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
    
    
}
