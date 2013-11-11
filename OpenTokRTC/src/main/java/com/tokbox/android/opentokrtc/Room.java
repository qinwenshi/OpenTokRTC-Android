package com.tokbox.android.opentokrtc;

/**
 * Created by ankur on 11/11/13.
 */
public class Room {

    protected String mName;
    protected String mSessionId;
    protected String mToken;

    public Room(String name, String sessionId, String token) {
        mName = name;
        mSessionId = sessionId;
        mToken = token;
        return;
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
}
