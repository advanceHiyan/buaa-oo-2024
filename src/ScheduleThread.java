import java.util.HashMap;
import java.util.Random;

import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

public class ScheduleThread extends Thread {
    private final Waitqueue waitQueue;
    private final HashMap<Integer,ReQueue> elevatorQueues;

    public ScheduleThread(Waitqueue waitQueue, HashMap<Integer,ReQueue> elevatorQueues) {
        this.waitQueue = waitQueue;
        this.elevatorQueues = elevatorQueues;
    }

    @Override
    public void run() {
        while (true) {
            if (waitQueue.isEmpty() && waitQueue.isEnd()) {
                for (int i = 1; i <= 6; i++) {
                    elevatorQueues.get(i).setEnd(true);
                }
                return;
            }
            Request request = null;
            if (waitQueue.getFlag()) {
                request = this.waitQueue.getOneRemove();
            } else {
                request = this.waitQueue.getOneRequestAndRemove();
            }
            if (request == null) {
                continue;
            }
            if (request instanceof PersonRequest) {
                int from = ((PersonRequest) request).getFromFloor();
                int to = ((PersonRequest) request).getToFloor();
                int recommended = getWhich();
                while (recommended == 7) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (waitQueue.getFlag()) {
                        DoubleCarResetRequest dc = (DoubleCarResetRequest) waitQueue.getOneRemove();
                        int id = dc.getElevatorId();
                        RequestQueue  re = (RequestQueue) elevatorQueues.get(id);
                        elevatorQueues.replace(id,re.getMyQueue());
                    }
                    recommended = getWhich();
                }
                elevatorQueues.get(recommended).addRequest(request);
            } else if (request instanceof NormalResetRequest) {
                int id = ((NormalResetRequest) request).getElevatorId();
                elevatorQueues.get(id).addRequest(request);
            } else {
                int id = ((DoubleCarResetRequest) request).getElevatorId();
                RequestQueue  re = (RequestQueue) elevatorQueues.get(id);
                elevatorQueues.replace(id,re.getMyQueue());
            }
        }
    }

    public int getWhich() {
        int [] arr = new int[]{0,0,0,0,0,0,0,0,0,0,0,0};
        int k = 0;
        boolean flag = false;
        for (int i = 1;i <= 6;i++) {
            if (elevatorQueues.get(i).isCanChange() && elevatorQueues.get(i).getBalance()
                    > 0 && elevatorQueues.get(i).getSize() < 6) {
                if (elevatorQueues.get(i) instanceof DoubleReQueue) {
                    arr[k] = i;
                    arr[k + 1] = i;
                    k += 2;
                } else {
                    arr[k] = i;
                    k++;
                    if (elevatorQueues.get(i).getBalance() > 4 &&
                            elevatorQueues.get(i).getSpeedTime() < 400) {
                        arr[k] = i;
                        k++;
                    }
                }
                flag = true;
            }
        }
        int ret = 7;
        if (flag) {
            while ((ret = arr[new Random().nextInt(arr.length)]) == 0) {
                ret = 1;
            }
        } else {
            for (int i = 1;i <= 6;i++) {
                if (elevatorQueues.get(i).isCanChange() && elevatorQueues.get(i).getSize() < 6) {
                    flag = true;
                    ret = i;
                }
            }
        }
        if (flag == false) {
            ret = 7;
        }
        return ret;
    }
}
