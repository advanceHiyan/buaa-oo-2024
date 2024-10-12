import java.util.HashMap;

public class CopyTagFlag {
    private static HashMap<CopyTag,Boolean> isNewValue = new HashMap<>();

    public static void isNewValuel(CopyTag tag,boolean fuck) {
        CopyTagFlag.isNewValue.put(tag,fuck);
    }

    public static Boolean find(CopyTag tag) {
        return isNewValue.get(tag);
    }

    public static HashMap<CopyTag,Boolean> getIsNewValue() {
        return isNewValue;
    }
}
