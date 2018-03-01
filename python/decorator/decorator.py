# coding=utf-8
import urllib2,json,time,logging

handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
handler.setFormatter(formatter)
log = logging.getLogger('Logger')
log.addHandler(handler)
log.setLevel(logging.DEBUG)

#钉钉webHook接口
webHook = 'https://oapi.dingtalk.com/robot/send?access_token=[Token]'
header = {"Content-Type": "application/json"}

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


if __name__ == '__main__':
    get()
