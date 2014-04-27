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
    	
    	
//    	tree.addAnnotator("TextSegmenter", 1,1);
//    	tree.addAnnotator("TextSegmenter", 1,2);
//    	tree.addAnnotator("TextSegmenter", 1,3);
//    	tree.addAnnotator("Standford", 2,1);
//    	tree.addAnnotator("Annotator_A", 3,1);
//    	tree.addAnnotator("Annotator_A", 3,2);
//    	tree.addAnnotator("Annotator_A", 3,3);
//    	tree.addAnnotator("Annotator_B", 3,1);
//    	tree.addAnnotator("Annotator_B", 3,2);
//    	tree.addAnnotator("NoiseFilter", 4,1);
//    	tree.addAnnotator("NoiseFilter", 4,2);
//    	tree.addAnnotator("NoiseFilter", 4,3);
//    	tree.addAnnotator("NoiseFilter", 4,4);
//    	tree.addAnnotator("NoiseFilter", 4,5);
//    	tree.addAnnotator("NoiseFilter", 4,6);
//    	tree.addAnnotator("NoiseFilter", 4,7);
//    	tree.addAnnotator("NoiseFilter", 4,8);
//    	tree.printTree();
//    	System.out.println("\n Total paths: " + tree.getTotalPaths());
    	
	

        //Tree CS = new Tree();
        
        /*
         * Parámetros para probar el algoritmo (test) en orden:
         * 
         * El árbol generado
         * El factor de amplitud (hijos por nodo) (entero > 1)
         * El factor de profundidad (niveles del árbol) (entero > 1)
         * Costo mínimo que puede tener un nodo
         * Costo máximo que puede tener un nodo
         * Factor de exploración (que tanto exploro del árbol) (decimal entre 0 y 1)
         * Factor de mejora (que tan estricto filtro los caminos) (decimal entre 0 y 1)
         * Número de iteraciones para filtrar resultados (entero entre 1 y factor de produndidad)
         * 
         * Ejemplo : test(CS,5,6,10,30,0.1,0.05,3); 
         * 
         * Evitar valores de amplitud y profundidad mayores a 6,6 (por cuestiones de tiempo)
         */

//        for(int i=0;i<5;i++){
//            test(CS,6,6,10,30,0.02,0.02,3);             
//            System.out.println("");       
//        }
//        meanError = meanError/(double)5;
//        System.out.println("\nMean Error: " + twoPlace.format(meanError) + "%");
//        System.out.println("Time: " + ((System.nanoTime()-time)/1000000) + " miliseconds");
        
    }
    
    public void buildTree(String s){
    	System.out.println("\nBouncing");
    }
    
    private static void test(Tree CS, int b, int d, int min, int max, double exp, double improv, int depurations){
        CS = new Tree();
        CS.generateTree(b, d, min, max);//Branch and depth (2-6,2-6)
        CS.printTree(false);
        System.out.println("True Min: " + CS.getTrueMin()); 
        System.out.println("True Max: " + CS.getTrueMax()+"\n"); 
        for(int i=0;i<depurations;i++){
            CS.explore(exp,improv);//Explore n% of paths, and take the best m% of them
            currentError = CS.getCurrentError()*100;           
            System.out.println("Evaluated paths so far: " + CS.exploredPaths + " \tMin so far: " + CS.getCurrentMin() + "\t Error so far: " + twoPlace.format(currentError) + "%"); 
        } 
        
        meanError+=currentError;
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
}
