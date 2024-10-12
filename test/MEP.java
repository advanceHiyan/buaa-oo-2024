import com.oocourse.spec1.exceptions.EqualPersonIdException;

import java.util.HashMap;

public class MEP extends EqualPersonIdException {
    private static int id;
    private static HashMap<Integer,Integer> counter = new HashMap<>();
    private static int sum;

    public MEP(int id) {
        MEP.id = id;
        if (counter.get(id) != null) {
            counter.put(id,counter.get(id) + 1);
        } else {
            counter.put(id,1);
        }
        sum++;
    }


    public static int getId() {
        return id;
    }

    public static int getSum() {
        return sum;
    }

    @Override
    public void print() {
        System.out.println(String.format("epi-%d, %d-%d",sum,id,counter.get(id)));
    }
}
