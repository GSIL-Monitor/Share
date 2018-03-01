# -*- coding: UTF-8 -*-
import urllib2

from lxml import etree

questionUrl = "https://wenda.so.com/c/125?pn=1"
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
print(questions)
