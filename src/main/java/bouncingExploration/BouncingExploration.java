package bouncingExploration;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class BouncingExploration {

    private static DecimalFormat twoPlace = new DecimalFormat( "0.00" );
    private static double meanError=0;
    private static double currentError=0;
    private static long time=System.nanoTime();
    public static ConfigurationTree tree;
    
    public static void main(String[] args) {
    	
    	tree = new ConfigurationTree(0);  	   	
        
    }
    
    
    
    public void addAnnotator(String name, int phase, int option){
    	tree.addAnnotator(name, phase, option);
    }
    
    public void printTree(){
    	tree.printTree();
    }
    
    public int totalPaths(){
    	return tree.getTotalPaths();
    }
    
    public int getTotalNodes(){
    	return tree.getTotalNodes();
    }
    
    public void setPhases(int phases){
    	tree.setPhases(phases);
    }
    
    public void genPaths(){
    	LinkedList<LinkedList<Integer>> paths = tree.generatePaths(0.1);
    	System.out.println("\nSample Size: " + paths.size());
    	paths = tree.bestLists(paths, 1.0);
    	tree.generateSampleTree(paths);
    	//tree.printTree();
    	paths = tree.generatePaths(0.5);
    	paths = tree.bestLists(paths, 1.0);
    	
    	tree.generateSampleTree(paths);
    	
    	paths = tree.generatePaths(0.5);
    	paths = tree.bestLists(paths, 1.0);
    	
    	tree.generateSampleTree(paths);
    	
    	paths = tree.generatePaths(0.5);
    	paths = tree.bestLists(paths, 1.0);
    }
}
