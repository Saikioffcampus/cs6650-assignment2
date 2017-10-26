package com.cs6650.assignment2.server;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import bsdsass2testdata.RFIDLiftData;

/**
 * Created by saikikwok on 23/10/2017.
 */
public class BackgroundMessengerManager implements ServletContextListener{

    public static ConcurrentLinkedQueue<RFIDLiftData> messageQueue = new ConcurrentLinkedQueue<RFIDLiftData>();
    private static int THREAD_NUM = 20;
    private LinkedList<BackgroundMessenger> threadPool = new LinkedList<BackgroundMessenger>();

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("messenger threads are requesting...");
        for (int i = 0; i < THREAD_NUM; i++) {
            BackgroundMessenger r = new BackgroundMessenger();
            threadPool.add(r);
            r.start();
        }
        System.out.println("messenger threads requesting done...");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        for (int i = 0; i < 10; i++) {
            threadPool.get(i).setDone();
        }
        ConnectionFactory.closeDatasource();
    }


}
