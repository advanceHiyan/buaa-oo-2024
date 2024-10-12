import com.oocourse.elevator1.TimableOutput;
import java.util.ArrayList;

class Main {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();  // 初始化时间戳

        RequestQueue waitQueue = new RequestQueue();

        ArrayList<RequestQueue> elevatorQueues = new ArrayList<>();
        for (int i = 1;i <= 6;i++) {
            RequestQueue requestQueue = new RequestQueue();
            elevatorQueues.add(requestQueue);
            ElevatorThread elevatorThread = new ElevatorThread(requestQueue,i);
            elevatorThread.start();
            requestQueue.checkEle(elevatorThread);
        }
        ScheduleThread schedule = new ScheduleThread(waitQueue,elevatorQueues);
        schedule.start();
        InputThread inputThread = new InputThread(waitQueue);
        inputThread.start();
    }
}
