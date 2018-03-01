# coding=utf-8

import matplotlib.pyplot as plt
import numpy as np
import pyqtgraph as pg
from PyQt5.QtCore import QTimer


# 根据x和y的数据区间确定绘图的size，保证美观
def resizeFig(x, y):
    x1 = np.max(x) - np.min(x)
    y1 = np.max(y) - np.min(y)


# def plot_date(x, y, fmt='o', tz=None, xdate=True, ydate=False, hold=None,data=None, **kwargs):
# 绘图
def plot(x, y, fmt='b-', size=(8, 8), tz=None, xdate=False, ydate=False, hold=None, data=None, **kwargs):
    fig = plt.figure(figsize=size, dpi=80)
    if xdate:
        plt.plot_date(x, y, fmt)
    else:
        plt.plot(x, y, fmt)
    plt.show()


# 多幅图
def multiPlot(data, size=(8, 8)):
    data = np.asarray(data)
    n = np.ceil(np.sqrt(data.shape[0]))
    fig = plt.figure(figsize=size, dpi=80)  # 多条曲线
    index = 0
    for (x, y) in data:
        index += 1
        fig.add_subplot(n * 110 + index)
        plt.plot(x, y)
    plt.show()


# 实时图 (使用PyQtgraph进行绘图)
def pgPlot(x, y, title=None, background='w'):
    global curve
    curve = pg.plot(x, y, title=title, background=background)
    startPgQTimer()
    pg.QtGui.QGuiApplication.exec_()


# -- 更新数据
def updateGraph():
    print 'update'
    global x, y, curve
    x.append(np.random.random(1)[0])
    y = x ** 2
    curve.setData(x=x, y=y)


# -- 启动定时器 (类对象方式调用)
def startPgQTimer():
    # 定时更新
    timer = QTimer()  # 初始化一个定时器
    timer.timeout.connect(updateGraph)  # 计时结束调用operate()方法
    timer.start(1)  # 设置计时间隔并启动


if __name__ == '__main__':
    x = np.asarray(range(1, 100))
    y = x ** 2
    # pgPlot(x, y)
    fig = plt.figure(figsize=(8, 8), dpi=80)
    plt.plot_date(x, y, xdate=True)
    plt.show()

    # pyqtgraph example
    # import pyqtgraph.examples

    # pyqtgraph.examples.run()
