import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;

public class DoubleCpu {
    private ArrayList<PersonRequest> inWantOuts = new ArrayList<>();
    private ArrayList<PersonRequest> outWantIns = new ArrayList<>();
    private ArrayList<PersonRequest> specialNeeds = new ArrayList<>();
    private ArrayList<PersonRequest> seeRequests = null;
    private ArrayList<PersonRequest> personIns = null;
    private final DoubleReQueue requestQueue;
    private final DoublEleThread elevator;
    private final int isAorB;
    private final int maxNum;
    private final int exchange;
    private int dir;
    private boolean startORstop;
    private boolean ifOpen = false;
    private int now;

    public DoubleCpu(DoubleReQueue requestQueueQe, DoublEleThread elevator,
                     int isAorB, int maxNum, int exchange) {
        this.requestQueue = requestQueueQe;
        this.elevator = elevator;
        this.isAorB = isAorB;
        this.maxNum = maxNum;
        this.exchange = exchange;
        this.now = exchange + isAorB;
        this.dir = 0;
    }

    public void check() {
        inWantOuts.clear();
        outWantIns.clear();
        specialNeeds.clear();
        this.seeRequests = getQueRequests();
        this.personIns = elevator.getInElevators();
        this.now = elevator.getNowFloor();
        if (elevator.getInNum() == 0 && (seeRequests == null || seeRequests.size() == 0)) {
            startORstop = false;
            ifOpen = false;
            return;
        }
        if (now == exchange) {
            boolean havein = checkExchange();
            ifOpen = !((inWantOuts == null || inWantOuts.size() == 0) &&
                    (specialNeeds == null || specialNeeds.isEmpty()) && havein == false);
            startORstop = true;
            return;
        }
        boolean xiagaung = downContinueDir();
        if (xiagaung) {
            getToDir();
        }
        boolean havePin = upCome();
        ifOpen = !((inWantOuts == null || inWantOuts.size() == 0) && havePin == false);
        startORstop = !(xiagaung && (seeRequests == null || seeRequests.size() == 0));
    }

    public boolean checkExchange() {
        boolean havePin = false;
        for (int i = 0;i < personIns.size();i++) {
            if (personIns.get(i).getToFloor() == elevator.getNowFloor()) {
                inWantOuts.add(personIns.get(i));
            } else if ((personIns.get(i).getToFloor() - now) * isAorB < 0) {
                specialNeeds.add(personIns.get(i));
            }
        }
        dir = isAorB;
        int ming = maxNum - elevator.getInNum() + inWantOuts.size() + specialNeeds.size();
        if (seeRequests == null || seeRequests.size() == 0) {
            return false;
        }
        for (PersonRequest request:seeRequests) {
            if (ming == 0) {
                break;
            }
            if (request.getFromFloor() == now) {
                ming--;
                outWantIns.add(request);
                havePin = true;
            }
        }
        return havePin;
    }

    public boolean downContinueDir() { //
        boolean xiaguang = true;
        for (int i = 0;i < personIns.size();i++) {
            if (personIns.get(i).getToFloor() == elevator.getNowFloor()) {
                inWantOuts.add(personIns.get(i));
            } else {
                if (xiaguang) {
                    if (personIns.get(i).getToFloor() > elevator.getNowFloor()) {
                        dir = 1;
                    } else {
                        dir = -1;
                    }
                }
                xiaguang = false;
            }
        }
        return xiaguang;
    }

    public void getToDir() {
        //下光了
        if (seeRequests == null || seeRequests.size() == 0) {
            dir = 0;
            return;
        }
        PersonRequest first = seeRequests.get(0);
        if (first.getFromFloor() > elevator.getNowFloor()) {
            dir = 1;
        } else if (first.getFromFloor() < elevator.getNowFloor()) {
            dir = -1;
        } else {
            if (first.getToFloor() < elevator.getNowFloor()) {
                dir = -1;
            } else {
                dir = 1;
            }
        }
    }

    public boolean upCome() {
        boolean havePin = false;
        int ming = maxNum - elevator.getInNum() + inWantOuts.size();
        if (seeRequests == null || seeRequests.size() == 0) {
            return false;
        }
        synchronized (seeRequests) {
            for (PersonRequest request:seeRequests) {
                if (ming < 0) {
                    System.out.println("404 at CPU upCome");
                }
                if (ming == 0) {
                    break;
                }
                int from = request.getFromFloor();
                int cha = request.getToFloor() - from;
                if (from == now & (cha * dir) > 0) {
                    ming--;
                    outWantIns.add(request);
                    havePin = true;
                }
            }
        }
        return havePin;
    }

    public ArrayList<PersonRequest> getQueRequests() {
        if (isAorB == -1) {
            return requestQueue.getDowns();
        } else {
            return requestQueue.getUps();
        }
    }

    public ArrayList<PersonRequest> getInWantOuts() {
        return inWantOuts;
    }

    public ArrayList<PersonRequest> getOutWantIns() {
        return outWantIns;
    }

    public ArrayList<PersonRequest> getSpecialNeeds() {
        return specialNeeds;
    }

    public int getDir() {
        return dir;
    }

    public boolean isIfOpen() {
        return ifOpen;
    }

    public boolean isStartORstop() {
        return startORstop;
    }
}
