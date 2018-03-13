# Share - Python篇
# Wind
## WSD数据获取
```python
# coding=utf-8
# version:python-2.7
# 具体指标使用wind-量化-代码生成器进行获取
import numpy as np
import pandas as pd
from WindPy import *
from sqlalchemy import create_engine

# 获取指定日期基金净值数据
db_connect_string = 'mysql://user:pass@10.18.0.2:3306/beta_psbc?charset=utf8'
engine = create_engine(db_connect_string)
w.start()

raw_data = w.wsd('000001.OF', "nav,NAV_adj", '2017-01-01', '2018-01-01', "")
with engine.connect() as conn:
    if not raw_data.ErrorCode:
        df = pd.DataFrame(index=raw_data.Times, columns=raw_data.Fields,
                          data=np.array(raw_data.Data).transpose())
        df.to_sql("fund_calculate", conn, if_exists="append", index=False)
```
## WSQ数据订阅
```python
from WindPy import *

w.start()

# 回调函数
def __lastWSQCallback(raw_data):
    """
    DemoWSQCallback 是WSQ订阅时提供的回调函数模板。该函数只有一个为w.WindData类型的参数indata。
    该函数是被C中线程调用的，因此此线程应该仅仅限于简单的数据处理，并且还应该主要线程之间互斥考虑。
    用户自定义回调函数，请一定要使用try...except
    """
    try:
        if not raw_data.ErrorCode:
            data = [k[0] for k in raw_data.Data]
            dic_data = dict(zip(raw_data.Fields, data))
            print (dic_data)

    except:
        print u'wsq数据获取异常'

# 返回data包含订阅产生的request_id
data = w.wsq("10001000.SH", "rt_time,rt_ask1,rt_bid1,rt_latest", func=__lastWSQCallback)

# 取消订阅
w.cancelRequest(data.RequestID)

# 取消所有订阅
w.cancelRequest(0)

```

