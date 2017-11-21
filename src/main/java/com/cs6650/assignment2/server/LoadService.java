package com.cs6650.assignment2.server;


import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import bsdsass2testdata.RFIDLiftData;

/**
 * Created by saikikwok on 21/10/2017.
 */
@Path("load")
public class LoadService {

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String load(@QueryParam("resortID") int resortID,
                       @QueryParam("dayNum") int dayNum,
                       @QueryParam("timestamp") int timestamp,
                       @QueryParam("skierID") int skierID,
                       @QueryParam("liftID") int liftID) throws Exception{

        Long start = System.currentTimeMillis();
        RFIDLiftData data = new RFIDLiftData(resortID, dayNum, skierID, liftID, timestamp);
        BackgroundMessengerManager.messageQueue.offer(data);
        Long response_time = System.currentTimeMillis() - start;
        BackgroundMessengerManager
                .sqsMessageQueue
                .offer("hostname: " + BackgroundMessengerManager.hostname +
                        "; method_type: post; start_time: " + start +
                        "; response_time: " + response_time.toString());
        return "ok";

    }

}
