package com.tokbox.android.opentokrtc;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.opentok.android.Connection;
import com.opentok.android.OpentokException;
import com.opentok.android.Publisher;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ankur on 11/10/13.
 */
public class ChatRoomFragment extends Fragment implements Session.Listener, Publisher.Listener, Subscriber.Listener, AdapterView.OnItemSelectedListener {

    public static final String ARG_ROOM_ID = "room_id";
    public static final String TAG = "ChatRoomFragment";

    protected String mRoomName;
    protected Room mRoom;

    protected Session mSession;
    protected Publisher mPublisher;
    protected boolean mIsPublisherStreaming;
    protected Subscriber mSubscriber;
    protected ArrayList<Stream> mStreams;
    protected ArrayAdapter<Stream> mStreamArrayAdapter;

    protected FrameLayout mSubscriberContainer;
    protected FrameLayout mPublisherContainer;
    protected Spinner mStreamSpinner;
    protected ViewGroup mStreamSelectionContainer;
    protected ProgressDialog mConnectingDialog;

    private class GetRoomDataTask extends AsyncTask<String, Void, Room> {

        // TODO: support cancellation and progress indicator?
        // TODO: better error handling

        protected HttpClient mHttpClient;
        protected HttpGet mHttpGet;

        public GetRoomDataTask() {
            mHttpClient = new DefaultHttpClient();
        }

        @Override
        protected Room doInBackground(String... params) {
            String sessionId = null;
            String token = null;
            String apiKey = null;
            initializeGetRequest(params[0]);
            try {
                HttpResponse roomResponse = mHttpClient.execute(mHttpGet);
                HttpEntity roomEntity = roomResponse.getEntity();
                String temp = EntityUtils.toString(roomEntity);
                Log.i(TAG, "retrieved room response: " + temp);
                JSONObject roomJson = new JSONObject(temp);
                sessionId = roomJson.getString("sid");
                token = roomJson.getString("token");
                apiKey = roomJson.getString("apiKey");
            } catch (Exception exception) {
                Log.e(TAG, "could not get room data: " + exception.getMessage());
            }
            return new Room(params[0], sessionId, token, apiKey);
        }

        @Override
        protected void onPostExecute(final Room result) {
            // TODO: it might be better to set up some kind of callback interface
            setRoom(result);
        }

        protected void initializeGetRequest(String room) {
            URI roomURI;
            URL url;
            // TODO: construct urlStr from injectable values for testing
            String urlStr = "https://opentokrtc.com/" + room + ".json";
            try {
                url = new URL(urlStr);
                roomURI = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            } catch (URISyntaxException exception) {
                Log.e(TAG, "the room URI is malformed: " + exception.getMessage());
                return;
            } catch (MalformedURLException exception) {
                Log.e(TAG, "the room URI is malformed: " + exception.getMessage());
                return;
            }
            // TODO: check if alternate constructor will escape invalid characters properly, might be able to avoid all above code in this method
            mHttpGet = new HttpGet(roomURI);
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

        Log.i(TAG, "onCreate");

        // Maintain our instance alive across rotations.
        setRetainInstance(true);

        String roomName = getArguments().getString(ARG_ROOM_ID);
        setRoomName(roomName);

        mStreams = new ArrayList<Stream>();
        mStreamArrayAdapter = new ArrayAdapter<Stream>(getActivity(), android.R.layout.simple_spinner_item, mStreams);
        mStreamArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIsPublisherStreaming = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_chat_room, container, false);

        mSubscriberContainer = (FrameLayout) rootView.findViewById(R.id.subscriberContainer);
        mPublisherContainer = (FrameLayout) rootView.findViewById(R.id.publisherContainer);
        mStreamSpinner = (Spinner) rootView.findViewById(R.id.streamSelectionSpinner);
        mStreamSpinner.setAdapter(mStreamArrayAdapter);
        mStreamSpinner.setOnItemSelectedListener(this);
        mStreamSelectionContainer = (ViewGroup) rootView.findViewById(R.id.streamSelectionContainer);

