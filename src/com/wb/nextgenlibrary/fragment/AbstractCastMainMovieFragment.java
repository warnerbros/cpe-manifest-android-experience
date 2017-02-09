package com.wb.nextgenlibrary.fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.NextGenPlayer;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.CustomMediaController;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by gzcheng on 2/8/17.
 */

public abstract class AbstractCastMainMovieFragment extends AbstractNextGenMainMovieFragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener/*, DialogBuilder.DialogListener*/ {

	/////////////////////
	// MediaInfo Constant tags

	public static final String MEDIAINFO_TAG_RIGHTID = "rightId";
	public static final String MEDIAINFO_TAG_STREAMID = "streamId";
	public static final String MEDIAINFO_TAG_USERID = "userId";
	public static final String MEDIAINFO_TAG_ASSETID = "assetId";
	public static final String MEDIAINFO_TAG_AUTHTOKEN = "authToken";
	public static final String MEDIAINFO_TAG_LICENSEURL = "licenseUrl";
	public static final String MEDIAINFO_TAG_LICENSECUSTOMDATA = "licenseCustomData";
	public static final String MEDIAINFO_TAG_SELECTED_SUBTITLE_TRACK = "selectedSubtitle";

	private ImageView ivPoster, ivPlayPause, ivCcToggle;
	private TextView tvTarget, tvTimeCur, tvTimeDur;
	private SeekBar sbSeek;
	private ProgressBar pbPlayPause;

	private boolean mUpdateSeekbar = true;

	private boolean playPauseMadeVisible = false;
	private boolean playPauseIsVisible = false;
	protected boolean mPlaybackCompleted = false;

	private String loadedTitle;
	private Uri loadedImageUri;


	protected String movieUrl = "", posterUrl = "", movieName = "";
	protected long mStartTime = 0L;

