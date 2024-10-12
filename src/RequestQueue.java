import java.util.ArrayList;
import com.oocourse.elevator1.PersonRequest;

public class RequestQueue {
    private final ArrayList<PersonRequest> requests;
    private boolean isEnd;
    private ElevatorThread elevator = null;

    public RequestQueue() {
        requests = new ArrayList<>();
        this.isEnd = false;
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
        //TODO
        // 请替换sentence1为合适内容(4)
        notifyAll();;
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

    public synchronized PersonRequest getGlag() {
        if (this.requests.isEmpty() && !isEnd) {
            try {
                //sentence3;
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (requests.isEmpty()) {
            return null;
        }
        notifyAll();
        return requests.get(0);
    }

    public synchronized PersonRequest getOneRequestAndRemove() {
        //TODO
        //请替换sentence2为合适内容(5)
        //请替换sentence3为合适内容(6)
        if (this.requests.isEmpty() && !isEnd) {
            try {
                //sentence3;
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (requests.isEmpty()) {
            return null;
        }
        PersonRequest req = requests.get(0);
        requests.remove(req);
        notifyAll();
        return req;
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
        notifyAll();
        return requests.isEmpty();
    }
}
