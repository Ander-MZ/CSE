package bouncingExploration;
import java.util.LinkedList;

public class Path implements Comparable<Path>{
    
    private LinkedList<Integer> path;
    private int totalCost;
    private int startLevel;
    public int size;
    
    public Path(){
        totalCost=0;
        startLevel=0;
    }
    
    public Path(LinkedList path, int totalCost, int startLevel){
        this();
        this.path=path;
        this.totalCost=totalCost;
        this.startLevel=startLevel;
        this.size=path.size();
    }
    
    public int getPathIndex(int i){
        return path.get(i);
    }

    public LinkedList getPath() {
        return path;
    }

    public void setPath(LinkedList path) {
        this.path = path;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public int getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(int startLevel) {
        this.startLevel = startLevel;
    }
    
    public int compareTo(Path p){
        return this.totalCost-p.totalCost;
    }
    
}
