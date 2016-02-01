package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.MongoConnection;
import data.MongoHelper;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.filter.LoggingFilter;
import org.mongodb.morphia.Datastore;
import resource.ClientObject;
import resource.ObjInstance;
import resource.Resource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created by jilongsun on 7/18/15.
 */
public class Handler {


    public static void parser(String inputData) throws Exception {
        System.out.println(inputData);
        if (inputData.equals("Update!")) {

        } else if (inputData.equals("Register success!")) {

        } else {
            JSONObject jsonObject = new JSONObject(inputData);
            int resourceId = -1;
            int ObjectInsId = -1;
            String value = null;
            if (jsonObject.has("value")) {
                value = jsonObject.getString("value");
            }
            String operation = jsonObject.getString("operation");
            String directory = jsonObject.getString("directory");
            String[] ids = directory.split("/");
            int ObjectId = Integer.parseInt(ids[0]);
            if (ids.length > 1) {
                ObjectInsId = Integer.parseInt(ids[1]);
            }

            if (ids.length == 3) {
                resourceId = Integer.parseInt(ids[2]);
            }
            if (operation.equals("read")) {
                handleRead(ObjectId, ObjectInsId, resourceId);
            } else if (operation.equals("write")) {
                handleWrite(ObjectId, ObjectInsId, resourceId, value);
            } else if (operation.equals("delete")) {
                handleDelete(ObjectId, ObjectInsId);
            } else if (operation.equals("discover")) {
                handleDiscover(ObjectId, ObjectInsId, resourceId);
            } else if (operation.equals("writeAttribute")) {
                handleWriteAttribute(ObjectId, ObjectInsId, resourceId, value);
            } else if (operation.equals("create")) {
                handleCreate(ObjectId, ObjectInsId, value);
            } else if (operation.equals("execute")){
                handleExecute(ObjectId,ObjectInsId,resourceId,value);
            } else if (operation.equals("observe")){
                handleObserve(ObjectId, ObjectInsId, resourceId);
            } else if (operation.equals("cancelObserve")){
                handleCancelObserve(ObjectId, ObjectInsId, resourceId);
            }
        }
    }


