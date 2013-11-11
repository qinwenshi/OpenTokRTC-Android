package com.tokbox.android.opentokrtc;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by ankur on 11/10/13.
 */
public class ChatRoomActivity extends Activity {

    public static final String TAG = "ChatRoomActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(ChatRoomFragment.ARG_ROOM_ID,
                    getIntent().getStringExtra(ChatRoomFragment.ARG_ROOM_ID));
            ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
            chatRoomFragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, chatRoomFragment)
                    .commit();
        }

        setContentView(R.layout.activity_chat_room);
    }
}