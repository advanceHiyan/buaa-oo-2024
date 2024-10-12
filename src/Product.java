public class Product {

    private Process currentProcess;
    private final int id;
    private boolean ifOccupied; // 表示当前产品是否正在被某一工人加工

    public Product(Process initState, int id) {
        this.currentProcess = initState;
        this.id = id;
        this.ifOccupied = false;
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized Process getCurrentProcess() {
        return currentProcess;
    }

    public synchronized boolean isIfOccupied() {
        return ifOccupied;
    }

    public synchronized void setIfOccupied(boolean ifOccupied) {
        this.ifOccupied = ifOccupied;
    }

    public synchronized void toNextProcess() {
        switch (currentProcess) {
            case A:
                currentProcess = Process.B;
                break;
            case B:
                currentProcess = Process.C;
                break;
            case C:
                currentProcess = Process.D;
                break;
            default:
                currentProcess = Process.COMPLETE;
                break;
        }
    }

    public synchronized int getProcessTime() {
        int time = 0;
        switch (currentProcess) {
            case A:
                time = 500;
                break;
            case B:
                time = 1000;
                break;
            case C:
                time = 1500;
                break;
            default:
                time = 2000;
                break;
        }
        return time;
    }
}
