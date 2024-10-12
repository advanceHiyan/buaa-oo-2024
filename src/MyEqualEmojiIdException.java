import com.oocourse.spec3.exceptions.EqualEmojiIdException;

import java.util.HashMap;

public class MyEqualEmojiIdException extends EqualEmojiIdException {
    private static int id;
    private static HashMap<Integer,Integer> counter = new HashMap<>();
    private static int sum;

    public MyEqualEmojiIdException(int id) {
        MyEqualEmojiIdException.id = id;
        if (counter.get(id) != null) {
            counter.put(id,counter.get(id) + 1);
        } else {
            counter.put(id,1);
        }
        sum++;
    }

    public void print() {
        System.out.println(String.format("eei-%d, %d-%d",sum,id,counter.get(id)));
    }
}