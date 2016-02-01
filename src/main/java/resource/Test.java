package resource;

import data.MongoConnection;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import java.util.*;

/**
 * Created by jilongsun on 7/16/15.
 */
public class Test {
    public static void main(String args[]) throws Exception {
        Datastore ds = MongoConnection.getClientTest();
        ClientObject clientObject = new ClientObject();
        Client_BS_Obj client_bs_obj = new Client_BS_Obj();
        clientObject.setClient_bs_obj(client_bs_obj);
        client_bs_obj.getDevice_id();



        Map<Integer,Object> objectMap = new HashMap<Integer, Object>();

        Map<Integer,ObjInstance> objInstanceMap = new HashMap<Integer, ObjInstance>();

        Map<Integer,Resource> resourceMap = new HashMap<Integer, Resource>();

        clientObject.setObjectMap(objectMap);
        Object object = new Object();
        object.setPmin("10");
        object.setPmax("20");
        object.setStep("3");
        object.setName("Temperature");
        object.setGreaterThan("36.0");
        object.setLessThan("38.0");



        object.setObject_id(0);
        objectMap.put(0, object);

        object.setObjInstanceMap(objInstanceMap);
        ObjInstance objInstance = new ObjInstance();
        objInstance.setObjInstance_id(0);
        objInstanceMap.put(0, objInstance);

        objInstance.setResourceMap(resourceMap);


        Resource resource = new Resource();
        resource.setRecourse_id(0);
        resource.setName("temperature");
        resource.setS_value("vibration");




//        Map<Date, Double> map =  new HashMap<Date, Double>();
//
//        for(int i = 0; i< 10; i++) {
//            try{
//                Thread.sleep(1000);
//            }catch (Exception e){
//
//            }
//
//            Calendar calendar = Calendar.getInstance();
//            Double value = 35+Math.random()*6;
//            Double result= Double.valueOf(String.format("%.2f",value));
//            map.put(calendar.getTime(),result);
//        }
//        resource.setValue(map);
        resourceMap.put(0,resource);

//        Resource resource1 = new Resource();
//        resource1.setRecourse_id(1);
//        resource1.setName("movement");
//        resource1.setS_value("vibration");
//
////        Map<Date,Integer> map1 = new HashMap<Date, Integer>();
////        for(int j = 0; j< 30; j++) {
////            try{
////                Thread.sleep(1000);
////            }catch (Exception e){
////
////            }
////            Calendar calendar = Calendar.getInstance();
////            int times = (int)(Math.random()*2000+4000);
////            map1.put(calendar.getTime(),times);
////        }
////
////        resource1.setMvalue(map1);
//        resourceMap.put(1, resource1);


        Query<ClientObject> query = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", client_bs_obj.getDevice_id());
        final List<ClientObject> clientObjects = query
                .asList();
        if(clientObjects.size() == 0){
            ds.save(clientObject);

        }else {
            ds.updateFirst(query,clientObject,true);

        }

    }
}
