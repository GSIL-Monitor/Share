# coding=utf-8
from PyQt5 import QtWidgets
from PyQt5.QtWidgets import QMainWindow
from PyQt5.QtWidgets import QMessageBox
from QtSignal import QtSignal
import numpy as np
import pyqtgraph as pg
from PyQt5.QtCore import QTimer


class Ui_TradeWindow(QMainWindow):
    def setupUi(self, TradeWindow):
        TradeWindow.setObjectName("TradeWindow")
        TradeWindow.resize(800, 600)
        self.horizontalLayout_1 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_1.setObjectName("horizontalLayout_1")

        self.statusbar = QtWidgets.QStatusBar(TradeWindow)
        self.statusbar.setObjectName("statusbar")
        self.statusbar.showMessage('未连接')
        TradeWindow.setStatusBar(self.statusbar)
        # 指定关闭事件
        TradeWindow.closeEvent = self.myCloseEvent
        self.qtSignal = QtSignal()
        self.qtSignal.signal.connect(self.receiveSignal)
        self.graph()

    # 添加到qt
    def graph(self):
        # clean
        for i in reversed(range(self.horizontalLayout_1.count())):
            self.horizontalLayout_1.itemAt(0).widget().setParent(None)
        # show
        self.horizontalLayout_1.addWidget(self.plot())

    # 画图
    def plot(self):
        global curve2, data2
        data2 = np.linspace(-2 * np.pi, 2 * np.pi, 500).tolist()
        y = np.sin(data2)
        plt = pg.PlotWidget(title=u"动态图", background='w')
        plt.showGrid(x=True, y=True)
        curve2 = plt.plot(x=data2, y=y, pen='b')
        return plt

    # 更新数据
    def update(self):
        global curve2, data2
        data2.append(data2[-1] + (4 * np.pi / 500))  # 在最后追加
        y = np.sin(data2)
        curve2.setData(x=data2, y=y)

    # 定时器
    def updateEvent(self):
        # 定时更新
        self.timer = QTimer()  # 初始化一个定时器
        self.timer.timeout.connect(self.update)  # 计时结束调用operate()方法
        self.timer.start(10)  # 设置计时间隔并启动

    # 接收信号
    def receiveSignal(self, event):
        print (event)

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
