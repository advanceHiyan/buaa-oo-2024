import java.util.HashMap;

public class TagFlag {
    private static HashMap<MyTag,Boolean> isNewValue = new HashMap<>();

    public static void isNewValuel(MyTag tag,boolean fuck) {
        TagFlag.isNewValue.put(tag,fuck);
    }

    public static Boolean find(MyTag tag) {
        return isNewValue.get(tag);
    }

    public static HashMap<MyTag,Boolean> getIsNewValue() {
        return isNewValue;
    }
}
