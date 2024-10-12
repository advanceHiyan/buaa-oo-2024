import com.oocourse.spec2.exceptions.EqualTagIdException;

import java.util.HashMap;

public class ETN extends EqualTagIdException {
    private static int sum;
    private static int id;
    private static HashMap<Integer,Integer> counter = new HashMap<>();

    public ETN(int id) {
        ETN.id = id;
        if (counter.get(id) != null) {
            counter.put(id,counter.get(id) + 1);
        } else {
            counter.put(id,1);
        }
        sum++;
    }

    @Override
    public void print() {
        System.out.println(String.format("eti-%d, %d-%d",sum,id,counter.get(id)));
    }
}