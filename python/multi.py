# -*- coding: UTF-8 -*-
import json, sys, urllib2
from multiprocessing import Pool

import pandas as pd
from sqlalchemy import create_engine

reload(sys)
sys.setdefaultencoding('utf-8')

db_connect_string = 'mysql://betago:Bea71b3zc^as@10.18.100.11:3306/beta?charset=utf8'
engine = create_engine(db_connect_string)
url = 'http://wxp.betago2016.com/api/chat'


def get_ai_answer(df_param):
    for id in df_param['id']:
        try:
            question = df_param.loc[id]['question']
            data = {
                'question': question
            }
            headers = {'Content-Type': 'application/json'}
            request = urllib2.Request(url=url, headers=headers, data=json.dumps(data))
            response = urllib2.urlopen(request)
            result_msg = response.read()
            result_msg = result_msg.decode('UTF-8')
            result = json.loads(result_msg)
            ai_answer = result['data']['answer'][0]['content']['content']
            df_param.loc[id, 'ai_answer'] = ai_answer[0]
        except Exception as e:
            continue
    return df_param


def func(data):
    global results
    results = results.append(data)


def multi_task():
    global results, err_data
    with engine.connect() as conn:
        df = pd.read_sql("select id,question,answer from ai_question_answer limit 500", conn)
        df.index = df['id']
        process_size = 4  # 进程数量(默认cpu核数)
        pool = Pool(process_size)
        size = int(df.shape[0] / process_size) + 1
        results = pd.DataFrame()
        err_data = pd.DataFrame()
        for i in range(process_size):
            start = i * size
            sub_df = df.iloc[start:start + size][:]
            # map_async异步执行，io密集型可以用协程，计算密集型用进程
            result = pool.map_async(get_ai_answer, [sub_df], callback=func)
        pool.close()
        pool.join()
        results = results.sort_values(by='id')  # 排序
        results.to_excel('output.xls', index=False)
        err_data.to_excel('err.xls', index=False)
        print u'错误率：%f' % (err_data.shape[0] * 1.0 / df.shape[0])


if __name__ == "__main__":
    multi_task()
