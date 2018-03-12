# coding:utf-8
from selenium import webdriver

from lxml import etree

questionUrl = "https://wenda.so.com/c/125?pn=1"
xpath = '/html/body/div[6]/div[2]/div/div[2]/div[2]/ul/li/div/p/a/text()'
driver = webdriver.PhantomJS()
driver.get(questionUrl)
for i in range(3):
    result_msg = driver.page_source
    selector = etree.HTML(result_msg)
    questions = selector.xpath(xpath)
    print(questions)
    # 点击下一页
    elem = driver.find_element_by_class_name('next')
    elem.click()
