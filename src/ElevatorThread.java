import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.PersonRequest;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class ElevatorThread extends Thread {
    private final RequestQueue elevatorQueue;
    private final int id;
    private int nowFloor = 1;
    private int maxNum = 6;
    private boolean isOpen = false;
    private boolean isMove = false;
    private int isUpDoenNo = 0;
    private EleCpu cpu;
    private EleRequestCpu elCpu;
    private ArrayList<PersonRequest> inElevators = new ArrayList<>();
    private ArrayList<PersonRequest> specialNeeds = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private long timed = System.currentTimeMillis();
    private int speedTime = 400;

    public ElevatorThread(RequestQueue processingQueue, int id) {
        this.elevatorQueue = processingQueue;
        this.id = id;
        this.cpu = new EleCpu(this,processingQueue);
        this.elCpu = new EleRequestCpu(this,this.elevatorQueue);
    }

    @Override
    public void run() {
        while (true) { //?
            synchronized (elevatorQueue) {
                if (elevatorQueue.isEmpty() && elevatorQueue.isEnd() && getInNum() == 0) {
                    return;
                }
                if (inElevators.size() == 0) {
                    if (elevatorQueue.getGlag() == false) {
                        continue;
                    }
                }
                if (elevatorQueue.getSets() != null) {
                    elCpu.checkEleRequest();
                    resetThis(elCpu.getNewSpeedTime(),elCpu.getNewMaxNum(),elCpu.getInWantOuts());
                }
                if (elevatorQueue.isNeedOver()) {
                    over(elevatorQueue.getDoubleCarResetRequest(),elevatorQueue.getMyQueue());
                    return;
                }
            }
            cpu.checkStop();
            isUpDoenNo = cpu.getDir();
            isMove = cpu.isStartORstop();
            boolean ifopen = cpu.isIfOpen();
            ArrayList<PersonRequest> inWantOs = cpu.getInWantOuts();
            ArrayList<PersonRequest> outWantIs = cpu.getOutWantIns();
            if (ifopen) {
                OutAndIn(inWantOs,outWantIs);
            }
            closeDoor();
            if (isMove) {
                moveOneFloor();
            }
            elevatorQueue.writeSomething(this.isUpDoenNo,this.nowFloor,
                    this.speedTime,(this.maxNum - this.getInNum()));
        }
    }

    public void over(DoubleCarResetRequest cre,DoubleReQueue queue) {
        specialNeeds.clear();
        if (!inElevators.isEmpty()) {
            openDoor();
            for (PersonRequest request:inElevators) {
                outAperson(request);
                if (request.getToFloor() != this.nowFloor) {
                    specialNeeds.add(request);
                }
            }
        }
        closeDoor();
        inElevators.clear();
        elevatorQueue.writeCanChange(false);
        GetTime.outAndGet(String.format("RESET_BEGIN-%d",this.id));
        trySleep(1200);
        GetTime.outAndGet(String.format("RESET_END-%d",this.id));
        ArrayList<PersonRequest> oldFriends = elevatorQueue.getRequests();
        fuckolds(oldFriends,queue);
        elevatorQueue.setEnd(true);
        DoublEleThread ub = new DoublEleThread(queue,id,(int) (cre.getSpeed() * 1000),
                cre.getCapacity(),cre.getTransferFloor(),1);
        DoublEleThread da = new DoublEleThread(queue,id,(int) (cre.getSpeed() * 1000),
                cre.getCapacity(),cre.getTransferFloor(),-1);
        queue.makeEle(da,ub);
        da.start();
        ub.start();
        queue.writeCanChange(true);
    }

    public void resetThis(int newSt,int newMax,ArrayList<PersonRequest> wantOuts) {
        specialNeeds.clear();
        if (!inElevators.isEmpty()) {
            openDoor();
            for (PersonRequest request:inElevators) {
                outAperson(request);
                if (!wantOuts.contains(request)) {
                    specialNeeds.add(request);
                }
            }
            closeDoor();
        }
        inElevators.clear();
        elevatorQueue.writeCanChange(false);
        GetTime.outAndGet(String.format("RESET_BEGIN-%d",this.id));
        this.speedTime = newSt;
        this.maxNum = newMax;
        trySleep(1200);
        GetTime.outAndGet(String.format("RESET_END-%d",this.id));
        elevatorQueue.writeCanChange(true);
        ArrayList<PersonRequest> oldFriends = elevatorQueue.getRequests();
        receiveAgainUp(oldFriends);
    }

    public void fuckolds(ArrayList<PersonRequest> olds,DoubleReQueue queue) {
        synchronized (queue) {
            if (specialNeeds.isEmpty() == false) {
                for (PersonRequest request:specialNeeds) {
                    PersonRequest idk = new PersonRequest(this.nowFloor,
                            request.getToFloor(),request.getPersonId());
                    queue.addRequest(idk);
                }
            }
            specialNeeds.clear();
            if (olds != null && olds.isEmpty() == false) {
                for (PersonRequest request:olds) {
                    queue.addRequest(request);
                }
            }
        }
    }

    public void receiveAgainUp(ArrayList<PersonRequest> oldFriends) {
        int i = 0;
        for (PersonRequest request:specialNeeds) {
            if (i < maxNum) {
                GetTime.outAndGet(String.format("RECEIVE-%d-%d",request.getPersonId(),this.id));
                i++;
            }
        }
        if (oldFriends != null) {
            for (PersonRequest request:oldFriends) {
                GetTime.outAndGet(String.format("RECEIVE-%d-%d",request.getPersonId(),this.id));
            }
        }
        if (!specialNeeds.isEmpty()) {
            openDoor();
            i = 0;
            for (PersonRequest request:specialNeeds) {
                if (i < maxNum) {
                    inElevators.add(request);
                    GetTime.outAndGet(String.format("IN-%d-%d-%d",
                            request.getPersonId(),nowFloor,id));
                    i++;
                } else {
                    PersonRequest idk = new PersonRequest(this.nowFloor,
                            request.getToFloor(),request.getPersonId());
                    elevatorQueue.addRequest(idk);
                }
            }
        }
    }

    public void OutAndIn(ArrayList<PersonRequest> wantOuts, ArrayList<PersonRequest> wantIns) {
        if (wantOuts.size() != 0) {
            openDoor();
            for (PersonRequest request:wantOuts) {
                inElevators.remove(request);
                timed = GetTime.outAndGet(String.format("OUT-%d-%d-%d",
                        request.getPersonId(),nowFloor, id));
            }
        }
        if (wantIns.size() != 0) {
            openDoor();
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
    }

    public long moveOneFloor() {
        long ret = -1;
        try {
            lock.lock();
            try {
                if (true) {
                    nowFloor += isUpDoenNo;
                    Thread.sleep(speedTime);
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

    public void closeDoor() {
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
            return;
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

    public void outAperson(PersonRequest request) {
        timed = GetTime.outAndGet(String.format("OUT-%d-%d-%d",
                request.getPersonId(),nowFloor, id));
    }

    public void trySleep(long time) {
        try {
            lock.lock();
            try {
                Thread.sleep(time);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getNowFloor() {
        return nowFloor;
    }

    public ArrayList<PersonRequest> getInElevators() {
        return inElevators;
    }

    public int getInNum() {
        return inElevators.size();
    }

    public int getMaxNum() {
        return maxNum;
    }
}
