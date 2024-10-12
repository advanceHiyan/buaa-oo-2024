import java.util.ArrayList;
import java.util.HashMap;

public class Pipeline extends Thread {
    private final ArrayList<Worker> workers;
    private final ArrayList<Product> productList;

    private static final HashMap<Worker, Boolean> IF_WORKING = new HashMap<>(); // 用于记录工人是否正在工作

    public Pipeline(ArrayList<Worker> workers, ArrayList<Product> productList) {
        this.workers = workers;
        this.productList = productList;
        for (Worker worker : workers) {
            IF_WORKING.put(worker, false);
        }
    }

    public static void finishWork(Worker worker) {
        IF_WORKING.replace(worker, false);
    }

    @Override
    public void run() {
        while (true) {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int completeNum = 0; // 已经完成四道工序的产品的数量，用于终止线程
            for (Product product : productList) {
                if (product.isIfOccupied()) {
                    continue;
                }
                if (product.getCurrentProcess() == Process.COMPLETE) {
                    completeNum += 1;
                    continue;
                }
                Process curProcess = product.getCurrentProcess();
                Worker workerX;
                Worker workerY;
                switch (curProcess) {
                    case A:
                        workerX = workers.get(0);
                        workerY = workers.get(3);
                        break;
                    case B:
                        workerX = workers.get(0);
                        workerY = workers.get(1);
                        break;
                    case C:
                        workerX = workers.get(1);
                        workerY = workers.get(2);
                        break;
                    default:
                        workerX = workers.get(2);
                        workerY = workers.get(3);
                        break;
                }
                //TODO
                //请替换sentence1, sentence2, sentence3为合适内容(1)
                if (!IF_WORKING.get(workerX)) {
                    IF_WORKING.replace(workerX, true);
                    product.setIfOccupied(true);
                    workerX.assignProduct(product);
                } else if (!IF_WORKING.get(workerY)) {
                    IF_WORKING.replace(workerY,true);
                    product.setIfOccupied(true);
                    workerY.assignProduct(product);
                }
            }
            //TODO
            //请替换sentence4为合适内容(2)
            if (completeNum == productList.size()) {
                for (Worker worker : workers) {
                    worker.setEnd();
                }
                return;
            }
        }
    }
}
