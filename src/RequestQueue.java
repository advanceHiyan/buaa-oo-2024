import java.util.ArrayList;
import com.oocourse.elevator2.ResetRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;

public class RequestQueue {
    private final ArrayList<PersonRequest> requests;
    private boolean isEnd;
    private ElevatorThread elevator = null;
    private ArrayList<ResetRequest> setRequests;
    private ArrayList<Request> waitRequests = null;
    private boolean canChange;
    private final int id;
    private int dir;
    private int floor;
    private int balance;
    private int speedTime;

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

    public synchronized boolean make() {
        boolean flag = true;
        if (this.waitRequests == null) {
            this.waitRequests = new ArrayList<>();
        } else {
            flag = false;
        }
        notifyAll();
        return flag;
    }

    public synchronized void checkEle(ElevatorThread elevator) {
        this.elevator = elevator;
        notifyAll();
    }

    public synchronized void addPersonRequest(PersonRequest personrequest) {
        if (this.elevator == null) {
            requests.add(personrequest);
        } else {
            elevator.getlock().lock();
            requests.add(personrequest);
            elevator.getlock().unlock();
        }
        GetTime.outAndGet(String.format("RECEIVE-%d-%d",personrequest.getPersonId(),id));
        notifyAll();
    }

    public synchronized void addRequest(Request request) {
        waitRequests.add(request);
        notifyAll();
    }

    public synchronized void setAddRequest(ResetRequest request) {
        if (this.elevator == null) {
            setRequests.add(request);
        } else {
            elevator.getlock().lock();
            setRequests.add(request);
            elevator.getlock().unlock();
        }
        notifyAll();;
    }

    public synchronized ResetRequest setGetAndRemove() {
        if (setRequests.isEmpty()) {
            return null;
        }
        ResetRequest request = setRequests.get(0);
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

    public synchronized ArrayList<ResetRequest> getSets() {
        if (setRequests.isEmpty()) {
            return null;
        }
        ArrayList<ResetRequest> copyr = new ArrayList<>();
        for (ResetRequest request:setRequests) {
            copyr.add(request);
        }
        notifyAll();
        return copyr;
    }

    public synchronized boolean getGlag() { //让电梯wait
        if (this.isEmpty() && !isEnd) {
            try {
                //sentence3;
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (this.isEmpty()) {
            return false;
        }
        notifyAll();
        return true;
    }

    public synchronized Request getOneRequestAndRemove() { //让调度器wait
        if (this.waitRequests.isEmpty() && !isEnd) {
            try {
                //sentence3;
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (waitRequests.isEmpty()) {
            return null;
        }
        Request req = waitRequests.get(0);
        waitRequests.remove(req);
        notifyAll();
        return req;
    }

    public synchronized boolean waitIsEmppty() {
        boolean f = this.waitRequests.isEmpty();
        notifyAll();
        return f;
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
}