# QT
## PyQT
### 使用pyuic5生成
```python
# 使用QtCreator创建界面，然后使用> pyuic5 -o _TradeWindow.py tradewindow.ui 生成py文件
# pyuic5.bat 源码为：
# "D:/Program Files/Anaconda3/python.exe" -m PyQt5.uic.pyuic %1 %2 %3 %4 %5 %6 %7 %8 %9
# 其中路径中包含空格，用引号包裹表示完整路径，后面指定传递过来的参数
# coding=utf-8
from PyQt5 import QtWidgets
from PyQt5.QtWidgets import QMainWindow
from PyQt5.QtWidgets import QMessageBox

class Ui_TradeWindow(QMainWindow):
    def setupUi(self, TradeWindow):
        TradeWindow.setObjectName("TradeWindow")
        TradeWindow.resize(800, 600)
        self.statusbar = QtWidgets.QStatusBar(TradeWindow)
        self.statusbar.setObjectName("statusbar")
        self.statusbar.showMessage('未连接')
        TradeWindow.setStatusBar(self.statusbar)
        # 指定关闭事件
        TradeWindow.closeEvent = self.myCloseEvent

    # 关闭事件
    def myCloseEvent(self, event):
        reply = QMessageBox.question(self, 'Message',
                                     "Are you sure to quit?", QMessageBox.Yes, QMessageBox.No)
        if reply == QMessageBox.Yes:
            print '取消订阅合约'
            event.accept()
        else:
            event.ignore()

if __name__ == '__main__':
    import sys
    from PyQt5.QtWidgets import QApplication, QMainWindow

    app = QApplication(sys.argv)
    # 主界面
    mainWindow = QMainWindow()
    mainUI = Ui_TradeWindow()
    mainUI.setupUi(mainWindow)
    mainWindow.show()
    sys.exit(app.exec_())

```
### 使用PyQt5.uic引入ui文件方式
```python
# coding=utf-8
import sys

from PyQt5 import uic
from PyQt5.QtWidgets import QMainWindow
from PyQt5.QtWidgets import QMessageBox


class MainWindow(QMainWindow):
    def __init__(self):
        QMainWindow.__init__(self)
        # 加载UI文件
        self.ui = uic.loadUi('mainwindow.ui')
        self.ui.closeEvent = self.closeEvent
        self.ui.pushButton.clicked.connect(self.btnClickEvent)
        # 调用函数需要其他参数使用lambda
        # self.ui.pushButton.clicked.connect(lambda: self.btnClickEvent(1))
        self.ui.show()

    def btnClickEvent(self, event):
        msg_box = QMessageBox()
        msg_box.setIcon(QMessageBox.Information)
        msg_box.setWindowTitle('Information')
        msg_box.setText(self.ui.lineEdit.text())
        msg_box.exec_()

    # 关闭事件
    def closeEvent(self, event):
        print("event")
        reply = QMessageBox.question(self, 'Message',
                                     "Are you sure to quit?", QMessageBox.Yes, QMessageBox.No)

        if reply == QMessageBox.Yes:
            event.accept()
        else:
            event.ignore()


if __name__ == "__main__":
    from PyQt5.QtWidgets import QApplication, QMainWindow

    app = QApplication(sys.argv)
    win = MainWindow()
    sys.exit(app.exec_())
```
## pyqtSignal
```python
# 发送信号
# coding=utf-8
from PyQt5.QtCore import *
class QtSignal(QThread):
    signal = pyqtSignal(int)
    # signal = pyqtSignal(type(class))
    
    def __init__(self, parent=None):
        super(QtSignal, self).__init__(parent)
        self.start()

    def run(self):
        while True:
            self.sendSignal()

    # 发送信号
    def sendSignal(self):
        self.signal.emit(1)
        QThread.msleep(500)
# 接受信号
from PyQt5.QtWidgets import QMainWindow
class Ui_TradeWindow(QMainWindow):
    def setupUi(self, TradeWindow):
        self.qtSignal = QtSignal()
        self.qtSignal.signal.connect(self.receiveSignal)
    # 接收信号
    def receiveSignal(self, event):
        print (event)

```
## pyqtGraph/matplotlib
### pyqtGraph
参考[pyqtGraph Document](http://www.pyqtgraph.org/documentation/introduction.html)
```python
# pyqtGraph自带example项目
import pyqtgraph.examples as example
example.run()
```
```python
import numpy as np
import pyqtgraph as pg

def pgPlot(x,y):
    pg.plot(x, y, title="Graph01", background='w')

if __name__ == '__main__':
    x = np.asarray(range(1, 100))
    y = x ** 2
    pgPlot(x,y)
    pg.QtGui.QGuiApplication.exec_()
```

### matplotlib
参考[matplotlib Document](https://matplotlib.org/gallery/index.html)
```python
# def plot_date(x, y, fmt='o', tz=None, xdate=True, ydate=False, hold=None,data=None, **kwargs):
import numpy as np
#import matplotlib
#matplotlib.use('Agg')

import matplotlib.pyplot as plt

# Fixing random state for reproducibility
np.random.seed(19680801)


fig, ax = plt.subplots()
ax.plot(np.random.rand(20), '-o', ms=20, lw=2, alpha=0.7, mfc='orange')
ax.grid()

# position bottom right
fig.text(0.95, 0.05, 'Property of MPL',
         fontsize=50, color='gray',
         ha='right', va='bottom', alpha=0.5)

plt.show()
```
# Spider
## 使用urllib和xpath
```python
# -*- coding: UTF-8 -*-
import urllib2

from lxml import etree

questionUrl = "https://wenda.so.com/c/125?pn=1"
header = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36",
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
}
xpath = '/html/body/div[6]/div[2]/div/div[2]/div[2]/ul/li/div/p/a/text()'
request = urllib2.Request(url=questionUrl, headers=header)
response = urllib2.urlopen(request)
result_msg = response.read().decode('utf-8')
selector = etree.HTML(result_msg)
questions = selector.xpath(xpath)
print(questions)

```
## 使用PhantomJS爬取动态网页
```bash
# 1、下载PhantomJS，并添加到环境变量
> http://phantomjs.org/
# 2、安装selenium
> pip install selenium
```
```python
# coding:utf-8
from selenium import webdriver

from lxml import etree

questionUrl = "https://wenda.so.com/c/125?pn=1"
xpath = '/html/body/div[6]/div[2]/div/div[2]/div[2]/ul/li/div/p/a/text()'
driver = webdriver.PhantomJS()
driver.get(questionUrl)
for i in range(3):
    result_msg = driver.page_source
    selector = etree.HTML(result_msg)
    questions = selector.xpath(xpath)
    print(questions)
    # 点击下一页
    elem = driver.find_element_by_class_name('next')
    elem.click()

```
## 轻量级爬虫框架 pySpider
参考[pySpider中文网](http://www.pyspider.cn/book/pyspider/pyspider-Quickstart-2.html)
```bash
> pip install pyspider
> pyspider # 启动web UI 
> pyspider --help
```
```cmd
Usage: pyspider [OPTIONS] COMMAND [ARGS]...

  A powerful spider system in python.

Options:
  -c, --config FILENAME           a json file with default values for
                                  subcommands. {"webui": {"port":5001}}
  --logging-config TEXT           logging config file for built-in python
                                  logging module  [default: d:\program
                                  files\anaconda3\lib\site-
                                  packages\pyspider\logging.conf]
  --debug                         debug mode
  --queue-maxsize INTEGER         maxsize of queue
  --taskdb TEXT                   database url for taskdb, default: sqlite
  --projectdb TEXT                database url for projectdb, default: sqlite
  --resultdb TEXT                 database url for resultdb, default: sqlite
  --message-queue TEXT            connection url to message queue, default:
                                  builtin multiprocessing.Queue
  --amqp-url TEXT                 [deprecated] amqp url for rabbitmq. please
                                  use --message-queue instead.
  --beanstalk TEXT                [deprecated] beanstalk config for beanstalk
                                  queue. please use --message-queue instead.
  --phantomjs-proxy TEXT          phantomjs proxy ip:port
  --data-path TEXT                data dir path
  --add-sys-path / --not-add-sys-path
                                  add current working directory to python lib
                                  search path
  --version                       Show the version and exit.
  --help                          Show this message and exit.

Commands:
  all            Run all the components in subprocess or...
  bench          Run Benchmark test.
  fetcher        Run Fetcher.
  one            One mode not only means all-in-one, it runs...
  phantomjs      Run phantomjs fetcher if phantomjs is...
  processor      Run Processor.
  result_worker  Run result worker.
  scheduler      Run Scheduler, only one scheduler is allowed.
  send_message   Send Message to project from command line
  webui          Run WebUI
```
## 重量级爬虫框架 scrapy
参考[Scrapy Document](http://scrapy-chs.readthedocs.io/zh_CN/0.24/intro/overview.html)
```
Scrapy主要包括了以下组件：
    引擎(Scrapy)
        用来处理整个系统的数据流, 触发事务(框架核心)
    调度器(Scheduler)
        用来接受引擎发过来的请求, 压入队列中, 并在引擎再次请求的时候返回. 可以想像成一个URL（抓取网页的网址或者说是链接）的优先队列, 由它来决定下一个要抓取的网址是什么, 同时去除重复的网址
    下载器(Downloader)
        用于下载网页内容, 并将网页内容返回给蜘蛛(Scrapy下载器是建立在twisted这个高效的异步模型上的)
    爬虫(Spiders)
        爬虫是主要干活的, 用于从特定的网页中提取自己需要的信息, 即所谓的实体(Item)。用户也可以从中提取出链接,让Scrapy继续抓取下一个页面
    项目管道(Pipeline)
        负责处理爬虫从网页中抽取的实体，主要的功能是持久化实体、验证实体的有效性、清除不需要的信息。当页面被爬虫解析后，将被发送到项目管道，并经过几个特定的次序处理数据。
    下载器中间件(Downloader Middlewares)
        位于Scrapy引擎和下载器之间的框架，主要是处理Scrapy引擎与下载器之间的请求及响应。
    爬虫中间件(Spider Middlewares)
        介于Scrapy引擎和爬虫之间的框架，主要工作是处理蜘蛛的响应输入和请求输出。
    调度中间件(Scheduler Middewares)
        介于Scrapy引擎和调度之间的中间件，从Scrapy引擎发送到调度的请求和响应。
```

# win32相关操作 窗口句柄
```python
# coding=utf-8
import win32api, win32gui, win32con

# 1. win32gui.FindWindow找到目标程序：
win = win32gui.FindWindow("Notepad", u'无标题 - 记事本')
# 2. 使用win32gui.FindWindowEx找到目标文本框：
tid = win32gui.FindWindowEx(win, None, 'Edit', None)
# 3.使用win32gui.SendMessage发送文本到目标文本框：
win32gui.SendMessage(tid, win32con.WM_SETTEXT, None, 'hello')
# 输入中文
win32gui.SendMessage(tid, win32con.WM_SETTEXT, None, u'你好'.encode('gbk'))
# 4.读取文本
# 文本框内容长度
length = win32gui.SendMessage(tid, win32con.WM_GETTEXTLENGTH) + 1
# 生成buffer对象
buf = win32gui.PyMakeBuffer(length)
win32gui.SendMessage(tid, win32con.WM_GETTEXT, length, buf)
address, length = win32gui.PyGetBufferAddressAndLen(buf)
text = win32gui.PyGetString(address, length)
print('text: ', text)
# 发送回车的方法
win32gui.SendMessage(tid, win32con.WM_SETTEXT, None, text + 'hello')
win32gui.PostMessage(tid, win32con.WM_KEYDOWN, win32con.VK_RETURN, 0)
win32gui.PostMessage(tid, win32con.WM_KEYUP, win32con.VK_RETURN, 0)
# 5.鼠标定位与点击
# 鼠标定位到(30,50)
win32api.SetCursorPos([30, 150])
# 也可以根据控件句柄获取控件位置
btn_id = win32gui.FindWindowEx(tid, None, 'Button', None)
(left, top, right, bottom) = win32gui.GetWindowRect(btn_id)
# 鼠标定位到按钮中点
win32api.SetCursorPos((left + (right - left) / 2, top + (bottom - top) / 2))
# 执行左单键击，若需要双击则延时几毫秒再点击一次即可
win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP | win32con.MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0)
# 右键单击
win32api.mouse_event(win32con.MOUSEEVENTF_RIGHTUP | win32con.MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0)
# 6.关闭窗口
win32gui.PostMessage(win, win32con.WM_CLOSE, 0, 0)
```
# 装饰器
参考[python装饰器](http://python.jobbole.com/81683/)
## 示例
```python
# coding=utf-8
# 面向切面编程
import urllib2,json,time,logging
import functools

handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
handler.setFormatter(formatter)
log = logging.getLogger('Logger')
log.addHandler(handler)
log.setLevel(logging.DEBUG)

#钉钉webHook接口
webHook = 'https://oapi.dingtalk.com/robot/send?access_token=[Token]'
header = {"Content-Type": "application/json"}

# 带返回值的装饰器
def httpSend(func):
    # 使用functools.wraps将装饰器的返回值返回,调用方式为 doc = func(*args,**kwargs)
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        url = func(*args, **kwargs)
        req = urllib2.Request(url=url)
        response = urllib2.urlopen(req)
        doc = json.loads(response.read())
        return doc
    return wrapper

# httpSend装饰器
def httpSend(func):
    def wrapper(*args, **kwargs):
        data = func(*args, **kwargs)
        request = urllib2.Request(url=webHook, data=json.dumps(data), headers=header)
        response = urllib2.urlopen(request)
        return response
    return wrapper

# http请求装饰器
def http(func):
    def wrapper(*args, **kwargs):
        url, params, headers = func(*args, **kwargs)
        request = urllib2.Request(url=url, data=json.dumps(params), headers=headers)
        response = urllib2.urlopen(request)
        # print response.read().decode('utf-8')
        return response
    return wrapper

# 计时器
def timeStatis(func):
    def wrapper(*args, **kwargs):
        start = time.time()
        func(*args, **kwargs)
        log.debug(time.time() - start)
    return wrapper

@httpSend
def sendMsg(msg, at=None, atAll=False):
    data = {
        "msgtype": "text",
        "text": {
            "content": msg
        },
        "at": {
            "atMobiles": at,
            "isAtAll": atAll
        }
    }
    return data

# 多个装饰器的调用顺序是自下往上，但是运行时的执行顺序是自上往下
@timeStatis
@http
def get():
    url = "https://www.baidu.com"
    param = None
    header = {"Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
              "User-Agent": "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36"}
    return url, param, header
```

# multiprocessing/asyncio

>* multiprocessing 多进程，适合cpu密集型任务
>* asyncio 协程，适合io密集型任务

## multiprocessing
参考：[MultiProcessing Document](https://docs.python.org/2/library/multiprocessing.html)


```python
from multiprocessing import Pool
import pandas as pd
from sqlalchemy import create_engine
db_connect_string = 'mysql://user:pass@10.18.100.11:3306/beta?charset=utf8'
engine = create_engine(db_connect_string)

def multi_task():
    global results
    with engine.connect() as conn:
        df = pd.read_sql("select id,question,answer from ai_question_answer limit 500", conn)
        df.index = df['id']
        process_size = 4  # 进程数量(默认cpu核数)
        pool = Pool(process_size)
        results = pd.DataFrame()
        err_data = pd.DataFrame()
        for i in range(process_size):
            # map_async异步执行，io密集型可以用协程，计算密集型用进程
            # def map_async(self, func, iterable, chunksize=None, callback=None):
            # 使用chunksize指定分片数量，这里分4份
            pool.map_async(get_ai_answer, [df, 4], callback=callBackFunc)
        pool.close()
        pool.join()
        results = results.sort_values(by='id')  # 排序
        results.to_excel('output.xls', index=False)
        print u'错误率：%f' % (err_data.shape[0] * 1.0 / df.shape[0])

# 回调函数
def callBackFunc(data):
    global results
    results = results.append(data)

```
## asyncio
参考[Asyncio Document](http://python.jobbole.com/87310/)

```python
import asyncio
import time
 
now = lambda: time.time()
 
async def do_some_work(x):
    print('Waiting: ', x)
 
    await asyncio.sleep(x)
    return 'Done after {}s'.format(x)
 
async def main():
    coroutine1 = do_some_work(1)
    coroutine2 = do_some_work(2)
    coroutine3 = do_some_work(4)
 
    tasks = [
        asyncio.ensure_future(coroutine1),
        asyncio.ensure_future(coroutine2),
        asyncio.ensure_future(coroutine3)
    ]
 
    dones, pendings = await asyncio.wait(tasks)
 
    for task in dones:
        print('Task ret: ', task.result())
 
start = now()
 
loop = asyncio.get_event_loop()
loop.run_until_complete(main())
 
print('TIME: ', now() - start)
```

# 网络相关
## websocker
```python
# -*- coding: utf-8 -*-
# version:3.6
import gzip,json,time
from websocket import create_connection

# webSocker订阅比特币实时行情
def start():
    global list_close
    while True:
        try:
            ws = create_connection("wss://api.huobipro.com/ws")
            break
        except:
            print('connect ws error,retry...')
            time.sleep(5)

    # 订阅 KLine 数据
    tradeStr = """{"sub": "market.ethusdt.kline.1min","id": "id10"}"""

    ws.send(tradeStr)
    while (1):
        compressData = ws.recv()
        result = gzip.decompress(compressData).decode('utf-8')
        # 心跳包
        if result[:7] == '{"ping"':
            ts = result[8:21]
            pong = '{"pong":' + ts + '}'
            ws.send(pong)
            ws.send(tradeStr)
        else:
            data = json.loads(result)
            print(data)
```
## RPC 远程代码调用
参考：[Google-gRpc](https://grpc.io/docs/quickstart/python.html)\
[Python RPC 之 gRPC](https://www.jianshu.com/p/14e6f5217f40)
## P2P网络
### Kademlia算法
参考：
[kademlia算法](https://www.jianshu.com/p/f2c31e632f1d?utm_campaign=maleskine&utm_content=note&utm_medium=seo_notes&utm_source=recommendation)
[github-kademlia](https://github.com/bmuller/kademlia)
```
# 恒等率
x ^ 0 = x
# 自反
x ^ y ^ y = x ^ 0 = x
# 自己与自己的距离为0:
x ^ x = 0
# 不同的节点间必有距离:
x ^ y > 0
# 交换律，x到y的距离等于y到x的距离:
x ^ y = y ^ x
# 从a经b绕到c, 要比直接从a到c距离长:
a ^ b + b ^ c >= a ^ c
# 暂时不清楚
a + b >= a ^ b
# 符合交换律、结合律
(a ^ b) ^ (b ^ c) = a ^ c
下表反映了每个K-桶所储存的信息
K-桶    储存的距离区间  储存的距离范围 储存比率
0	      [2^0, 2^1)	    1	        100%
1	      [2^1, 2^2)	    2-3	        100%
2	      [2^2, 2^3)	    4-7	        100%
3	      [2^3, 2^4)	    8-15	    100%
4	      [2^4, 2^5)	    16-31	    75%
5	      [2^5, 2^6)	    32-63	    57%
10	      [2^10, 2^11)      1024-2047	13%
i	      [2^i, 2^(i+1))    /	        0.75i-3
```
![png](https://upload-images.jianshu.io/upload_images/947209-6bdd6e96a80d0780.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/430)
![png](https://upload-images.jianshu.io/upload_images/947209-1143169c8318a2ff.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/666)

### nat打洞
```
 # kademlia节点使用rpc通过udp进行通信，这意味着它能够在nat后面工作。
 # UDP打洞过程：
（1）ClientA请求Server。
（2）ClientB请求Server。
（3）Server把ClientA的IP和端口信息发给ClientB。
（4）Server把ClientB的IP和端口信息发给ClientA。
（5）ClientA利用信息给ClientB发消息。（A信任B）
（6）ClinetB利用信息给ClientA发消息。（B信任A）
（7）连接已经建立。两者可以直接通信了。
```
### Socket连接
```python
# server
import socket

address = ('127.0.0.1', 31500)
# socket.SOCK_STREAM	基于TCP的流式socket通信
# socket.SOCK_DGRAM	基于UDP的数据报式socket通信
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)  
s.bind(address)
s.listen(5)
ss, addr = s.accept()
print 'got connected from', addr

ss.send('byebye')
ra = ss.recv(512)
print ra

ss.close()
s.close()

# client
import socket

address = ('127.0.0.1', 31500)
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(address)

data = s.recv(512)
print 'the data received is', data

s.send('hihi')

s.close()
```

# 远程部署-Fabric

```python
#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Fabric是一个用Python开发的部署工具，最大特点是不用登录远程服务器，在本地运行远程命令，几行Python脚本就可以轻松部署。
from datetime import datetime
from fabric.api import *

# 登录用户和主机名：
env.user = 'root'
env.hosts = ['www.example.com'] # 如果有多个主机，fabric会自动依次部署

def pack():
    '定义一个pack任务 '
    # 打一个tar包：
    tar_files = ['*.py', 'static/*', 'templates/*', 'favicon.ico']
    local('rm -f example.tar.gz')
    local('tar -czvf example.tar.gz --exclude=\'*.tar.gz\' --exclude=\'fabfile.py\' %s' % ' '.join(tar_files))

def deploy():
    '定义一个部署任务 '
    # 远程服务器的临时文件：
    remote_tmp_tar = '/tmp/example.tar.gz'
    tag = datetime.now().strftime('%y.%m.%d_%H.%M.%S')
    run('rm -f %s' % remote_tmp_tar)
    # 上传tar文件至远程服务器：
    put('shici.tar.gz', remote_tmp_tar)
    # 解压：
    remote_dist_dir = '/srv/www.example.com@%s' % tag
    remote_dist_link = '/srv/www.example.com'
    run('mkdir %s' % remote_dist_dir)
    with cd(remote_dist_dir):
        run('tar -xzvf %s' % remote_tmp_tar)
    # 设定新目录的www-data权限:
    run('chown -R www-data:www-data %s' % remote_dist_dir)
    # 删除旧的软链接：
    run('rm -f %s' % remote_dist_link)
    # 创建新的软链接指向新部署的目录：
    run('ln -s %s %s' % (remote_dist_dir, remote_dist_link))
    run('chown -R www-data:www-data %s' % remote_dist_link)
    # 重启fastcgi：
    fcgi = '/etc/init.d/py-fastcgi'
    with settings(warn_only=True):
        run('%s stop' % fcgi)
    run('%s start' % fcgi)
```

# scikit-learn/tesorflow/keras
参考：
[book-TensorFlow技术解析与实战+-+李嘉璇](https://www.jianguoyun.com/p/DQsUj8cQ68fhBhi430U)\
[data-science-ipython-notebooks](https://github.com/donnemartin/data-science-ipython-notebooks)\
[github-tensorflow](https://github.com/tensorflow/tensorflow)\
[github-tensorflow-models](https://github.com/tensorflow/models)\
[github-karas](https://github.com/keras-team/keras)\
[document-karas](https://keras-cn.readthedocs.io/en/latest/)\
[迁移学习](https://morvanzhou.github.io/tutorials/machine-learning/ML-intro/2-9-transfer-learning/)\
todo 网络结构分析：CNN、RNN、LSTM、生成对抗网络、迁移学习……
