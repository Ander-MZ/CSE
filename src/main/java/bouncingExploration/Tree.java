package bouncingExploration;

import java.security.SecureRandom;
import java.util.LinkedList;

public class Tree {
    
    private static final int MAX_VALUE=2147483647;
    private static final int MIN_VALUE=-2147483648;
    public int exploredPaths;
    private int b;//Branching factor
    private int d;//Depth factor
    private int min;//Minimum cost
    private int max;//Maximum cost
    private int improvLevel;
    private Node root;
    private SecureRandom random;
    private LinkedList<Path> paths;
    private int trueMin, trueMax, currentMin;
    
    public Tree(){
        random = new SecureRandom();
        improvLevel=0;
        exploredPaths=0;
        trueMin=MAX_VALUE;
        trueMax=MIN_VALUE;
        currentMin=0;
    }
    
    public void newTree(){
    	
    }
    
    public void newPhase(){
    	
    }
    
    public void addChild(){
    	
    }
    
    
    //-------------------------------- OLD METHODS ----------------------------------//
    
    public void generateTree(int b, int d, int min, int max){
        this.b=b;
        this.d=d;
        this.min=min;
        this.max=max;
        root = new Node(0,0,null);
        generateTreeP(d,root);
        paths = new LinkedList();
        System.out.println("Total possible paths: " + (int)Math.pow(b, d)); 
    }
    
    private void generateTreeP(int d_, Node currentNode){
        if(currentNode.getFather()!=null){
                currentNode.setCost(random.nextInt(max-min)+min);
        }  
        if(d_>0){          
            int b_=b;
            while(b_>0){
                currentNode.addChild(b-b_);
                generateTreeP(d_-1,currentNode.getChild(b-b_));
                b_--;
            }          
        }
    }
    
    private int calulateFinalCost(Node n){
        int cost=0;
        while(n!=null){
            cost+=n.getCost();
            n=n.getFather();
        }
        return cost;
    }
    
    public void printTree(boolean print){
        printTree(root,0,print);
    }
    
    private void printTree(Node currentNode, int acumCost, boolean print){
        String tab = "\t";
        String arrow = "--> ";
        for(int i=0;i<currentNode.getLevel();i++){
            tab+= "\t";
        }
        
        if(currentNode.getLevel()==d){//Node is a leave
            if(acumCost<trueMin){
                trueMin=acumCost;
            }
            if(acumCost>trueMax){
                trueMax=acumCost;
            }
            if(print)System.out.println(tab+arrow+currentNode.getID() + " (" + currentNode.getCost() +") " + "[" + acumCost + "]");
        }
        else{
            if(print)System.out.println(tab+arrow+currentNode.getID() + " (" + currentNode.getCost() +")");
            
            for(Node x : currentNode.getChilds() ){
                printTree(x,acumCost+x.getCost(),print);  
            }
        }     
    }
    
    public void explore(double expFact, double improvFact){
        Path p = null;
        Node n = root;
        int acumCost=0;
        int percentOfPaths = (int)Math.ceil(Math.pow(b, d-improvLevel) *expFact); 
        for(int i=0;i<percentOfPaths;i++){//For a defined percentage of the total paths of the tree do:
            p = this.createRandomPath(0); 
            n = root;
            for(int j=0;j<p.size;j++){            
                n = n.getChild(p.getPathIndex(j));
                acumCost += n.getCost();               
            }
            p.setTotalCost(acumCost);
            acumCost=0;        
            paths.add(p);
        }       
        
        this.improve(improvFact);
        currentMin=paths.getFirst().getTotalCost();
        improvLevel++;
        
    }
    
    private Path createRandomPath(int startLevel){
        LinkedList path = new LinkedList();
        for(int i=startLevel;i<d;i++){
            path.add(i, random.nextInt(b));          
        }
        Path p = new Path(path,0,startLevel);
        exploredPaths++;
        return p;
    }
    
    private LinkedList<Path> getBestNPaths(double n){
        int index=0;
        LinkedList<Path> best = new LinkedList();
        for(int i=0;i<Math.ceil(n*paths.size());i++){
            int min=MAX_VALUE;
            for(int j=0;j<paths.size();j++){
                if(paths.get(j).getTotalCost()<min){
                    min=paths.get(j).getTotalCost();
                    index=j;
                }
            }
            best.addLast(paths.get(index));
            //paths.remove(index);
        }
        return best;
    }
    
    private void improve(double improvFact){
        /*
          After this method is called, paths will become a sorted list,
          with the smaller elements first and the bigger elements last. 
        */
        this.paths=this.getBestNPaths(improvFact);
    }
    
    public void printPaths(){
        System.out.println(""); 
        for(Path p : paths){
            System.out.println(p.getTotalCost()); 
        }
        System.out.println("");
    }
    
    public int getTrueMin(){
        return this.trueMin;
    }
    
    public int getTrueMax(){
        return this.trueMax;
    }
    
    public int getCurrentMin(){
        return currentMin;
    }
    
    public double getCurrentError(){
        return (double)(currentMin-trueMin)/(double)(trueMin);
    }
   
}
