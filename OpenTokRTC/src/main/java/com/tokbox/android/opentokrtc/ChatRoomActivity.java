package com.tokbox.android.opentokrtc;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by ankur on 11/10/13.
 */
public class ChatRoomActivity extends Activity {

    public static final String TAG = "ChatRoomActivity";
    ChatRoomFragment mChatRoomFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Stop screen from going to sleep
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_chat_room);

        FragmentManager fm = getFragmentManager();
        mChatRoomFragment = (ChatRoomFragment) fm.findFragmentById(R.id.container);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mChatRoomFragment == null) {
            mChatRoomFragment = new ChatRoomFragment();
            if (savedInstanceState == null) {
                Bundle arguments = new Bundle();
                arguments.putString(ChatRoomFragment.ARG_ROOM_ID,
                        getIntent().getStringExtra(ChatRoomFragment.ARG_ROOM_ID));
                mChatRoomFragment.setArguments(arguments);
            }
            fm.beginTransaction().add(R.id.container, mChatRoomFragment).commit();
        }

    }
}