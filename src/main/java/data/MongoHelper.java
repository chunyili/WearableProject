package data;

import client.ClientInfo;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by jilongsun on 7/20/15.
 */
public class MongoHelper {
    public static JSONObject parseClientSideData(int ObjectId, int ObjectInsId, int resourceId) throws JSONException {
        JSONObject jsonSendBack=new JSONObject();
        jsonSendBack.put("client_bs_obj.device_id", ClientInfo.device_id);
        jsonSendBack.put("ObjectId",ObjectId);
        jsonSendBack.put("ObjectInsId",ObjectInsId);
        jsonSendBack.put("resourceId",resourceId);

        return jsonSendBack;

    }

}
