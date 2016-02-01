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
public class DetectSteps implements Runnable{
    public int ObjectId;
    public int ObjectInsId;
    public int resourceId;

    public DetectSteps(int ObjectId, int ObjectInsId, int resourceId) {
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

        Map<Date, Integer> map = new LinkedHashMap<Date, Integer>();

        while (true) {
            Calendar calendar = Calendar.getInstance();
            int steps = (int)(Math.random()*2000+4000);

            if (ClientInfo.isObserved) {
                if (steps < lt) {
                    //notify(ObjectId,ObjectInsId,resourceId,"Reached the max limit!");
                    Client client = ClientBuilder.newClient();
                    WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/broadcast");
                    Response response = webTarget.request().post(Entity.entity("Reached the min limit! The Steps you walked today is ", MediaType.TEXT_PLAIN));

                } else if (steps > gt) {
                    //notify(ObjectId,ObjectInsId,resourceId,"Reached the min limit!");
                    Client client = ClientBuilder.newClient();
                    WebTarget webTarget = client.target("http://localhost:8080/WearableProject_war_exploded/api/server").path("/broadcast");
                    Response response = webTarget.request().post(Entity.entity("Reached the max limit! the Steps you walked today is ", MediaType.TEXT_PLAIN));

                }

            }

            map.put(calendar.getTime(), steps);
            System.out.println(calendar.getTime()+"" +steps);
            resource1.getMvalue().putAll(map);
            ds.save(clientObject);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
