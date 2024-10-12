
- [总体架构分析](#总体架构分析)
  - [架构图示意](#架构图示意)
  - [线程与共享](#线程与共享)
  - [数据竞争](#数据竞争)
  - [hw7双轿厢线程结构](#hw7双轿厢线程结构)
- [电梯捎带算法](#电梯捎带算法)
  - [Ele与CPU算法](#ele与cpu算法)
  - [方向优先](#方向优先)
  - [换成层算法不能撞](#换成层算法不能撞)
- [电梯分配hw6](#电梯分配hw6)
  - [分配思路](#分配思路)
  - [四组电梯](#四组电梯)
- [电梯Normalreset](#电梯normalreset)
  - [Normalreset流动](#normalreset流动)
  - [异常乘客处理](#异常乘客处理)
- [hw7的思路](#hw7的思路)
  - [如何启动双桥厢](#如何启动双桥厢)
  - [hw7如何分配电梯](#hw7如何分配电梯)
- [共享信息\&\&锁](#共享信息锁)
  - [请求池信息](#请求池信息)
  - [信息保护](#信息保护)
  - [唤醒和阻塞](#唤醒和阻塞)
- [其它](#其它)
  - [易变与不变](#易变与不变)
  - [debug算法](#debug算法)
- [新的体会](#新的体会)

# 总体架构分析
## 架构图示意
![img](https://img-community.csdnimg.cn/images/b235906bdecf434bbeed565fd72ddf71.png "#left")

**All类解析**
- InputThread：负责解析输入，并传输请求至请求池（一般是总请求池）
- waitQueue 总请求池
- scheduleThread 从总请求池拿请求，按照一定的算法分发给各个电梯请求池。
- ReQueue 请求池接口
- RequestQueue 实现ReQueue，电梯请求池，有6个，id为1到6
- DoubleReQueue 实现ReQueue，有0到6个，和双桥厢电梯一一对应
- ElevatorThred 电梯线程，有6个，id为1到6，和电梯请求池一一对应。
- DoublEleThread 双桥厢电梯线程，不可和id相同的ElevatorThred共存，和DoubleReQueue二一对应
- EleCpu 为ElevatorThred做决策
- EleRequestCpu 为ElevatorThred的Reset决策
- DoubleCpu 为DoublEleThread做决策
- Main 主线程
- GetTime 静态类，输出时间
  
## 线程与共享
这三次作业，本人使用了和exp3相同的架构。即在主线程之外，创建三类线程（hw7增加至4类）和两类请求池（hw7增加至3类和一接口）。Input线程解析输入并把获得的请求传送给waitQueue总请求池。调度器schedule从总请求池获得请求并分给对应的电梯请求池。电梯线程从自己的请求池获取请求。
*下图不包含Reset请求流动方向*
![img](https://img-community.csdnimg.cn/images/8c0c737513b24a9aad23471c475e4fad.png "#left")

**所有的共享对象都在请求池！**
## 数据竞争
schedule和Input线程之间存在数据竞争
schedule和elevator线程之间存在数据竞争
**两个双桥厢线程之间存在严重竞争！**
## hw7双轿厢线程结构
hw7新增了双桥厢电梯，双桥厢电梯需要各自用线程实现。两个电梯线程和一个调度器都要对DoubleReQueue里的数据进行读写。
![img](https://img-community.csdnimg.cn/images/480c03d3f73941a3aad8a1cc43df5a5b.png "#left")

# 电梯捎带算法
本人的电梯算法，本人也不知道叫什么名字。电梯每到一层，都要向自己的CPU（决策者）发出请求，获得下一步的指令……
## Ele与CPU算法
首先，电梯线程会调用决策者的check函数，决策者会获得电梯目前的层数，电梯里的人的信息。
```
        outWantIns.clear();
        inWantOuts.clear();
        now = elevator.getNowFloor();
        seeRequests = requestQueue.getRequests();
        personIns = elevator.getInElevators();
```
最后电梯会返回outWantIns下电梯人名单，inWantOuts上电梯人名单，dir方向，isStartORstop启动还是暂停,ifOpen是否开门。
```
    public int getDir() {
        return dir;
    }

    public boolean isIfOpen() {
        return ifOpen;
    }

    public boolean isStartORstop() {
        return startORstop;
    }

    public ArrayList<PersonRequest> getInWantOuts() {
        return inWantOuts;
    }

    public ArrayList<PersonRequest> getOutWantIns() {
        return outWantIns;
    }
```
具体实现方法如下
## 方向优先
先下人，凡是终点在这一层的一律下。 
如果下光了，未处理的请求中最早的的from为电梯运动的方向，如果from就是这一层，那么to为方向。 
如果没下光，那么电梯里来的最早的人的to为电梯方向。 
然后是上人，如果乘客的from是本层**并且（to - from） * dir > 0**，才能上电梯。
## 换成层算法不能撞
换成层不能同时存在两个电梯，本人在两个电梯共同读写的请求池DoubleReQueue放置了一个锁，如果是0代表换成层没有电梯，1，和-1分别代表，B或A在换乘层。
```
    public synchronized void setTranID(int isUpD) {
        this.tranDownUp = isUpD;
        notifyAll();
    }

    public synchronized int getTranDownUp() {
        return tranDownUp;
    }
```
为了节省时间，假如电梯运行一层时间需要400ms，那么获得锁的电梯不会在400ms后释放锁，而是sleep50ms就释放，然后继续sleep。
```
    public void moveOneFloor() {
        int nextFr = nowFloor + eleDir;
        if (nextFr == exchange) {
            while (elevatorQueue.getTranDownUp() != 0) {
                trySleep((long) speedTime / 2);
            }
            elevatorQueue.setTranID(isAorB);
            trySleep(speedTime);
            //……
            //前往换成层
        } else if (nowFloor == exchange) {
            trySleep((long) speedTime / 10);
            elevatorQueue.setTranID(0);
            trySleep((long) speedTime / 10 * 9);
            //……
            //离开换成层
        } else {
            //……
            //正常移动
        }
    }
```
# 电梯分配hw6
## 分配思路
第六七次作业需要程序员设计分配电梯，本人第六次作业，设计了分组电梯分配方法。
**目的是省电0.4和减少乘客等待时间0.3。**
剩下0.3权重的运行时间，经测试和轮流分配在均值上提升不大。
## 四组电梯
```
#define 好电梯 {
    电梯没有在重置
    && 电梯里人不太多
    && 电梯未处理的请求不太多
}
    int poor = t - f;
    Double average = (f + t) / 2.0;
```
- 1号：如果是好电梯，并且abs(poor) > 7则分给1
- 34号：average < 5.5，在低层运动
- 56号：average > 7.5 在高层运动
- 2号：尝试分担1的压力压力，在中层运动并且移动范围不大。
  **说明**
- 1号尽可能捎带中层2的份额
- 34和56内部有算法，来决定把电梯分配给谁
- 如果没有好电梯，则某一组会返回-1，有新的算法分配电梯 

**经过hw6强测，性能很高**因为3456的局限运动会节省电量，并且相对也更容易快速接到乘客。
**局限性**：
- 参数不好把握，经过大量本地测试，才保证在均匀请求下，每个电梯接到的乘客在合理范围。
- 互测容易被攻击，比如“置五缺一”，比如全是“from-1-to-11” 

# 电梯Normalreset
## Normalreset流动
***蓝色为NormalResetRequest，黑色为PersonRequest***
![img](https://img-community.csdnimg.cn/images/9724768fc2a44a99974db61b73a5e58a.png "#left") 

因为schedule处理请求是一个一个进行的，为了避免Reset不及时，ResetRequest会直接被放到对应的电梯请求池。
## 异常乘客处理
所有乘客都要下电梯，如果乘客的to正好是本层，那么万事大吉，假如不是,那么电梯会把他们存入specialNeeds，等到重置完毕立刻receive并且接上来。
**但是Reset后电梯容量可能下降，出现所有specialNeeds全接上来超载的情况！！！**
这时候多余的人会放入对应id的请求池，From改为次楼层。
**本人尽量不向上（总请求池waitQueue）传递请求**
因为不会写……
# hw7的思路
ElevatorThread的关闭和DoublEleThread的启动二者之间的衔接、ReQueue接口下两类请求池之间的转换、电梯的分配是三个难点。
## 如何启动双桥厢
Input收到DCreset后会立即向对应的RequestQueue发送信号，创造新的DoubleReQueue并保存相关信息，并立即封存请求池信息为只读。 
随后同时向schedule和Elevator发送信号，让schedule立即更新请求池，让Elevator创建新的DoublEleThread并且启动！启动之后，结束自己的run();
## hw7如何分配电梯
本人新建了一个大小为12的int数组，初始化为0，遍历六个电梯请求池，如果电梯是好电梯（上面有定义），那么按照电梯的情况，选取1到2个空间赋值为电梯id。
然后调用Java随机数从12个数中随机选一个作为目标电梯。
**可能会出现所有电梯都不符合情况的时候**
这个时候schedule要向总请求池更新DCReset信息，并sleep一段时间等待Ele的新信息，否则会死循环。
# 共享信息&&锁
## 请求池信息
**本人所有共享信息都放在了请求池，包括电梯的实时信息**当然请求池里的电梯信息不会是最新的。
***部分共享信息展示***
```
// Waitqueue
    private ArrayList<Request> waitRequests；
//  RequestQueue
    private final ArrayList<PersonRequest> requests;
    private boolean isEnd;
    private ArrayList<NormalResetRequest> setRequests;
    private boolean canChange;
    private int dir;
    private int floor;
    private int balance;
    private int speedTime;
// DoubleReQueue
    private final int exchange;
    private int tranDownUp;
    private ArrayList<PersonRequest> upRequests;
    private ArrayList<PersonRequest> downRequests;
    private final int maxNum;
    private int numdA = 0;
    private int numuB = 0;
```
请求池和电梯时态相关信息在每次电梯移动前更新
## 信息保护
**请求池锁全部为synchronized**
还有输出保护
```
    public synchronized ArrayList<PersonRequest> getRequests() {
        if (requests.isEmpty()) {
            return null;
        }
        ArrayList<PersonRequest> copyRe = new ArrayList<>();
        for (PersonRequest request:requests) {
            copyRe.add(request);
        }
        notifyAll();
        return copyRe;
    }
```
## 唤醒和阻塞
**一律为wait() ———— notifyAll();**
并且都在请求池进行，没什么好说的，值得注意的是，hw双桥厢请求池有两个电梯线程，也更要复杂
```
//DoubleReQueue
    public synchronized boolean getUpbGlag() {
        if (upRequests.isEmpty() && !isEnd) {
            //tryWait
        }
        if (upRequests.isEmpty() && isEnd && (numdA != 0 || downRequests.size() != 0)) {
            //tryWait
        }
        //return something;
    }
```
# 其它
## 易变与不变
- **易变**：电梯分配方式，电梯的形式，电梯的参数，访问共享信息线程的数量。
- **不变**：电梯的微观行动方法，Input——waitQueue——schedule——ReQueue——EleThread——ELeCPU结构，电梯自己的捎带算法。 

## debug算法
- 自己编特殊样例然后print（）
- 用大佬的评测机 

# 新的体会
第一次接触多线程，hw5非常怵头，尤其是同样的输入可能会有不同的结果很吓人，差点没交上中测。
但是只要搞明白了
**生产者——消费者模式**、**多个线程竞争的对象**还有**wait——notify原理**这三点，本人感觉比第一单元要简单。
