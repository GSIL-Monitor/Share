# -*- coding: UTF-8 -*-
import json
import sys
import urllib2
from datetime import datetime

import pandas as pd
from lxml import etree

reload(sys)
sys.setdefaultencoding('utf-8')

url = 'http://wxp.betago2016.com/api/chat'


def getQuestion(questionUrl):
    header = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36",
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"
    }
    xpath = '/html/body/div[6]/div[2]/div/div[2]/div[2]/ul/li/div/p/a/text()'
    request = urllib2.Request(url=questionUrl, headers=header)
    response = urllib2.urlopen(request)
    result_msg = response.read().decode('utf-8')
    selector = etree.HTML(result_msg)
    questions = selector.xpath(xpath)
    return questions


def chatTask(questions, userCookie):
    df = pd.DataFrame()
    for question in questions:
        data = {
            'question': question
        }
        headers = {'Content-Type': 'application/json',
                   "Cookie": userCookie
                   }
        request = urllib2.Request(url=url, headers=headers, data=json.dumps(data))
        response = urllib2.urlopen(request)
        result_msg = response.read()
        result_msg = result_msg.decode('UTF-8')
        data = json.loads(result_msg)
        result = {}
        # 获取答案
        answer = data['data']['answer']
        if answer:
            isAnswer = True
            answer = answer[0]['content']['content'][0]
        else:
            answer = ""
            isAnswer = False
            simQuestions = data['data']['suggest']["questions"]
            for simQuestion in simQuestions:
                answer += (str(simQuestion['showQuestion']) + " | ")
        result[u'问题'] = question
        # result[u'问题来源'] = ''
        # result[u'问题类型'] = ''
        result[u'机器人回答'] = answer
        result[u'是否应答'] = isAnswer
        df = df.append(df.from_dict(result, orient="index").T, ignore_index=True)
    fileName = datetime.today().strftime("%Y%m%d")
    df.to_html(fileName + ".html", index=False)
    df.to_csv(fileName + ".csv", index=False)


if __name__ == "__main__":
    # 问题来源(todo 修改问题网址,暂时仅限http://wenda.so.com内)
    questionUrl = "https://wenda.so.com/c/125?pn=1"
    # beta 用户标识cookie（todo 修改为自己的cookie）
    userCookie = "ACCESS_TOKEN=eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqViotTi3yTFGyMjI10lFKzkjMy0vNAfFNdJTyMpOz_RJzU5WslF7sXf9sxvynPa1P105Q0lFKT81LSS1SsjLUUUosSyxJBDKVMkpKCqz09csr9Apz8tPz9ZLz9HNz8wtS8_TLMuONjfTDM5NC_L0tDSpdAirSfdItHb0zk0r8jXMzkxxNIkLLkwxTI_KKi93KKnIN_IrccxKDMpND0wzM0osqozIMssw9TV2SDNxL_UMMLUssk5LDAoJzXHxSohwNQsv1DY2NlGoBAAAA__8.gc1kPt6r1BAVW2lNhcX-vQ9uyQGItdmGP6zgMetfMbQ; ACCESS_USER_ID=JzQHb3uckT8="
    questions = getQuestion(questionUrl)
    chatTask(questions, userCookie)

