# coding=utf-8
from WindPy import *
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.pylab import date2num
import matplotlib.finance as mpf

codes = ["399300.SZ", "DJI.GI"]


def get_index_data():
    w.start()
    for code in codes:
        raw_data = w.wsd(code, "open,high,low,close,volume,amt,pct_chg,swing,turn", "2016-02-27", "2018-03-27", "")
        if not raw_data.ErrorCode:
            df = pd.DataFrame(index=raw_data.Times, columns=raw_data.Fields,
                              data=np.array(raw_data.Data).transpose())
            df['code'] = code
            df.to_excel(code + '.xls')


def analysis():
    for code in codes:
        df = pd.read_excel(code + ".xls")
        draw(df)
    plt.show()


def draw(df):
    # 对tushare获取到的数据转换成candlestick_ohlc()方法可读取的格式
    data_list = []
    code = df['code'][0]
    def func(column):
        t = date2num(column['DATE'])
        # t = date2num(datetime.today())
        datas = (t, column['OPEN'], column['HIGH'], column['LOW'], column['CLOSE'])
        data_list.append(datas)

    df.T.apply(func)
    # 创建子图
    fig, ax = plt.subplots()
    fig.subplots_adjust(bottom=0.2)
    # 设置X轴刻度为日期时间
    ax.xaxis_date()
    plt.xticks(rotation=45)
    plt.yticks()
    plt.title(code)
    plt.xlabel(u"Date")
    plt.ylabel(u"Tick")
    mpf.candlestick_ohlc(ax, data_list, width=1.5, colorup='r', colordown='green')
    plt.grid()


if __name__ == '__main__':
    analysis()
