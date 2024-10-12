import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.ResetRequest;

import java.io.IOException;
import java.util.ArrayList;

public class InputThread extends Thread {
    private final RequestQueue waitQueue;
    private final ArrayList<RequestQueue> elevatorQueues;

    public InputThread(RequestQueue waitQueue, ArrayList<RequestQueue> elevatorQueues) {
        this.waitQueue = waitQueue;
        this.elevatorQueues = elevatorQueues;
    }

    public void run() {
        ElevatorInput input = new ElevatorInput(System.in);
        while (true) {
            Request request = input.nextRequest();
            if (request == null) {
                waitQueue.setEnd(true);
                break;
            } else if (request instanceof ResetRequest) {
                int id = ((ResetRequest) request).getElevatorId();
                elevatorQueues.get(id - 1).setAddRequest((ResetRequest) request);
            } else {
                waitQueue.addRequest(request);
            }
        }
        try {
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
