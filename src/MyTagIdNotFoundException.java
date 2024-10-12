import com.oocourse.spec3.exceptions.TagIdNotFoundException;

import java.util.HashMap;

public class MyTagIdNotFoundException extends TagIdNotFoundException {
    private static int id;
    private static HashMap<Integer,Integer> counter = new HashMap<>();
    private static int sum;

    public MyTagIdNotFoundException(int id) {
        MyTagIdNotFoundException.id = id;
        if (counter.get(id) != null) {
            counter.put(id,counter.get(id) + 1);
        } else {
            counter.put(id,1);
        }
        sum++;
    }

    @Override
    public void print() {
        System.out.println(String.format("tinf-%d, %d-%d",
                sum, id, counter.get(id)));
    }
}