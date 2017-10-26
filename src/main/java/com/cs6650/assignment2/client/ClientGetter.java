package com.cs6650.assignment2.client;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;


/**
 * Created by saikikwok on 24/10/2017.
 */
public class ClientGetter implements Runnable {
    private String urlBase;
    private String path = "/myvert";
    private int barNum;
    private int numServe;
    private int dayNum;
    private CyclicBarrier barrier;
    private javax.ws.rs.client.Client client;
    private ConcurrentLinkedQueue<Long> latencies;

    public ClientGetter(final String urlBase,
                        final int barNum,
                        final int numServe,
                        final int dayNum,
                        final CyclicBarrier barrier,
                        final ConcurrentLinkedQueue<Long> latencies) {
        this.urlBase = urlBase;
        this.barNum = barNum;
        this.dayNum = dayNum;
        this.numServe = numServe;
        this.barrier = barrier;
        this.client = ClientBuilder.newClient();
        this.latencies = latencies;
    }

    public void run() {
        try {
            for (int i = 1; i < this.numServe; i++) {
                int skierId = this.barNum * this.numServe + i;
                UriBuilder builder = UriBuilder.fromPath(this.urlBase).path(this.path);
                builder.queryParam("dayNum", this.dayNum);
                builder.queryParam("skierID", skierId);
                long start = System.currentTimeMillis();
                Response r = client.target(builder.build())
                        .request()
                        .accept(MediaType.APPLICATION_JSON)
                        .get();
                JSONObject obj = (JSONObject) new JSONParser().parse(r.readEntity(String.class));
                r.close();
                long latency = System.currentTimeMillis() - start;
                this.latencies.offer(latency);
            }
            this.barrier.await();
            this.client.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
