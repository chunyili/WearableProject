package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import data.MongoConnection;
import org.bson.Document;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import resource.ClientObject;
import resource.ObjInstance;
import resource.Resource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;



/**
 * Created by jianxin on 6/25/15.
 */

@Path("/server")
public class Server {



    @Path("/rd")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput respondToRegisterDevice (String test, @QueryParam("ep")final String endPoint,@QueryParam("lt")long lt,@QueryParam("sms") String sms
            ,@QueryParam("lwm2m") String lwm2mVersion,@QueryParam("b")String bindingMode
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
       final ClientObject clientObject = mapper.readValue(test, ClientObject.class);


        final Datastore ds = MongoConnection.getServer();


        final Query<ClientObject> query = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", endPoint);
        final List<ClientObject> clientObjects = query
                .asList();


        final EventOutput eventOutput = new EventOutput();


            Thread rdThread = new Thread(new Runnable() {
                public void run() {
                    try {


                        final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
                        if (clientObjects.size() == 0){
                            ds.save(clientObject);

                            eventBuilder.name("register-message-to-client");
                            eventBuilder.data(String.class, "Register success!");
                            final OutboundEvent event = eventBuilder.build();
                            eventOutput.write(event);



                        }else{
                            ds.updateFirst(query, clientObject,true);

                            eventBuilder.name("register-message-to-client");
                            eventBuilder.data(String.class, "Update!");
                            final OutboundEvent event = eventBuilder.build();
                            eventOutput.write(event);
                        }
                        while(true) {

                            final OutboundEvent.Builder eventBuilder1 = new OutboundEvent.Builder();

                            eventBuilder1.name("register-message-to-client");
                            //wait for a signal
                            CountDownLatch signal = new CountDownLatch(1);
                            Info.setCountDownLatchMap(endPoint,signal);


                            signal.await();
                            System.out.println(endPoint);
                            System.out.println("hello");
                            eventBuilder1.data(String.class, Info.getInfo(endPoint));
                            final OutboundEvent event = eventBuilder1.build();
                            eventOutput.write(event);

                            signal = new CountDownLatch(1);
                            Info.setCountDownLatchMap(endPoint,signal);

//                                Info.setInfo(endPoint, null);



                        }



                    } catch (IOException e) {
                        throw new RuntimeException(
                                "Bad Request", e);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            eventOutput.close();
                        } catch (IOException ioClose) {
                            throw new RuntimeException(
                                    "Error when closing the event output.", ioClose);
                        }
                    }
                }
            });

            rdThread.start();




