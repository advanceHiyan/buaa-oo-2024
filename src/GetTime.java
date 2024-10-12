import com.oocourse.elevator2.TimableOutput;

public class GetTime {
    public static synchronized long outAndGet(String input) {
        long time;
        time = TimableOutput.println(input);
        return time;
    }
}
