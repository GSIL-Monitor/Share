# -*- coding: utf-8 -*-
# version:3.6
import gzip
import json
import time

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
        if result[:7] == '{"ping"':
            ts = result[8:21]
            pong = '{"pong":' + ts + '}'
            ws.send(pong)
            ws.send(tradeStr)
        else:
            data = json.loads(result)
            print(data)


if __name__ == '__main__':
    start()
