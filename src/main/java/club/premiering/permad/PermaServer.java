package club.premiering.permad;

import club.premiering.permad.commands.*;
import club.premiering.permad.networking.NetworkManager;
import club.premiering.permad.profile.ProfileManager;
import club.premiering.permad.profile.ProfileManagerImpl;
import club.premiering.permad.room.RoomManager;
import club.premiering.permad.room.RoomManagerImpl;
import club.premiering.permad.util.FileUtils;
import club.premiering.permad.util.GsonUtils;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;

public class PermaServer {
    private static PermaServer SERVER_INSTANCE = null;

    public static PermaServer getServer() {
        if (SERVER_INSTANCE == null)
            throw new RuntimeException("Server not initialized!");
        return SERVER_INSTANCE;
    }

    @Getter
    private ProfileManager profileManager;
    @Getter
    private RoomManager roomManager;
    @Getter
    private CommandManager commandManager;
    @Getter
    private NetworkManager networkServer = new NetworkManager();

    //Mongo
    @Getter
    private MongoClient mongoClient;
    @Getter
    private MongoDatabase gameDatabase;

    public PermaServer() throws Exception {
        SERVER_INSTANCE = this;

        this.loadConfig();
        this.loadDb();
        this.profileManager = new ProfileManagerImpl(gameDatabase);
        this.roomManager = new RoomManagerImpl();
        this.commandManager = new CommandManagerImpl();
        this.commandManager.registerCommand(new TpCommand());
        this.commandManager.registerCommand(new GodCommand());
        this.commandManager.registerCommand(new ResCommand());
    }

    private void loadConfig() {
        byte[] scData;
        try {
            scData = FileUtils.loadFileData("server-conf.json");
        } catch (Exception e) {
            System.out.println("server-conf.json file invalid or not present! Exiting.");
            System.exit(2);
            return;
        }

        try {
            PermaGlobals.CONFIG = GsonUtils.GSON.fromJson(new String(scData), PermaConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load server-conf.json! Exiting.");
            System.exit(2);
        }
    }

    public PermaConfig getSkipConfig() {
        return PermaGlobals.CONFIG;
    }

    // TODO: 10/30/2023 Login with username and password
    private void loadDb() {
        try (MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .applyConnectionString(new ConnectionString(this.getSkipConfig().mongoAddress))
                .build())) {
            this.mongoClient = mongoClient;
            this.gameDatabase = mongoClient.getDatabase("neptune");

            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = this.gameDatabase.runCommand(command);
            System.out.println("MongoDB | connected");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(3);
        }
    }

    public void start() {
        this.networkServer.start();
    }
}
