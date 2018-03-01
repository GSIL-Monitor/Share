# coding=utf-8
# version:python-2.7
# 具体指标使用wind-量化-代码生成器进行获取
import numpy as np
import pandas as pd
from WindPy import *
from sqlalchemy import create_engine

# 获取指定日期基金净值数据
db_connect_string = 'mysql://betaWR:betaWR123@10.18.0.2:3306/beta_psbc?charset=utf8'
engine = create_engine(db_connect_string)

raw_data = w.wsd('000001.OF', "nav,NAV_adj", '2017-01-01', '2018-01-01', "")
w.start()
with engine.connect() as conn:
    if not raw_data.ErrorCode:
        df = pd.DataFrame(index=raw_data.Times, columns=raw_data.Fields,
                          data=np.array(raw_data.Data).transpose())
        df.to_sql("fund_calculate", conn, if_exists="append", index=False)
