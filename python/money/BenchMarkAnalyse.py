import tushare as ts

df = ts.get_index()
df.to_csv('index.csv')
