# Share-区块链
[MBA wiki - BlockChain](http://wiki.mbalib.com/wiki/%E5%8C%BA%E5%9D%97%E9%93%BE)
```
从区块链的形成过程看，区块链技术具有以下特征。

　　一是去中心化。区块链技术不依赖额外的第三方管理机构或硬件设施，没有中心管制，除了自成一体的区块链本身，通过分布式核算和存储，各个节点实现了信息自我验证、传递和管理。去中心化是区块链最突出最本质的特征。

　　二是开放性。区块链技术基础是开源的，除了交易各方的私有信息被加密外，区块链的数据对所有人开放，任何人都可以通过公开的接口查询区块链数据和开发相关应用，因此整个系统信息高度透明。

　　三是独立性。基于协商一致的规范和协议(类似比特币采用的哈希算法等各种数学算法)，整个区块链系统不依赖其他第三方，所有节点能够在系统内自动安全地验证、交换数据，不需要任何人为的干预。

　　四是安全性。只要不能掌控全部数据节点的51%，就无法肆意操控修改网络数据，这使区块链本身变得相对安全，避免了主观人为的数据变更。

　　五是匿名性。除非有法律规范要求，单从技术上来讲，各区块节点的身份信息不需要公开或验证，信息传递可以匿名进行。
```


## 共识算法
参考：[区块链共识算法 PBFT（拜占庭容错）、PAXOS、RAFT简述](http://blog.csdn.net/jerry81333/article/details/74303194)
```
比特币使用的是POS（Proof of Work，工作量证明），
以太币使用的是POS（Proof of Stake，股权证明）而今POS的变体DPOS（Delegated Proof of Stake，股份授权证明）进一步削减算力的浪费，同时也加强了区块链的安全性。
传统的一致性算法成为首选，PBFT（拜占庭容错）、PAXOS、RAFT。
```
## P2P网络和Nat打洞

## 密码学
数字签名：
	sha256
对称加密：
	AES
非对称加密：
	ECC椭圆曲线加密
	RSA
数字证书：
	CA
认证码：
	HMAC（基于hash的消息认证码）
	
## 分布式文件系统 IPFS

## 区块存储结构 Merkle和DAG图

## 区块hash的计算

## 分布式一致性 CAP理论

[生成钱包地址](https://pic1.zhimg.com/80/v2-75d938393614bf24b2b0f55ed553d7ba_hd.jpg)

缩略术语：
```
缩略语 原始术语
PoW 工作量证明（Proof of Work）
PoS 权益证明（Proof of Stake）
DPoS 股份授权证明（Delegate Proof of Stake）
PBFT 实用拜占庭容错（Practical Byzantine Fault Tolerance）
P2P 点对点（Peer to Peer）
DAPP 分布式应用（Decentralized Application）
KYC 客户识别（Know Your Customer）
RSA RSA加密算法（RSA Algorithm）
ECC 椭圆加密算法（Elliptic Curve Cryptography）
BaaS 区块链即服务（Blockchain as a Service）
```
```
常用的共识机制主要有PoW、PoS、DPoS、Paxos、PBFT等。另
外，基于区块链技术的不同应用场景，以及各种共识机制的特性，本白皮
书建议按照以下维度来评价各种共识机制的技术水平：
合规监管：是否支持超级权限节点对全网节点、数据进行监管。
性能效率：交易达成共识被确认的效率。
资源消耗：共识过程中耗费的CPU、网络输入输出、存储等计算机
资源。
容错性：防攻击、防欺诈的能力。

1、PoW：依赖机器进行数学运算来获取记账权，资源消耗相比其他
共识机制高、可监管性弱，同时每次达成共识需要全网共同参与运算，性
能效率比较低，容错性方面允许全网50%节点出错。
2、PoS：主要思想是节点记账权的获得难度与节点持有的权益成反
比，相对于PoW，一定程度减少了数学运算带来的资源消耗，性能也得到
了相应的提升，但依然是基于哈希运算竞争获取记账权的方式，可监管性
弱。该共识机制容错性和PoW相同。
3、DPoS：与PoS的主要区别在于节点选举若干代理人，由代理人验
证和记账。其合规监管、性能、资源消耗和容错性与PoS相似。
4、Paxos：是一种基于选举领导者的共识机制，领导者节点拥有绝
对权限，并允许强监管节点参与，性能高，资源消耗低。所有节点一般有
线下准入机制，但选举过程中不允许有作恶节点，不具备容错性。
5、PBFT：与Paxos类似，也是一种采用许可投票、少数服从多数来
选举领导者进行记账的共识机制，但该共识机制允许拜占庭容错。该共识
机制允许强监管节点参与，具备权限分级能力，性能更高，耗能更低，该
算法每轮记账都会由全网节点共同选举领导者，允许33%的节点作恶，容
错性为33%.
```
```
表4-1 典型散列算法的特点
| 加密算法 | 安全性 | 运算速度 | 输出大小（位）|
| --------   | -----:  | -----:  | :----:  |
| MD5 | 低 | 快 | 128 |
| SHA1 | 中 | 中 | 160 |
|SHA256 |高 |比SHA1略低 |256|
|SM3 |高 |比SHA1略低 |256|


在近代公钥密码系统的研究中, 其安全性都是基于难解的可计算问题
的，常用的非对称加密算法特点及其比较如表4-2和表4-3所示。
表4-2 非对称加密算法的特点
保密级别 RSA密钥长度 ECC/SM2密钥长度
80 1024 160
112 2048 224
表4-3 RSA、ECC/SM2总体比较
加密算法 成熟度 安全性 运算速度 资源消耗
RSA 高 低 慢 高
ECC 高 高 中 中
SM2 高 高 中 中

4.3.5 隐私保护
目前区块链上传输和存储的数据都是公开可见的，仅通过“伪匿名”
的方式对交易双方进行一定的隐私保护。对于某些涉及大量的商业机密和
利益的业务场景来说，数据的暴露不符合业务规则和监管要求。目前，业
界普遍认为零知识证明、环签名和同态加密等技术比较有希望解决区块链
的隐私问题。

```
参考：
[比特币钱包地址的生成](https://zhuanlan.zhihu.com/p/28036845)\
[比特币找零地址](http://www.8btc.com/joybtc_5)\
[浅析比特币的找零机制](http://www.8btc.com/bitcoin-change-addresses-explanation)\
![gif](http://img.blog.csdn.net/20161210180600786?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvd281NDEwNzU3NTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)\
[区块的存储结构-Merkle数](https://learnblockchain.cn/2017/11/09/merkle/)\
![png](https://diycode.b0.upaiyun.com/photo/2017/f59da4e17b1224d6a3fd46309ff6edd7.jpeg)
[ECDSA数字签名算法](https://segmentfault.com/a/1190000012288285)\

```
以太坊 oracle 网络和加密
数字货币、比特信（p2p加密通信https://github.com/Bitmessage/PyBitmessage）、征信
超级账本、闪电网络
```

# 公有链平台
## 以太坊
参考Solidity在线游戏CryptoZombies(https://cryptozombies.io/zh/)
## steem
[steem]https://smt.steem.io/
