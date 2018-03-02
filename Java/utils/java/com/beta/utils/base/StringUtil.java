package com.beta.utils.base;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    public static final String LINE_SEPARATOR = "\r\n";

    public static final String EMPTY = "";

    public static List<Integer> splitToInt(String str, String split) {
        String[] strArr = str.split(split);
        return Arrays.stream(strArr).filter(s -> !s.isEmpty()).map(s -> Integer.parseInt(s))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        System.out.println(splitToInt("", ","));
    }

}
