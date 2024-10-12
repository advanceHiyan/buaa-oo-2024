import com.oocourse.spec2.exceptions.EqualRelationException;

import java.util.HashMap;

public class MyEqualRelationException extends EqualRelationException {
    private static HashMap<Integer,Integer> counter = new HashMap<>();
    private static int sum;
    private static int id1;
    private static int id2;

    public MyEqualRelationException(int id1, int id2) {
        MyEqualRelationException.id1 = id1;
        MyEqualRelationException.id2 = id2;
        if (id1 == id2) {
            countPut(id1);
        } else {
            countPut(id1);
            countPut(id2);
        }
        sum++;
    }

    @Override
    public void print() {
        if (id1 < id2) {
            System.out.println(String.format("er-%d, %d-%d, %d-%d",
                    sum, id1, counter.get(id1), id2, counter.get(id2)));
        } else {
            System.out.println(String.format("er-%d, %d-%d, %d-%d",
                    sum, id2, counter.get(id2), id1, counter.get(id1)));
        }
    }

    public void countPut(int id) {
        if (counter.get(id) != null) {
            counter.put(id,counter.get(id) + 1);
        } else {
            counter.put(id,1);
        }
    }

    public static int getSum() {
        return sum;
    }

    public static int getId1() {
        return id1;
    }

    public static int getId2() {
        return id2;
    }
}