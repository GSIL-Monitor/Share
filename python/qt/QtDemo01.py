# coding=utf-8
from PyQt5 import QtWidgets
from PyQt5.QtWidgets import QMainWindow
from PyQt5.QtWidgets import QMessageBox
from QtSignal import QtSignal


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
        self.qtSignal = QtSignal()
        self.qtSignal.signal.connect(self.receiveSignal)

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
