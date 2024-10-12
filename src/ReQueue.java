import com.oocourse.elevator3.Request;

public interface ReQueue {
    void setEnd(boolean isEnd);

    boolean isEnd();

    boolean isEmpty();

    void addRequest(Request request);

    boolean isCanChange();

    int getSize();

    void writeCanChange(boolean canChange);

    int getSpeedTime();

    int getBalance();
}
