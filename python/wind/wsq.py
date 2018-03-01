# coding=utf-8
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
