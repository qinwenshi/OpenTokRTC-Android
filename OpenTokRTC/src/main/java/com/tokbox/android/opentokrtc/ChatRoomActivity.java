package com.tokbox.android.opentokrtc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by ankur on 11/10/13.
 */
public class ChatRoomActivity extends Activity {

    public static final String TAG = "ChatRoomActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(ChatRoomFragment.EXTRA_ROOM,
                    getIntent().getStringExtra(ChatRoomFragment.EXTRA_ROOM));
            ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
            chatRoomFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, chatRoomFragment)
                    .commit();
        }

        setContentView(R.layout.activity_chat_room);
    }
}