package com.tokbox.android.opentokrtc;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ankur on 11/9/13.
 */
public class RoomSelectionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RoomSelectionFragment";
    private Callbacks mCallbacks = sDummyCallbacks;

    public interface Callbacks {
        public void onRoomSelected(String roomName);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onRoomSelected(String roomName) {
            return;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_room_selection, container, false);

        Typeface avantGarde = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Avant+Garde+Book+BT.ttf");
        TextView introText = (TextView) rootView.findViewById(R.id.introText);
        introText.setTypeface(avantGarde);

        Button joinRoomButton = (Button) rootView.findViewById(R.id.button_join_room);
        joinRoomButton.setOnClickListener(this);
        joinRoomButton.setTypeface(avantGarde);

        EditText roomName = (EditText) rootView.findViewById(R.id.input_room_name);
        roomName.setTypeface(avantGarde);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callback");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks = sDummyCallbacks;
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
        Log.i(TAG, "join room button clicked.");

        EditText roomNameInput = (EditText) getView().findViewById(R.id.input_room_name);
        String roomName = roomNameInput.getText().toString();

        Log.i(TAG, "the room name is " + roomName);

        mCallbacks.onRoomSelected(roomName);
    }
}