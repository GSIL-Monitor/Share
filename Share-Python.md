# Wind
```python
# coding=utf-8
# version:python-2.7
import numpy as np
import pandas as pd
from WindPy import *
from sqlalchemy import create_engine

# 获取指定日期基金净值数据
db_connect_string = 'mysql://betaWR:betaWR123@10.18.0.2:3306/beta_psbc?charset=utf8'
engine = create_engine(db_connect_string)

raw_data = w.wsd('000001.OF', "nav,NAV_adj", '2017-01-01', '2018-01-01', "")
w.start()
with engine.connect() as conn:
    if not raw_data.ErrorCode:
        df = pd.DataFrame(index=raw_data.Times, columns=raw_data.Fields,
                          data=np.array(raw_data.Data).transpose())

        df.to_sql("fund_calculate", conn, if_exists="append", index=False)
```
# QT
## PyQT
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
```python
# 使用PyQt5.uic引入ui文件方式
# coding=utf-8
import sys

from PyQt5 import uic
from PyQt5.QtWidgets import QMainWindow
from PyQt5.QtWidgets import QMessageBox


class MainWindow(QMainWindow):
    def __init__(self):
        QMainWindow.__init__(self)

        self.ui = uic.loadUi('mainwindow.ui')
        self.ui.closeEvent = self.closeEvent
        self.ui.pushButton.clicked.connect(self.btnClickEvent)
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
## pyqtGraph
```python
# 绘图
```
# Spider
```python
# xpath,urllib3
```
# scikit-learn/tesorflow

# multiprocessing/asyncio

# P2P/china-block

# win32相关操作 窗口句柄

# 装饰器