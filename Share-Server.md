# Docker相关
## Docker
```cmd
# 查看已创建的容器相关配置
> docker inspect nginx
# 启动/停止/重启/运行 -- start/stop/restart/run
> docker restart nginx
# 创建和运行容器
> docker run --privileged=true -p80:80 -v /root/nginx_docker/www:/www -v /root/nginx.conf:/etc/nginx/nginx.conf --name nginx -d docker.io/nginx
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
### rsync
```cmd


```
## 文件下载
### wget
```cmd
# wget [url] --user [user] --password [passwd]
> wget http://wxp.betago2016.com

```

### aria2
```cmd

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
## 文件查找
```cmd
> df -lh # 磁盘占用
> free -lh # 内存占用
> ls -lh --sort size # 指定排序
> find / -size +100M #查找大于100MB的文件
> find -mtime -1 #扫描1天内的改动文件
> du . -h --max-depth=2 # 统计当前目录所有目录、文件大小，深度为2
> ps -ef | grep java # 查看进程信息
> ps -aux --sort %mem #根据内存排序
> top -s | grep java 查看进程信息
> kill -9 $(pidof 进程名关键字) #根据进程名称杀死进程
```
## 网络相关
```cmd
> netstat -tln #查看所有开放的端口  
> netstat -anlp | grep 8080 # 查看8080端口
> telnet 114.215.222.138 9999 #测试远程端口是否连通
> lsof -i tcp:80  #Centos查看端口占用情况命令，比如查看80端口占用情况使用如下命令：
```

## 压缩/解压
### tar
```cmd
Usage: tar [OPTION...] [FILE]...
# 压缩
> tar -cvzf temp.tar ./dic 
# 解压
tar -zxvf temp.tar
```
### zip
```cmd
Usage: unzip [-Z] [-opts[modifiers]] file[.zip] [list] [-x xlist] [-d exdir]
  Default action is to extract files in list, except those in xlist, to exdir;
  file[.zip] may be a wildcard.  -Z => ZipInfo mode ("unzip -Z" for usage).
Usage: zip [-options] [-b path] [-t mmddyyyy] [-n suffixes] [zipfile list] [-xi list]
# 压缩
zip -r mydata.zip mydata
# 解压
unzip temp.zip
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
```
# ElasticSearch
```cmd
# es curl查询
> curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{"query":{"bool":{"must":[{"term":{"question":"你好"}}],"must_not":[],"should":[]}},"from":0,"size":10,"sort":[],"aggs":{}}' 'http://10.18.0.9:9200/qa_bm/_search'
```


# JHipster

# Git
```cmd
# 区分远端分支和本地分支
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
