import java.util.ArrayList;
import com.oocourse.elevator1.PersonRequest;

public class ScheduleThread extends Thread {
    private final RequestQueue waitQueue;
    private final ArrayList<RequestQueue> elevatorQueues;

    public ScheduleThread(RequestQueue waitQueue, ArrayList<RequestQueue> elevatorQueues) {
        this.waitQueue = waitQueue;
        this.elevatorQueues = elevatorQueues;
    }

    @Override
    public void run() {
        while (true) {
            //TODO
            //请替换sentence1为合适内容(7)
            if (waitQueue.isEmpty() && waitQueue.isEnd()) {
                for (int i = 0; i < elevatorQueues.size(); i++) {
                    //sentence1;
                    elevatorQueues.get(i).setEnd(true);
                }
                return;
            }
            //TODO
            //请替换sentence2为合适内容(8)
            PersonRequest request = this.waitQueue.getOneRequestAndRemove();
            if (request == null) {
                continue;
            }
            //TODO
            elevatorQueues.get(request.getElevatorId() - 1).addPersonRequest(request);
            //请替换sentence3为合适内容(9)
        }
    }
}
