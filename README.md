# r6-snipe 

育碧莫名其妙在04/12的更新后给改变订单状态数加了限制，现在购买和售卖的订单都有一个独立的限制值，当你一天之内操作次数超过这个限额，你就无法改变订单的状态，只能等24小时前离你最近的操作订单时间后，这个限额还很低，这样子我一开始对面改我们也改的逻辑就不行了，太容易被打限额了，我最近忙，没时间改，你们自己想方案的，我是想可以每件物品只狙击一次

<img src="https://github.com/user-attachments/assets/59bb6a40-72f9-405b-bc3a-db93981f2f56" alt="Image" style="width:800px;">


好久没更新了，因为市场没啥可买的，都一段时间没看了，毕竟我已经截取了四五百万点数，现在游戏内和市场大部分物品买完，还剩下一百多万没处用。我刚开源那几天截取很简单，有人反馈一周就搞了二十多万，然后对面商家被截取狠了，开始小心起来，我一天可能就一两单了，上两周商家优化了他那边代码，开始搞超快单，就是他清空到最后一件到转移完成，时间在1.1s-1.3s之间，我代码最快也只能检测到下单1.5s后，频率就大幅度下降了，上次成功还是20号的35000，再上次还是17号的24000.我是不知道也不想优化了，毕竟实在没有点数需求了，集思广益，有什么优化方案希望分享一下
![49802669c8b249c137349e6efcfe8e8](https://github.com/user-attachments/assets/316d1121-213f-46ab-8076-a901d1b5f340)


有没有成功过的回馈一下，这几天每天我都还是有三四件进账的，开源了没一点回馈和打空气一样，我看日志还是有人成功过的

![515317ff03c01a7d0066768bde8440c](https://github.com/user-attachments/assets/8aae80aa-6ddf-40c5-abe3-3c9d80991119)
<img src="https://github.com/user-attachments/assets/bc02d019-2ab4-4d3b-9f7a-3e638279d911" alt="Image" style="width:300px;">


大狙击时代结束了，市场狙击所针对的点数主要是来自xbox退款点数，Y9S3前市场转移条件很难，而自从Y9S3调整市场物品价格的上下限后，转移变得非常容易，甚至达到了每天转移两三百万点数，在持续转移两个多月超过几千万点数后，2024/10/24日育碧终于受不了封禁这种手段了，我是已经毕业了，狙击了差不多三四百万点数，距离我开源这个项目刚好十天，希望你们已经喝到汤了，之后运行这个项目的收益率就没这么大了，以前是每天至少三四件的

![67f5a0629b91a66de34b8b8f04d31bb](https://github.com/user-attachments/assets/8138dd35-24d6-4d4c-9d54-8d556e06dc38)

因为我删除了部分敏感代码信息，所以直接运行是无法运行的，需要自己排查

启动需要手动添加育碧的二步cookie数据，这个自己控制台随便抓一个API手动获取吧，数据放到data.json里面
![cbe5a6651f31894d243d1642b7bea52](https://github.com/user-attachments/assets/32bcf265-d89a-4e71-bfa7-ef1af81c06e7)

截胡最重要的就是时间，

一是获取数据，监控到异常数据，马上发送售卖订单请求，创建的订单时间，我最低只能优化到监控后创建订单时间为上次订单的1.5秒后，如果你能对我处理的代码逻辑进行优化，希望能分享下；

## 二是监控到异常数据后，什么时候下订单，这个是重中之重，有时候对面会等一两秒再交易，发现有人截胡就取消交易，有时候却会立即交易，我设置的时间只做推荐，我也时不时在修改，这个就看你自己选择了，选择合适的延迟时间，这里不做建议

# 本库只是分享一个思路，毕竟对面不是人机，截胡的人多了，对面也会改策略，所以我说等待时间自己修改，不然我打印这么多日志干什么，我举个例子：
今天的更新是因为多了一个商家的做单思路，他不是像之前那家使用软件1s内下几十个订单清空，而是一个个手动清空，到最后一个时，他和买家约定好同时下单，而不是之前那家买家先下售单。商家刷新到再买，那我们就得更新。
我的思路是监控到是一个个清时，当售单小于1时，我们等待1秒直接盲下售单，因为不知道具体会转移多少，我为了成功率直接下9999的售单，这样子你同时按又如何，我根本不等你，直接预测你要转移

# 然后现在可以设置点延迟了，晚上那个大商家都被搞的不敢直接弄了，我看了下日志，他那边一下售单，马上就冒出来三个比他低的售单，所以现在是延迟个一二三秒是比较好
最后，本人非科班随便写写的，如果你有更好的优化建议，希望能够进行分享
