import com.oocourse.spec2.exceptions.PersonIdNotFoundException;

import java.util.HashMap;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private static int id;
    private static HashMap<Integer,Integer> counter = new HashMap<>();
    private static int sum;

    public MyPersonIdNotFoundException(int id) {
        MyPersonIdNotFoundException.id = id;
        if (counter.get(id) != null) {
            counter.put(id,counter.get(id) + 1);
        } else {
            counter.put(id,1);
        }
        sum++;
    }

    @Override
    public void print() {
        System.out.println(String.format("pinf-%d, %d-%d",
                sum, id, counter.get(id)));
    }

    public static int getId() {
        return id;
    }

    public static int getSum() {
        return sum;
    }
}
