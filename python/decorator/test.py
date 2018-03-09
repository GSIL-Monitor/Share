# coding=utf-8

import functools


def itcast1(fun):
    @functools.wraps(fun)
    def inner(*args, **kwargs):
        print("itcast1 start")

        result = fun(*args, **kwargs)

        print("itcast1 end")

        return 123

    return inner


@itcast1
def say_hello():
    return "haha"


val = say_hello()  # inner()
print val
