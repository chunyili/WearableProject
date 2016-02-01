package client;


import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import data.MongoConnection;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.mongodb.morphia.Datastore;
import resource.ClientObject;
import resource.Client_BS_Obj;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by jilongsun on 7/15/15.
 */
public class RegisterRequest {
    public static String registerInfo(Client_BS_Obj client_bs_obj) throws Exception {
        ClientConfig config = new ClientConfig().register(JacksonJsonProvider.class);
        Client client = ClientBuilder.newClient(config).register(SseFeature.class);

        WebTarget target = client.target(client_bs_obj.getServerURI()).register(new LoggingFilter(java.util.logging.Logger.getLogger("test"), true))
                .queryParam("ep", client_bs_obj.getDevice_id())
                .queryParam("lt", client_bs_obj.getLifeTime())
                .queryParam("sms", client_bs_obj.getSmsNumber())
                .queryParam("lwm2m", client_bs_obj.getLwm2mVersion())
                .queryParam("b", client_bs_obj.getBinding());

        Datastore ds = MongoConnection.getClientTest();
        List<ClientObject> clientObjects = ds.createQuery(ClientObject.class)
                .filter("client_bs_obj.device_id =", client_bs_obj.getDevice_id())
                .asList();

        EventInput eventInput = target.request().accept(SseFeature.SERVER_SENT_EVENTS)
                .post(Entity.entity(clientObjects.get(0), MediaType.APPLICATION_JSON), EventInput.class);


        while (!eventInput.isClosed()) {
            final InboundEvent inboundEvent = eventInput.read();
            if (inboundEvent == null) {

                break;
            }

            String inputData = inboundEvent.readData(String.class);
            Handler.parser(inputData);


//            System.out.println(inputData);




        }
        return "client register success";
    }
}