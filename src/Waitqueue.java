import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.Request;

import java.util.ArrayList;

public class Waitqueue {
    private boolean isEnd;
    private ArrayList<Request> waitRequests = null;
    private ArrayList<Request> dcs = null;

    public Waitqueue() {
        this.isEnd = false;
        this.waitRequests = new ArrayList<>();
        this.dcs = new ArrayList<>();
    }

    public synchronized void addRequest(Request request) {
        if (request instanceof DoubleCarResetRequest) {
            dcs.add(request);
            notifyAll();
            return;
        }
        waitRequests.add(request);
        notifyAll();
    }

    public synchronized boolean getFlag() {
        return (!dcs.isEmpty());
    }

    public synchronized Request getOneRequestAndRemove() { //让调度器wait
        if (this.waitRequests.isEmpty() && !isEnd) {
            try {
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

    public synchronized Request getOneRemove() {
        Request req = dcs.get(0);
        dcs.remove(req);
        notifyAll();
        return req;
    }

    public synchronized boolean isEnd() {
        notifyAll();
        return isEnd;
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        notifyAll();
    }

    public synchronized boolean isEmpty() {
        boolean f = (this.waitRequests.isEmpty() && this.dcs.isEmpty());
        notifyAll();
        return f;
    }
}
