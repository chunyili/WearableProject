package resource;

import org.mongodb.morphia.annotations.Embedded;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jilongsun on 7/16/15.
 */
@Embedded
public class Object {

    public void setObject_id(int object_id) {
        this.object_id = object_id;
    }

    public String getPmin() {
        return pmin;
    }

    public void setPmin(String pmin) {
        this.pmin = pmin;
    }

    public String getPmax() {
        return pmax;
    }

    public void setPmax(String pmax) {
        this.pmax = pmax;
    }

    public String getGreaterThan() {
        return greaterThan;
    }

    public void setGreaterThan(String greaterThan) {
        this.greaterThan = greaterThan;
    }

    public String getLessThan() {
        return lessThan;
    }

    public void setLessThan(String lessThan) {
        this.lessThan = lessThan;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    private  int object_id;
    private String name;
    private String instanceType;
    private String pmin;
    private String pmax;
    private String greaterThan;
    private String lessThan;
    private String step;

    public int getObject_id() {
        return object_id;
    }

//    public  void setObject_id(int object_id) {
//        this.object_id = object_id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    private boolean mandatory;
    private String description;

    public Map<Integer, ObjInstance> getObjInstanceMap() {
        return objInstanceMap;
    }

    public void setObjInstanceMap(Map<Integer, ObjInstance> objInstanceMap) {
        this.objInstanceMap = objInstanceMap;
    }

    @Embedded
    private Map<Integer,ObjInstance> objInstanceMap = new HashMap<Integer, ObjInstance>();
//    private ObjInstance[] objInstance ;

}
