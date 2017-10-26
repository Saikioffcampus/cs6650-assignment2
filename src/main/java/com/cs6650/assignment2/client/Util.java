package com.cs6650.assignment2.client;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by saikikwok on 25/10/2017.
 */
public class Util {

    final static int MEAN = 0;
    final static int MEDIAN = 1;
    final static int P99 = 2;
    final static int P95 = 3;

    static double[] parseLatency(List<Long> requests) {
        Collections.sort(requests);
        double[] ans = new double[4];
        if (requests.size() == 0) {
            return ans;
        }
        int indexMedian = requests.size() / 2;
        int index99 = (int) Math.ceil(((99 / (double) 100) * (double) requests.size()));
        int index95 = (int) Math.ceil(((95 / (double) 100) * (double) requests.size()));
        ans[MEAN] = requests.stream().map(Object::toString)
                .mapToDouble(Double::parseDouble)
                .average().getAsDouble();
        if (requests.size() % 2 == 1) {
            ans[MEDIAN] = requests.get(indexMedian);
        } else {
            ans[MEDIAN] = (requests.get(indexMedian) + requests.get(indexMedian - 1)) / 2;
        }
        ans[P99] = requests.get(index99 - 1);
        ans[P95] = requests.get(index95 - 1);
        return ans;
    }

    public static List<Long> queue2List(ConcurrentLinkedQueue<Long> queue) throws IOException {
        List<Long> list = new LinkedList<>();
        list.addAll(queue);
        return list;
    }
}
