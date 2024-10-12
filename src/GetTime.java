import com.oocourse.elevator3.TimableOutput;

public class GetTime {
    public static synchronized long outAndGet(String input) {
        long time;
        time = TimableOutput.println(input);
        return time;
    }
}
