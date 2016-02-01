package resource;

import org.mongodb.morphia.annotations.Embedded;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jilongsun on 7/16/15.
 */
@Embedded
public class Resource {
    private int recourse_id;
    private String name;

    public int getRecourse_id() {
        return recourse_id;
    }

    public void setRecourse_id(int recourse_id) {
        this.recourse_id = recourse_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Map<Date, Double> getValue() {
        return value;
    }

    public void setMvalue(Map<Date, Integer> mvalue) {
        this.mvalue = mvalue;
    }

    public void setValue(Map<Date, Double> value) {

        this.value = value;
    }

    public Map<Date, Integer> getMvalue() {
        return mvalue;
    }



    private  Map<Date,Integer> mvalue = new HashMap<Date, Integer>();

    private Map<Date,Double> value = new HashMap<Date, Double>();

    public String getS_value() {
        return s_value;
    }

    public void setS_value(String s_value) {
        this.s_value = s_value;
    }

    private String s_value;


}
