# coding=utf-8

import win32api, win32gui, win32con

# 1. win32gui.FindWindow找到目标程序：
win = win32gui.FindWindow("Notepad", u'无标题 - 记事本')
# 2. 使用win32gui.FindWindowEx找到目标文本框：
tid = win32gui.FindWindowEx(win, None, 'Edit', None)
# 3.使用win32gui.SendMessage发送文本到目标文本框：
win32gui.SendMessage(tid, win32con.WM_SETTEXT, None, 'hello')
# 输入中文
win32gui.SendMessage(tid, win32con.WM_SETTEXT, None, u'你好'.encode('gbk'))
# 4.读取文本
# 文本框内容长度
length = win32gui.SendMessage(tid, win32con.WM_GETTEXTLENGTH) + 1
# 生成buffer对象
buf = win32gui.PyMakeBuffer(length)
win32gui.SendMessage(tid, win32con.WM_GETTEXT, length, buf)
address, length = win32gui.PyGetBufferAddressAndLen(buf)
text = win32gui.PyGetString(address, length)
print('text: ', text)
# 发送回车的方法
win32gui.SendMessage(tid, win32con.WM_SETTEXT, None, text + 'hello')
win32gui.PostMessage(tid, win32con.WM_KEYDOWN, win32con.VK_RETURN, 0)
win32gui.PostMessage(tid, win32con.WM_KEYUP, win32con.VK_RETURN, 0)
# 5.鼠标定位与点击
# 鼠标定位到(30,50)
win32api.SetCursorPos([30, 150])
# 也可以根据控件句柄获取控件位置
btn_id = win32gui.FindWindowEx(tid, None, 'Button', None)
(left, top, right, bottom) = win32gui.GetWindowRect(btn_id)
# 鼠标定位到按钮中点
win32api.SetCursorPos((left + (right - left) / 2, top + (bottom - top) / 2))
# 执行左单键击，若需要双击则延时几毫秒再点击一次即可
win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP | win32con.MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0)
# 右键单击
win32api.mouse_event(win32con.MOUSEEVENTF_RIGHTUP | win32con.MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0)
# 6.关闭窗口
win32gui.PostMessage(win, win32con.WM_CLOSE, 0, 0)