        // If we already have a publisher or a subscriber view add them
        // this happens after a rotation
        if(mSubscriber != null) {
            FrameLayout subscriberContainer = (FrameLayout) rootView.findViewById(R.id.subscriberContainer);
            subscriberContainer.addView(mSubscriber.getView());
        }
        if(mPublisher != null) {
            FrameLayout publisherContainer = (FrameLayout) rootView.findViewById(R.id.publisherContainer);
            publisherContainer.addView(mPublisher.getView());
        }

        return rootView;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (mRoomName != null) {
            activity.setTitle(mRoomName);
        }
    }

    @Override
    public void onDestroyView() {
        // Publisher and subscriber views are reused so we have to remove them from their parents.
        FrameLayout publisherContainer = (FrameLayout) getView().findViewById(R.id.publisherContainer);
        publisherContainer.removeAllViews();

        FrameLayout subscriberContainer = (FrameLayout) getView().findViewById(R.id.subscriberContainer);
        subscriberContainer.removeAllViews();

        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        if (mSession != null) {
            mSession.disconnect();
        }
    }

    public void setRoomName(String roomName) {
        Log.i(TAG, "setRoomName");
        if (roomName != null && !roomName.equals(mRoomName)) {
            mRoomName = roomName;
            initializeRoom();
        }
    }

    public String getRoomName() {
        return mRoomName;
    }

    protected void setRoom(Room room) {
        Log.i(TAG, "setting room: " + room.toString());
        mRoom = room;
        enterRoom();
    }

    protected void enterRoom() {
        Log.i(TAG, "enterRoom");
        mSession = Session.newInstance(getActivity(), mRoom.getSessionId(), this);
        mSession.connect(mRoom.getApiKey(), mRoom.getToken());
    }

    protected void leaveRoom() {
        Log.i(TAG, "leaveRoom");
        // TODO: make sure this method is called before the activity goes away
        mSession.disconnect();
    }

    /**
     * Initialization occurs as soon as a new mRoomName has been set
     */
    private void initializeRoom() {
        Log.i(TAG, "initializing chat room fragment for room: " + mRoomName);
        getActivity().setTitle(mRoomName);
        GetRoomDataTask task = new GetRoomDataTask();
        task.execute(mRoomName);

        // show connecting dialog
        mConnectingDialog = new ProgressDialog(getActivity());
        mConnectingDialog.setTitle("Joining Room...");
        mConnectingDialog.setMessage("Please wait.");
        mConnectingDialog.setCancelable(false);
        mConnectingDialog.setIndeterminate(true);
        mConnectingDialog.show();
    }

    private void subscribeToStream(Stream stream) {
        // check to see if we are already subscribing to this stream
        if (mSubscriber != null && mSubscriber.getStream().getStreamId().equals(stream.getStreamId())) {
            return;
        }

        // unsubscribe to any previous streams
        if(mSubscriber != null) {
            mSubscriberContainer.removeView(mSubscriber.getView());
            mSession.unsubscribe(mSubscriber);
            mSubscriber = null;
        }

        Log.i(TAG, "subscribing to stream: " + stream.getStreamId());
        mSubscriber = Subscriber.newInstance(getActivity(), stream, this);
        ((GLSurfaceView)mSubscriber.getView()).setPreserveEGLContextOnPause(true);
        FrameLayout subscriberContainer = (FrameLayout) getView().findViewById(R.id.subscriberContainer);
        subscriberContainer.removeAllViews();
        subscriberContainer.addView(mSubscriber.getView());
        mSession.subscribe(mSubscriber);
        mStreamSpinner.setSelection(mStreams.indexOf(stream));
    }

    @Override
    public void onSessionConnected() {
        Log.i(TAG, "session connected.");

        // dismiss connecting dialog
        mConnectingDialog.dismiss();
        mConnectingDialog = null;

        if (mPublisher == null) {
            mPublisher = Publisher.newInstance(getActivity(), this, null);
            ((GLSurfaceView)mPublisher.getView()).setZOrderMediaOverlay(true);
            ((GLSurfaceView)mPublisher.getView()).setPreserveEGLContextOnPause(true);
            mSession.publish(mPublisher);
            // TODO: find out how to do this right, method is missing?
            //mPublisher.setStyle("videoScale", "fill");
            mIsPublisherStreaming = false;

            FrameLayout publisherContainer = (FrameLayout) getView().findViewById(R.id.publisherContainer);
            publisherContainer.addView(mPublisher.getView());
        }
    }

