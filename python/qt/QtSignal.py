# coding=utf-8

from PyQt5.QtCore import *


class QtSignal(QThread):
    signal = pyqtSignal(int)

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
