import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.*;

import java.util.*;

@RunWith(Parameterized.class)
public class PathTest {//包含题目[3]
    //构造方法中的参数默认由PrepareData中的数据提供
    private Path path;
    private int u;
    private int v;
    private int x;
    
    public PathTest(Path path, int x, int u, int v) {
        this.path = path;
        this.u = u;
        this.x = x;
        this.v = v;
    }
    
    @Parameters
    public static Collection prepareData() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        int testNum = 10;//测试次数,可根据需求调整
        
        //该二维数组的类型必须是Object类型的
        //该二维数组中的第一维代表有多少组测试数据,有多少次测试就会创造多少个PathTest对象
        //该二维数组的第二维代表PathTest构造方法中的参数，位置一一对应
        Object[][] object = new Object[testNum][];
        for (int i = 0; i < testNum; i++) {
            Path path;
            if (i % 3 == 0) {
                path = generateInvalidRoad();
            } else if (i % 3 == 1) {
                path = generateLoopValidRoad();
            } else {
                path = generateNoLoopValidRoad();
            }
            int loc = random.nextInt(path.size() - 2);
            int u = path.getNode(loc);
            int v = path.getNode(random.nextInt(path.size() - 1));
            if (random.nextInt(2) == 0)//生成的测试数据需要覆盖掉边存在和边不存在的情况
                v = path.getNode(loc + 1);
            int MaxX = 100;//x参数的最大值是多少s
            object[i] = new Object[]{path, random.nextInt(MaxX), u, v};
        }
        return Arrays.asList(object);
    }
    
    @Test
    public void testAddPoint() {
        //对于junit中的test方法建议按照jml逐条编写,下面给出addPoint方法的例子
        List<Integer> oldNodes = path.getNodes();
        Path oldPath = new Path(oldNodes.stream().mapToInt(i -> i).toArray());
        /*
         requires (\exists int i; 0 <= i && i < nodes.length - 1; nodes[i] == u && nodes[i + 1] == v) && isValid();
         */
        boolean fl = false;
        for (int i = 0; i < oldNodes.size() - 1; i++) {
            if (oldNodes.get(i) == u && oldNodes.get(i + 1) == v) {
                fl = true;
                break;
            }
        }
        if (fl && oldPath.isValid()) {
            /*
            public normal_behavior
             */
            boolean exception = false;
            try {
                path.addPoint(x, u, v);
            } catch (Exception e) {
                exception = true;
            }
            assertEquals(false, exception);
            /*
             ensures (\forall int i; 0 <= i && i < \old(nodes).length - 1; (\old(nodes[i]) == u && \old(nodes[i + 1]) == v) ==>
                (\forall int j; 0 <= j && j <= i; \old(nodes[j]) == nodes[j]) &&
                (\forall int j; i < j && j < \old(nodes.length); \old(nodes[j]) == nodes[j + 1]) &&
                (nodes[i + 1] == x);
             */
            List<Integer> nodes = path.getNodes();
            for (int i = 0; i < oldNodes.size() - 1; i++) {
                if (oldNodes.get(i) == u && oldNodes.get(i + 1) == v) {
                    for (int j = 0; j <= i; j++) {
                        assertEquals(oldNodes.get(j), nodes.get(j));//第二个forall
                    }
                    for (int j = i + 1; j < oldNodes.size(); j++) {
                        assertEquals(oldNodes.get(j), nodes.get(j + 1));//第三个forall
                    }
                    assertEquals((int) nodes.get(i + 1), x);//nodes[i + 1] == x
                }
            }
            /*
            ensures nodes.length == \old(nodes).length + 1
             */
            assertEquals(nodes.size(), oldNodes.size() + 1);
        }
        /*
        requires !isValid();
        */
        if (!oldPath.isValid()) {
            /*
            signals_only PathIsNotValid
             */
            assertThrows(PathIsNotValid.class, () -> path.addPoint(x, u, v));
            /*
            assignable \nothing;
             */
            assertEquals(oldNodes, path.getNodes());
        }
        /*
        requires (\forall int i; 0 <= i && i < nodes.length - 1; nodes[i] != u || nodes[i + 1] != v) && isValid();
        */
        boolean forall = true;
        for (int i = 0; i < oldNodes.size() - 1; i++) {
            if (oldNodes.get(i) != u || oldNodes.get(i + 1) != v) {
            
            } else {
                forall = false;
                break;
            }
        }
        if (oldPath.isValid() && forall) {
            /*
            signals_only EdgeNotFoundException
             */
            assertThrows(EdgeNotFoundException.class, () -> path.addPoint(x, u, v));
             /*
            assignable \nothing;
             */
            assertEquals(oldNodes, path.getNodes());
        }
    }
    
    @Test
    public void testExtractLoopPath() {
        Path pathWithLoop = generateLoopValidRoad();
        Path extractedLoop = pathWithLoop.extractLoopPath();
        assertNotNull("Extracted loop should not be null for a path with a loop", extractedLoop);
        List<Integer> loopNodes = extractedLoop.getNodes();
        int firstNode = loopNodes.get(0);
        int lastNode = loopNodes.get(loopNodes.size() - 1);
        assertTrue("First and last node of extracted loop should be the same", firstNode == lastNode);
        List<Integer> originalNodes = pathWithLoop.getNodes();
        for (int node : loopNodes) {
            assertTrue("Node " + node + " should be in the original path", originalNodes.contains(node));
        }
        //TODO
    }
    
    //随机生成一条无环有效路径
    public static Path generateNoLoopValidRoad() {
        List<Integer> list = generateList();
        return new Path(list.stream().mapToInt(i -> i).toArray());
    }
    
    //随机生成一条有环有效路径
    public static Path generateLoopValidRoad() {
        List<Integer> list = generateList();
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        list.add(random.nextInt(list.size() + 1), list.get(random.nextInt(list.size())));
        return new Path(list.stream().mapToInt(i -> i).toArray());
    }
    
    //随机生成一条无效路径
    public static Path generateInvalidRoad() {
        List<Integer> list = generateList();
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        int addNumMax = list.size();//最多可添加多少个点，参数可根据需要调整
        int addNum = random.nextInt(addNumMax - 1) + 2;
        for (int i = 1; i <= addNum; i++) {
            list.add(random.nextInt(list.size() + 1), list.get(random.nextInt(list.size())));
        }
        return new Path(list.stream().mapToInt(i -> i).toArray());
    }
    
    //随机生成一个元素不重复的List<Integer>
    public static List<Integer> generateList() {
        //可以根据你的需要更改参数设置
        int MinRoadLength = 10;
        int MaxRoadLength = 100;
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        int size = random.nextInt(MaxRoadLength - MinRoadLength + 1) + MinRoadLength;
        HashSet<Integer> hashset = new HashSet<>();
        int maxNode = size * 10;//点的最大编号是多少，可根据需求调参
        while (hashset.size() != size) {
            hashset.add(random.nextInt(maxNode));
        }
        List<Integer> list = new ArrayList<>(hashset);
        return list;
    }
}