    @Override
    public void onSessionDisconnected() {
        Log.i(TAG, "session disconnected.");

        if (mPublisher != null) {
            mPublisherContainer.removeView(mPublisher.getView());
        }

        if (mSubscriber != null) {
            mSession.unsubscribe(mSubscriber);
            mSubscriberContainer.removeView(mSubscriber.getView());
        }

        // TODO: let the user know we disconnected and go back

        mPublisher = null;
        mIsPublisherStreaming = false;
        mSubscriber = null;
        mStreams.clear();
        mStreamArrayAdapter.notifyDataSetChanged();
        mSession = null;
    }

    @Override
    public void onSessionReceivedStream(Stream stream) {
        Log.i(TAG, "stream received: " + stream.getStreamId());

        if (mPublisher != null && ((!mIsPublisherStreaming) || (mIsPublisherStreaming && !mPublisher.getStreamId().equals(stream.getStreamId())))) {

            addStreamToSelections(stream);

            if (mSubscriber == null) {
                subscribeToStream(stream);
            }
        }
    }

    @Override
    public void onSessionDroppedStream(Stream stream) {
        Log.i(TAG, "stream dropped: " + stream.getStreamId());

        removeStreamFromSelections(stream);

        if (stream.getStreamId().equals(mSubscriber.getStream().getStreamId())) {
            mSubscriberContainer.removeView(mSubscriber.getView());
            mSubscriber = null;
            if (!mStreams.isEmpty()) {
                subscribeToStream(mStreams.get(0));
            }
        }
    }

    @Override
    public void onSessionCreatedConnection(Connection connection) {
        Log.i(TAG, "connection created: " + connection.getConnectionId());
    }

    @Override
    public void onSessionDroppedConnection(Connection connection) {
        Log.i(TAG, "connection dropped: " + connection.getConnectionId());
    }

    @Override
    public void onSessionException(OpentokException e) {
        Log.e(TAG, "session exception: " + e.getMessage());
    }

    @Override
    public void onPublisherStreamingStarted() {
        Log.i(TAG, "publisher is streaming.");
        mIsPublisherStreaming = true;
    }

    @Override
    public void onPublisherStreamingStopped() {
        Log.i(TAG, "publisher is not streaming.");
    }

    @Override
    public void onPublisherChangedCamera(int i) {
        Log.i(TAG, "publisher changed camera.");
    }

    @Override
    public void onPublisherException(OpentokException e) {
        Log.e(TAG, "publisher exception: " + e.getMessage());
    }

    @Override
    public void onSubscriberConnected(Subscriber subscriber) {
        Log.i(TAG, "subscriber connected, stream id: " + subscriber.getStream().getStreamId());
    }

    @Override
    public void onSubscriberVideoDisabled(Subscriber subscriber) {
        Log.i(TAG, "subscriber video disabled, stream id: " + subscriber.getStream().getStreamId());
    }

    @Override
    public void onSubscriberException(Subscriber subscriber, OpentokException e) {
        Log.e(TAG, "subscriber exception: " + e.getMessage() + ", stream id: " + subscriber.getStream().getStreamId());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.i(TAG, "item selected in spinner.");
        subscribeToStream(mStreams.get(i));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i(TAG, "nothing selected in spinner.");
    }

    private void addStreamToSelections(Stream stream) {
        // TODO: draw attention to the spinner
        mStreams.add(stream);
        mStreamArrayAdapter.notifyDataSetChanged();
        layoutStreamSelectionChanges();
    }

    private void removeStreamFromSelections(Stream stream) {
        // TODO: draw attention to the spinner
        mStreams.remove(stream);
        mStreamArrayAdapter.notifyDataSetChanged();
        layoutStreamSelectionChanges();
    }

    private void layoutStreamSelectionChanges() {
        if (mStreams.size() > 1) {
            Log.i(TAG, "setting stream selection container to visible, size: " + mStreams.size());
            mStreamSelectionContainer.setVisibility(View.VISIBLE);
        } else {
            Log.i(TAG, "setting stream selection container to gone, size: " + mStreams.size());
            mStreamSelectionContainer.setVisibility(View.GONE);
        }
    }
}