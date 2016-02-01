package resource;

import client.ClientInfo;
import org.mongodb.morphia.annotations.Embedded;

/**
 * Created by jianxin on 6/23/15.
 */

//@JsonIgnoreProperties(value = { "device_id" })


@Embedded
public class Client_BS_Obj {


    @Override
    public String toString() {
        return "Client_BS_Obj{" +
                "manufacturer='" + manufacturer + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", lifeTime=" + lifeTime +
                ", lwm2mVersion='" + lwm2mVersion + '\'' +
                ", binding='" + binding + '\'' +
                ", smsNumber='" + smsNumber + '\'' +
                ", serverURI='" + serverURI + '\'' +
                '}';
    }

    private  String device_id = ClientInfo.device_id;
    private  String manufacturer;
    private  String modelNumber;
    private String serialNumber;
    private  long lifeTime;
    private  String lwm2mVersion;
    private  String binding;
    private  String smsNumber;
    private  String serverURI;

    public  String getDevice_id() {
        return device_id;
    }



    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }

    public String getLwm2mVersion() {
        return lwm2mVersion;
    }

    public void setLwm2mVersion(String lwm2mVersion) {
        this.lwm2mVersion = lwm2mVersion;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getSmsNumber() {
        return smsNumber;
    }

    public void setSmsNumber(String smsNumber) {
        this.smsNumber = smsNumber;
    }

    public String getServerURI() {
        return serverURI;
    }

    public void setServerURI(String serverURI) {
        this.serverURI = serverURI;
    }
}
