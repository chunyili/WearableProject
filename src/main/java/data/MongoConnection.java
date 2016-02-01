package data;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import resource.ClientObject;
import resource.Client_BS_Obj;

/**
 * Created by jilongsun on 7/16/15.
 */

public class MongoConnection {
    private static  MongoClient mongoClient;

    public static Datastore getServer() throws  Exception{
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        Morphia morphia = new Morphia();
        morphia.map(ClientObject.class);
        Datastore ds = morphia.createDatastore(mongoClient,"server");
        return ds;


    }
    public static Datastore getClientTest() throws Exception{
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        Morphia morphia = new Morphia();
        morphia.map(ClientObject.class);
        Datastore ds = morphia.createDatastore(mongoClient,"clientTest");

        return ds;
    }

    public static Datastore getBootstrap() throws Exception{
        mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        Morphia morphia = new Morphia();
        morphia.map(Client_BS_Obj.class);
        Datastore ds = morphia.createDatastore(mongoClient,"Bootstrap");
        return ds;
    }

    public static MongoCollection getServerSideCLientObject() throws Exception{
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("server");
        final MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("ClientObject");
        return mongoCollection;

    }


}
