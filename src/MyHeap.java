public class MyHeap<T extends Comparable<T>> {
    //@ public instance model non_null T[] elements;
    //包含题目[2]
    private Object[] elements;
    private /*@ spec_public @*/ int capacity;
    private /*@ spec_public @*/ int size;

    MyHeap(int capacity) {
        elements = new Object[capacity + 1];
        this.capacity = capacity;
        this.size = 0;
    }

    //@ ensures \result == size;
    public /*@ pure @*/ int getSize() {
        return size;
    }

    //@ ensures \result == elements;
    public /*@ pure @*/ Object[] getElements() {
        return elements;
    }

    /*@ public normal_behavior
      @ requires index > 0 && index <= size;
      @ ensures \result == (elements[index]);
      @*/
    public /*@ pure @*/ T getElement(int index) {
        return ((T) elements[index]);
    }

    /*@ public normal_behavior
      @ requires index >= 1 && index <= size;
      @ assignable elements;
      @ ensures (\forall int i; 1 <= i && i <= size && i != index;
      @          \not_modified(elements[i]));
      @ ensures elements[index] == element;
      @*/
    public void setelements(int index, T element) {
        this.elements[index] = element;
    }

    /*@ public normal_behavior
      @ requires newSize >= 0;
      @ assignable size;
      @ ensures size == newSize;
      @*/
    public void setSize(int newSize) {
        this.size = newSize;
    }

    /*@ private normal_behavior
      @ requires indexA >= 1 && indexA <= size && indexB >= 1 && indexB <= size;
      @ assignable elements;
      @ ensures (\forall int i; 1 <= i && i <= size && i != indexA && i != indexB;
      @          \not_modified(elements[i]));
      @ ensures elements[indexA] == \old(elements[indexB]);
      @ ensures elements[indexB] == \old(elements[indexA]);
      @*/
    private void swap(int indexA, int indexB) {
        Object temp = elements[indexA];
        elements[indexA] = elements[indexB];
        elements[indexB] = temp;
    }

    /*@ public normal_behavior
      @ assignable elements, capacity, size;
      @ ensures capacity >= size;
      @ ensures size == \old(size) + 1;
      @ ensures (\exists int i; 0 < i && i <= size; elements[i].equals(newElement));
      @ ensures (\forall int i; 0 < i && i <= \old(size);
      @           (\exists int j; 0 < j && j <= size; elements[j].equals(\old(elements[i]))));
      @ensures [2];保证是小顶堆
      @ensures (\forall int i; 1 <= i && i < size; compare(i, i/2) >= 0);
      @*/
    public void add(/*@ non_null @*/T newElement) {
        if (size == capacity) {
            Object[] oldelements = elements.clone();
            capacity = capacity << 1;
            elements = new Object[capacity + 1];
            for (int i = 1; i <= size; i++) {
                elements[i] = oldelements[i];
            }
        }
        elements[++size] = newElement;
        int tempIndex = size;
        while (tempIndex / 2 != 0 && compare(tempIndex, tempIndex / 2) < 0) {
            swap(tempIndex, tempIndex / 2);
            tempIndex /= 2;
        }
    }

    /*@ private normal_behavior
      @ requires (indexA >= 1 && indexA <= size) && (indexB >= 1 && indexB <= size);
      @ ensures \result == elements[indexA].compareTo(elements[indexB]);
      @*/
    private /*@ pure helper @*/ int compare(int indexA, int indexB) {
        return getElement(indexA).compareTo(getElement(indexB));
    }
}