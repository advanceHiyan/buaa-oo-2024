import com.oocourse.TimableOutput;

public class Worker extends Thread {
    private static final int WAITING = 0;
    private static final int WORKING = 1;
    private static final int END = 2;
    private int state;
    private final int id;
    private Product product;

    public Worker(int id) {
        this.id = id;
        this.product = null;
        this.state = WAITING;
    }

    @Override
    public void run() {
        while (true) {
            if (ifEnd()) {
                return;
            }
            if (ifWorking()) {
                workOnOutput(product);
                try {
                    sleep(product.getProcessTime());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                completeOutput(product);
                //TODO
                //请替换sentence1为合适内容（提示：进行下一步产品工序）(3)
                product.toNextProcess();
                product.setIfOccupied(false);
                state = WAITING;
                //TODO
                //请替换sentence2为合适内容（提示：解除流水线中工人的工作状态）(4)
                Pipeline.finishWork(this);
            } else {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void workOnOutput(Product product) {
        TimableOutput.println(id + "-Work_On-" + product.getId()
                + "-Process-" + product.getCurrentProcess().name());
    }

    public void completeOutput(Product product) {
        //TODO
        //请替换sentence3为合适内容(5)
        TimableOutput.println(id + "-Complete-" +product.getId()
                + "-Process-" + product.getCurrentProcess().name());
    }

    public synchronized boolean ifWorking() {
        return state == WORKING;
    }

    public synchronized boolean ifEnd() {
        return state == END;
    }

    public synchronized void assignProduct(Product product) {
        this.product = product;
        state = WORKING;
    }

    public synchronized void setEnd() {
        state = END;
    }
}
