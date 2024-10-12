import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Path path = new Path();
        while (scanner.hasNext()) {
            String operation = scanner.next();
            if (operation.equals("CreatePath")) {
                int count = scanner.nextInt();
                List<Integer> nodeList = new ArrayList<>();
                for (int i = 1; i <= count; i++) {
                    int node = scanner.nextInt();
                    nodeList.add(node);
                }
                path = new Path(nodeList.stream().mapToInt(i -> i).toArray());
                System.out.println("Create " + path);
            } else if (operation.equals("Add")) {
                int x = scanner.nextInt();
                int u = scanner.nextInt();
                int v = scanner.nextInt();
                try {
                    path.addPoint(x, u, v);
                    System.out.println("Add Success");
                } catch (Exception e) {
                    if (e instanceof EdgeNotFoundException) {
                        System.out.println("EdgeNotFoundException");
                    } else if (e instanceof PathIsNotValid) {
                        System.out.println("PathIsNotValidException");
                    } else {
                        System.out.println("System Error");
                    }
                }
            } else if (operation.equals("ExtractLoopPath")) {
                Path ans = path.extractLoopPath();
                if (ans == null) {
                    System.out.println("null");
                } else {
                    System.out.println(ans);
                }
            } else if (operation.equals("PrintPath")) {
                System.out.println(path);
            } else if (operation.equals("End")) {
                break;
            }
        }
    }
}