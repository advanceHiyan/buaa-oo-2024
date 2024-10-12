import com.oocourse.elevator3.PersonRequest;
import java.util.ArrayList;

public class DoublEleThread extends Thread {
    private final DoubleReQueue elevatorQueue;
    private final int id;
    private final int speedTime;
    private final int maxNum;
    private final int exchange;
    private final int isAorB;
    private final DoubleCpu elCpu;
    private ArrayList<PersonRequest> inElevators = new ArrayList<>();
    private boolean isOpen = false;
    private int nowFloor;
    private int eleDir = 0;

    public DoublEleThread(DoubleReQueue queue,int id, int speedTime,
                          int maxNum, int exchange, int isAorB) {
        this.elevatorQueue = queue;
        this.id = id;
        this.speedTime = speedTime;
        this.maxNum = maxNum;
        this.exchange = exchange;
        this.isAorB = isAorB;
        this.nowFloor = exchange + isAorB;
        this.elCpu = new DoubleCpu(elevatorQueue,this,isAorB,maxNum,exchange);
    }

    @Override
    public void run() {
        while (true) {
            synchronized (elevatorQueue) {
                if (elevatorQueue.isEmpty() && elevatorQueue.isEnd() &&
                        getInNum() == 0 && elevatorQueue.getAnotherN(isAorB) == 0) {
                    return;
                }
                if (inElevators.size() == 0) {
                    if (getGlag() == false) {
                        continue;
                    }
                }
            }
            elCpu.check();
            if (nowFloor == exchange) {
                tranAway();
            } else {
                boolean ifopen = elCpu.isIfOpen();
                eleDir = elCpu.getDir();
                boolean ifstart = elCpu.isStartORstop();
                if (ifopen) {
                    outAndin();
                }
                closeDoor();
                if (ifstart) {
                    moveOneFloor();
                }
            }
        }
    }

    public void tranAway() {
        ArrayList<PersonRequest> inWouts = elCpu.getInWantOuts();
        ArrayList<PersonRequest> outWins = elCpu.getOutWantIns();
        ArrayList<PersonRequest> specialNeeds = elCpu.getSpecialNeeds();
        eleDir = isAorB;
        boolean ifopen = elCpu.isIfOpen();
        if (ifopen) {
            openDoor();
            for (int i = 0;i < inWouts.size();i++) {
                inElevators.remove(inWouts.get(i));
                outAperson(inWouts.get(i));
            }
            for (int i = 0;i < specialNeeds.size();i++) {
                inElevators.remove(specialNeeds.get(i));
                outAperson(specialNeeds.get(i));
                PersonRequest idk = new PersonRequest(exchange,specialNeeds.get(i).
                        getToFloor(),specialNeeds.get(i).getPersonId());
                elevatorQueue.addRequest(idk);
            }
            synchronized (elevatorQueue) {
                elevatorQueue.writeSomething(isAorB,inElevators.size());
            }
            for (int i = 0;i < outWins.size();i++) {
                if (inElevators.size() < maxNum) {
                    inOnePer(outWins.get(i));
                    inElevators.add(outWins.get(i));
                    synchronized (elevatorQueue) {
                        elevatorQueue.writeSomething(isAorB,inElevators.size());
                    }
                    removeRqs(outWins.get(i));
                } else {
                    break;
                }
            }
        }
        closeDoor();
        moveOneFloor();
    }

    public void outAndin() {
        ArrayList<PersonRequest> inWouts = elCpu.getInWantOuts();
        ArrayList<PersonRequest> outWins = elCpu.getOutWantIns();
        openDoor();
        for (int i = 0;i < inWouts.size();i++) {
            inElevators.remove(inWouts.get(i));
            synchronized (elevatorQueue) {
                elevatorQueue.writeSomething(isAorB,inElevators.size());
            }
            outAperson(inWouts.get(i));
        }
        for (int i = 0;i < outWins.size();i++) {
            if (inElevators.size() < maxNum) {
                inOnePer(outWins.get(i));
                inElevators.add(outWins.get(i));
                synchronized (elevatorQueue) {
                    elevatorQueue.writeSomething(isAorB,inElevators.size());
                }
                removeRqs(outWins.get(i));
            } else {
                break;
            }
        }
    }

    public void moveOneFloor() {
        int nextFr = nowFloor + eleDir;
        if (nextFr == exchange) {
            while (elevatorQueue.getTranDownUp() != 0) {
                trySleep((long) speedTime / 2);
            }
            elevatorQueue.setTranID(isAorB);
            trySleep(speedTime);
            nowFloor = nextFr;
            Arrive();
        } else if (nowFloor == exchange) {
            trySleep((long) speedTime / 10);
            elevatorQueue.setTranID(0);
            trySleep((long) speedTime / 10 * 9);
            nowFloor += isAorB;
            Arrive();
        } else {
            trySleep(speedTime);
            nowFloor = nextFr;
            Arrive();
        }
    }

    public void closeDoor() {
        if (isOpen) {
            trySleep(200);
            isOpen = false;
            if (isAorB == -1) {
                GetTime.outAndGet(String.format("CLOSE-%d-%d-A",nowFloor,id));
            } else {
                GetTime.outAndGet(String.format("CLOSE-%d-%d-B",nowFloor,id));
            }
        }
    }

    public void  openDoor() {
        if (!isOpen) {
            if (isAorB == -1) {
                GetTime.outAndGet(String.format("OPEN-%d-%d-A",nowFloor,id));
            } else {
                GetTime.outAndGet(String.format("OPEN-%d-%d-B",nowFloor,id));
            }
            trySleep(200);
            isOpen = true;
        }
    }

    public boolean getGlag() {
        if (isAorB == -1) {
            return elevatorQueue.getDownaGlag();
        } else {
            return elevatorQueue.getUpbGlag();
        }
    }

    public void trySleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Arrive() {
        if (isAorB == -1) {
            GetTime.outAndGet(String.format("ARRIVE-%d-%d-A",nowFloor,id));
        } else {
            GetTime.outAndGet(String.format("ARRIVE-%d-%d-B",nowFloor,id));
        }
    }

    public void removeRqs(PersonRequest request) {
        if (isAorB == -1) {
            elevatorQueue.removeDown(request);
        } else {
            elevatorQueue.removeUp(request);
        }
    }

    public void outAperson(PersonRequest request) {
        if (isAorB == -1) {
            GetTime.outAndGet(String.format("OUT-%d-%d-%d-A",
                    request.getPersonId(),nowFloor, id));
        } else {
            GetTime.outAndGet(String.format("OUT-%d-%d-%d-B",
                    request.getPersonId(),nowFloor, id));
        }
    }

    public void inOnePer(PersonRequest request) {
        if (isAorB == -1) {
            GetTime.outAndGet(String.format("IN-%d-%d-%d-A",
                    request.getPersonId(),nowFloor,id));
        } else {
            GetTime.outAndGet(String.format("IN-%d-%d-%d-B",
                    request.getPersonId(),nowFloor,id));
        }
    }

    public int getNowFloor() {
        return nowFloor;
    }

    public int getInNum() {
        return inElevators.size();
    }

    public ArrayList<PersonRequest> getInElevators() {
        return inElevators;
    }
}
