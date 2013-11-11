package com.tokbox.android.opentokrtc;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ankur on 11/10/13.
 */
public class ChatRoomFragment extends Fragment {

    public static final String ARG_ROOM_ID = "room_id";
    public static final String TAG = "ChatRoomFragment";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatRoomFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String roomName = getArguments().getString(ARG_ROOM_ID);
        if (roomName != null) {
            // TODO: initialize everything
            Log.i(TAG, "initializing chat room fragment for room: " + roomName);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }
}