import com.oocourse.TimableOutput;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        // 输入UpdateThread信息
        Scanner scanner = new Scanner(System.in);
        int numInsertThread = scanner.nextInt();
        ArrayList<String> updateOps = new ArrayList<>();
        ArrayList<String> updateKeys = new ArrayList<>();
        ArrayList<String> updateValues = new ArrayList<>();
        for (int i = 0; i < numInsertThread; i++) {
            String op = scanner.next();
            updateOps.add(op);
            String key = scanner.next();
            updateKeys.add(key);
            String value = scanner.next();
            updateValues.add(value);
        }
        
        int numSelectThread = scanner.nextInt();
        ArrayList<String> selectKeys = new ArrayList<>();
        for (int i = 0; i < numSelectThread; i++) {
            String key = scanner.next();
            selectKeys.add(key);
        }
    
        Database<String, String> database = new Database<>();
        
        // 启动UpdateThread线程
        for (int i = 0; i < numInsertThread; i++) {
            new UpdateThread(database, updateOps.get(i), updateKeys.get(i), updateValues.get(i)).start();
        }
        
        
        // 启动SelectThread线程
        for (int i = 0; i < numSelectThread; i++) {
            new SelectThread(database, selectKeys.get(i)).start();
        }
        
        // 停止约5秒
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 强制终止
        System.exit(0);
    }
}
