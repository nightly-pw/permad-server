package club.premiering.permad.profile;

import club.premiering.permad.util.GsonUtils;
import club.premiering.permad.util.HashUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Work in progress - profiles.
// Any code in this package is not referenced anywhere else yet except for this profile package
public class ProfileManagerImpl implements ProfileManager {
    private static final int MIN_PASSWORD_LENGTH = 10;

    public MongoDatabase gameDatabase;
    public MongoCollection<Document> profileCollection;
    private final ExecutorService mongoExecutor = Executors.newFixedThreadPool(1);

    private final Collection<Profile> profileCache = new CopyOnWriteArraySet<>();

    public ProfileManagerImpl(MongoDatabase gameDatabase) {
        this.gameDatabase = gameDatabase;

        this.profileCollection = gameDatabase.getCollection("profiles");
    }

    @Override
    public boolean profileExists(String username) {
        return this.profileCollection.countDocuments(this.findProfByUsername(username), new CountOptions().limit(1)) == 1;
    }

    @Override
    public Profile loadOrGetProfile(String username) throws ProfileException {
        var profile = this.getCachedProfile(username);
        if (profile != null)
            return profile;

        var doc = this.findProfByUsername(username);
        if (doc == null)
            throw new ProfileException("That profile doesn't exist!");

        profile = GsonUtils.GSON.fromJson(doc.toJson(), Profile.class);
        this.profileCache.add(profile);
        return profile;
    }

    @Override
    public Profile loginProfile(String username, String password) throws ProfileException {
        Profile profile = this.loadOrGetProfile(username);
        if (HashUtils.sha256(password.getBytes()) == profile.passwordHash) {
            return profile;
        }

        throw new ProfileException("Incorrect username or password!");
    }

    @Override
    public Profile registerProfile(String username, String password) throws ProfileException {
        if (this.profileExists(username))
            throw new ProfileException("Someone already has that username!");
        if (password.length() < MIN_PASSWORD_LENGTH)
            throw new ProfileException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long!");

        Profile profile = new Profile();
        profile.name = username;
        profile.passwordHash = HashUtils.sha256(password.getBytes());
        profile.accountId = UUID.randomUUID();

        this.submitUpdateProfile(profile);

        return profile;
    }

    @Override
    public void doOnMongoThreadPool(Runnable runnable) {
        this.mongoExecutor.submit(runnable);
    }

    @Override
    public void submitUpdateProfile(Profile profile) {
        this.mongoExecutor.submit(() -> {
            var doc = Document.parse(GsonUtils.GSON.toJson(profile));
            this.profileCollection.replaceOne(this.findByUsernameFilter(profile.name), doc, new ReplaceOptions().upsert(true));
        });
    }

    //Return null if not present
    private Profile getCachedProfile(String username) {
        for (var profile : this.profileCache) {
            if (Objects.equals(profile.name, username))
                return profile;
        }
        return null;
    }

    private Document findProfByUsername(String username) {
        var it = this.profileCollection.find(this.findByUsernameFilter(username)).limit(1);
        return it.first();
    }

    private Bson findByUsernameFilter(String username) {
        return Filters.eq("username", username);
    }
}
