import java.util.List;

public class MyJvm {//包含题目[3]
    private static final int DEFAULT_CAPACITY = 16;
    private final JvmHeap heap;

    MyJvm() {
        heap = new JvmHeap(DEFAULT_CAPACITY);
    }
    
    /*@ public normal_behavior
      @ assignable heap;
      @ requires \old(size)+count < DEFAULT_CAPACITY;
      @ ensures size == \old(size)+count;
      @ ensures (\forall int i;1 <= i && i <= \old(size);(\exists int j ; 1 <= j <= size;elements[j].equals(\old(elements[i]))));
      @ also
      @ requires \old(size)+count >= DEFAULT_CAPACITY;
      @ ensures size == (\sum int i;1 <= i && i <= \old(size) && \old(elements[i].isReferenced();1))+count;
      @ ensures [3-1]; 调用方法后，elements数组应满足: (1)若原elements数组中元素仍被引用，则该元素应包含于elements数组
      @ ensures (\forall int i; 1 <= i && i <= \old(heap.size);
      @          \old(heap.elements[i].isReferenced()) ==>
      @           (\exists int j; 1 <= j && j <= size; heap.elements[j].equals(\old(getElement(i)))));
      @ ensures [3-2]; 调用方法后，elements数组应满足: (2)若原elements数组中元素未被引用，则该元素应不包含于elements数组
      @ ensures (\forall int i; 1 <= i && i <= \old(heap.size);
      @          !(\old(heap.elements[i].isReferenced())) ==>
      @           (\forall int j; 1 <= j && j <= size;
      @           !elements[j].equals(\old(heap.elements[i]))));
      @*/
    public void createObject(int count) {
        for (int i = 0; i < count; i++) {
            MyObject newObject = new MyObject();
            heap.add(newObject);
            if (heap.getSize() == DEFAULT_CAPACITY) {
                System.out.println("Heap reaches its capacity,triggered Garbage Collection.");
                GC();
            }
        }
    }

    public void setUnreferenced(List<Integer> objectId) {
        heap.setUnreferencedId(objectId);
    }

    public void GC() {
        heap.removeUnreferenced();
    }

    public void getSnapShot() {
        System.out.println("Heap: " + heap.getSize());
        for (int i = 1; i <= heap.getSize(); i++) {
            MyObject mo = (MyObject) heap.getElements()[i];
            System.out.print(mo.getId() + " ");
        }
        System.out.println("");
        MyObject youngest = heap.getYoungestOne();
        if (youngest != null) {
            System.out.print("the youngest one's id is " + youngest.getId());
        }
        System.out.println("");
        System.out.println("\n---------------------------------");
    }
}
