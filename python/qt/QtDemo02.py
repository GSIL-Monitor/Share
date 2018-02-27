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
        msg_box.setWindowTitle('Warning')
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
