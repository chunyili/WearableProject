package server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jilongsun on 7/17/15.
 */
public class Info {

    private static volatile Map<String,String> clientSendToServerInfoMap = new HashMap<String, String>();

    private static volatile Map<String,String> infoMap = new HashMap<String, String>();

    private static volatile Map<String,CountDownLatch> countDownLatchMap = new HashMap<String, CountDownLatch>();

    private static volatile Map<String,CountDownLatch> countDownLatchMessageMap = new HashMap<String, CountDownLatch>();


   public static String getClientSendToServerInfoMap(String value){
       return clientSendToServerInfoMap.get(value);
   }

    public static void setClientSendToServerInfoMap(String value, String message) {
       clientSendToServerInfoMap.put(value,message);
    }

    public static CountDownLatch getCountDown(String client){
        return countDownLatchMap.get(client);

    }

    public static void setCountDownLatchMap(String client, CountDownLatch countDownLatch){
        countDownLatchMap.put(client,countDownLatch);
    }


    public static String getInfo(String client) {
        return infoMap.get(client);
    }

    public static void setInfo(String client,String info) {
        infoMap.put(client, info);
    }

    public static CountDownLatch getCountDownMessage(String device_id){
        return countDownLatchMessageMap.get(device_id);
    }

    public  static void setCountDownLatchMessageMap(String device_id, CountDownLatch countDownLatch){
        countDownLatchMessageMap.put(device_id,countDownLatch);
    }


}