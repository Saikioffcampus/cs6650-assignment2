package com.cs6650.assignment2.server;

import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by saikikwok on 21/10/2017.
 */
@Path("myvert")
public class MyVertService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response myvert(@QueryParam("skierID") int skierID, @QueryParam("dayNum") int dayNum) throws Exception {
        RecordDAO dao = new RecordDAO();
        JSONObject ret = dao.getSkierDataBySkierIDAndDayNum(skierID, dayNum);
        return Response.status(200).entity(ret.toString()).build();
    }
}
