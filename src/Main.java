import com.oocourse.elevator3.TimableOutput;
import java.util.HashMap;

class Main {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();  // 初始化时间戳

        Waitqueue waitQueue = new Waitqueue();
        HashMap<Integer,ReQueue> elevatorQueues = new HashMap<>();
        for (int i = 1;i <= 6;i++) {
            RequestQueue requestQueue = new RequestQueue(i);
            elevatorQueues.put(i,requestQueue);
            ElevatorThread elevatorThread = new ElevatorThread(requestQueue,i);
            elevatorThread.start();
            requestQueue.checkEle(elevatorThread);
        }
        ScheduleThread schedule = new ScheduleThread(waitQueue,elevatorQueues);
        schedule.start();
        InputThread inputThread = new InputThread(waitQueue, elevatorQueues);
        inputThread.start();
    }
}
