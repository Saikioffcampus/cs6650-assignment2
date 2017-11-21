package com.cs6650.assignment2.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import bsdsass2testdata.RFIDLiftData;

/**
 * Created by saikikwok on 23/10/2017.
 */
public class BackgroundMessengerManager implements ServletContextListener{

    public static ConcurrentLinkedQueue<RFIDLiftData> messageQueue = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<String> sqsMessageQueue = new ConcurrentLinkedQueue<>();
    private static int THREAD_NUM = 20;
    private static int SQS_MSG_THREAD_NUM = 5;
    private LinkedList<MessengerThread> threadPool = new LinkedList<>();
    public static String hostname;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "unknown";
        }
        System.out.println("messenger threads are requesting...");
        for (int i = 0; i < THREAD_NUM; i++) {
            BackgroundMessenger r = new BackgroundMessenger();
            threadPool.add(r);
            r.start();
        }
        System.out.println("messenger threads requesting done...");
        System.out.println("sqs messenger threads are requesting...");
        for (int i = 0; i < SQS_MSG_THREAD_NUM; i++) {
            PerformanceMessenger r = new PerformanceMessenger();
            threadPool.add(r);
            r.start();
        }
        System.out.println("messenger threads requesting done...");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        for (int i = 0; i < THREAD_NUM + SQS_MSG_THREAD_NUM; i++) {
            threadPool.get(i).setDone();
        }
        ConnectionFactory.closeDatasource();
    }


}
