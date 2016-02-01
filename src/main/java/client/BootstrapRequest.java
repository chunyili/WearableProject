package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.filter.LoggingFilter;
import resource.Client_BS_Obj;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by jianxin on 6/23/15.
 */
public class BootstrapRequest {

    public static Client_BS_Obj getBootInfo() throws Exception {

        //Bootstrap
        Client_BS_Obj client_bs_obj = new Client_BS_Obj();
        try{
            Client client = ClientBuilder.newClient();
            WebTarget target = client.target(ServerObj.bs_uri).path("/bs").register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true));

            Response response = target.queryParam("ep", client_bs_obj.getDevice_id()).request().post(Entity.text(""));

            if (response.getStatus() != 200){
               return  null;
            }else{

                ObjectMapper mapper = new ObjectMapper();
                String string = response.readEntity(String.class);
                client_bs_obj = mapper.readValue(string, Client_BS_Obj.class);

                return client_bs_obj;
            }

        }catch (Exception e){
            e.printStackTrace( System.out );

        }
        return client_bs_obj;

    }
}
