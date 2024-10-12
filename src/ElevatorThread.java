import com.oocourse.elevator1.PersonRequest;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ElevatorThread extends Thread {
    private final RequestQueue elevatorQueue;
    private final int id;
    private int nowFloor = 1;
    private int maxNum = 6;
    private int inNum = 0;
    private boolean isOpen = false;
    private boolean isMove = false;
    private int isUpDoenNo = 0;
    private EleCpu cpu;
    private ArrayList<PersonRequest> inElevators = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private long timed = System.currentTimeMillis();
    private boolean flag = true;

    public ElevatorThread(RequestQueue processingQueue, int id) {
        this.elevatorQueue = processingQueue;
        this.id = id;
        this.cpu = new EleCpu(this,processingQueue);
    }

    @Override
    public void run() {
        //        try {
        //
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }
        while (true) {
            synchronized (elevatorQueue) {
                if (elevatorQueue.isEmpty() && elevatorQueue.isEnd() && getInNum() == 0) {
                    return;
                }
                PersonRequest request = elevatorQueue.getGlag();
                if ((request == null) && inElevators.size() == 0) {
                    //                    elevatorQueue.wait();
                    continue;
                }
            }

            cpu.checkStop();
            //                System.out.println("floor"+nowFloor+"id"+id);
            isUpDoenNo = cpu.getDir();
            isMove = cpu.isStartORstop();
            boolean ifopen = cpu.isIfOpen();
            ArrayList<PersonRequest> inWantOs = cpu.getInWantOuts();
            ArrayList<PersonRequest> outWantIs = cpu.getOutWantIns();
            if (ifopen) {
                long point = OutAndIn(inWantOs,outWantIs);
                closeDoor(timed);
            }
            if (isMove) {
                moveOneFloor(timed);
            }
        }
    }

    public long OutAndIn(ArrayList<PersonRequest> wantOuts, ArrayList<PersonRequest> wantIns) {
        long lastOpenTime = -1;
        if (wantOuts.size() != 0) {
            if (openDoor()) {
                lastOpenTime = timed;
            }
            for (PersonRequest request:wantOuts) {
                inElevators.remove(request);
                timed = GetTime.outAndGet(String.format("OUT-%d-%d-%d",
                        request.getPersonId(),nowFloor, id));
            }
        }
        if (wantIns.size() != 0) {
            if (openDoor()) {
                lastOpenTime = timed;
            }
            for (PersonRequest request: wantIns) {
                if (inElevators.size() < maxNum) {
                    inElevators.add(request);
                    synchronized (elevatorQueue) {
                        elevatorQueue.removeRequest(request);
                    }
                    timed = GetTime.outAndGet(String.format("IN-%d-%d-%d",
                            request.getPersonId(),nowFloor,id));
                }
            }
        }
        return lastOpenTime;
    }

    public long moveOneFloor(long point) {
        long ret = -1;
        try {
            long focus = System.currentTimeMillis();
            long wait = 400;
            lock.lock();
            try {
                if (true) {
                    nowFloor += isUpDoenNo;
                    Thread.sleep(400);
                    timed = GetTime.outAndGet(String.format("ARRIVE-%d-%d",nowFloor,id));
                    ret = timed;
                }
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void closeDoor(long point) {
        if (isOpen) {
            try {
                lock.lock();
                try {
                    if (true) {
                        Thread.sleep(200);
                        isOpen = false;
                        timed = GetTime.outAndGet(String.format("CLOSE-%d-%d",nowFloor,id));
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("already close!\n");
        }
    }

    public boolean openDoor() {
        if (!isOpen) {
            try {
                lock.lock();
                try {
                    if (true) {
                        timed = GetTime.outAndGet(String.format("OPEN-%d-%d",nowFloor,id));
                        Thread.sleep(200);
                        isOpen = true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public int getNowFloor() {
        return nowFloor;
    }

    public boolean getIfMove() {
        return isMove;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public ArrayList<PersonRequest> getInElevators() {
        return inElevators;
    }

    public ReentrantLock getlock() {
        return lock;
    }

    public Condition getCondition() {
        return condition;
    }

    public int getInNum() {
        return inElevators.size();
    }

    public int getDir() {
        return isUpDoenNo;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public int geteleId() {
        return id;
    }
}
