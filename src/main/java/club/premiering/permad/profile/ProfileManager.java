package club.premiering.permad.profile;

public interface ProfileManager {
    /*
     * The following are synchronous
     */
    boolean profileExists(String username);
    Profile loadOrGetProfile(String username) throws ProfileException;
    Profile loginProfile(String username, String password) throws ProfileException;
    Profile registerProfile(String username, String password) throws ProfileException;
    void doOnMongoThreadPool(Runnable runnable);
    /*
     * The following are asynchronous
     */
    void submitUpdateProfile(Profile profile);
}
