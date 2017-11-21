package com.cs6650.assignment2.server;

import java.sql.SQLException;
import java.util.LinkedList;

import bsdsass2testdata.RFIDLiftData;

/**
 * Created by saikikwok on 23/10/2017.
 */
public class BackgroundMessenger extends MessengerThread{

    protected final int BATCH_SIZE = 20;

    public void run() {
        while (this.isActive) {
            LinkedList<RFIDLiftData> dataset = gatherDataSet();
            if (dataset.size() > 0) {
                RecordDAO dao = new RecordDAO();
                try {
                    dao.insertRecord(dataset);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    BackgroundMessengerManager
                            .sqsMessageQueue
                            .offer("hostname: " + BackgroundMessengerManager.hostname +
                                    "; start_time: " + System.currentTimeMillis() +
                                    "; failed: " + e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    private LinkedList<RFIDLiftData> gatherDataSet() {
        LinkedList<RFIDLiftData> dataset = new LinkedList<RFIDLiftData>();
        long t = System.currentTimeMillis();
        long end = t + INTERVAL;
        while (System.currentTimeMillis() < end && dataset.size() < BATCH_SIZE) {
            RFIDLiftData data = BackgroundMessengerManager.messageQueue.poll();
            if (data == null) {
                try {
                    Thread.sleep(INTERVAL);
                    continue;
                } catch (InterruptedException e) {
                    BackgroundMessengerManager
                            .sqsMessageQueue
                            .offer("hostname: " + BackgroundMessengerManager.hostname +
                                    "; start_time: " + System.currentTimeMillis() +
                                    "; failed: " + e.toString());
                    e.printStackTrace();
                }
            }
            dataset.add(data);
        }
        return dataset;
    }
}
