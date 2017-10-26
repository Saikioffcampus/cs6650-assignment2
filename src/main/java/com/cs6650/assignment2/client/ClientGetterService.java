package com.cs6650.assignment2.client;

import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by saikikwok on 24/10/2017.
 */
public class ClientGetterService {

    private final static int ARGV_LENTH = 3;
    private final static int IP = 2;
    private final static int PORT = 3;
    private final static String DEFAULT_IP = "34.213.182.195";
    private final static String DEFAULT_PORT = "8080";
    private final static int NUM_BAR = 100;
    private final static int NUM_SKIER_SERVERED = 400;
    private final static ConcurrentLinkedQueue<Long> latencies = new ConcurrentLinkedQueue<>();

    public static void main(String[] argv) throws ParseException {
        // Please, do not remove this line from file template, here invocation of web service will be inserted
        String ip = DEFAULT_IP;
        String port = DEFAULT_PORT;
        if (argv.length == ARGV_LENTH) {
            ip = argv[IP];
            port = argv[PORT];
        }

        String urlBase = "http://" + ip + ":" + port + "/cs6650-assignment2_war/rest/";
//        String urlBase = "http://localhost:8080/rest";

        ExecutorService threadPool = Executors.newFixedThreadPool(NUM_BAR);
        CyclicBarrier barrier = new CyclicBarrier(NUM_BAR + 1);
        System.out.println(NUM_BAR + " threads are requesting...");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < NUM_BAR; i++) {
            threadPool.execute(new ClientGetter(urlBase, i, NUM_SKIER_SERVERED, 1, barrier, latencies));
        }
        try {
            barrier.await();
            long wallTime = System.currentTimeMillis() - startTime;
            System.out.println("——————————————————————————————");
            System.out.println("Wall time of client getter service: " + wallTime + " ms");
            System.out.println("——————————————————————————————");
            double[] stats = Util.parseLatency(Util.queue2List(latencies));
            System.out.println("Mean latency for all requests: " + stats[Util.MEAN] + " ms");
            System.out.println("Median latency for all requests: " + stats[Util.MEDIAN] + " ms");
            System.out.println("P95 latency for all requests: " + stats[Util.P95] + " ms");
            System.out.println("P99 latency for all requests: " + stats[Util.P99] + " ms");
            System.out.println("——————————————————————————————");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            threadPool.shutdown();
        }
    }

}
