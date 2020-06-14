package com.hkey.summarystatistics;

import java.util.IntSummaryStatistics;
import java.util.stream.Stream;

public class SummaryStatisticsUse {
    public static void main(String[] args) {

    }

    /**
     * 这里注意一下，只有IntStream DoubleStream 等等基本数据类型的 stream才能获取 统计数据IntSummaryStatistics
     */
    public static void intSummary() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5, 6, 7);
        IntSummaryStatistics intSummaryStatistics = integerStream.mapToInt(u -> u).summaryStatistics();
        System.out.println(intSummaryStatistics.getMax());
        System.out.println(intSummaryStatistics.getMin());
        System.out.println(intSummaryStatistics.getCount());
        System.out.println(intSummaryStatistics.getAverage());
        System.out.println(intSummaryStatistics.getSum());
    }

}
