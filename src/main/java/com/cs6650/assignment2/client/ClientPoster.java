package com.cs6650.assignment2.client;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import bsdsass2testdata.RFIDLiftData;

/**
 * Created by saikikwok on 21/10/2017.
 */
public class ClientPoster implements Runnable{

    private String urlBase;
    private String path = "/load";
    private ClientContentProducer provider;
    private CyclicBarrier barrier;
    private javax.ws.rs.client.Client client;
    private ConcurrentLinkedQueue<Long> latencies;

    public ClientPoster(final String urlBase,
                        final ClientContentProducer provider,
                        final CyclicBarrier barrier,
                        final ConcurrentLinkedQueue<Long> latencies) {
        this.urlBase = urlBase;
        this.provider = provider;
        this.barrier = barrier;
        this.client = ClientBuilder.newClient();
        this.latencies = latencies;
    }

    public void run() {
        while (!provider.isEmpty()) {
            RFIDLiftData data = provider.consume();
            if (data != null) {
                UriBuilder builder = UriBuilder.fromPath(this.urlBase).path(this.path);
                builder.queryParam("resortID", data.getResortID());
                builder.queryParam("dayNum", data.getDayNum());
                builder.queryParam("timestamp", data.getTime());
                builder.queryParam("skierID", data.getSkierID());
                builder.queryParam("liftID", data.getLiftID());
                long start = System.currentTimeMillis();
                String b = client.target(builder.build())
                        .request(MediaType.TEXT_PLAIN)
                        .post(Entity.text(null), String.class);
                long latency = System.currentTimeMillis() - start;
                this.latencies.offer(latency);
            }
        }
        try {
            this.barrier.await();
            this.client.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}
