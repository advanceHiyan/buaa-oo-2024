import com.oocourse.TimableOutput;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        Scanner scanner = new Scanner(System.in);

        ArrayList<Product> productList = new ArrayList<>();
        int num = scanner.nextInt();
        for (int x = 0; x < num; x++) {
            Product product;
            String p = scanner.next();
            switch (p) {
                case "A":
                    product = new Product(Process.A, x);
                    break;
                case "B":
                    product = new Product(Process.B, x);
                    break;
                case "C":
                    product = new Product(Process.C, x);
                    break;
                default:
                    product = new Product(Process.D, x);
                    break;
            }
            productList.add(product);
        }

        ArrayList<Worker> workers = new ArrayList<>();
        for (int x = 0; x < 4; x++) {
            Worker worker = new Worker(x);
            workers.add(worker);
            worker.start();
        }

        Pipeline pipeline = new Pipeline(workers, productList);
        pipeline.start();
    }
}