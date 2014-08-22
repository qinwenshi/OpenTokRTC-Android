package com.tokbox.android.opentokrtc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

public class Participant extends Subscriber {

	private static final String LOGTAG = "Participant";

	private String mUserId;
	private String mName;
	private Context mContext;
	protected Boolean mSubscriberAudioOnly = false;
	private ChatRoomActivity mActivity;

	public Participant(Context context, Stream stream) {
		super(context, stream);

		this.mContext = context;
		this.mActivity = (ChatRoomActivity) this.mContext;
		setName("User" + ((int) (Math.random() * 1000)));
		this.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
				BaseVideoRenderer.STYLE_VIDEO_FILL);
	}

	public String getUserId() {
		return mUserId;
	}

	public void setUserId(String name) {
		this.mUserId = name;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public Boolean isSubscriberInAudioOnlyMode() {
		return mSubscriberAudioOnly;
	}

	@Override
	protected void onVideoDisabled(String reason) {
		super.onVideoDisabled(reason);
		Log.i(LOGTAG, "Video disabled for the subscriber:" + reason);
		if( VIDEO_REASON_QUALITY.equals(reason) ) {
			mSubscriberAudioOnly = true;
			mActivity.getRoom().getPagerAdapter().notifyDataSetChanged();
		}
	}

	@Override
	protected void onVideoEnabled(String reason) {
		super.onVideoEnabled(reason);
		Log.i(LOGTAG, "Video enabled for the subscriber:" + reason);
		if( VIDEO_REASON_QUALITY.equals(reason) ) { 
			mSubscriberAudioOnly = false;
			mActivity.getRoom().getPagerAdapter().notifyDataSetChanged();
		}
	}

	@Override
	protected void onVideoDataReceived() {
		super.onVideoDataReceived();
		Log.i(LOGTAG, "First frame received");
		mActivity.updateLoadingSub();
	}

	@Override
	protected void onError(OpentokError error) {
		super.onError(error);
		showErrorDialog(error);
	}

	private void showErrorDialog(OpentokError error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this.mContext);

		alertDialogBuilder.setTitle("OpenTokRTC Error");
		alertDialogBuilder
				.setMessage(error.getMessage())
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								mActivity.finish();
							}
						});
		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

}