	private ScheduledFuture<?> mFuture;
	private ScheduledThreadPoolExecutor mThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);

	protected RemoteMediaClient remoteMediaClient;
	protected RemoteMediaClient.Listener castListener = null;

	protected CastSession mCastSession;
	protected SessionManager mSessionManager;

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

		//FlixsterApplication.getCastControl().onStart();

	}

	@Override
	public void onPause() {

		//savePlayPosition(getCurrentPosition());
		super.onPause();

		mFuture.cancel(false);
	}

	public void setVideoInfo(String movieName, String movieUrl, String posterUrl){
		this.movieName = movieName;
		this.movieUrl = movieUrl;
		this.posterUrl = posterUrl;
		if (ivPoster != null && !StringHelper.isEmpty(this.posterUrl)){
			Glide.with(getActivity()).load(this.posterUrl).fitCenter().into(ivPoster);

		}
	}

	protected boolean isCasting(){
		boolean isCasting = false;

		if (getActivity() != null && getActivity() instanceof NextGenPlayer){
			isCasting = ((NextGenPlayer) getActivity()).isCasting();
		}

		return  isCasting;
	}

	public abstract MediaInfo getCastMediaInfo();

	public abstract long getStoredPlaybackPositon();


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
		}
	}
	/*
	public void startCasting(){

		if ((!StringHelper.isEmpty(movieUrl) || stream != null) && isCasting()) {
			MediaInfo mediaInfo = null;

			if (stream == null) {
				MediaMetadata mediaMetadata = new MediaMetadata();
				mediaMetadata.putString(MediaMetadata.KEY_TITLE, movieName);
				if (!StringHelper.isEmpty(posterUrl)) {
					mediaMetadata.addImage(new WebImage(Uri.parse(posterUrl)));
				}
				String url = "http://wb-extras.warnerbros.com/extrasplus/prod/Manifest/BatmanvSuperman/streams/BVS_506_DOOMSDAY01_.m3u8";
				mediaInfo = new MediaInfo.Builder(url)
						.setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
						.setContentType("application/x-mpegURL")
						//"application/vnd.ms-sstr+xml""application/dash+xml""application/x-mpegurl""application/dash+xml"
						.setMetadata(mediaMetadata)
						.build();
			} else {
				try {

					if (stream.streamAssetList != null && stream.streamAssetList.size() > 0){
						streamAsset = stream.streamAssetList.get(0);
					}
					if ( streamAsset != null) {
						JSONObject metadataJson = new JSONObject();
						metadataJson.put(MEDIAINFO_TAG_RIGHTID, currentPlaybackContent.getRightsId());
						metadataJson.put(MEDIAINFO_TAG_STREAMID, streamAsset.streamId);
						metadataJson.put(MEDIAINFO_TAG_USERID, FlixsterApplication.getUserID());
						metadataJson.put(MEDIAINFO_TAG_ASSETID, currentPlaybackContent.getContentId());

						String licenseUrl = StringHelper.isEmpty(streamAsset.drmServerUrl)? F.getBaseURL() + F.PLAYREADY_SERVER_KEY_STREAM: streamAsset.drmServerUrl;
						metadataJson.put(MEDIAINFO_TAG_LICENSEURL, licenseUrl);
						if (!StringHelper.isEmpty(streamAsset.playbackParams))
							metadataJson.put(MEDIAINFO_TAG_LICENSECUSTOMDATA, streamAsset.playbackParams);


						metadataJson.put(MEDIAINFO_TAG_SELECTED_SUBTITLE_TRACK, 0);
						if (!StringHelper.isEmpty(streamAsset.amsLicenseToken))
							metadataJson.put(MEDIAINFO_TAG_AUTHTOKEN, streamAsset.amsLicenseToken);
						else if (!StringHelper.isEmpty(FlixsterApplication.getAuthToken()) && ContentLocker.Lasp.WB.toString().equals(streamAsset.lasp))
							metadataJson.put(MEDIAINFO_TAG_AUTHTOKEN, FlixsterApplication.getAuthToken());

						MediaMetadata mediaMetadata = new MediaMetadata();
						mediaMetadata.putString(MediaMetadata.KEY_TITLE, currentPlaybackContent.getName());

						String posterUrl = currentPlaybackContent.getChromeCastPosterURL();

						if (posterUrl != null) {
							mediaMetadata.addImage(new WebImage(Uri.parse(posterUrl)));
						}
						String contentType = "video/mp4";
						if (!streamAsset.drm.equals("PLAY_READY")){
							contentType = "application/dash+xml";
						}

						mediaInfo = new MediaInfo.Builder(streamAsset.fileLocation)
								.setCustomData(metadataJson)
								.setContentType(contentType)
								.setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
								.setMetadata(mediaMetadata).build();


						//flixMovieId = castMediaInfo.rightsId;
					}
				}catch (Exception ex){

				}

			}

			MediaInfo currentCastMediaInfo = remoteMediaClient.getMediaInfo();
			if (areEqualMediaInfo(currentCastMediaInfo, mediaInfo) && (remoteMediaClient.isPlaying() || remoteMediaClient.isPaused()) ){
				updateStatus();
			}else if (currentPlaybackContent != null){
				mStartTime = loadPlayPosition(currentPlaybackContent.getRightsId());
				remoteMediaClient.load(mediaInfo, true, mStartTime);
			}
		}
	}*/

	public static boolean areEqualMediaInfo(MediaInfo info1, MediaInfo info2){
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

		}

		int position = (int) (remoteMediaClient.getApproximateStreamPosition()) /1000;
		int duration = (int) (remoteMediaClient.getStreamDuration())/1000;

		if (duration <= 0)
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
		if (mPlaybackCompleted && completionListener != null){
			completionListener.onCompletion(null);
		}
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
		ivCcToggle.setEnabled(mCastSession.getRemoteMediaClient().getMediaInfo().getMediaTracks() != null);
		ivCcToggle.setSelected(mCastSession.getRemoteMediaClient().getMediaStatus().getActiveTrackIds() != null);

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
			if (mCastSession.getRemoteMediaClient().getMediaInfo().getMediaTracks() != null){
				if (mCastSession.getRemoteMediaClient().getMediaStatus().getActiveTrackIds() != null)
					mCastSession.getRemoteMediaClient().setActiveMediaTracks(new long[]{});
				else
					mCastSession.getRemoteMediaClient().setActiveMediaTracks(new long[]{1});

				//mCastSession.getRemoteMediaClient().set
			}
				/*if (mRemote != null && mRemote.isCaptionsAvailable()) {
					mRemote.setCaptionsEnabled(!mRemote.isCaptionsEnabled());
				}*/
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
			return 0;
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

}
