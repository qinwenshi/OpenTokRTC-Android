package com.tokbox.android.opentokrtc;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.URL;

/**
 * Created by ankur on 11/10/13.
 */
public class ChatRoomFragment extends Fragment {

    public static final String ARG_ROOM_ID = "room_id";
    public static final String TAG = "ChatRoomFragment";

    private String mRoomName;

    private class GetRoomDataTask extends AsyncTask<URL, Void, Room> {

        @Override
        protected Room doInBackground(URL... params) {
            return new Room("", "", "");
        }

        @Override
        protected void onPostExecute(final Room result) {

        }
    }

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
        setRoomName(roomName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room, container, false);
    }


    public void setRoomName(String roomName) {
        if (roomName != null && !roomName.equals(mRoomName)) {
            mRoomName = roomName;
            initializeRoom();
        }
    }

    public String getRoomName() {
        return mRoomName;
    }

    /**
     * Initialization occurs as soon as a new roomName has been set
     */
    private void initializeRoom() {
        Log.i(TAG, "initializing chat room fragment for room: " + mRoomName);
        getActivity().setTitle(mRoomName);
    }
}