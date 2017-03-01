package com.wb.nextgenlibrary.fragment;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.InMovieExperience;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.CustomMediaController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by gzcheng on 2/8/17.
 */

public abstract class AbstractCastMainMovieFragment extends AbstractNGEMainMovieFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener/*, DialogBuilder.DialogListener*/ {


	private ImageView ivPoster, ivPlayPause, ivCcToggle;
	private TextView tvTarget, tvTimeCur, tvTimeDur;
	private SeekBar sbSeek;
	private ProgressBar pbPlayPause;

	private boolean mUpdateSeekbar = true;

	private boolean playPauseMadeVisible = false;
	private boolean playPauseIsVisible = false;
	protected boolean mPlaybackCompleted = false;

	private String loadedTitle;

	protected long mStartTime = 0L;

	private ScheduledFuture<?> mFuture;
	private ScheduledThreadPoolExecutor mThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

	protected RemoteMediaClient remoteMediaClient;
	protected RemoteMediaClient.Listener castListener = null;

	protected CastSession mCastSession;
	protected SessionManager mSessionManager;

	ListPopupWindow subtitleListPopupWindow = null;
	AudioSubtitleListAdapter subtitleListAdapter = null;

	private long lastRecordedPlaybackPosition = 0L;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.cast_player_layout, container, false);
	}

	public void setCastControllers(CastSession castSession, RemoteMediaClient remoteMediaClient, SessionManager sessionManager){
		this.remoteMediaClient = remoteMediaClient;
		this.mCastSession = castSession;
		this.mSessionManager = sessionManager;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		ivPoster = (ImageView) view.findViewById(R.id.cast_poster);

		ivPlayPause = (ImageView) view.findViewById(R.id.cast_playpause);
		ivPlayPause.setOnClickListener(this);

		pbPlayPause = (ProgressBar) view.findViewById(R.id.cast_playpause_spinner);

		ivCcToggle = (ImageView) view.findViewById(R.id.cast_cc_toggle);
		ivCcToggle.setOnClickListener(this);
		ivCcToggle.setEnabled(false);

		tvTarget = (TextView) view.findViewById(R.id.cast_target);
		tvTimeCur = (TextView) view.findViewById(R.id.cast_time_cur);
		tvTimeDur = (TextView) view.findViewById(R.id.cast_time_dur);

		sbSeek = (SeekBar) view.findViewById(R.id.cast_seek);
		sbSeek.setOnSeekBarChangeListener(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		startCasting();
		mFuture = mThreadPoolExecutor.scheduleWithFixedDelay(mUpdateRunnable, 500, 500, TimeUnit.MILLISECONDS);
	}

	@Override
	public void onPause() {
		super.onPause();

		mFuture.cancel(false);
	}

	protected boolean isCasting(){
		boolean isCasting = false;

		if (getActivity() != null && getActivity() instanceof InMovieExperience){
			isCasting = ((InMovieExperience) getActivity()).isCasting();
		}

		return  isCasting;
	}

	public abstract MediaInfo getCastMediaInfo();

	public abstract long getStoredPlaybackPositon();

	public abstract String getMovieName();
	public abstract String getPosterUrl();

	protected void startCasting(){
		MediaInfo mediaInfo = getCastMediaInfo();
		if (mediaInfo == null)
			return;

		mSessionManager = CastContext.getSharedInstance(getActivity()).getSessionManager();
		mCastSession = mSessionManager.getCurrentCastSession();//AbstractStartupActivity.getInstance().getCastSession();
		remoteMediaClient = mCastSession.getRemoteMediaClient();

		MediaInfo currentCastMediaInfo = remoteMediaClient.getMediaInfo();
		if (areEqualMediaInfo(currentCastMediaInfo, mediaInfo) && (remoteMediaClient.isPlaying() || remoteMediaClient.isPaused()) ){
			updateStatus();
		}else if (mStartTime <= 0){
			mStartTime = getStoredPlaybackPositon();
			remoteMediaClient.load(mediaInfo, true, mStartTime);
		} else{

			remoteMediaClient.load(mediaInfo, true, mStartTime);
		}
	}

	public boolean areEqualMediaInfo(MediaInfo info1, MediaInfo info2){
		if (info1 == null || info2 == null)
			return false;
		if (info1.getMetadata().equals(info2.getMetadata()) &&
				info1.getContentType().equals(info2.getContentType()) )
			return true;
		else
			return false;
	}

	private Runnable mUpdateRunnable = new Runnable() {

		@Override
		public void run() {
			if (remoteMediaClient == null) {
				return;
			}
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (remoteMediaClient != null) {
						updateStatus();
					}
				}
			});
		}
	};

	private void updateStatus(){
		if (remoteMediaClient == null || mCastSession == null || mCastSession.getCastDevice() == null)
			return;

		if (getAvailableSubtitleList() != null && getAvailableSubtitleList().size() > 0)
			ivCcToggle.setVisibility(View.VISIBLE);
		else
			ivCcToggle.setVisibility(View.GONE);

		int playerState = remoteMediaClient.getPlayerState();
		String deviceName = mCastSession.getCastDevice().getFriendlyName();
		int idleReason = remoteMediaClient.getIdleReason();

		if (playerState == MediaStatus.PLAYER_STATE_BUFFERING
				|| playerState == MediaStatus.PLAYER_STATE_IDLE) {
			pbPlayPause.setVisibility(View.VISIBLE);
			ivPlayPause.setVisibility(View.GONE);
			playPauseIsVisible = false;

			tvTarget.setText(getResources().getString(R.string.loading_on_str, deviceName));
		} else if (!playPauseIsVisible && playerState != MediaStatus.PLAYER_STATE_IDLE) {
			pbPlayPause.setVisibility(View.GONE);
			ivPlayPause.setVisibility(View.VISIBLE);
			playPauseMadeVisible = true;
			playPauseIsVisible = true;

			tvTarget.setText(getResources().getString(R.string.playing_on_str, deviceName));
		}

		if (playerState == MediaStatus.PLAYER_STATE_PLAYING) {
			ivPlayPause.setImageResource(R.drawable.ic_media_pause_flat_large);
		} else if (playerState == MediaStatus.PLAYER_STATE_PAUSED) {
			ivPlayPause.setImageResource(R.drawable.ic_media_play_flat_large);
		} else if (playPauseMadeVisible && playerState == MediaStatus.PLAYER_STATE_IDLE
				&& idleReason != MediaStatus.IDLE_REASON_INTERRUPTED
				&& idleReason != MediaStatus.IDLE_REASON_NONE) {

		}

		int position = (int) (remoteMediaClient.getApproximateStreamPosition()) /1000;
		int duration = (int) (remoteMediaClient.getStreamDuration())/1000;

		if (duration <= 0)
			return;

		if (duration == position)
			mPlaybackCompleted = true;

		lastRecordedPlaybackPosition = remoteMediaClient.getApproximateStreamPosition();
		if (duration > 0) {
			if (mUpdateSeekbar) {
				sbSeek.setProgress((int) position);
				sbSeek.setMax((int) duration);
			}
			sbSeek.setVisibility(View.VISIBLE);
			tvTimeCur.setVisibility(View.VISIBLE);
			tvTimeCur.setText(secondsToHms(position));
			tvTimeDur.setVisibility(View.VISIBLE);
			tvTimeDur.setText(secondsToHms(duration));
		} else {
			sbSeek.setVisibility(View.INVISIBLE);
			tvTimeCur.setVisibility(View.INVISIBLE);
			tvTimeDur.setVisibility(View.INVISIBLE);
		}

		refreshMetadata();
		if (mPlaybackCompleted && completionListener != null){
			completionListener.onCompletion(null);
		}
	}

	private void refreshMetadata() {

		if (remoteMediaClient == null) {
			return;
		}

		String title = getMovieName();
		if (!StringHelper.isEmpty(title) ) {
			getActivity().setTitle(title);
			loadedTitle = title;
		}

		int timeCode = getCurrentPosition();
		String posterUrl = getPosterUrl();
		if (NextGenExperience.getMovieMetaData().hasShareClipExp() && getActivity() instanceof InMovieExperience){
			posterUrl = NextGenExperience.getMovieMetaData().getClosestShareClipImage(timeCode);
		}
		if (!StringHelper.isEmpty(posterUrl) && ivPoster != null){
			Glide.with(getActivity()).load(posterUrl).fitCenter().into(ivPoster);
		}
		ivCcToggle.setEnabled(mCastSession.getRemoteMediaClient().getMediaInfo().getMediaTracks() != null);
		ivCcToggle.setSelected(mCastSession.getRemoteMediaClient().getMediaStatus().getActiveTrackIds() != null);
	}

	private static String secondsToHms(int inputSeconds) {
		int hours = inputSeconds / 3600;
		int minutes = (inputSeconds % 3600) / 60;
		int seconds = inputSeconds % 60;

		if (hours > 0) {
			return String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds);
		}

		return String.format(Locale.US, "%d:%02d", minutes, seconds);
	}

	public static class NGESubtitleElement{
		final int trackNumber;
		final String displayName;
		public NGESubtitleElement(int track, String name){
			trackNumber = track;
			displayName = name;
		}
	}

	abstract protected List<NGESubtitleElement> getAvailableSubtitleList();

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cast_playpause) {
			if (remoteMediaClient != null) {
				if (remoteMediaClient.isPlaying())
					remoteMediaClient.pause();
				else
					remoteMediaClient.play();
			}
		} else if (v.getId() == R.id.cast_cc_toggle) {

			if (subtitleListPopupWindow == null){
				if (getAvailableSubtitleList() == null || getAvailableSubtitleList().size() == 0) {
					ivCcToggle.setVisibility(View.GONE);
				} else {
					ivCcToggle.setVisibility(View.VISIBLE);

					List<String> list = new ArrayList<>();
					for (NGESubtitleElement elements : getAvailableSubtitleList()) {
						list.add(elements.displayName);
					}
					list.add(0, getResources().getString(R.string.subtitle_off));
					subtitleListAdapter = new AudioSubtitleListAdapter(list);

					subtitleListPopupWindow = new ListPopupWindow(getActivity());
					subtitleListPopupWindow.setModal(false);
					subtitleListPopupWindow.setAdapter(subtitleListAdapter);

					subtitleListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							subtitleListAdapter.setSelection(position);

							int selectedTrack = -1;
							if (position != 0 && position <= getAvailableSubtitleList().size()) {
								selectedTrack = getAvailableSubtitleList().get(position - 1).trackNumber;
							}

							if (mCastSession.getRemoteMediaClient().getMediaInfo().getMediaTracks() != null){

								if (selectedTrack == -1){
									mCastSession.getRemoteMediaClient().setActiveMediaTracks(new long[]{});
								}else
									mCastSession.getRemoteMediaClient().setActiveMediaTracks(new long[]{selectedTrack});

							}

							subtitleListPopupWindow.dismiss();
						}
					});
				}
			}

			subtitleListPopupWindow.setAnchorView(v);
			subtitleListPopupWindow.setContentWidth(measureContentWidth(subtitleListAdapter));
			subtitleListPopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
			subtitleListPopupWindow.show();



		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mUpdateSeekbar = false;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mUpdateSeekbar = true;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser && remoteMediaClient != null) {
			remoteMediaClient.seek(progress*1000);
		}
	}

	public void setCustomMediaController(CustomMediaController customMC){

	}

	@Override
	public void setResumeTime(int resumeTime){
		mStartTime = resumeTime;
		if (remoteMediaClient != null) {
			/*remoteMediaClient.l
			player.seekTo(resumeTime);
			if (isPausedByUser)
				pause();*/
		}else
			mStartTime = resumeTime;

	}

	@Override
	public int getCurrentPosition(){
		if (remoteMediaClient != null){
			return (int)remoteMediaClient.getApproximateStreamPosition();
		} else
			return (int)lastRecordedPlaybackPosition;
	}

	@Override
	public int getDuration(){
		if (remoteMediaClient != null){
			return (int)remoteMediaClient.getStreamDuration();
		} else
			return 0;
	}

	@Override
	public boolean isPlaying(){
		if (remoteMediaClient != null){
			return remoteMediaClient.isPlaying();
		} else{
			return false;
		}
	}

	@Override
	public void pause(){
		if (remoteMediaClient != null){
			remoteMediaClient.pause();
		}
	}

	@Override
	public void resumePlayback(){
		if (remoteMediaClient != null){
			remoteMediaClient.play();
		}
	}

	@Override
	public void pauseForIME(){
		if (remoteMediaClient != null){
			remoteMediaClient.pause();
		}

	}
	/*
		this function will be called when the ime video finished playback, so if you are using exoplayer, you can recreate the player and resume its playback
	 */

	@Override
	public void resumePlaybackFromIME(){
		if (remoteMediaClient != null){
			remoteMediaClient.play();
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		mFuture.cancel(false);
	}


	class AudioSubtitleListAdapter extends BaseAdapter {
		List<String> items;
		int selectedIndex = 0;
		AudioSubtitleListAdapter(List<String> items) {
			this.items = items;
		}

		public void setSelection(int index){
			selectedIndex = index;
			notifyDataSetChanged();
		}


		@Override
		public int getCount() {
			return items == null ? 0 : items.size();
		}


		@Override
		public String getItem(int index) {
			if (items != null && index <items.size()){
				return items.get(index);
			}else
				return null;
		}


		@Override
		public long getItemId(int position) {
			return position;
		}


		@Override
		public View getView(final int index, View view, ViewGroup arg2) {
			View target = null;

			if (view != null){
				target = view;
			}else{
				LayoutInflater inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				target = inflate.inflate(R.layout.subtitle_spinner_item, arg2, false);

			}


			TextView tView = (TextView)target.findViewById(android.R.id.text1);
			tView.setText(getItem(index));

			if (index == selectedIndex){
				tView.setTextColor(getResources().getColor(R.color.drawer_yellow));
			}else
				tView.setTextColor(getResources().getColor(R.color.white));

			return target;
		}

	}

	private int measureContentWidth(ListAdapter listAdapter) {
		ViewGroup mMeasureParent = null;
		int maxWidth = 0;
		View itemView = null;
		int itemType = 0;

		final ListAdapter adapter = listAdapter;
		final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		final int count = adapter.getCount();
		for (int i = 0; i < count; i++) {
			final int positionType = adapter.getItemViewType(i);
			if (positionType != itemType) {
				itemType = positionType;
				itemView = null;
			}

			if (mMeasureParent == null) {
				mMeasureParent = new FrameLayout(getActivity());
			}

			itemView = adapter.getView(i, itemView, mMeasureParent);
			itemView.measure(widthMeasureSpec, heightMeasureSpec);

			final int itemWidth = itemView.getMeasuredWidth();

			if (itemWidth > maxWidth) {
				maxWidth = itemWidth;
			}
		}

		return maxWidth;
	}

}
