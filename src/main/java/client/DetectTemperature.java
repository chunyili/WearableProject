package client;

import data.MongoConnection;
import org.mongodb.morphia.Datastore;
import resource.ClientObject;
import resource.Resource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Created by jilongsun on 7/28/15.
 */
public class DetectTemperature implements Runnable {

    /**
     * Created by jianxin on 7/27/15.
     */


    public int ObjectId;
    public int ObjectInsId;
    public int resourceId;

    public DetectTemperature(int ObjectId, int ObjectInsId, int resourceId) {
        this.ObjectId = ObjectId;
        this.ObjectInsId = ObjectInsId;
        this.resourceId = resourceId;
    }

    public void run() {

        Datastore ds = null;
        try {
            ds = MongoConnection.getClientTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", ClientInfo.device_id)
                .asList();
        ClientObject clientObject = clientObjects.get(0);
        Resource resource1 = clientObject.getObjectMap().get(ObjectId)
                .getObjInstanceMap().get(ObjectInsId).
                        getResourceMap().get(resourceId);
        resource.Object obj = clientObject.getObjectMap().get(ObjectId);
        Double gt = Double.parseDouble(obj.getGreaterThan());
        Double lt = Double.parseDouble(obj.getLessThan());

        Map<Date, Double> map = new LinkedHashMap<Date, Double>();
        ClientInfo.isDetected = true;

        while (ClientInfo.isDetected) {
            Calendar calendar = Calendar.getInstance();
            Double value = 35 + Math.random() * 4;
            Double result = Double.valueOf(String.format("%.2f", value));

            if (ClientInfo.isObserved) {
                if (result < lt) {
                    //notify(ObjectId,ObjectInsId,resourceId,"Reached the max limit!");
                    Client client = ClientBuilder.newClient();
                    WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/broadcast");
                    Response response = webTarget.request().post(Entity.entity("Reached the min limit! The temperature is "+result, MediaType.TEXT_PLAIN));

                } else if (result > gt) {
                    //notify(ObjectId,ObjectInsId,resourceId,"Reached the min limit!");
                    Client client = ClientBuilder.newClient();
                    WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/broadcast");
                    Response response = webTarget.request().post(Entity.entity("Reached the max limit! The temperature is "+result, MediaType.TEXT_PLAIN));

                }

            }

            map.put(calendar.getTime(), result);
            System.out.println(calendar.getTime()+" "+result);
            resource1.getValue().putAll(map);
            ds.save(clientObject);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

