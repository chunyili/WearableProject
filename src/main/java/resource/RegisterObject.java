package resource;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by jilongsun on 7/15/15.
 */
@Entity
public class RegisterObject {
    @Override
    public String toString() {
        return "RegisterObject{" +
                "objectId=" + objectId +
                '}';
    }

    @Id
    private  int objectId;



    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectInstanceId() {
        return objectInstanceId;
    }

    public void setObjectInstanceId(int objectInstanceId) {
        this.objectInstanceId = objectInstanceId;
    }

    public RegiResource getRecource() {
        return recource;
    }

    public void setRecource(RegiResource recource) {
        this.recource = recource;
    }

    private  int objectInstanceId;
    @Embedded
    private RegiResource recource;

}
