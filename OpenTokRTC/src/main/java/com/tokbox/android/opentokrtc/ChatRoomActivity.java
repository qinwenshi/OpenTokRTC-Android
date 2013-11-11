package com.tokbox.android.opentokrtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by ankur on 11/10/13.
 */
public class ChatRoomActivity extends Activity {

    public static final String EXTRA_ROOM = "com.tokbox.android.opentokrtc.ChatRoomActivity.room";
    public static final String TAG = "ChatRoomActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String roomName = intent.getStringExtra(EXTRA_ROOM);
        if (roomName == null) {
            throw new IllegalStateException("Chat Room Activity cannot be created without a room");
        }

        Log.i(TAG, "chat room activity started for room: " + roomName);

        setContentView(R.layout.activity_chat_room);
    }
}