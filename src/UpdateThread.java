import java.util.Random;

public class UpdateThread extends Thread {
    private static final Random random = new Random(314159);
    private final Database<String,String> database;
    private final String key;
    private final String value;
    private final String op;
    
    public UpdateThread(Database<String,String> database, String op, String key, String value) {
        this.database = database;
        this.op = op;
        this.key = key;
        this.value = value;
    }
    
    public void run() {
        while (true) {
            if (op.equals("insert")) {
                database.insert(key, value);
            } else {
                database.replace(key, value);
            }

            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
