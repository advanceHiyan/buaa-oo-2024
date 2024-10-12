import com.oocourse.elevator3.PersonRequest;
import java.util.ArrayList;

public class EleCpu {
    private final ElevatorThread elevator;
    private final RequestQueue requestQueue;
    private int now;
    private ArrayList<PersonRequest> inWantOuts = new ArrayList<>();
    private ArrayList<PersonRequest> outWantIns = new ArrayList<>();
    private int dir = 0;
    private boolean startORstop;
    private boolean ifOpen = false;
    private ArrayList<PersonRequest> seeRequests;
    private ArrayList<PersonRequest> personIns;

    public EleCpu(ElevatorThread elevator, RequestQueue requestQueue) {
        this.elevator = elevator;
        this.requestQueue = requestQueue;
    }

    public synchronized void checkStop() {
        outWantIns.clear();
        inWantOuts.clear();
        now = elevator.getNowFloor();
        seeRequests = requestQueue.getRequests();
        personIns = elevator.getInElevators();
        if (elevator.getInNum() == 0 && (seeRequests == null || seeRequests.size() == 0)) {
            startORstop = false;
            ifOpen = false;
            return;
        }
        boolean xiagaung = downContinueDir();
        if (xiagaung) {
            getToDir();
        }
        boolean havePin = false;
        havePin = upCome();
        ifOpen = !((inWantOuts == null || inWantOuts.size() == 0) && havePin == false);
        startORstop = !(xiagaung && (seeRequests == null || seeRequests.size() == 0));
    }

    public boolean upCome() {
        boolean havePin = false;
        int ming = elevator.getMaxNum() - elevator.getInNum() + inWantOuts.size();
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

    public boolean downContinueDir() {
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

    public int getDir() {
        return dir;
    }

    public boolean isIfOpen() {
        return ifOpen;
    }

    public boolean isStartORstop() {
        return startORstop;
    }

    public ArrayList<PersonRequest> getInWantOuts() {
        return inWantOuts;
    }

    public ArrayList<PersonRequest> getOutWantIns() {
        return outWantIns;
    }
}
