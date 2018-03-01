#!/usr/bin/env bash
1、使用搜狗开放数据库
	wget http://www.sogou.com/labs/sogoudownload/SogouCS/news_sohusite_xml.full.tar.gz --user asd@163.com --password }z094rIazNwe8h8k
2、文件解压
	tar -zxvf ./news_sohusite_xml.full.tar.gz ./
3、文件转码和获取<content></content>内容
	cat news_tensite_xml.dat | iconv -f gbk -t utf-8 -c | grep "<content>"  > corpus.txt 
4、安装python第三方插件
	1、jieba分词
		pip install jieba
	2、word2vec
		pip install word2vec
		#Linux会遇到一些问题需要安装cython和python-devel
		pip install cython
		yum install python-devel
5、进行分词
	```
	##!/usr/bin/env python
	## coding=utf-8
	import jieba

	filePath='corpus.txt'
	fileSegWordDonePath ='corpusSegDone.txt'
	# read the file by line
	fileTrainRead = []
	#fileTestRead = []
	with open(filePath) as fileTrainRaw:
		for line in fileTrainRaw:
			fileTrainRead.append(line)


	# define this function to print a list with Chinese
	def PrintListChinese(list):
		for i in range(len(list)):
			print list[i],
	# segment word with jieba
	fileTrainSeg=[]
	for i in range(len(fileTrainRead)):
		
		fileTrainSeg.append([' '.join(list(jieba.cut(fileTrainRead[i][9:-11],cut_all=False)))])
		#每分词100行，存储到文件
		if i % 100 == 0 :
			with open(fileSegWordDonePath,'a') as fW:
				for j in range(len(fileTrainSeg)):
					fW.write(fileTrainSeg.pop()[0].encode('utf-8'))
					fW.write('\n')
			

	# to test the segment result
	#PrintListChinese(fileTrainSeg[10])

	# save the result
	#with open(fileSegWordDonePath,'wb') as fW:
	#    for i in range(len(fileTrainSeg)):
	#        fW.write(fileTrainSeg[i][0].encode('utf-8'))
	#        fW.write('\n')
	```
6、用word2vec计算生成词向量
	```
	##!/usr/bin/env python
	## coding=utf-8
	import word2vec
	#size为生成的词向量维度
	word2vec.word2vec('corpusSegDone.txt', 'corpusWord2Vec.bin', size=100,verbose=True)
	```
7、查看词向量
	```
	import word2vec
	model = word2vec.load('corpusWord2Vec.bin')
	print (model.vectors) #查看词向量
	print (model.vocab[1000]) #查看词表中下标为1000的词
	#计算单词距离
	indexes = model.cosine(u'加拿大')
	indexes,metric = model.cosine(u'中国')
	for index in indexes[0]:
		print (model.vocab[index])
	```
8、生成近义词表
	#Java：使用wiki_chinese_word2vec(Google).model模型
	#Java：使用训练产生的word2vec.mod
9、使用neo4j生成知识图谱

10、使用卷积神经网络训练一个模型

11、使用循环神经网络训练一个模型

附：
使用matplotlib绘制pca降维后的词向量
```
#!/usr/bin/env python
# coding=utf-8
import numpy as np
import matplotlib
import matplotlib.pyplot as plt

from sklearn.decomposition import PCA
import word2vec
# load the word2vec model
model = word2vec.load('corpusWord2Vec.bin')
rawWordVec=model.vectors

# reduce the dimension of word vector
X_reduced = PCA(n_components=2).fit_transform(rawWordVec)

# show some word(center word) and it's similar words
index1,metrics1 = model.cosine(u'中国')
index2,metrics2 = model.cosine(u'清华')
index3,metrics3 = model.cosine(u'牛顿')
index4,metrics4 = model.cosine(u'自动化')
index5,metrics5 = model.cosine(u'刘亦菲')

# add the index of center word 
index01=np.where(model.vocab==u'中国')
index02=np.where(model.vocab==u'清华')
index03=np.where(model.vocab==u'牛顿')
index04=np.where(model.vocab==u'自动化')
index05=np.where(model.vocab==u'刘亦菲')

index1=np.append(index1,index01)
index2=np.append(index2,index03)
index3=np.append(index3,index03)
index4=np.append(index4,index04)
index5=np.append(index5,index05)

# plot the result
zhfont = matplotlib.font_manager.FontProperties(fname='/usr/share/fonts/truetype/wqy/wqy-microhei.ttc')
fig = plt.figure()
ax = fig.add_subplot(111)

for i in index1:
    ax.text(X_reduced[i][0],X_reduced[i][1], model.vocab[i], fontproperties=zhfont,color='r')

for i in index2:
    ax.text(X_reduced[i][0],X_reduced[i][1], model.vocab[i], fontproperties=zhfont,color='b')

for i in index3:
    ax.text(X_reduced[i][0],X_reduced[i][1], model.vocab[i], fontproperties=zhfont,color='g')

for i in index4:
    ax.text(X_reduced[i][0],X_reduced[i][1], model.vocab[i], fontproperties=zhfont,color='k')

for i in index5:
    ax.text(X_reduced[i][0],X_reduced[i][1], model.vocab[i], fontproperties=zhfont,color='c')

ax.axis([0,0.8,-0.5,0.5])
plt.show()
```
	
	
	