package be.webtito.ebitu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

import com.google.android.gms.nearby.messages.Message;
import com.google.gson.Gson;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Used to prepare the payload for a
 * {@link com.google.android.gms.nearby.messages.Message Nearby Message}. Adds a unique id
 * to the Message payload, which helps Nearby distinguish between multiple devices with
 * the same model name.
 */
public class NearbyMessage {
    private static final Gson gson = new Gson();

    private final String mUUID;
    private final String mSender;
    private final String mTitle;
    private final int mSong;

    /**
     * Builds a new {@link Message} object using a unique identifier.
     */
    public static Message newNearbyMessage(String uuid, String sender, String title, int id) {
        NearbyMessage nearbyMessage = new NearbyMessage(uuid, sender, title, id);
        return new Message(gson.toJson(nearbyMessage).getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Creates a {@code NearbyMessage} object from the string used to construct the payload to a
     * {@code Nearby} {@code Message}.
     */
    public static NearbyMessage fromNearbyMessage(Message message) {
        String nearbyMessageString = new String(message.getContent()).trim();
        return gson.fromJson(
                (new String(nearbyMessageString.getBytes(Charset.forName("UTF-8")))),
                NearbyMessage.class);
    }

    private NearbyMessage(String uuid, String sender, String title, int id) {
        mUUID = uuid;
        mSender = sender;
        mTitle = title;
        mSong = id;
    }

    protected String getSender() {
        return mSender;
    }
    protected String getTitle() {
        return mTitle;
    }
    protected int getSong() {
        return mSong;
    }
    protected String getMessageUUID() {
        return mUUID;
    }
}