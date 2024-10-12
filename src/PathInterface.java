import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public interface PathInterface {
    
    //@ public instance model non_null int[] nodes;
    
    //@   ensures \result = nodes
    public ArrayList<Integer> getNodes();
    
    //@ ensures \result == nodes.length;
    public /*@ pure @*/ int size();
    
    /*@ public normal_behavior
      @ requires index >= 0 && index < size();
      @ assignable \nothing;
      @ ensures \result == nodes[index];
      @
      @ also
      @ public exceptional_behavior
      @ assignable \nothing;
      @ signals (IndexOutOfBoundsException e) index < 0 || index >= size();
      @*/
    public /*@ pure @*/ int getNode(final int index) throws IndexOutOfBoundsException;
    
    /*@ ensures \result == (nodes.length >= 2) &&
      @         ((\sum int i; 0 <= i && i < nodes.length - 1 &&
      @         (\exists int j; i < j && j < nodes.length; nodes[i] == nodes[j]);1)<= 1);
      */
    public /*@ pure @*/ boolean isValid();
    
    /*@ public normal_behavior
      @ assignable \nothing;
      @ requires (\exists int i,j; 0 <= i && i < j && j < nodes.length; nodes[i] == nodes[j]) && isValid();
      @ ensures \result instanceof Path &&
      @           (\exists int i; 0 <= i && i < nodes.length-1;
      @           (\forall int j; 0 <= j && j < \result.nodes.length;
      @           (i + j) < nodes.length && \result.nodes[j] == nodes[i + j]))
      @           && \result.nodes[0] == \result.nodes[\result.nodes.length - 1];
      @
      @ also
      @ public normal_behavior
      @ requires (\forall int i, j; 0 <= i && i < j && j < nodes.length; nodes[i] != nodes[j]) || !isValid();
      @ assignable \nothing;
      @ ensures \result == null;
      @*/
    public /*@ pure @*/ Path extractLoopPath();
    
    /*@ public normal_behavior
      @ requires (\exists int i; 0 <= i && i < nodes.length - 1; nodes[i] == u && nodes[i + 1] == v) && isValid();
      @ ensures (\exists int i; 0 <= i && i < \old(nodes).length - 1 && \old(nodes[i]) == u && \old(nodes[i + 1]) == v);
                (\forall int j; 0 <= j && j <= i; \old(nodes[j]) == nodes[j]) &&
                (\forall int j; i < j && j < \old(nodes.length); \old(nodes[j]) == nodes[j + 1]) &&
                nodes[i + 1] == x);
      @ ensures nodes.length == \old(nodes).length + 1
      @ also
      @ public exceptional_behavior
      @ assignable \nothing;
      @ requires !isValid();
      @ signals_only PathIsNotValid
      @ also
      @ public exceptional_behavior
      @ assignable \nothing;
      @ requires (\forall int i; 0 <= i && i < nodes.length - 1; nodes[i] != u || nodes[i + 1] != v) && isValid();
      @ signals_only EdgeNotFoundException
      @*/
    public void addPoint(int x, int u, int v) throws PathIsNotValid, EdgeNotFoundException;
    
    @Override
    public String toString();
}