        return eventOutput;
    }




    @Path("/read")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response readResponse(String input) throws Exception {
        JSONObject test=new JSONObject(input);
        Datastore ds = MongoConnection.getServer();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", test.getString("client_bs_obj.device_id"))
                .asList();
        Resource resourceO = clientObjects.get(0).getObjectMap().get(test.getInt("ObjectId")).getObjInstanceMap().get(test.getInt("ObjectInsId"))
                .getResourceMap().get(test.getInt("resourceId"));
        ObjectMapper mapper = new ObjectMapper();
        Resource resourceN = mapper.readValue(test.getString("value"), Resource.class);

       resourceO.getValue().putAll(resourceN.getValue());

        ds.save(clientObjects.get(0));


        CountDownLatch signalRead = Info.getCountDownMessage(test.getString("client_bs_obj.device_id"));
        signalRead.countDown();

        return Response.status(200).entity("success").build();

    }


    @Path("/read/{ObjID}/{ObjInsId}/{resourceId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendRead(@PathParam("ObjID") Integer objectId,@PathParam("ObjInsId") Integer objectInstanceId
                            ,@PathParam("resourceId") Integer resourceID,@QueryParam("ep") String endPoint) throws Exception {
        String directory = "{"+"\"operation\":\"read\""+","+"\""+"directory\":"+"\""+objectId+"/"+objectInstanceId+"/"+resourceID+"\"}";
        Info.setInfo(endPoint,directory);
        CountDownLatch signal = Info.getCountDown(endPoint);
        if(signal == null){
            return Response.status(200).entity("Devices not registerd yet").build();
        }
        signal.countDown();

        CountDownLatch signalRead =  new CountDownLatch(1);
        Info.setCountDownLatchMessageMap(endPoint, signalRead);
        signalRead.await();

        Datastore ds = MongoConnection.getServer();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", endPoint)
                .asList();

        JSONArray jsonArray = new JSONArray();
        if( objectInstanceId== 0){

        Map<Date, Double> hashMap;
        hashMap = clientObjects.get(0)
                .getObjectMap()
                .get(objectId)
                .getObjInstanceMap()
                .get(objectInstanceId)
                .getResourceMap()
                .get(resourceID).getValue();
        Map<Date, Double> sortedMap = new TreeMap<Date,Double>(hashMap);

        Iterator it = sortedMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            JSONArray jsonArray1 = new JSONArray();
            Date date =(Date) pair.getKey();
            jsonArray1.put(date.getTime()).put(pair.getValue());
            jsonArray.put(jsonArray1);
            it.remove(); // avoids a ConcurrentModificationException
        }}else if (objectInstanceId == 1){

            Map<Date, Integer> hashMap;
            hashMap = clientObjects.get(0).getObjectMap().get(objectId).getObjInstanceMap().get(objectInstanceId).getResourceMap()
                    .get(resourceID).getMvalue();
            Map<Date, Integer> sortedMap = new TreeMap<Date,Integer>(hashMap);
            Iterator it = sortedMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                JSONArray jsonArray1 = new JSONArray();
                Date date =(Date) pair.getKey();

                jsonArray1.put(date.getTime()).put(pair.getValue());
                jsonArray.put(jsonArray1);
                it.remove(); // avoids a ConcurrentModificationException
            }

        }








        return Response.status(200).entity(jsonArray.toString()).build();



    }

    @Path("/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteResponse(String input) throws Exception {
        JSONObject inputObj = new JSONObject(input);
        Datastore ds = MongoConnection.getServer();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", inputObj.getString("client_bs_obj.device_id"))
                .asList();

        ClientObject clientObject = clientObjects.get(0);
        Map<Integer,ObjInstance> objInstanceMap = clientObject.getObjectMap().get(inputObj.getInt("ObjectId"))
                .getObjInstanceMap();
        objInstanceMap.remove(inputObj.getInt("ObjectInsId"));
        ds.save(clientObject);



        CountDownLatch signalRead = Info.getCountDownMessage(inputObj.getString("client_bs_obj.device_id"));
        signalRead.countDown();

        return Response.status(200).entity(inputObj.getString("message")).build();


    }




    @Path("/delete/{ObjID}/{ObjInsId}")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)

    public Response sendDelete(@PathParam("ObjID") String objectId,@PathParam("ObjInsId") String objectInstanceId
            ,@QueryParam("ep") String endPoint) throws InterruptedException {


        String string = objectId+"/"+objectInstanceId;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation","delete");
        jsonObject.addProperty("directory",string);


        Info.setInfo(endPoint,jsonObject.toString());
        CountDownLatch signal = Info.getCountDown(endPoint);
        if(signal == null){
            return Response.status(200).entity("Devices not registerd yet").build();
        }
        signal.countDown();
        System.out.println("hello11111");
        CountDownLatch signalRead =  new CountDownLatch(1);
        Info.setCountDownLatchMessageMap(endPoint, signalRead);
        signalRead.await();

        return Response.status(200).entity("delete success").build();
    }


    @Path("/discover")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response discoverResponse(String input) throws Exception {

        JSONObject inputObj = new JSONObject(input);
        String value="pmin:"+inputObj.getString("pmin")+"; pmax:"+inputObj.getString("pmax");



        CountDownLatch signalRead = Info.getCountDownMessage(inputObj.getString("client_bs_obj.device_id"));
        signalRead.countDown();


        return Response.status(200).entity(inputObj.getString("message") + "; " + value).build();


    }

    @Path("/discover/{ObjID}/{ObjInsId}/{resourceId}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendDiscover(@PathParam("ObjID") String objectId,@PathParam("ObjInsId") String objectInstanceId
            ,@PathParam("resourceId") String resourceID,@QueryParam("ep") String endPoint) throws Exception {
        String directory = "{\"operation\":\"discover\""+","+"\""+"directory\":"+"\""+objectId+"/"+objectInstanceId+"/"+resourceID+"\"}";
        Info.setInfo(endPoint,directory);
        CountDownLatch signal = Info.getCountDown(endPoint);
        if(signal == null){
            return Response.status(200).entity("Devices not registerd yet").build();
        }
        signal.countDown();


        CountDownLatch signalRead =  new CountDownLatch(1);
        Info.setCountDownLatchMessageMap(endPoint, signalRead);
        signalRead.await();

        Datastore ds = MongoConnection.getServer();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", endPoint)
                .asList();
        resource.Object obj = clientObjects.get(0).getObjectMap().get(Integer.parseInt(objectId));
        String message = "discover success! "+ "pmin:"+obj.getPmin()+" pmax:"+obj.getPmax();



        return Response.status(200).entity(message).build();


    }


    @Path("/write")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response writeResponse(String input) throws Exception {

        JSONObject inputObj = new JSONObject(input);
        Datastore ds = MongoConnection.getServer();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", inputObj.getString("client_bs_obj.device_id"))
                .asList();

        ClientObject clientObject = clientObjects.get(0);
        Resource resource1 = clientObject.getObjectMap().get(inputObj.getInt("ObjectId"))
                .getObjInstanceMap().get(inputObj.getInt("ObjectInsId"))
                .getResourceMap().get(inputObj.getInt("resourceId"));
        resource1.setS_value(inputObj.getString("s_value"));
        ds.save(clientObject);



        CountDownLatch signalRead = Info.getCountDownMessage(inputObj.getString("client_bs_obj.device_id"));
        signalRead.countDown();

        return Response.status(200).entity(inputObj.getString("message")).build();


    }

    @Path("/write/{ObjID}/{ObjInsId}/{resourceId}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendWrite(@PathParam("ObjID") String objectId,@PathParam("ObjInsId") String objectInstanceId
            ,@PathParam("resourceId") String resourceID,@QueryParam("ep") String endPoint, String value) throws InterruptedException {
        String directory = "{\"operation\":\"write\""+","+"\""+"directory\":"+"\""+objectId+"/"+objectInstanceId+"/"+resourceID+"\""
                +","+"\""+"value\":"+ "\""+value+"\""+"}";
        Info.setInfo(endPoint,directory);
        CountDownLatch signal = Info.getCountDown(endPoint);
        if(signal == null){
            return Response.status(200).entity("Devices not registerd yet").build();
        }
        signal.countDown();

        CountDownLatch signalRead =  new CountDownLatch(1);
        Info.setCountDownLatchMessageMap(endPoint, signalRead);
        signalRead.await();

        return Response.status(200).entity("write success").build();
    }

    @Path("/writeAttribute")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response writeAttributeResponse(String input) throws Exception {

        JSONObject inputObj = new JSONObject(input);
        Datastore ds = MongoConnection.getServer();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", inputObj.getString("client_bs_obj.device_id"))
                .asList();

        ClientObject clientObject = clientObjects.get(0);
        resource.Object obj = clientObject.getObjectMap().get(inputObj.getInt("ObjectId"));
        obj.setPmin(inputObj.getJSONObject("value").getString("pmin"));
        obj.setPmax(inputObj.getJSONObject("value").getString("pmax"));
        obj.setLessThan(inputObj.getJSONObject("value").getString("lt"));
        obj.setGreaterThan(inputObj.getJSONObject("value").getString("gt"));
        obj.setStep(inputObj.getJSONObject("value").getString("st"));
        ds.save(clientObject);



        CountDownLatch signalRead = Info.getCountDownMessage(inputObj.getString("client_bs_obj.device_id"));
        signalRead.countDown();

        return Response.status(200).entity(inputObj.getString("message")).build();


    }

    @Path("/writeAttribute/{ObjID}/{ObjInsId}/{resourceId}")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendWriteAttribute(@PathParam("ObjID") String objectId,@PathParam("ObjInsId") String objectInstanceId
            ,@PathParam("resourceId") String resourceID,@QueryParam("ep") String endPoint
            ,@QueryParam("pmin") String pmin,@QueryParam("pmax") String pmax,@QueryParam("gt") String gt
            ,@QueryParam("lt") String lt, @QueryParam("st") String st) throws InterruptedException {

        String string = objectId+"/"+objectInstanceId+"/"+resourceID;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation", "writeAttribute");
        jsonObject.addProperty("directory", string);
        JsonObject value = new JsonObject();
        if(pmin != null){value.addProperty("pmin", pmin);}
        if(pmax != null){value.addProperty("pmax", pmax);}
        if(gt != null){value.addProperty("gt", gt);}
        if(lt != null){value.addProperty("lt", lt);}
        if(st != null){value.addProperty("st", st);}
        jsonObject.addProperty("value",value.toString());


//        String directory = "{\"operation\":\"discover\""+","+"\""+"directory\":"+"\""+objectId+"/"+objectInstanceId+"/"+resourceID+"\"}";
        Info.setInfo(endPoint,jsonObject.toString());
        CountDownLatch signal = Info.getCountDown(endPoint);
        if(signal == null){
            return Response.status(200).entity("Devices not registered yet").build();
        }
        signal.countDown();

        CountDownLatch signalRead =  new CountDownLatch(1);
        Info.setCountDownLatchMessageMap(endPoint, signalRead);
        signalRead.await();



        return Response.status(200).entity("writeAttribute success").build();


    }

    @Path("/execute/{ObjID}/{ObjInsId}/{resourceId}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendExecute(@PathParam("ObjID") String objectId,@PathParam("ObjInsId") String objectInstanceId
            ,@PathParam("resourceId") String resourceID,@QueryParam("ep") String endPoint, String value) throws InterruptedException {

        String string = objectId+"/"+objectInstanceId+"/"+resourceID;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation", "execute");
        jsonObject.addProperty("directory", string);
        jsonObject.addProperty("value",value);

        Info.setInfo(endPoint, jsonObject.toString());
        CountDownLatch signal = Info.getCountDown(endPoint);
        if(signal == null){
            return Response.status(200).entity("Devices not registered yet").build();
        }
        signal.countDown();

        CountDownLatch signalRead =  new CountDownLatch(1);
        Info.setCountDownLatchMessageMap(endPoint, signalRead);
        signalRead.await();
        String temp = Info.getClientSendToServerInfoMap(endPoint);
        Double result= Double.valueOf(temp);
        Double result1= Double.valueOf(String.format("%.2f",result));
        String temp1 = String.valueOf(result1);


        return Response.status(200).entity("execution success！The temperature is "+ temp1).build();


    }

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createResponse(String input) throws Exception {

        JSONObject inputObj = new JSONObject(input);
        Datastore ds = MongoConnection.getServer();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", inputObj.getString("client_bs_obj.device_id"))
                .asList();

        ClientObject clientObject = clientObjects.get(0);
        Map<Integer,ObjInstance> objInstanceMap = clientObject.getObjectMap().get(inputObj.getInt("ObjectId"))
                .getObjInstanceMap();
        Map<Integer,Resource> resourceMap = objInstanceMap.get(inputObj.getInt("ObjectInsId")).getResourceMap();
        ObjInstance objInstance =new ObjInstance();
        objInstance.setObjInstance_id(inputObj.getInt("ObjectInsId")+1);
        Resource resource = new Resource();
        resource.setRecourse_id(0);
        resource.setName(inputObj.getString("value"));
        Map<Integer,Resource> resourceMapNew=objInstance.getResourceMap();
        resourceMapNew.put(0,resource);
        objInstanceMap.put(objInstance.getObjInstance_id(),objInstance);

        //Map<Integer,Resource> resourceMap = objInstanceMap.get(ObjectInsId).getResourceMap();

        ds.save(clientObject);



        CountDownLatch signalRead = Info.getCountDownMessage(inputObj.getString("client_bs_obj.device_id"));
        signalRead.countDown();

        return Response.status(200).entity(inputObj.getString("message")).build();


    }

    @Path("/create/{ObjID}/{ObjInsId}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendCreate(@PathParam("ObjID") String objectId,@PathParam("ObjInsId") String objectInstanceId
          ,@QueryParam("ep") String endPoint, String value) throws InterruptedException {

        String string = objectId+"/"+objectInstanceId;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation","create");
        jsonObject.addProperty("directory",string);
        jsonObject.addProperty("value",value);

        Info.setInfo(endPoint,jsonObject.toString());
        CountDownLatch signal = Info.getCountDown(endPoint);
        if(signal == null){
            return Response.status(200).entity("Devices not registered yet").build();
        }
        signal.countDown();


        CountDownLatch signalRead =  new CountDownLatch(1);
        Info.setCountDownLatchMessageMap(endPoint, signalRead);
        signalRead.await();
        return Response.status(200).entity("creation success").build();


    }


    @Path("/observe/{ObjID}/{ObjInsId}/{resourceId}")
    @GET
    public Response sendObserve(@PathParam("ObjID") String objectId,@PathParam("ObjInsId") String objectInstanceId
            ,@PathParam("resourceId") String resourceID,@QueryParam("ep") String endPoint) {
        String directory = "{\"operation\":\"observe\""+","+"\""+"directory\":"+"\""+objectId+"/"+objectInstanceId+"/"+resourceID+"\"}";
        Info.setInfo(endPoint,directory);
        CountDownLatch signal = Info.getCountDown(endPoint);
        if(signal == null){
            return Response.status(200).entity("Devices not registerd yet").build();
        }
        signal.countDown();
        return Response.status(200).entity("observe success").build();


    }
    @Path("/cancelObserve/{ObjID}/{ObjInsId}/{resourceId}")
    @GET
    public Response sendObservecCancel(@PathParam("ObjID") String objectId,@PathParam("ObjInsId") String objectInstanceId
            ,@PathParam("resourceId") String resourceID,@QueryParam("ep") String endPoint) {
//        String directory = "{\"operation\":\"observe\""+","+"\""+"directory\":"+"\""+objectId+"/"+objectInstanceId+"/"+resourceID+"\"}";

        String string = objectId+"/"+objectInstanceId+"/"+resourceID;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("operation","cancelObserve");
        jsonObject.addProperty("directory",string);
        Info.setInfo(endPoint, jsonObject.toString());
        CountDownLatch signal = Info.getCountDown(endPoint);
        if(signal == null){
            return Response.status(200).entity("Devices not registerd yet").build();
        }
        signal.countDown();
        return Response.status(200).entity("cancelObserve success").build();


    }


    @Path("/clientObjects")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendObservecCancel() throws Exception {




        MongoCollection<Document> document =  MongoConnection.getServerSideCLientObject();
        List<Document> foundDocument = document.find().into(new ArrayList<Document>());
        JSONArray jsonArray = new JSONArray(foundDocument);



        return Response.status(200).entity(jsonArray.toString()).build();


    }



    @Path("/execute")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendExecute(String result) throws  Exception{
        JSONObject jsonObject = new JSONObject(result);
        String device_id = jsonObject.getString("client_bs_obj.device_id");

        CountDownLatch signalRead = Info.getCountDownMessage(jsonObject.getString("client_bs_obj.device_id"));
        signalRead.countDown();


        return Response.status(200).entity("success").build();

    }









}
