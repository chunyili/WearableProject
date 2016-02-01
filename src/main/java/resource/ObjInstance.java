package resource;

import org.mongodb.morphia.annotations.Embedded;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jilongsun on 7/16/15.
 */
@Embedded
public class ObjInstance {
    public int getObjInstance_id() {
        return objInstance_id;
    }

    public void setObjInstance_id(int objInstance_id) {
        this.objInstance_id = objInstance_id;
    }



    private int objInstance_id;

    public Map<Integer, Resource> getResourceMap() {
        return resourceMap;
    }

    public void setResourceMap(Map<Integer, Resource> resourceMap) {
        this.resourceMap = resourceMap;
    }

    @Embedded
    private Map<Integer,Resource> resourceMap = new HashMap<Integer, Resource>();
}
