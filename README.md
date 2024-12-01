# buaa-oo-2024
北航面向对象2024春季课设代码
- hw1到hw3:第一单元：递归下降解析字符串
- hw5到hw7:第二单元，多线程电梯系统（难点）
- hw9到hw11:第三单元
- hw13到hw15:第四单元

# 第一单元总述
第一单元三次作业都是对一行表达式进行去括号整理 。本人借鉴了课程组在公众号发布的代码，使用递归下降过程先将表达式解析为因子，再将因子链表解析为语法树，最后将语法树解析为由单项式组成的多项式。
[源代码地址](https://github.com/advanceHiyan/buaa-oo-2024)
## 第一次作业总架构
### Main.class的PlantUML parser示意图

![](https://i-blog.csdnimg.cn/direct/e2e5d769f0a74d37b692842800cb1929.png)


### 预处理prePro
预处理可以是解决表达式处理尤其是符号处理的最佳方法。时间和空间复杂度低、操作简单，所以如果有些方法可以写入预处理类，一定要优先在预处理运行，写入递归下降不仅复杂度高，而且很难debug，往往事倍功半。
预处理首先将输入的表达式字符串所有的空字符去除（StringBuilder）然后对特殊的符号进行处理，比如replace("+-","-"),replace("(+","(0+")等等。值得注意的是，由于括号嵌套和符号位，可能会出现“---”和“+++”的情况，也要在预处理里面解决。
### 表达式解析
把预处理后的字符串加入解析，本人几乎沿用了公众号的代码架构。只是为了处理^因子，以及（）^类型的数据，本人引入了核心因子: CoreFactor,即Expr -> Term -> CoreFactor -> Factor四级架构。

```java
    public CoreFactor parserCore() {
        ArrayList<Factor> factors = new ArrayList<>();
        factors.add(parserFactor());
        while (lexer.notEnd() && lexer.now().getType() == Token.Type.EXP) {
            lexer.move();
            factors.add(parserFactor());
        }
        return new CoreFactor(factors);
    }

```
如果第四级提取到了（，则进行递归。叶节点的类型有Num,Letter(字母)，第二次作业又引入了ExPexP来表示exp（）^()因子。经过解析，一行字符串成功转化成了一个四级树结构。
#### 四级树递归结构
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/d149bfc886784cd2911e98df83b65c5a.png)

### 从树结构到单项式结构
单项式Mono：系数C,指数E,结构 C*x^E
多项式Poly:由Mono组成的链表
注意树的每一级都有toPoly()结构。底层Factor都是直接生成Mono随后，然后再CoreFactor的toPoly里形成Poly,并在Term和Expr级进行组合。
#### 符号处理
本人在递归构建Poly时，为了处理符号，在Mono中设立了符号位，并在Expr一级把每一个Term所有mono的符号和ops的符号进行check
#### Mono的合并
如果不进行Mono合并的话，(（1+x）^5)^5就能TLE，所以本人在Expr一级实现toPoly并且检测符号位以后，会根据每个Mono的toString（）进行合并，如此可以极大减小Poly的数据量，从而避免再Expr返回给Factor——toPoly一个超级亢杂的链表。
### 处理输出FinPro
对最终返回的一个多项式转化成字符串链表，对系数进行合并相加，对符号进行处理，并且对系数为0，指数为1的情况进行if判断，最终返回要输出的字符串。
## 第二次作业处理
为了实现作业的可迭代性，本人尽可能对底层的方法进行封装，如果有需要改动的，也尽可能另写一个方法，而不是随便对原来的代码进行修改。
### 函数的预处理
#### 函数类方法
PrePro还是比较简单的，虽然可能会增加时间复杂度 。本人新建了CustFun函数类，并在Main留下了Hashmap链表。在函数类里，本人记录函数名称，提取了形参个数，并将等号后面的函数表达式里面的形参替换为&，|，%。并构建替换方法，输入的参数为实参，返回的是将表达式的&%|替换为形参后的字符串。
#### 替换注意
exp也含有x，为了防止替换破坏exp，本人先把exp替换为j，并在xyz替换后，把j换成exp。在替换时，替换形参时，本人额外嵌套了（）并在最后返回含有实参的函数表达式时又添加了（）。
#### 预处理递归
在预处理中函数替换方法，方法的形参为一行字符串返回值为字符串，本人遍历字符串，一旦检测到fgh就提取实参 ，如果没有fgh则返回字符串本身，否则调用函数替换方法把分（）替换，最后返回值为，再次调用本方法（参数为本次替换后的字符串）的返回值。
虽然略微增加复杂度，但是可以保证替换的准确性。
### exp结构处理
第一次作业Mono为C*x^E
本次则是C*x^E*exp(Poly)
对exp（）^n,则直接更改exp（）内的String，更改后再处理。
所以Mono中要存系数C,指数E，符号位sign，还有exp（）内的exPoly。
#### 示意图

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/bfbcb0bea1464b1e9929d27b5502fdf1.png)


