import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;

public class EleRequestCpu {
    private final ElevatorThread elevator;
    private final RequestQueue requestQueue;
    private ArrayList<NormalResetRequest> seeRequests = null;
    private ArrayList<PersonRequest> inWantOuts = null;
    private ArrayList<PersonRequest> personIns = null;
    private int newSpeedTime;
    private int newMaxNum;

    public EleRequestCpu(ElevatorThread elevator, RequestQueue requestQueue) {
        this.elevator = elevator;
        this.requestQueue = requestQueue;
        this.newSpeedTime = 400;
        this.newMaxNum = 6;
        this.inWantOuts = new ArrayList<>();
    }

    public void checkEleRequest() {
        this.seeRequests = requestQueue.getSets();
        this.personIns = elevator.getInElevators();
        inWantOuts.clear();
        int floor = elevator.getNowFloor();
        if (seeRequests == null || seeRequests.isEmpty()) {
            return;
        }
        NormalResetRequest request = requestQueue.setGetAndRemove();
        this.newMaxNum = request.getCapacity();
        this.newSpeedTime = (int) (request.getSpeed() * 1000);
        for (PersonRequest personRequest:personIns) {
            if (personRequest.getToFloor() == floor) {
                inWantOuts.add(personRequest);
            }
        }
    }

    public int getNewMaxNum() {
        return newMaxNum;
    }

    public int getNewSpeedTime() {
        return newSpeedTime;
    }

    public ArrayList<PersonRequest> getInWantOuts() {
        return inWantOuts;
    }
}
