# server
import socket

address = ('127.0.0.1', 31500)
# socket.SOCK_STREAM	基于TCP的流式socket通信
# socket.SOCK_DGRAM	基于UDP的数据报式socket通信
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(address)
s.listen(5)
ss, addr = s.accept()
print 'got connected from', addr

ss.send('byebye')
ra = ss.recv(512)
print ra

ss.close()
s.close()