#### 递归区别
原本只是自下而上把所有Mono存入上层的Poly，而现在，每个Mono中可能有新的Poly，好像一棵树上的一个树枝。
除此之外在合并时，含有exp的Mono和不含有的要独立判断，分开合并。
## 第三次作业
### 输入函数非递归调用
无需更改
### 求导处理
参考第二次实验求导方法，根据链式法则和乘法法则进行自上而下的求导。
#### 尤其注意
因为Mono中可能存在Poly，而Mono存取的只是Poly的地址（指针），所以克隆Mono时如果只是浅克隆，会导致两个Mono调用同一个expoly，产生Wrong Answer。
因此一定要在Mono和Poly中写**递归克隆**方法。
## bug分析与程序优化
### 复杂度分析
预处理复杂度
![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/442a5275b27e4b46b17d825dbf2e7ad9.png)

递归复杂度

![在这里插入图片描述](https://i-blog.csdnimg.cn/direct/5f88c266273f48d9a78e111266be39cd.png)


架构复杂度主要集中在这两处。如果要优化，只能更改底层逻辑。
### 底层封装与重构
底层的封装是最重要的，为了代码的可延展性，底层一般只提供普适性的方法。如果有新的需求，那么就增加新的方法，而不是更改底层代码。假如一定要极大改动，那么说明底层结构不具备可迭代性，最好是重构。
### 底层类不返回new此类对象
本人Mono类里面的方法没有方法返回Mono对象，别的底层类亦然。
### 克隆方法
因为底层不返回与自己类型相同的对象，因此非常有必要写一个clone（）方法。尤其是类似于Mono中存取Poly的指针这样的结构，一定要写好克隆方法。
## 体会与未来展望
OO真的是一门很难但是很有意思的课，看着自己的代码 一点一点形成一个工程真的很有成就感。同时本人也建议适当降低互测门槛，让更多的同学进入互测，并合理分配房间，让所有人都能体验到hack与被hack。

# 第二单元综述
## 总体架构分析
[源代码地址](https://github.com/advanceHiyan/buaa-oo-2024)
### 架构图示意
![架构图](https://i-blog.csdnimg.cn/blog_migrate/a16fdafd59d68a37ea5add2cb6e99bf1.png)


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
  
### 线程与共享
这三次作业，本人使用了和exp3相同的架构。即在主线程之外，创建三类线程（hw7增加至4类）和两类请求池（hw7增加至3类和一接口）。Input线程解析输入并把获得的请求传送给waitQueue总请求池。调度器schedule从总请求池获得请求并分给对应的电梯请求池。电梯线程从自己的请求池获取请求。
*下图不包含Reset请求流动方向*
![所有的共享对象都在请求池](https://i-blog.csdnimg.cn/blog_migrate/c1a41c220175e848a30eed37f71ddece.png)

**所有的共享对象都在请求池！**
### 数据竞争
schedule和Input线程之间存在数据竞争
schedule和elevator线程之间存在数据竞争
**两个双桥厢线程之间存在严重竞争！**
### hw7双轿厢线程结构
hw7新增了双桥厢电梯，双桥厢电梯需要各自用线程实现。两个电梯线程和一个调度器都要对DoubleReQueue里的数据进行读写。
![在这里插入图片描述](https://i-blog.csdnimg.cn/blog_migrate/5174fa95f34df5de11ac6dbcd48070c2.png)

## 电梯捎带算法
本人的电梯算法，本人也不知道叫什么名字。电梯每到一层，都要向自己的CPU（决策者）发出请求，获得下一步的指令……
### Ele与CPU算法
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
## #方向优先
先下人，凡是终点在这一层的一律下。 
如果下光了，未处理的请求中最早的的from为电梯运动的方向，如果from就是这一层，那么to为方向。 
如果没下光，那么电梯里来的最早的人的to为电梯方向。 
然后是上人，如果乘客的from是本层**并且（to - from） * dir > 0**，才能上电梯。
### 换成层算法不能撞
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
## 电梯分配hw6
### 分配思路
第六七次作业需要程序员设计分配电梯，本人第六次作业，设计了分组电梯分配方法。
**目的是省电0.4和减少乘客等待时间0.3。**
剩下0.3权重的运行时间，经测试和轮流分配在均值上提升不大。
### 四组电梯
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

## 电梯Normalreset
### Normalreset流动
***蓝色为NormalResetRequest，黑色为PersonRequest***
![在这里插入图片描述](https://i-blog.csdnimg.cn/blog_migrate/0ee8d41e0aea7d819010522e9fb19f44.png)
 

因为schedule处理请求是一个一个进行的，为了避免Reset不及时，ResetRequest会直接被放到对应的电梯请求池。
### 异常乘客处理
所有乘客都要下电梯，如果乘客的to正好是本层，那么万事大吉，假如不是,那么电梯会把他们存入specialNeeds，等到重置完毕立刻receive并且接上来。
**但是Reset后电梯容量可能下降，出现所有specialNeeds全接上来超载的情况！！！**
这时候多余的人会放入对应id的请求池，From改为次楼层。
**本人尽量不向上（总请求池waitQueue）传递请求**
因为不会写……
## hw7的思路
ElevatorThread的关闭和DoublEleThread的启动二者之间的衔接、ReQueue接口下两类请求池之间的转换、电梯的分配是三个难点。
### 如何启动双桥厢
Input收到DCreset后会立即向对应的RequestQueue发送信号，创造新的DoubleReQueue并保存相关信息，并立即封存请求池信息为只读。 
随后同时向schedule和Elevator发送信号，让schedule立即更新请求池，让Elevator创建新的DoublEleThread并且启动！启动之后，结束自己的run();
### hw7如何分配电梯
本人新建了一个大小为12的int数组，初始化为0，遍历六个电梯请求池，如果电梯是好电梯（上面有定义），那么按照电梯的情况，选取1到2个空间赋值为电梯id。
然后调用Java随机数从12个数中随机选一个作为目标电梯。
**可能会出现所有电梯都不符合情况的时候**
这个时候schedule要向总请求池更新DCReset信息，并sleep一段时间等待Ele的新信息，否则会死循环。
## 共享信息&&锁
### 请求池信息
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
### 信息保护
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
### 唤醒和阻塞
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
## 其它
### 易变与不变
- **易变**：电梯分配方式，电梯的形式，电梯的参数，访问共享信息线程的数量。
- **不变**：电梯的微观行动方法，Input——waitQueue——schedule——ReQueue——EleThread——ELeCPU结构，电梯自己的捎带算法。 

### debug算法
- 自己编特殊样例然后print（）
- 用大佬的评测机 

## 新的体会
第一次接触多线程，hw5非常怵头，尤其是同样的输入可能会有不同的结果很吓人，差点没交上中测。
但是只要搞明白了
**生产者——消费者模式**、**多个线程竞争的对象**还有**wait——notify原理**这三点，本人感觉比第一单元要简单。

