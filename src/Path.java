import java.util.*;

public class Path implements PathInterface { //包含题目[1]-[2]

    private final ArrayList<Integer> nodes;
    private final HashSet<Integer> distinct;
    
    public ArrayList<Integer> getNodes() {
        ArrayList<Integer> nodesCopy = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodesCopy.add(nodes.get(i));
        }
        return nodesCopy;
    }
    
    public HashSet<Integer> getDistinctNodes() {
        HashSet<Integer> distinctCopy = new HashSet<>();
        distinctCopy.addAll(distinct);
        return distinctCopy;
    }
    
    
    public Path(final int... nodeList) {
        nodes = new ArrayList<>(nodeList.length);
        distinct = new HashSet<>(nodeList.length);
        for (final int x : nodeList) {
            nodes.add(x);
            distinct.add(x);
        }
    }
    
    public int size() {
        return nodes.size();
    }
    
    public int getNode(final int index) throws IndexOutOfBoundsException {
        if (index >= 0 && index < nodes.size()) {
            return nodes.get(index);
        } else {
            System.err.println("Index not available in getNode(int index) !");
            throw new IndexOutOfBoundsException();
        }
    }
    
    public  boolean isValid() {
        return (nodes.size() >= 2 && nodes.size() - distinct.size() <= 1);
    }
    
    public  Path extractLoopPath() {
        if (nodes.size() == distinct.size() || !isValid()) {
            return null;
        }
        HashMap<Integer, Integer> nodeToIndex = new HashMap<>();
        int index;
        int loopStartIndex;
        int loopLastIndex;
        for (index = 0; index < nodes.size() &&
                !nodeToIndex.containsKey(nodes.get(index)); index++) {
            nodeToIndex.put(nodes.get(index), index);
        }
        loopStartIndex = nodeToIndex.get(nodes.get(index));
        loopLastIndex = index;
        
        int[] loopList = new int[loopLastIndex - loopStartIndex + 1];
        for (int i = loopStartIndex; i <= loopLastIndex; i++) {
            loopList[i - loopStartIndex] = nodes.get(i);
        }
        return new Path(loopList);
    }
    
    
    public void addPoint(int x, int u, int v) throws PathIsNotValid, EdgeNotFoundException {
        if (!isValid()) {
            throw new PathIsNotValid();
        }
        boolean isHasEdge = false;
        for (int i = 0; i < nodes.size() - 1; i++) {
            if (nodes.get(i) == u && nodes.get(i + 1) == v) {
                nodes.add(i + 1, x);
                distinct.add(x);
                isHasEdge = true;
                break;
            }
        }
        if (!isHasEdge) {
            throw new EdgeNotFoundException();
        }
    }
    
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Path : ");
        Iterator<Integer> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            res.append(iterator.next().toString());
            if (iterator.hasNext()) {
                res.append("->");
            }
        }
        return res.toString();
    }
}
