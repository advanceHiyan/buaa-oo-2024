import java.util.ArrayList;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.ResetRequest;

import static java.lang.Math.abs;

public class ScheduleThread extends Thread {
    private final RequestQueue waitQueue;
    private final ArrayList<RequestQueue> elevatorQueues;
    private static long random = 0;

    public ScheduleThread(RequestQueue waitQueue, ArrayList<RequestQueue> elevatorQueues) {
        this.waitQueue = waitQueue;
        this.elevatorQueues = elevatorQueues;
    }

    @Override
    public void run() {
        while (true) {
            if (waitQueue.waitIsEmppty() && waitQueue.isEnd()) {
                for (int i = 0; i < elevatorQueues.size(); i++) {
                    elevatorQueues.get(i).setEnd(true);
                }
                return;
            }
            Request request = this.waitQueue.getOneRequestAndRemove();
            if (request == null) {
                continue;
            }
            if (request instanceof PersonRequest) {
                int from = ((PersonRequest) request).getFromFloor();
                int to = ((PersonRequest) request).getToFloor();
                int recommended = getWhich(from,to);
                elevatorQueues.get(recommended - 1).addPersonRequest((PersonRequest) request);
            } else if (request instanceof ResetRequest) {
                int id = ((ResetRequest) request).getElevatorId();
                elevatorQueues.get(id - 1).setAddRequest((ResetRequest) request);
            }
        }
    }

    public int getWhich(int f,int t) {
        int ret = (int) (random % 6 + 1);
        int poor = t - f;
        Double average = (f + t) / 2.0;
        if (abs(poor) > 7 && elevatorQueues.get(0).getSize() < 5 &&
                elevatorQueues.get(0).getBalance() > 0) {
            ret = 1;
            if (!elevatorQueues.get(0).isCanChange() || (getDistance(elevatorQueues.get(0),f,t)
                    > 16 && getDistance(elevatorQueues.get(1),f,t) <= 13)) {
                ret = 2;
                if (!elevatorQueues.get(1).isCanChange()) {
                    ret = -1;
                }
            }
        } else {
            if (average <= 5) {
                ret = comparativeWeight(2,3,f,t);
            } else if (average >= 8) {
                ret = comparativeWeight(4,5,f,t);
            } else {
                if (elevatorQueues.get(0).isCanChange() && elevatorQueues.get(0).getDir() * poor > 0
                        && (f - elevatorQueues.get(0).getFloor()) * poor > 0) {
                    ret = 1;
                } else if (elevatorQueues.get(1).isCanChange() &&
                        elevatorQueues.get(1).getSize() < 5 &&
                        elevatorQueues.get(1).getDir() * poor >= 0) {
                    ret = 2;
                } else if (average > 6 && elevatorQueues.get(4).getSize() < 6
                        && elevatorQueues.get(5).getSize() < 6) {
                    ret = comparativeWeight(4,5,f,t);
                } else if (elevatorQueues.get(2).getSize() < 6 &&
                        elevatorQueues.get(3).getSize() < 6) {
                    ret = comparativeWeight(2,3,f,t);
                } else if (elevatorQueues.get(4).getSize() < 6
                        && elevatorQueues.get(5).getSize() < 6) {
                    ret = comparativeWeight(4,5,f,t);
                } else {
                    ret = -1;
                }
            }
        }
        random++;
        if (ret != -1) {
            return ret;
        } else {
            int r = 1;
            for (int i = 0;i <= 5;i++) {
                if (elevatorQueues.get(i).isCanChange()) {
                    r = i + 1;
                    break;
                }
            }
            return r;
        }
    }

    public int comparativeWeight(int i,int j,int f,int t) {
        int first = i + 1;
        int second = j + 1;
        RequestQueue e1 = elevatorQueues.get(i);
        RequestQueue e2 = elevatorQueues.get(j);
        if (e1.isCanChange() == false && e2.isCanChange() == false) { // ischange
            return -1;
        }
        if (!e1.isCanChange()) {
            return second;
        }
        if (!e2.isCanChange()) {
            return first;
        }
        int poor = t - f;
        if (e1.getDir() * poor > 0 && (f - e1.getFloor()) * poor > 0) { // 顺路
            return first;
        }
        if (e2.getDir() * poor > 0 && (f - e2.getFloor()) * poor  > 0) {
            return second;
        }
        if (e1.getBalance() <= 2 || e1.getSize() > 5) { // 满了
            return second;
        }
        if (e2.getBalance() <= 2 || e2.getSize() > 5) {
            return first;
        }
        if (e1.getSpeedTime() < e2.getSpeedTime() && e1.getBalance() > 4) { // 速度
            return first;
        }
        if (e1.getSpeedTime() > e2.getSpeedTime() && e2.getBalance() > 4) {
            return second;
        }
        if (e1.getBalance() == 6 && e1.getDir() == 0 &&
                e2.getBalance() == 6 && e2.getDir() == 0) { // 都空
            if (random % 2 == 0) {
                return first;
            } else {
                return second;
            }
        }
        if (e1.getBalance() == 6 && e1.getDir() == 0) {
            return first;
        }
        if (e2.getBalance() == 6 && e2.getDir() == 0) {
            return second;
        }
        if (random % 2 == 0) { //随机
            return first;
        } else {
            return second;
        }
    }

    public int getDistance(RequestQueue e,int f,int t) {
        int dir = e.getDir();
        int now = e.getFloor();
        int ret = 0;
        if (dir * (f - now) < 0) {
            ret += 5;
        }
        ret += abs(f - now);
        ret += abs(f - t);
        return ret;
    }
}
