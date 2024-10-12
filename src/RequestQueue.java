import java.util.ArrayList;

import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.Request;

public class RequestQueue implements ReQueue {
    private final ArrayList<PersonRequest> requests;
    private DoubleReQueue myQueue;
    private boolean isEnd;
    private ElevatorThread elevator = null;
    private ArrayList<NormalResetRequest> setRequests;
    private boolean canChange;
    private final int id;
    private int dir;
    private int floor;
    private int balance;
    private int speedTime;
    private boolean needOver = false;
    private DoubleCarResetRequest doubleCarResetRequest = null;

    public RequestQueue(int id) {
        this.id = id;
        requests = new ArrayList<>();
        setRequests = new ArrayList<>();
        this.isEnd = false;
        this.dir = 0;
        this.floor = 1;
        this.balance = 6;
        this.speedTime = 400;
        this.canChange = true;
    }

    public synchronized void addRequest(Request request) {
        if (request instanceof PersonRequest) {
            PersonRequest personRequest = (PersonRequest) request;
            requests.add(personRequest);
            GetTime.outAndGet(String.format("RECEIVE-%d-%d",personRequest.getPersonId(),id));
        } else if (request instanceof NormalResetRequest) {
            setRequests.add((NormalResetRequest) request);
        } else {
            System.out.println("???你来干啥");
        }
        notifyAll();
    }

    public synchronized void checkEle(ElevatorThread elevator) {
        this.elevator = elevator;
        notifyAll();
    }

    public synchronized NormalResetRequest setGetAndRemove() {
        if (setRequests.isEmpty()) {
            return null;
        }
        NormalResetRequest request = setRequests.get(0);
        setRequests.remove(request);
        notifyAll();
        return request;
    }

    public synchronized void removeRequest(PersonRequest request) {
        if (requests.contains(request)) {
            requests.remove(request);
        } else {
            System.out.println("no found request,an not remove");
        }
        notifyAll();
    }

    public synchronized ArrayList<PersonRequest> getRequests() {
        if (requests.isEmpty()) {
            return null;
        }
        ArrayList<PersonRequest> copyRe = new ArrayList<>();
        for (PersonRequest request:requests) {
            copyRe.add(request);
        }
        notifyAll();
        return copyRe;
    }

    public synchronized ArrayList<NormalResetRequest> getSets() {
        if (setRequests.isEmpty()) {
            return null;
        }
        ArrayList<NormalResetRequest> copyr = new ArrayList<>();
        for (NormalResetRequest request:setRequests) {
            copyr.add(request);
        }
        notifyAll();
        return copyr;
    }

    public synchronized boolean getGlag() { //让电梯wait
        if (this.isEmpty() && !isEnd && isNeedOver() == false) {
            try {
                //sentence3;
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (this.isEmpty() && needOver == false) {
            return false;
        }
        notifyAll();
        return true;
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        notifyAll();
    }

    public synchronized boolean isEnd() {
        notifyAll();
        return isEnd;
    }

    public synchronized boolean isEmpty() {
        boolean flag = requests.isEmpty() && setRequests.isEmpty();
        notifyAll();
        return flag;
    }

    public synchronized int getDir() {
        notifyAll();
        return dir;
    }

    public synchronized int getFloor() {
        notifyAll();
        return floor;
    }

    public synchronized int getBalance() {
        notifyAll();
        return balance;
    }

    public synchronized int getSpeedTime() {
        notifyAll();
        return speedTime;
    }

    public synchronized void writeSomething(int dir,int floor,int st,int balance) {
        this.speedTime = st;
        this.dir = dir;
        this.balance = balance;
        this.floor = floor;
        notifyAll();
    }

    public synchronized void writeCanChange(boolean canChange) {
        this.canChange = canChange;
        notifyAll();
    }

    public synchronized int getSize() {
        int f = requests.size();
        notifyAll();
        return f;
    }

    public synchronized boolean isCanChange() {
        notifyAll();
        return canChange;
    }

    public synchronized void makenewGG(DoubleCarResetRequest re) {
        int speed = (int) (re.getSpeed() * 1000);
        this.myQueue = new DoubleReQueue(this.id,re.getTransferFloor(),speed,re.getCapacity());
        this.needOver = true;
        this.doubleCarResetRequest = re;
        notifyAll();
    }

    public synchronized DoubleCarResetRequest getDoubleCarResetRequest() {
        notifyAll();
        return doubleCarResetRequest;
    }

    public synchronized boolean isNeedOver() {
        notifyAll();
        return needOver;
    }

    public synchronized DoubleReQueue getMyQueue() {
        notifyAll();
        return myQueue;
    }
}