    public static void handleRead(int ObjectId, int ObjectInsId, int resourceId) throws Exception {


        Datastore ds = MongoConnection.getClientTest();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", ClientInfo.device_id)
                .asList();

        ClientObject clientObject = clientObjects.get(0);
        Resource resource1 = clientObject.getObjectMap().get(ObjectId)
                .getObjInstanceMap().get(ObjectInsId).
                        getResourceMap().get(resourceId);

        JSONObject jsonSendBack = MongoHelper.parseClientSideData(ObjectId, ObjectInsId, resourceId);
        ObjectMapper mapper = new ObjectMapper();
        String resource2 = mapper.writeValueAsString(resource1);
        jsonSendBack.put("value", resource2);
        System.out.println(resource2);

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/read")
                .register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));


        Response response = webTarget.request().post(Entity.entity(jsonSendBack.toString(), MediaType.APPLICATION_JSON));


    }

    public static void handleWrite(int ObjectId, int ObjectInsId, int resourceId, String value) throws Exception {

        Datastore ds = MongoConnection.getClientTest();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", ClientInfo.device_id)
                .asList();
        ClientObject clientObject = clientObjects.get(0);
        Resource resource1 = clientObject.getObjectMap().get(ObjectId)
                .getObjInstanceMap().get(ObjectInsId).
                        getResourceMap().get(resourceId);
        resource1.setS_value(value);
        System.out.println(resource1.getS_value());
        ds.save(clientObject);

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/write")
                .register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));


        JSONObject jsonSendBack = MongoHelper.parseClientSideData(ObjectId, ObjectInsId, resourceId);
        jsonSendBack.put("s_value", value);
        jsonSendBack.put("message", "Write success! Changed!");
        System.out.println(jsonSendBack);


        Response response = webTarget.request().post(Entity.entity(jsonSendBack.toString(), MediaType.APPLICATION_JSON));


    }

    public static void handleDelete(int ObjectId, int ObjectInsId) throws Exception {
        Datastore ds = MongoConnection.getClientTest();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", ClientInfo.device_id)
                .asList();
        ClientObject clientObject = clientObjects.get(0);
        Map<Integer,ObjInstance> objInstanceMap = clientObject.getObjectMap().get(ObjectId)
                .getObjInstanceMap();
        objInstanceMap.remove(ObjectInsId);

        ds.save(clientObject);

        JSONObject jsonSendBack = new JSONObject();
        jsonSendBack.put("client_bs_obj.device_id", ClientInfo.device_id);
        jsonSendBack.put("ObjectId", ObjectId);
        jsonSendBack.put("ObjectInsId", ObjectInsId);
        jsonSendBack.put("message", "delete success! Changed!");
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/delete")
                .register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));

        Response response = webTarget.request().post(Entity.entity(jsonSendBack.toString(), MediaType.APPLICATION_JSON));


    }

    public static void handleDiscover(int ObjectId, int ObjectInsId, int resourceId) throws Exception {

        Datastore ds = MongoConnection.getClientTest();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", ClientInfo.device_id)
                .asList();
        ClientObject clientObject = clientObjects.get(0);
        resource.Object obj = clientObject.getObjectMap().get(ObjectId);

        ds.save(clientObject);

        JSONObject jsonSendBack = MongoHelper.parseClientSideData(ObjectId, ObjectInsId, resourceId);
        ;
        jsonSendBack.put("message", "discover success!");
        jsonSendBack.put("pmin", obj.getPmin());
        jsonSendBack.put("pmax", obj.getPmax());
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/discover")
                .register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));

        Response response = webTarget.request().post(Entity.entity(jsonSendBack.toString(), MediaType.APPLICATION_JSON));


    }

    public static void handleWriteAttribute(int ObjectId, int ObjectInsId, int resourceId, String value) throws Exception {

        JSONObject jsonAttribute = new JSONObject(value);
        Datastore ds = MongoConnection.getClientTest();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", ClientInfo.device_id)
                .asList();
        ClientObject clientObject = clientObjects.get(0);
        resource.Object obj = clientObject.getObjectMap().get(ObjectId);
        obj.setPmin(jsonAttribute.getString("pmin"));
        obj.setPmax(jsonAttribute.getString("pmax"));
        obj.setLessThan(jsonAttribute.getString("lt"));
        obj.setGreaterThan(jsonAttribute.getString("gt"));
        obj.setStep(jsonAttribute.getString("st"));
        ds.save(clientObject);

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/writeAttribute")
                .register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));


        JSONObject jsonSendBack = MongoHelper.parseClientSideData(ObjectId, ObjectInsId, resourceId);
        jsonSendBack.put("value", jsonAttribute);
        jsonSendBack.put("message", "WriteAttribute success! Changed!");


        Response response = webTarget.request().post(Entity.entity(jsonSendBack.toString(), MediaType.APPLICATION_JSON));

    }

    public static void handleExecute(int ObjectId, int ObjectInsId, int resourceId, String value) throws Exception {


        if(value.equals("temperature")){
            DetectTemperature detectTemperature = new DetectTemperature(ObjectId, ObjectInsId, resourceId);
            Thread thread = new Thread(detectTemperature);
            thread.start();

        }else if(value.equals("movement")){
            DetectSteps detectSteps = new DetectSteps(ObjectId,ObjectInsId,resourceId);
            Thread thread = new Thread(detectSteps);
            thread.start();
        }else if (value.equals("stopT")){
            ClientInfo.isDetected = false;
        }

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/execute")
                .register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));

        JSONObject jsonSendBack = MongoHelper.parseClientSideData(ObjectId,ObjectInsId,resourceId);

        jsonSendBack.put("message", "Execute success! Changed!");
        Response response = webTarget.request().post(Entity.entity(jsonSendBack.toString(), MediaType.APPLICATION_JSON));



    }

    public static void handleCreate(int ObjectId, int ObjectInsId, String value) throws Exception {

        Datastore ds = MongoConnection.getClientTest();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", ClientInfo.device_id)
                .asList();
        ClientObject clientObject = clientObjects.get(0);
        Map<Integer,ObjInstance> objInstanceMap = clientObject.getObjectMap().get(ObjectId)
                .getObjInstanceMap();

        Map<Integer,Resource> resourceMap = objInstanceMap.get(ObjectInsId).getResourceMap();
        int resource_id = resourceMap.size()-1;

        ObjInstance objInstance =new ObjInstance();
        objInstance.setObjInstance_id(ObjectInsId+1);
        Resource resource = new Resource();
        resource.setRecourse_id(0);
        resource.setName(value);
        Map<Integer,Resource> resourceMapNew=objInstance.getResourceMap();
        resourceMapNew.put(0,resource);
        objInstanceMap.put(ObjectInsId+1,objInstance);

        ds.save(clientObject);

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/create")
                .register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));


        JSONObject jsonSendBack = new JSONObject();
        jsonSendBack.put("client_bs_obj.device_id", ClientInfo.device_id);
        jsonSendBack.put("ObjectId", ObjectId);
        jsonSendBack.put("ObjectInsId", ObjectInsId);
        jsonSendBack.put("resourceId",resource.getRecourse_id());
        jsonSendBack.put("value", value);
        jsonSendBack.put("message", "Create success! Changed!");

        Response response = webTarget.request().post(Entity.entity(jsonSendBack.toString(), MediaType.APPLICATION_JSON));


    }

    public static void handleObserve(int ObjectId, int ObjectInsId, int resourceId) {

        ClientInfo.isObserved = true;

    }

    public static void handleCancelObserve(int ObjectId, int ObjectInsId, int resourceId) {
        ClientInfo.isObserved = false;


    }


//    public static void notify(int ObjectId, int ObjectInsId, int resourceId,String message) throws Exception {
////        Client client = ClientBuilder.newClient();
////        WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/notify")
////                .register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));
////
////        Response response = webTarget.request().post(Entity.entity(message, MediaType.TEXT_PLAIN));
//
//
//
//    }



}
