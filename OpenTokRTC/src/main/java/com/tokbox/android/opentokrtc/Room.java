package com.tokbox.android.opentokrtc;

/**
 * Created by ankur on 11/11/13.
 */
public class Room {

    protected String mName;
    protected String mSessionId;
    protected String mToken;
    protected String mApiKey;

    public Room(String name, String sessionId, String token, String apiKey) {
        if (name == null || sessionId == null || token == null || apiKey == null) {
            throw new IllegalArgumentException("Room must be initialized without any null parameters");
        }

        mName = name;
        mSessionId = sessionId;
        mToken = token;
        mApiKey = apiKey;
    }

    public String getName() {
        return mName;
    }

    public String getSessionId() {
        return mSessionId;
    }

    public String getToken() {
        return mToken;
    }

    public String getApiKey() {
        return mApiKey;
    }

    public String toString() {
        return "{ name: " + mName + ", sessionId: " + mSessionId + ", token: " + mToken + ", apiKey: " + mApiKey + " }";
    }
}
