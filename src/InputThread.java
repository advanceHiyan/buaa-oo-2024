import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

import java.io.IOException;

public class InputThread extends Thread {
    private final RequestQueue waitQueue;

    public InputThread(RequestQueue waitQueue) {
        this.waitQueue = waitQueue;
    }

    public void run() {
        ElevatorInput input = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = input.nextPersonRequest();
            if (request == null) {
                waitQueue.setEnd(true);
                break;
            } else {
                waitQueue.addPersonRequest(request);
            }
        }
        try {
            input.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
