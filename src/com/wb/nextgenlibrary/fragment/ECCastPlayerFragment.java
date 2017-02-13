package com.wb.nextgenlibrary.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.MediaRouteButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.squareup.picasso.Picasso;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.interfaces.ECVideoPlayerInterface;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by gzcheng on 1/27/17.
 */

public class ECCastPlayerFragment extends Fragment implements ECVideoPlayerInterface, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

	protected TextView countDownTextView;
	protected View countDownCountainer;
	protected ProgressBar countDownProgressBar;
	private ImageView ivPoster, ivPlayPause, ivCcToggle;
	private TextView tvTarget, tvTimeCur, tvTimeDur;
	private SeekBar sbSeek;
	private ProgressBar pbPlayPause;

	private MovieMetaData.AudioVisualItem selectedAVItem = null;
	boolean shouldAutoPlay = false;
	ECVideoViewFragment.ECVideoListAdaptor ecsAdaptor = null;

	private boolean mUpdateSeekbar = true;
	private boolean playPauseMadeVisible = false;
	private boolean playPauseIsVisible = false;
	private boolean mPlaybackCompleted = false;


	private String movieUrl = "", posterUrl = "", movieName = "";
	private long mStartTime = 0L;
	private String loadedTitle;
	private Uri loadedImageUri;

	private ScheduledFuture<?> mFuture;
	private ScheduledThreadPoolExecutor mThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

	RemoteMediaClient remoteMediaClient;
	RemoteMediaClient.Listener castListener = null;
	private CastSession mCastSession;
	private SessionManager mSessionManager;

	private int resumeTime = -1;

	//MediaRouteButton mMediaRouteButton;


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

		/*ivCcToggle = (ImageView) view.findViewById(R.id.cast_cc_toggle);
		ivCcToggle.setOnClickListener(this);
		ivCcToggle.setEnabled(false);*/

		tvTarget = (TextView) view.findViewById(R.id.cast_target);
		tvTimeCur = (TextView) view.findViewById(R.id.cast_time_cur);
		tvTimeDur = (TextView) view.findViewById(R.id.cast_time_dur);

		sbSeek = (SeekBar) view.findViewById(R.id.cast_seek);
		sbSeek.setOnSeekBarChangeListener(this);

		countDownCountainer = view.findViewById(R.id.count_down_container);
		countDownTextView = (TextView) view.findViewById(R.id.count_down_text_view);
		countDownProgressBar = (ProgressBar) view.findViewById(R.id.count_down_progressBar);

		if (countDownCountainer != null)
			countDownCountainer.setVisibility(View.INVISIBLE);
		if (countDownProgressBar != null){
			countDownProgressBar.setMax(ECVideoViewFragment.COUNT_DOWN_SECONDS);
		}

