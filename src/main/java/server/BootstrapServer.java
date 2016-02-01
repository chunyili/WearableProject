package server;

import data.MongoConnection;
import org.mongodb.morphia.Datastore;
import resource.Client_BS_Obj;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by jianxin on 6/25/15.
 */

@Path("/bs")
public class BootstrapServer {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response respondToBootstrap (@QueryParam("ep") String ep) throws Exception {

        Datastore ds = MongoConnection.getBootstrap();
        List<Client_BS_Obj> client_bs_objs = ds.createQuery(Client_BS_Obj.class)
                .filter("device_id =", ep)
                .asList();

        if (client_bs_objs.size() == 0){
            return Response.status(500).entity("Unable to bootstrap this device!").build();
        }else{
            return Response.status(200).entity(client_bs_objs.get(0)).build();
        }

    }

}
