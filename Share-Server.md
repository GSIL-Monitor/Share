# Share - 运维篇
# 配置SSH登录
```bash
# ServerA SSH登录ServerB
# 1、ServerB上生成密钥对
# -t 指定密钥类型，默认即 rsa ，可以省略
# -C 设置注释文字，比如你的邮箱
> ssh-keygen -t rsa -C  'your email@domain.com'
# 2、拷贝到公钥到ServerA
> scp ~/.ssh/id_rsa.pub username@hostname:~/ #将公钥文件复制至ssh服务器
# scp ~/.ssh/id_rsa.pub username@hostname:~/.ssh/authorized_keys # 简略操作，省略步骤3
# 3、在ServerA上将id_rsa.pub拷贝到 ~/.ssh/authorized_keys
> ssh username@hostname #使用用户名和密码方式登录至ssh服务器
> mkdir .ssh  #若.ssh目录已存在，可省略此步
> cat id_rsa.pub >> .ssh/authorized_keys  #将公钥文件id_rsa.pub文件内容追加到authorized_keys文件

```
# Docker相关
## Docker
参考[Docker — 从入门到实践](https://yeasy.gitbooks.io/docker_practice/)
```cmd
# 查看已创建的容器相关配置
> docker inspect nginx
# 启动/停止/重启/运行 -- start/stop/restart/run
> docker restart nginx
# 创建和运行容器
> docker rm [容器name/id] # 删除容器
> docker rmi [镜像name/id] # 删除镜像
> docker run --privileged=true -p80:80 -v /root/nginx_docker/www:/www -v /root/nginx.conf:/etc/nginx/nginx.conf --name nginx -d docker.io/nginx
# docker清理占用卷
# 如果你的docker目录仍然占据着大量空间，那可能是因为多余的卷占用了你的磁盘。RM命令的-v命令通常会处理这个问题。但有时，如果你关闭容器不会自动删除容器，VFS目录将增长很快。我们可以通过删除不需要的卷来恢复这个空间。要做到这一点，有一个Docker镜像，你可以使用如下命令来运行它：
> docker run -v /var/run/docker.sock:/var/run/docker.sock -v /var/lib/docker:/var/lib/docker --rm martin/docker-cleanup-volumes
# 删除所有已经停止的容器
> sudo docker rm $(sudo docker ps -a -q)
# 杀死所有正在运行的容器
> sudo docker kill $(sudo docker ps -a -q)
# 查看docker log和使用tail
>  sudo docker logs beta_api_qa | tail -100f
# 登陆到docker容器
> sudo docker attach [contain id]  
# 使用exec登陆容器
> sudo docker exec -it [contain name/id] /bin/bash 
 

# 备份镜像
> Usage
    docker save [OPTIONS] IMAGE [IMAGE...]
# 导出golang镜像
> sudo docker save --output golang.tar golang:1.2
# 从本地导入镜像
> Usage
    docker load [OPTIONS]
# 导入golang镜像
> sudo docker load --input golang.tar
# 导出容器快照
> Usage
     docker export [OPTIONS] CONTAINER
# 导出hello容器快照
> sudo docker export --output hello.tar
# 从容器快照导入镜像
> Usage
    docker import [OPTIONS] URL|- [REPOSITORY[:TAG]]
# 导入hello快照，并制定镜像标签为hello:1.0
> cat hello.tar | sudo docker import - hello:1.0
```

## Docker-compose

```cmd
# 安装docker-compose:
> apt-get install docker-compose 
# 使用docker-compose创建和启动容器
> docker-compose up -d (默认使用当前目录下docker-compose.yml)
> docker-compose --f [file] up -d (制定file)
# 使用docker-compose管理容器
> docker-compose start/stop/restart/run/rm/ps nginx
> docker-compose run nginx bash #运行容器并启动bash
```
docker-compose.yml
```yaml
version: '2'

services:
  nginx:
    restart: always
    image: nginx
    container_name: nginx
    hostname: nginx
    stdin_open: true
    tty: true
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /data1/auto_upline/nginx:/data/service
      - /data1/Docker/nginx:/etc/nginx
      - /data1/log/nginx:/data/log/nginx
    command: nginx -g 'daemon off;'
    links:
      - beta_api_qa

  beta_api_qa:
    # 自动重启
    restart: always
    image: jdk8:latest
    container_name: beta_api_qa
    hostname: beta_api_qa
    # 工作目录，表示容器运行后所在目录
    working_dir: /opt/app
    stdin_open: true
    # 分配tty设备，该可以支持终端登录
    tty: true
    # 端口映射
    ports:
      - "8080:8080"
    # 文件映射
    volumes:
      - /data1/auto_upline/common:/opt/common
      - /data1/auto_upline/beta_api:/opt/app
    # 参数
    environment:
      JAVA_OPTS: "-Duser.timezone=GMT+08 -Xmx1G -Xms1G -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m"
    # 启动命令 -Dspring.profiles.active=dev表示使用dev配置
    command: java -jar -Dspring.profiles.active=dev app-alone.war
```
# Nginx
```cmd
#重启nginx：restart
#重载配置:reload(nginx修改配置后重载)
#开启/关闭：start/stop
> sudo /usr/local/sbin/nginx -s reload
#另一种停止方式
> sudo /usr/local/sbin/nginx -s quit #kill -s SIGQUIT pid_master
> kill -s SIGWINCH pid_master
```
conf
```yaml
server {
  # 配置域名和端口
  listen 80;
  listen 443;
  server_name  wxp.betago2016.com wxgzh.betago2016.com;
  # 日志路径
  access_log   /data/log/nginx/m.log  main;
  error_log    /data/log/nginx/m-error.log error;

  # 映射
  location /api/ {
    include proxy.conf;
    proxy_pass http://upstream_api/;
  }

  location /(.*)\.(js|css|map|png|jpe?g|gif|svg)$ {
     expires 30d;
     root /data/service/h5/;
   }

  location / {
   open_file_cache off;
   expires -1;
   add_header Pragma no-cache;
   add_header Cache-Control no-cache;
   root /data/service/h5;
   try_files $uri $uri/ /index.html;
  }
}
```
upstream
```yaml
# api
upstream upstream_api {
       server 10.18.0.14:8080;
}
# management
upstream mgmt_api{
       server 10.18.0.14:18083;
}
# 负载均衡
upstream demo{
       # 负载均衡策略参考：http://blog.csdn.net/u010081710/article/details/52691406
       ip_hash;
       server 10.18.0.14:18083;
       server 10.18.0.14:18083;
       server 10.18.0.15:18083;
}
```


# MySQL
```sql
-- 统一字符编码,包含emoji表情,统一采用utf8mb4,排序规则utf8mb4_general_ci,即不区分大小写
-- 尽量不使用存储过程和事务,减少复合查询,反范式增加字段冗余、去掉外键关联。
> set CHARSET=utf8mb4
-- 单表导出
> sudo mysqldump -uroot -pbetaRoot1 -h10.18.100.11 -P3306 beta --tables fund_calculate >fund_calculate.sql
-- 整库备份
> sudo mysqldump -uroot -pbetaRoot1 -h10.18.100.11 -P3306 beta >beta.sql
-- 导入
> mysql -uroot -pbetaRoot1 -h10.18.0.2 -P3306 beta < fund_calculate.sql
-- 显示processList
> SHOW PROCESSLIST;
-- 查看当前正在执行查询的任务
> SELECT * FROM information_schema.`PROCESSLIST` t WHERE t.`COMMAND` <> 'Sleep';
-- 事务相关
> select @@tx_isolation; #查看当前会话隔离级别
> select @@global.tx_isolation; #查看全局事务隔离级别
> set global transaction isolation level READ UNCOMMITTED; #设置全局事务隔离级别
> set session transaction isolation level READ UNCOMMITTED; #设置会话隔离级别
> lock table t_order write; #对表加写锁
-- 采用READ UNCOMMITTED 读未提交的事务隔离级别可以加写锁不影响读(共享锁)
-- 采用REPEATABLE-READ可重复读的事务隔离级别会导致加写锁,读请求一支请求锁(排它锁)
```

# Shell
## 文件传输
### scp
```cmd
# 上传文件
> scp ./beta.war root@10.18.0.14:/data1/tomcat/ROOT.war
> scp -r . root@10.18.0.14:/data1/tomcat (上传目录)
# 下载文件
> scp root@10.18.0.14:/data1/ROOT.war ./beta.war
# 文件传输需要密码可以使用软件sshpass -p [password]或者配置成ssh登录,如：
> sshpass -p 123 scp -r root@10.25.13.3:/home/LucenceIndexDic /home/LucenceIndexDic

```
### rz/sz
```bash
> sudo apt-get install lrzsz

Usage: rz [options] [filename.if.xmodem]
> sudo rz

Usage: sz [options] file ...
   or: sz [options] -{c|i} COMMAND
> sudo  sz -be ./app-alone.war
```
### rsync

>* Rsync（remote synchronize）是一个远程数据同步工具，可通过LAN/WAN快速同步多台主机间的文件。Rsync使用所谓的“Rsync算法”来使本地和远程两个主机之间的文件达到同步，这个算法只传送两个文件的不同部分，而不是每次都整份传送，因此速度相当快。
>* 安装配置参考：[Linux-Rsync服务器/客户端搭建实战](https://www.cnblogs.com/JohnABC/p/6203524.html)

## 文件下载
### wget
```cmd
# wget [url] --user [user] --password [passwd]
> wget http://wxp.betago2016.com
> wget http://www.sogou.com/labs/sogoudownload/SogouCS/news_sohusite_xml.full.tar.gz --user asd@163.com --password }z094rIazNwe8h8k
```

### aria2
```bash
> aria2c http://xxx.com
# 使用aria2的分段和多线程下载功能可以加快文件的下载速度，对于下载大文件时特别有用。-x 分段下载，-s 多线程下载，如：
> aria2c -s 2 -x 2 http://xx.com/xx
# 种子和磁力下载：
> aria2c 'xxx.torrnet'
> aria2c '磁力链接'
# 列出种子内容
> aria2c -S xxx.torrent
# 下载种子内编号为1、4、5、6、7的文件，如：
> aria2c --select-file=1,4-7 xxx.torrent
# 设置bt端口
> aria2c --listen-port=3653 'xxx.torrent'
```
## HTTP请求
### curl
```cmd
# get请求，指定请求头
> curl -X GET --header 'Accept: application/json' 'http://10.18.0.14:8080/userManagerMsg/listMessages'
# post请求，RequestParam传参
> curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' 'http://10.18.0.14:8080/userManagerRelationship/saveInfo/1?comments=备注&tags=2'
# post请求,RequestBody传参
> curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"question": "你好"}' 'http://10.18.0.14:8080/chat'
# 使用--cookie "COKKIES"选项来指定cookie，多个cookie使用分号分隔
```
## 文本操作
### tail/cat/vim
```cmd
> tail -100f catalina.out  # 动态查看文件最后100行
> cat m.conf
> vim m.conf
# vim常用操作：
gg                          跳至文首
G                           调至文尾
5gg/5G                      调至第5行
/pattern                    向后搜索字符串pattern
?pattern                    向前搜索字符串pattern
n                           下一个匹配(如果是/搜索，则是向下的下一个，?搜索则是向上的下一个)
:%s/old/new/g               搜索整个文件，将所有的old替换为new
:%s/old/new/gc              搜索整个文件，将所有的old替换为new，每次都要你确认是否替换
gg=G                        格式化，自动对齐
```
### sed
参考[man-sed命令](http://man.linuxde.net/sed)
```cmd
# 删除空白行：
> sed '/^$/d' file
# 替换文本中的字符串:将所有book替换为books
> sed 's/book/books/' file
# 在test.conf文件第5行之前插入this is a test line：
> sed -i '5i\this is a test line' test.conf
# 在 test.conf 文件第2行之后插入 this is a test line：
> sed -i '2a\this is a test line' test.conf
# 打印第9-11行
> sed -n '9,11p' test.txt | cat -n
# 将行首的#号去掉
> sed 's/^#//g' test.txt | cat -n 
```
## 资源查看
```cmd
> df -lh # 磁盘占用
> free -lh # 内存占用
> ls -lh --sort size # 指定排序
> find / -size +100M #查找大于100MB的文件
> find -mtime -1 #扫描1天内的改动文件
> du . -h --max-depth=2 # 统计当前目录所有目录、文件大小，深度为2
> ps -ef | grep java # 查看进程信息
> ps -aux --sort %mem #根据内存排序
> ps -aux --sort %cpu #根据cpu排序
> top -s | grep java 查看进程信息
> kill -9 $(pidof 进程名关键字) #根据进程名称杀死进程
```
## 网络相关
```cmd
> netstat -tln #查看所有开放的端口  
> netstat -anlp | grep 8080 # 查看8080端口
> telnet 114.215.222.138 9999 #测试远程端口是否连通
> lsof -i tcp:80  #查看80端口占用情况
```
## 获取公网IP
```cmd
# ipip.net提供（推荐）
> curl myip.ipip.net
# ifconfig.me提供
> curl ifconfig.me  
```
## 压缩/解压
### tar
```cmd
Usage: tar [OPTION...] [FILE]...
# 压缩
> tar -cvzf temp.tar ./dic 
# 解压
> tar -zxvf temp.tar
```
### zip
```cmd
Usage: unzip [-Z] [-opts[modifiers]] file[.zip] [list] [-x xlist] [-d exdir]
  Default action is to extract files in list, except those in xlist, to exdir;
  file[.zip] may be a wildcard.  -Z => ZipInfo mode ("unzip -Z" for usage).
Usage: zip [-options] [-b path] [-t mmddyyyy] [-n suffixes] [zipfile list] [-xi list]
# 压缩
> zip -r mydata.zip mydata
# 解压
> unzip temp.zip
```
# 定时任务 corn
```cmd
# 文件：/etc/crontab
# 启动/关闭服务
> /sbin/service crond start
> /sbin/service crond stop
> /sbin/service crond restart
> /sbin/service crond reload
> crontab -l #查看定时任务
> crontab -e #编辑定时任务
# Example of job definition:
# .---------------- minute (0 - 59)
# |  .------------- hour (0 - 23)
# |  |  .---------- day of month (1 - 31)
# |  |  |  .------- month (1 - 12) OR jan,feb,mar,apr ...
# |  |  |  |  .---- day of week (0 - 6) (Sunday=0 or 7) OR sun,mon,tue,wed,thu,fri,sat
# |  |  |  |  |
# *  *  *  *  * user-name  command to be executed
# 0 2 * * * /bin/sh /root/indexSync.sh
```
## sh
```cmd
# 日期
> today=`date +"%Y-%m-%d %H:%M:%S:%s"`
> echo ${today} # 2018-02-27 16:46:02:1519721162
# 使用脚本部署maven项目
#!/bin/sh
git pull
mvn clean package -DskipTests=true -P sh
time=`date +%s`
cp /usr/local/tomcat/webapps/Robot.war /home/local/projects/back/Robot.${time}.war
mv target/Robot.war /usr/local/tomcat/webapps/Robot.war
echo 'success!'
```
# ElasticSearch
参考[Elasticsearch: 权威指南](https://www.elastic.co/guide/cn/elasticsearch/guide/current/index.html)
```cmd
# es curl查询
# question term为“你好”
> curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"query":{"bool":{"must":[{"term":{"question":"你好"}}],"must_not":[],"should":[]}},"from":0,"size":10,"sort":[],"aggs":{}}' 'http://10.18.0.9:9200/qa_bm/_search'
# question str包含“你好” & classify term为“1”
> {"query":{"bool":{"must":[{"query_string":{"default_field":"question","query":"你好"}},{"term":{"classify":"1"}}],"must_not":[],"should":[]}},"from":0,"size":10,"sort":[],"aggs":{}}
# 使用分析器beta_analyzer进行语句分析
> curl -XGET 'http://10.18.100.3:9200/qa_v2/_analyze?analyzer=beta_analyzer' -d '你们有什么事情'

```

# JHipster
参考[JHipster开发笔记](https://jh.jiankangsn.com/)
```bash
## Docker 方式
# 获取镜像文件
> docker pull docker.io/jhipster/jhipster
# 创建启动容器
> docker container run --name jhipster -v ~/jhipster:/home/jhipster/app -v ~/.m2:/home/jhipster/.m2 -p 8080:8080 -p 9000:9000 -p 3001:3001 -d -t jhipster/jhipster

## 本地方式
#下载jhipster-registry源码
> git clone https://github.com/jhipster/jhipster-registry.git
#进入 jhipster-registry 目录
> cd jhipster-registry
# 创建项目 参考[GitBook - JHipster](https://jh.jiankangsn.com/ch1)
> yo jhipster
# 使用 JDI Studio生成Entity
# [JDI Studio]https://start.jhipster.tech/jdl-studio/
# 导入entity
> yo jhipster:import-jdl jhipster-jdl.jh
#运行 jhipster-registry程序
> mvnw
```
# Git
```cmd
# 区分远端分支和本地分支，测试分支和正式分支
> git init                                          # 初始化本地git仓库（创建新仓库）
> git clone git+ssh://git@192.168.53.168/VT.git     # clone远程仓库
> git status                                        # 查看当前版本状态（是否修改）
> git add xyz                                       # 添加xyz文件
> git add ./*                                       # 增加当前子目录下所有更改过的文件
> git commit -m 'xxx'                               # 提交
> git commit -am 'xxx'                              # 将add和commit合为一步
> git log                                           # 显示提交日志
> git pull                                          # 获取远程相应分支并merge到当前分支
> git push                                          # 将当前分支push到远程相应分支
> git push -u origin master                         # 本地分支提交到线上分支
# 分支
> git branch                                        # 显示本地分支
> git checkout xxx                                  # 从当前分支创建新分支xxx并检出（切换分支）
> git branch -D hotfix                              # 强制删除分支hotfix
> git branch XXX				                    # 创建分支
> git merge [branch]                                # 将[branch]分支内容合并到当前分支
# remote conf
> git remote rename origin old-origin               # 重命名原配置的origin地址，防冲突
> git remote add origin [url]                       # 添加远程服务器地址
> git rm -r --cached .                              # 清除本地缓存(添加ignore文件不生效使用)
# stash and pop
> git stash                                         # 将当前修改压倒堆栈，用于暂存当前正在进行的工作，回复到上一个commit，如：fix 一个紧急的bug,  先stash, 使返回到自己上一个commit, 改完bug之后再stash pop, 继续原来的工作
> git stash save "msg"                              # 多个stash添加文本标注
> git stash list                                    # list stashed changes in this git
> git stash pop                                     # apply last stash and remove it from the list
> git stash pop <stash@{id}>                        # 根据id回复
# reset
> git reset --hard <head^>                          # 强制回退到版本号，清空未提交
``` 
# 内网环境
>* VPN(Virtual Private Network)，即虚拟专用网或虚拟私用网，是指利用开放的公共网络资源建立私有专用传输通道。而我们提供的VPN就是使客户利用internet互联网这个公共网络建立建立客户的个人电脑-VPN服务器之见的私有专用传输通道。连接VPN后客户的所有网络数据都将通过这个通道进行传输。严格来说VPN并不是代理，但大家都用它来实现代理的功能，所以大家习惯性称为VPN代理。所有的网络数据都会通过vpn通道传输，同等网络环境下速度快于代理。
>* Sock(socket security,SOCKS)是一种基于传输层的网络代理协议。对于各种基于 TCP/IP的应用层协议都能够适应。它能够忠实地转发客户端-服务器打的通讯包，完成协议本来要完成的功能。现在的协议是v5，也就是Scok5协议。使用Scok5协议的代理服务器即称为Sock5代理。用户可以选择哪些域名可以绕过代理。

```cmd
1、VPN主要有PPTP，L2TP，IPSEC，SSL等几种VPN技术。
    PPTP:Point to Point Protocol Tunnel Protocol 
    L2TP: Layer 2 Tunnel Protocol
2、代理分为透明代理、匿名代理和高匿代理
透明代理：
    REMOTE_ADDR = Proxy IP
    HTTP_VIA = Proxy IP
    HTTP_X_FORWARDED_FOR = Your IP
    透明代理虽然可以直接“隐藏”你的IP地址，但是还是可以从HTTP_X_FORWARDED_FOR来查到你是谁。
匿名代理(Anonymous Proxy)
    REMOTE_ADDR = proxy IP
    HTTP_VIA = proxy IP
    HTTP_X_FORWARDED_FOR = proxy IP
    匿名代理比透明代理进步了一点：别人只能知道你用了代理，无法知道你是谁。还有一种混淆代理是：HTTP_X_FORWARDED_FOR = Random IP address，使用假IP伪装。
高匿代理(Elite proxy或High Anonymity Proxy)
    REMOTE_ADDR = Proxy IP
    HTTP_VIA = not determined
    HTTP_X_FORWARDED_FOR = not determined
    可以看出来，高匿代理让别人根本无法发现你是在用代理，所以是最好的选择。
```
# 内网穿透 - Ngrok
参考[使用Docker搭建Ngrok服务器](https://hteen.cn/docker/docker-ngrok.html)
> 由于网站停止服务，使用docker搭建ngrok暂无法使用
```bash
# 服务端：
# 启动一个容器生成ngrok客户端,服务器端和CA证书
> docker run --rm -it -e DOMAIN="tunnel.hteen.cn" -v /data1/ngrok:/myfiles hteen/ngrok /bin/bash /build.sh
# 启动Ngrok server
> docker run -idt --name ngrok-server \
-v /data1/ngrok:/myfiles \
-p 80:80 \
-p 443:443 \
-p 4443:4443 \
-e DOMAIN='tunnel.hteen.cn' hteen/ngrok /bin/bash /server.sh
# 如果端口被占用，使用nginx代理
server {
     listen       80;
     server_name  tunnel.hteen.cn *.tunnel.hteen.cn;
     location / {
             proxy_redirect off;
             proxy_set_header Host $host;
             proxy_set_header X-Real-IP $remote_addr;
             proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
             proxy_pass http://10.24.198.241:8082;
     }
}
server {
     listen       443;
     server_name  tunnel.hteen.cn *.tunnel.hteen.cn;
     location / {
             proxy_redirect off;
             proxy_set_header Host $host;
             proxy_set_header X-Real-IP $remote_addr;
             proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
             proxy_pass http://10.24.198.241:4432;
     }
}

# 客户端：
# 创建配置文件ngrok.cfg
server_addr: "tunnel.hteen.cn:4443"
trust_host_root_certs: false
# 启动ngrok
./ngrok -config ./ngrok.cfg -subdomain wechat 80

```

## OpenVPN
具体参考[DockerHub-kylemanna/openvpn](https://hub.docker.com/r/kylemanna/openvpn/)
```bash
# 使用docker镜像
> docker volume create --name $OVPN_DATA
> docker run -v $OVPN_DATA:/etc/openvpn --rm kylemanna/openvpn ovpn_genconfig -u udp://VPN.SERVERNAME.COM
> docker run -v $OVPN_DATA:/etc/openvpn --rm -it kylemanna/openvpn ovpn_initpki
```
```cmd
# 客户端配置
1、在config目录下添加pass.txt
userName
password
2、在config目录下修改.opvn文件添加
auth-user-pass pass.txt
3、快捷方式"目标"后添加
--connect client.ovpn
```

## 代理-Socket5
具体参考 [自建SS服务器教程](https://app.yinxiang.com/shard/s48/nl/13169588/64ea6fe0-bc46-40da-9392-be395eea44c6)
```bash
# 使用ShadowSocksR代理
# 获取自动安装配置脚本
> wget -N --no-check-certificate https://softs.fun/Bash/ssr.sh && chmod +x ssr.sh && bash ssr.sh
```

# 水平/垂直扩展
## 一致性hash算法
参考[一致性hash算法](https://yikun.github.io/2016/06/09/%E4%B8%80%E8%87%B4%E6%80%A7%E5%93%88%E5%B8%8C%E7%AE%97%E6%B3%95%E7%9A%84%E7%90%86%E8%A7%A3%E4%B8%8E%E5%AE%9E%E8%B7%B5/)
```
[0-2^32]环形，hash后顺时针找下一个
问题：1、服务节点分布不均匀导致节点分布不均匀
解决：1、添加若干虚节点,虚节点映射到服务节点(两次映射，虚节点增加仍然会产生数据较大量移动)
     2、参考OpenStack的Swift组件中，使用了一种比较特殊的方法来解决分布不均的问题，改进了这些数据分布的算法，将环上的空间均匀的映射到一个线性空间，这样，就保证分布的均匀性。
```
![png](https://cloud.githubusercontent.com/assets/1736354/16341297/fe155f98-3a5e-11e6-834d-193e6f85afcd.png)\
![png](http://afghl.github.io/images/consistent-hash(3).jpeg)\
(普通hash算法)
![png](http://afghl.github.io/images/consistent-hash(5).jpg)
(采用虚节点后映射方式)
![png](https://cloud.githubusercontent.com/assets/1736354/16341455/b01139ec-3a5f-11e6-965a-070f5c4c0afa.png)
(线性映射)

## 分库分表
参考[中间件MyCat](https://www.jianshu.com/p/cd23e6ef9305)


## 消息队列MQ(略)

## 分布式事务(略)

## 集群资源调度(略)