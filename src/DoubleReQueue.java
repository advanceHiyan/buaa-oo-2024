import java.util.ArrayList;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

public class DoubleReQueue implements ReQueue {
    private final int id; //
    private boolean isEnd;
    private int balance;
    private final int exchange;
    private int tranDownUp;
    private final int speedTime;
    private boolean canChange;
    private ArrayList<PersonRequest> upRequests;
    private ArrayList<PersonRequest> downRequests;
    private DoublEleThread eleDownA;
    private DoublEleThread eleUpB;
    private final int maxNum;
    private int numdA = 0;
    private int numuB = 0;

    public DoubleReQueue(int id, int exchange, int speedTime,int maxNum) {
        this.exchange = exchange;
        this.speedTime = speedTime;
        this.upRequests = new ArrayList<>();
        this.downRequests = new ArrayList<>();
        this.id = id;
        this.canChange = false;
        this.isEnd = false;
        this.tranDownUp = 0;
        this.maxNum = maxNum;
        this.balance = 2 * maxNum;
    }

    public void makeEle(DoublEleThread da,DoublEleThread ub) {
        this.eleDownA = da;
        this.eleUpB = ub;
    }

    public synchronized boolean getUpbGlag() {
        if (upRequests.isEmpty() && !isEnd) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (upRequests.isEmpty() && isEnd && (numdA != 0 || downRequests.size() != 0)) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (upRequests.isEmpty()) {
            return false;
        }
        return true;
    }

    public synchronized void removeUp(PersonRequest request) {
        if (upRequests.contains(request)) {
            upRequests.remove(request);
        } else {
            System.out.println("can not remove in dou");
        }
        notifyAll();
    }

    public synchronized void addRequest(Request request) {
        PersonRequest personRequest = (PersonRequest) request;
        int f = personRequest.getFromFloor();
        int t = personRequest.getToFloor();
        if (f < exchange) {
            downRequests.add(personRequest);
            GetTime.outAndGet(String.format("RECEIVE-%d-%d-A",personRequest.getPersonId(),id));
        } else if (f > exchange) {
            upRequests.add(personRequest);
            GetTime.outAndGet(String.format("RECEIVE-%d-%d-B",personRequest.getPersonId(),id));
        } else {
            if (t < exchange) {
                downRequests.add(personRequest);
                GetTime.outAndGet(String.format("RECEIVE-%d-%d-A",personRequest.getPersonId(),id));
            } else {
                upRequests.add(personRequest);
                GetTime.outAndGet(String.format("RECEIVE-%d-%d-B",personRequest.getPersonId(),id));
            }
        }
        notifyAll();
    }

    public synchronized ArrayList<PersonRequest> getUps() {
        ArrayList<PersonRequest> copys = new ArrayList<>();
        if (upRequests.isEmpty() == false) {
            for (PersonRequest request:upRequests) {
                copys.add(request);
            }
        }
        return copys;
    }

    public synchronized int getSize() {
        int f = upRequests.size() + downRequests.size();
        return f;
    }

    public synchronized int getBalance() {
        return balance;
    }

    public synchronized int getSpeedTime() {
        notifyAll();
        return speedTime;
    }

    public synchronized void writeCanChange(boolean canChange) {
        this.canChange = canChange;
    }

    public synchronized boolean isCanChange() {
        return canChange;
    }

    public synchronized boolean isEnd() {
        return isEnd;
    }

    public synchronized boolean isEmpty() {
        boolean flag = (upRequests.isEmpty() && downRequests.isEmpty());
        return flag;
    }

    public synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
        notifyAll();
    }

    public synchronized void writeSomething(int isAorB,int innum) {
        if (isAorB == -1) {
            numdA = innum;
        } else {
            numuB = innum;
        }
        this.balance = 2 * maxNum - numdA - numuB;
        notifyAll();
    }

    public synchronized void setTranID(int isUpD) {
        this.tranDownUp = isUpD;
        notifyAll();
    }

    public synchronized int getTranDownUp() {
        return tranDownUp;
    }

    public synchronized int getAnotherN(int isAorB) {
        if (isAorB == -1) {
            return numuB;
        } else {
            return numdA;
        }
    }

    /*  *********** ******************    downAs     ***************************  ********   */

    public synchronized boolean getDownaGlag() {
        if (downRequests.isEmpty() && !isEnd) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (downRequests.isEmpty() && isEnd && (numuB != 0 || upRequests.size() != 0)) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (downRequests.isEmpty()) {
            return false;
        }
        return true;
    }

    public synchronized ArrayList<PersonRequest> getDowns() {
        ArrayList<PersonRequest> copys = new ArrayList<>();
        if (downRequests.isEmpty() == false) {
            for (PersonRequest request:downRequests) {
                copys.add(request);
            }
        }
        return copys;
    }

    public synchronized void removeDown(PersonRequest request) {
        if (downRequests.contains(request)) {
            downRequests.remove(request);
        } else {
            System.out.println("can not remove in dou");
        }
        notifyAll();
    }
}
