package com.tokbox.android.opentokrtc;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by ankur on 11/9/13.
 */
public class RoomSelectionFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_room_selection, container, false);

        Button joinRoomButton = (Button) rootView.findViewById(R.id.button_join_room);
        joinRoomButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_join_room:
                joinRoom();
                break;
        }
    }

    private void joinRoom() {
        Log.i("OpenTokRTC", "join room button clicked.");
    }
}