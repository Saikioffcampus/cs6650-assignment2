package com.cs6650.assignment2.server;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.partitions.model.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by saikikwok on 18/11/2017.
 */
public class PerformanceMessenger extends MessengerThread {

    private static String QUEUE_NAME = "cs6650-server-performace-queue";
    private static String aws_access_key_id = "";
    private static String aws_secret_access_key = "";
    private AmazonSQS sqsClient;
    private String queueUrl = "";
    private static final int BATCH_SIZE = 10;


    public PerformanceMessenger() {
        this.sqsClient = buildClient();
        this.createQueue();
    }

    public void run() {
        while (this.isActive) {
            LinkedList<SendMessageBatchRequestEntry> dataset = gatherDataSet();
            if (dataset.size() > 0) {
                SendMessageBatchRequest send_batch_request = new SendMessageBatchRequest()
                        .withQueueUrl(queueUrl)
                        .withEntries(dataset);
                this.sqsClient.sendMessageBatch(send_batch_request);
            }
        }
    }

    private LinkedList<SendMessageBatchRequestEntry> gatherDataSet() {
        LinkedList<SendMessageBatchRequestEntry> dataset = new LinkedList<>();
        long t = System.currentTimeMillis();
        long end = t + INTERVAL;
        Integer id = 1;
        while (System.currentTimeMillis() < end && dataset.size() < BATCH_SIZE) {
            String data = BackgroundMessengerManager.sqsMessageQueue.poll();
            if (data == null) {
                try {
                    Thread.sleep(INTERVAL);
                    continue;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dataset.add(new SendMessageBatchRequestEntry(id.toString(), data));
            id++;
        }
        return dataset;
    }

    private AmazonSQS buildClient() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(aws_access_key_id, aws_secret_access_key);
        return AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.US_WEST_2).build();
    }

    private String createQueue() {
        // Creating a Queue
        CreateQueueRequest create_request = new CreateQueueRequest(QUEUE_NAME)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        String myQueueUrl = "";
        try {
            myQueueUrl = this.sqsClient.createQueue(create_request).getQueueUrl();
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }
        this.queueUrl = myQueueUrl;
        return myQueueUrl;
    }


}
