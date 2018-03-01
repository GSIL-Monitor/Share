# coding=utf-8
import win32api
import win32gui

import win32con


class OptionSpider():
    def __init__(self):
        win = win32gui.FindWindow('Afx:400000:8:10003:0:500e9', u'主窗体： 国信证券汇点期权 - [期权策略交易]')
        tid1 = win32gui.FindWindowEx(win, None, 'AfxControlBar42', u'状态条')
        tid2 = win32gui.FindWindowEx(tid1, None, 'Afx:400000:8:10003:100057:0', u'交易')
        tid3 = win32gui.FindWindowEx(tid2, None, '#32770 (Dialog)', None)
        tid4 = win32gui.FindWindowEx(tid3, None, '#32770 (Dialog)', None)
        tid5 = win32gui.FindWindowEx(tid4, None, '#32770 (Dialog)', None)
        tid5_2 = win32gui.FindWindowEx(tid4, tid5, '#32770 (Dialog)', None)
        tid6 = win32gui.FindWindowEx(tid5_2, None, '#32770 (Dialog)', None)
        tid7 = win32gui.FindWindowEx(tid6, None, '#32770 (Dialog)', None)
        comboBox_symbol = win32gui.FindWindowEx(tid7, None, 'ComboBox', None)
        # 合约
        self.edit_symbol = win32gui.FindWindowEx(comboBox_symbol, None, 'Edit', None)
        # 开仓
        self.radioBtn_open = win32gui.FindWindowEx(tid7, None, 'OWERDRAWRADIO', None)
        # 平仓
        self.radioBtn_close = win32gui.FindWindowEx(tid7, self.radioBtn_open, 'OWERDRAWRADIO', None)
        # 数量
        self.edit_volume = win32gui.FindWindowEx(tid7, None, 'Edit', None)
        # 价格
        self.edit_price = win32gui.FindWindowEx(tid7, self.edit_volume, 'Edit', None)
        # 买入
        self.btn_buy = win32gui.FindWindowEx(tid7, None, 'Button', None)
        # 卖出
        self.btn_sell = win32gui.FindWindowEx(tid7, self.btn_buy, 'Button', None)

    def send_order(self, data):
        # 发送合约并回车
        win32gui.SendMessage(self.edit_symbol, win32con.WM_SETTEXT, None, data['symbol'])
        win32gui.PostMessage(self.edit_symbol, win32con.WM_KEYDOWN, win32con.VK_RETURN, 0)
        win32gui.PostMessage(self.edit_symbol, win32con.WM_KEYUP, win32con.VK_RETURN, 0)
        # 发送成交量
        win32gui.SendMessage(self.edit_volume, win32con.WM_SETTEXT, None, data['volume'])
        # 发送价格
        win32gui.SendMessage(self.edit_price, win32con.WM_SETTEXT, None, data['price'])
        if data['operation'] == "申购":
            # 点击买入
            self.click_button(self.btn_buy)
        else:
            # 点击卖出
            self.click_button(self.btn_sell)

        if data['direction'] == "开仓":
            # 点击开仓
            self.click_button(self.radioBtn_open)
        else:
            # 点击平仓
            self.click_button(self.radioBtn_close)

    def click_button(self, btn_id):
        if btn_id:
            # 鼠标定位到按钮中点
            (left, top, right, bottom) = win32gui.GetWindowRect(btn_id)
            win32api.SetCursorPos((left + (right - left) / 2, top + (bottom - top) / 2))
            # 执行左单键击，若需要双击则延时几毫秒再点击一次即可
            win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP | win32con.MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0)


if __name__ == "__main__":
    optionSpider = OptionSpider()
    data = {}
    data['symbol'] = '10001171'
    data['volume'] = '10'
    data['price'] = '1.0'
    data['operation'] = "申购"
    data['direction'] = "开仓"
    optionSpider.send_order(data)