/*
		mMediaRouteButton = (MediaRouteButton) view.findViewById(R.id.media_route_button);
		CastButtonFactory.setUpMediaRouteButton(NextGenExperience.getApplicationContext(), mMediaRouteButton);*/

	}


	@Override
	public void onResume() {
		super.onResume();
		startCasting();
		mFuture = mThreadPoolExecutor.scheduleWithFixedDelay(mUpdateRunnable, 500, 500, TimeUnit.MILLISECONDS);

		//FlixsterApplication.getCastControl().onStart();

	}

	@Override
	public void onDestroy(){
		super.onDestroy();
	}

	@Override
	public void onPause() {

		super.onPause();

		mFuture.cancel(false);
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
		if (mCastSession == null || !mCastSession.isConnected()) {
			mFuture.cancel(true);
			return;
		}

		if (mPlaybackCompleted)
			return;

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
						/*if (!startedPlaying){
							ContentLocker r = FlixsterApplication.getCurrentPlayableContent();
							if (r != null && r.isRental() && !hasRentalClockStarted){
								StreamDAO.tickRentalClock(r.getRightsId());
							}
							startedPlaying = true;
						}*/
			ivPlayPause.setImageResource(R.drawable.ic_media_pause_flat_large);
		} else if (playerState == MediaStatus.PLAYER_STATE_PAUSED) {
			ivPlayPause.setImageResource(R.drawable.ic_media_play_flat_large);
		} else if (playPauseMadeVisible && playerState == MediaStatus.PLAYER_STATE_IDLE
				&& idleReason != MediaStatus.IDLE_REASON_INTERRUPTED
				&& idleReason != MediaStatus.IDLE_REASON_NONE) {
			// Interrupted state happens when we transition from playing to new content
			// None state is what happens when we play over an existing connection
						/*FlixsterLogger.d(F.TAG_CAST,
								"CastPlayerActivity.mUpdateRunnable.run ending activity due to idle state "
										+ idleReason);
						finish();*/
		}

		int position = (int) (remoteMediaClient.getApproximateStreamPosition() ) / 1000;
		int duration = (int) (remoteMediaClient.getStreamDuration() ) / 1000;
		if (duration == -1 || duration == 0)
			return;
		if (duration == position)
			mPlaybackCompleted = true;

		if (duration > 0) {
			if (mUpdateSeekbar) {
				sbSeek.setProgress((int) position);
				sbSeek.setMax((int) duration);
			}
			sbSeek.setVisibility(View.VISIBLE);
						/*if (position - playbackPosition > 9 || position < playbackPosition){
							savePlayPosition(position);
						}*/
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

		if (mPlaybackCompleted){
			if (ecsAdaptor != null) {
				if (ecsAdaptor.shouldStartCountDownForNext()){
					//new
					startRepeatingTask();
				}else {
					if (getActivity() != null) {                // only do this if the activity is still alive
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								/*videoView.seekTo(0);
								if (previewFrame != null) {
									if (!StringHelper.isEmpty(selectedAVItem.getPosterImgUrl())) {
										Picasso.with(getActivity()).load(selectedAVItem.getPreviewImageUrl()).fit().into(previewImageView);
									}
									previewFrame.setVisibility(View.VISIBLE);
								}
								if (previewPlayBtn != null)
									previewPlayBtn.setVisibility(View.VISIBLE);*/
							}
						});
					}
				}
			}
		}
	}

	private Handler mHandler = new Handler();
	int counter = ECVideoViewFragment.COUNT_DOWN_SECONDS;
	Runnable mStatusChecker = new Runnable() {
		@Override
		public void run() {
			boolean shouldFinish = false;

			try {

				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (countDownCountainer != null) {
								if (counter >= 0) {
									countDownCountainer.setVisibility(View.VISIBLE);
									countDownTextView.setText(String.format(getResources().getString(R.string.count_down_text), Integer.toString(counter)) );
									countDownProgressBar.setProgress(counter);
								}else {
									countDownTextView.setText("");
									countDownCountainer.setVisibility(View.GONE);
								}
							}

						}
					});

					if (counter < 0){
						ecsAdaptor.playbackFinished();
						stopRepeatingTask();
						shouldFinish = true;
					}
					counter = counter - 1;
				}
			} finally {
				// 100% guarantee that this always happens, even if
				// your update method throws an exception
				if (!shouldFinish)
					mHandler.postDelayed(mStatusChecker, 1000);
			}
		}
	};

	void startRepeatingTask() {
		counter = 5;
		mStatusChecker.run();
	}

	void stopRepeatingTask() {
		mHandler.removeCallbacks(mStatusChecker);
	}

	private void refreshMetadata() {

		if (remoteMediaClient == null) {
			return;
		}

		String title = movieName;
		if (title != null && !title.equals(movieName) ) {
			getActivity().setTitle(title);
			loadedTitle = title;
		}

		Uri imageUrl = Uri.parse(posterUrl);
		if (imageUrl != null && !imageUrl.equals(imageUrl)) {


			loadedImageUri = imageUrl;
			if (ivPoster != null && !StringHelper.isEmpty(this.posterUrl)){
				Glide.with(getActivity()).load(this.posterUrl).fitCenter().into(ivPoster);

			}

		}

		//ivCcToggle.setEnabled(mRemote.isCaptionsAvailable());
		//ivCcToggle.setSelected(mRemote.isCaptionsEnabled());
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

	public boolean isCasting(){
		if (mCastSession == null && mSessionManager != null)
			mCastSession = mSessionManager.getCurrentCastSession();
		return mCastSession != null;
	}

	public void startCasting(){

		mPlaybackCompleted = false;
		if ((!StringHelper.isEmpty(movieUrl) || selectedAVItem != null) && isCasting()) {
			MediaInfo mediaInfo = null;

			if (selectedAVItem != null) {/*
				MediaMetadata mediaMetadata = new MediaMetadata();
				mediaMetadata.putString(MediaMetadata.KEY_TITLE, movieName);
				if (!StringHelper.isEmpty(posterUrl)) {
					mediaMetadata.addImage(new WebImage(Uri.parse(posterUrl)));
				}
				String url = "http://wb-extras.warnerbros.com/extrasplus/prod/Manifest/BatmanvSuperman/streams/BVS_506_DOOMSDAY01_.m3u8";
				//"http://distribution.bbb3d.renderfarming.net/video/mp4/bbb_sunflower_1080p_60fps_normal.mp4";
				//"https://cpe-extras-warnerbros.akamaized.net/121815a0-fa95-41e0-a415-2db93c991224/movies_all_access_sizzle.ism/manifest";
				//"http://wb-extras.warnerbros.com/extrasplus/prod/Manifest/BatmanvSuperman/streams/BVS_506_DOOMSDAY01_.m3u8";
				//"https://cpe-extras-warnerbros.akamaized.net/121815a0-fa95-41e0-a415-2db93c991224/movies_all_access_sizzle.ism/manifest(format=m3u8-aapl)";
				//"https://cpe-extras-warnerbros.akamaized.net/121815a0-fa95-41e0-a415-2db93c991224/movies_all_access_sizzle.ism/manifest(format=mpd-time-csf)";
				//"https://cpe-extras-warnerbros.akamaized.net/121815a0-fa95-41e0-a415-2db93c991224/movies_all_access_sizzle.ism/manifest(format=m3u8-aapl)";
				mediaInfo = new MediaInfo.Builder(url)
						.setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
						.setContentType("application/x-mpegURL")//"application/vnd.ms-sstr+xml""application/dash+xml""application/x-mpegurl""application/dash+xml")
						.setMetadata(mediaMetadata)
						//.setStreamDuration(mSelectedMedia.getDuration() * 1000)
						.build();
			} else {*/
				try {


					String clipUrl = selectedAVItem.getVideoUrl();
					if (!StringHelper.isEmpty(clipUrl)) {
						JSONObject metadataJson = NextGenExperience.getNextGenEventHandler().createECVideoCastMetaData(clipUrl, selectedAVItem.getTitle(), selectedAVItem.getPosterImgUrl());


						MediaMetadata mediaMetadata = new MediaMetadata();
						mediaMetadata.putString(MediaMetadata.KEY_TITLE, selectedAVItem.getTitle());

						String posterUrl = selectedAVItem.getPosterImgUrl();

						if (posterUrl != null) {
							Glide.with(getActivity()).load(posterUrl).fitCenter().into(ivPoster);
							mediaMetadata.addImage(new WebImage(Uri.parse(posterUrl)));
						}
						String contentType = "video/mp4";
						if (clipUrl.toLowerCase().endsWith(".mpd")) {
							contentType = "application/dash+xml";
						} else if (clipUrl.toLowerCase().endsWith(".m3u8")) {
							contentType = "application/x-mpegurl";
						}

						mediaInfo = new MediaInfo.Builder(clipUrl)
								.setCustomData(metadataJson)
								.setContentType(contentType)
								.setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
								.setMetadata(mediaMetadata).build();

					}
						//flixMovieId = castMediaInfo.rightsId;

				}catch (Exception ex){

				}

			}

			mSessionManager = CastContext.getSharedInstance(getActivity()).getSessionManager();
			/*mSessionManager.addSessionManagerListener(mSessionManagerListener);
			mSessionManager.addCastStateListener(new CastStateListener() {
				@Override
				public void onCastStateChanged(int i) {
					switch (i){

					}

				}
			});*/
			mCastSession = mSessionManager.getCurrentCastSession();//AbstractStartupActivity.getInstance().getCastSession();
			remoteMediaClient = mCastSession.getRemoteMediaClient();



			remoteMediaClient.addListener(castListener);

			MediaInfo currentCastMediaInfo = remoteMediaClient.getMediaInfo();
			if (areEqualMediaInfo(currentCastMediaInfo, mediaInfo) && (remoteMediaClient.isPlaying() || remoteMediaClient.isPaused()) ){
				updateStatus();
			}else if (selectedAVItem != null){
				if (resumeTime > 0) {
					remoteMediaClient.load(mediaInfo, true, resumeTime);
				}else {
					remoteMediaClient.load(mediaInfo, shouldAutoPlay, mStartTime);
				}
				shouldAutoPlay = true;
			}
		}
	}

	private boolean areEqualMediaInfo(MediaInfo info1, MediaInfo info2){
		if (info1 == null || info2 == null)
			return false;
		if (info1.getMetadata().equals(info2.getMetadata()) &&
				info1.getContentType().equals(info2.getContentType()) )
			return true;
		else
			return false;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cast_playpause) {
			if (remoteMediaClient != null) {
				if (remoteMediaClient.isPlaying())
					remoteMediaClient.pause();
				else
					remoteMediaClient.play();
			}

			/*case R.id.cast_cc_toggle:
				if (mRemote != null && mRemote.isCaptionsAvailable()) {
					mRemote.setCaptionsEnabled(!mRemote.isCaptionsEnabled());
				}
				break;*/
		}
	}

	/***********************OnSeekBarChangeListener********************/
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
	/******************************************************************/

	public void setAudioVisualItem(MovieMetaData.AudioVisualItem avItem){
		if (avItem != null) {
			selectedAVItem = avItem;
			if (countDownCountainer != null)
				countDownCountainer.setVisibility(View.INVISIBLE);

			startCasting();

			/*if (videoView != null) {

				if (!shouldAutoPlay) {
					if(videoView.isPlaying())
						videoView.stopPlayback();
					if (previewFrame != null) {
						previewFrame.setVisibility(View.VISIBLE);
						previewPlayBtn.setVisibility(View.GONE);
					}
					if (!StringHelper.isEmpty(selectedAVItem.getPosterImgUrl())) {

						Picasso.with(getActivity()).load(selectedAVItem.getPreviewImageUrl()).fit().into(previewImageView);

					}
				}else{
					if (previewFrame != null) {
						previewFrame.setVisibility(View.GONE);
						previewPlayBtn.setVisibility(View.GONE);
					}
				}
				videoView.setVideoURI(Uri.parse(avItem.getVideoUrl()));
				if (mediaController != null) {
					mediaController.reset();
				}
			}else{
				bSetOnResume = true;
			}*/
		}
	}
	public void setEcsAdaptor(ECVideoViewFragment.ECVideoListAdaptor adaptor){
		ecsAdaptor = adaptor;
	}

	public void setShouldAutoPlay(boolean shouldAutoPlay){
		this.shouldAutoPlay = shouldAutoPlay;
	}

	public void stopPlayback(){
		if (remoteMediaClient != null) {
			remoteMediaClient.stop();
		}
		//videoView.stopPlayback();
	}

	public void onFullScreenChange(boolean bFullscreen){}

	@Override
	public void setResumeTimeMillisecond(int resumeTime){
		this.resumeTime = resumeTime;
	}

	@Override
	public int getCurrentPlaybackTimeMillisecond(){
		if (remoteMediaClient != null)
			return (int) (remoteMediaClient.getApproximateStreamPosition() );
		else
			return 0;
	}

}
