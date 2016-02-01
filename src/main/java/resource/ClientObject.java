package resource;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jilongsun on 7/17/15.
 */
@Entity
public class ClientObject {
    @Id
    private ObjectId id;


    @Override
    public String toString() {
        return "ClientObject{" +
                "id=" + id +
                ", objectMap=" + objectMap +
                ", client_bs_obj=" + client_bs_obj +
                '}';
    }

    public void setObjectMap(Map<Integer, Object> objectMap) {
        this.objectMap = objectMap;
    }

    public Map<Integer, Object> getObjectMap() {
        return objectMap;
    }




    @Embedded
    private Map<Integer,Object> objectMap =  new HashMap<Integer, Object>();
    @Embedded
    private Client_BS_Obj client_bs_obj;

    public Client_BS_Obj getClient_bs_obj() {
        return client_bs_obj;
    }

    public void setClient_bs_obj(Client_BS_Obj client_bs_obj) {
        this.client_bs_obj = client_bs_obj;
    }
}
