import com.oocourse.spec3.exceptions.EqualMessageIdException;

import java.util.HashMap;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private static int id;
    private static HashMap<Integer,Integer> counter = new HashMap<>();
    private static int sum;

    public MyEqualMessageIdException(int id) {
        MyEqualMessageIdException.id = id;
        if (counter.get(id) != null) {
            counter.put(id,counter.get(id) + 1);
        } else {
            counter.put(id,1);
        }
        sum++;
    }

    public void print() {
        System.out.println(String.format("emi-%d, %d-%d",sum,id,counter.get(id)));
    }
}