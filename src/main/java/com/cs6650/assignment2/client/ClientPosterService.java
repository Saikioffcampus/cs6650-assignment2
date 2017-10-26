package com.cs6650.assignment2.client;

import org.json.simple.parser.ParseException;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by saikikwok on 21/10/2017.
 */
public class ClientPosterService {

    private final static int ARGV_LENTH = 4;
    private final static int THREAD_NUM = 0;
    private final static int IP = 2;
    private final static int PORT = 3;
    private final static int DEFAULT_THREAD_NUM = 150;
    private final static String DEFAULT_IP = "34.213.182.195";
    private final static String DEFAULT_PORT = "8080";
    private final static ConcurrentLinkedQueue<Long> latencies = new ConcurrentLinkedQueue<Long>();

    public static void main(String[] argv) throws ParseException {
    // Please, do not remove this line from file template, here invocation of web service will be inserted

        int threadNum = DEFAULT_THREAD_NUM;
        String ip = DEFAULT_IP;
        String port = DEFAULT_PORT;
        if (argv.length == ARGV_LENTH) {
            threadNum = Integer.parseInt(argv[THREAD_NUM]);
            ip = argv[IP];
            port = argv[PORT];
        }
        String urlBase = "http://" + ip + ":" + port + "/cs6650-assignment2_war/rest/";
//        String urlBase = "http://localhost:8080/rest/";

        ClientContentProducer producer = new ClientContentProducer();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadNum);
        CyclicBarrier barrier = new CyclicBarrier(threadNum + 1);
        System.out.println(threadNum + " threads are requesting...");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            threadPool.execute(new ClientPoster(urlBase, producer, barrier, latencies));
        }
        try {
            barrier.await();
            long wallTime = System.currentTimeMillis() - startTime;
            System.out.println("——————————————————————————————");
            System.out.println("Wall time of client poster service: " + wallTime + " ms");
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
