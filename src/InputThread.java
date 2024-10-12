import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.Request;

import java.io.IOException;
import java.util.HashMap;

public class InputThread extends Thread {
    private final Waitqueue waitQueue;
    private final HashMap<Integer,ReQueue> elevatorQueues;

    public InputThread(Waitqueue waitQueue, HashMap<Integer,ReQueue> elevatorQueues) {
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
            } else if (request instanceof NormalResetRequest) {
                int id = ((NormalResetRequest) request).getElevatorId();
                elevatorQueues.get(id).addRequest(request);
            } else if (request instanceof DoubleCarResetRequest) {
                int id = ((DoubleCarResetRequest) request).getElevatorId();
                if (elevatorQueues.get(id) instanceof RequestQueue) {
                    ((RequestQueue) elevatorQueues.get(id)).
                            makenewGG((DoubleCarResetRequest) request);
                }
                waitQueue.addRequest(request);
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
