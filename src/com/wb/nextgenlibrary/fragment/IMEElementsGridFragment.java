package com.wb.nextgenlibrary.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.InMovieExperience;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.MovieMetaData.IMEElementsGroup;
import com.wb.nextgenlibrary.data.TheTakeData.TheTakeProductFrame;
import com.wb.nextgenlibrary.interfaces.IMEVideoStatusListener;
import com.wb.nextgenlibrary.interfaces.NGEPlaybackStatusListener;
import com.wb.nextgenlibrary.model.AVGalleryIMEEngine;
import com.wb.nextgenlibrary.model.IMEEngine;
import com.wb.nextgenlibrary.model.TheTakeIMEEngine;
import com.wb.nextgenlibrary.network.TheTakeApiDAO;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.videoview.IVideoViewActionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 3/28/16.
 */
public class IMEElementsGridFragment extends AbstractGridViewFragment implements NGEPlaybackStatusListener, IMEVideoStatusListener {

	List<IMEElementsGroup> imeGroups;
    final List<IMEEngine> imeEngines = new ArrayList<IMEEngine>();
    long currentTimeCode = 0L;
    IVideoViewActionListener mainMovieListener = null;

    List<IMEDisplayObject> activeIMEs = new ArrayList<IMEDisplayObject>();

    private class IMEDisplayObject{
        final MovieMetaData.ExperienceData imeExperience;
        final Object imeObject;
        final String title;

        public IMEDisplayObject(MovieMetaData.ExperienceData experienceData, Object imeObject){
            if (experienceData.title != null)
                this.title = experienceData.title;
            else
                this.title = "";
            this.imeObject = imeObject;
            this.imeExperience = experienceData;
        }
    }

    Bundle savedInstanceState = null;
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imeGroups = NextGenExperience.getMovieMetaData().getImeElementGroups();
        this.savedInstanceState = savedInstanceState;
        for (IMEElementsGroup group : imeGroups){

            if (group.getExternalApiData() != null){
                if(MovieMetaData.THE_TAKE_MANIFEST_IDENTIFIER.equals(group.getExternalApiData().externalApiName)){
                    imeEngines.add(new TheTakeIMEEngine());
                }else
                    imeEngines.add(new AVGalleryIMEEngine(group.getIMEElementesList()));

            }else
                imeEngines.add(new AVGalleryIMEEngine(group.getIMEElementesList()));
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView()  {
        super.onDestroyView();
    }

    /***********************IMEVideoStatusListener***********************/
    @Override
    public void onVideoStartPlaying(){
        InMovieExperience playerActivity = null;
        if (getActivity() instanceof InMovieExperience) {
            playerActivity = (InMovieExperience) getActivity();
            playerActivity.pauseMovieForImeECPiece();
        }
    }
    @Override
    public void onFragmentDestroyed(){
        mainMovieListener = null;
        onVideoPause();
    }

    @Override
    public void onVideoPause(){
        InMovieExperience playerActivity = null;
        if (getActivity() instanceof InMovieExperience) {
            playerActivity = (InMovieExperience) getActivity();
            playerActivity.resumeMovideForImeECPiece();
        }
    }

    @Override
    public void onVideoResume(){
        onVideoStartPlaying();
    }

    @Override
    public void onVideoComplete(){
        onVideoPause();

    }
    /***********************************************/

    protected void onListItemClick(View v, int position, long id){
        if (position < 0 || position >= activeIMEs.size())
            return;
        IMEDisplayObject activeObj = activeIMEs.get(position);
        InMovieExperience playerActivity = null;
        if (getActivity() instanceof InMovieExperience) {
            playerActivity = (InMovieExperience) getActivity();
        }

        if (activeObj.imeObject instanceof MovieMetaData.IMEElement) {
            Object dataObj = ((MovieMetaData.IMEElement)activeObj.imeObject).imeObject ;
            if (dataObj instanceof MovieMetaData.PresentationDataItem) {
                MovieMetaData.PresentationDataItem headElement = (MovieMetaData.PresentationDataItem) dataObj;

                if (dataObj instanceof AVGalleryIMEEngine.IMECombineItem){
                    headElement = ((AVGalleryIMEEngine.IMECombineItem)dataObj).getAllPresentationItems().get(0);
                }

                if (playerActivity != null) {
                    if (headElement instanceof MovieMetaData.ECGalleryItem) {
                        ECGalleryViewFragment fragment = new ECGalleryViewFragment();
                        fragment.setContentViewId(R.layout.ime_gallery_frame_view);
                        fragment.setShouldShowCloseBtn(true);
                        /*if (NextGenExperience.getMovieMetaData().getInMovieExperience().style != null)
                            fragment.setBGImageUrl(NextGenExperience.getMovieMetaData().getInMovieExperience().style.getBackground().getImage().url);*/
                        fragment.setCurrentGallery((MovieMetaData.ECGalleryItem) headElement);
                        mainMovieListener = null;
                        playerActivity.transitMainFragment(fragment);
                        NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_IMAGE_GALLERY, ((MovieMetaData.ECGalleryItem) headElement).galleryId, null);
                        //playerActivity.pausMovieForImeECPiece();


                    } else if (headElement instanceof MovieMetaData.AudioVisualItem) {

                        if (((MovieMetaData.AudioVisualItem) headElement).isShareClip()){
                            ShareClipFragment fragment = new ShareClipFragment();
                            fragment.setShouldAutoPlay(playerActivity.isPlaying() ? false : true);
                            fragment.setShouldShowCloseBtn(true);
                            fragment.setExperienceAndIndex(activeObj.imeExperience, ((MovieMetaData.IMEElement) activeObj.imeObject).itemIndex);
                            /*if (NextGenExperience.getMovieMetaData().getInMovieExperience().style != null)
                                fragment.setBGImageUrl(NextGenExperience.getMovieMetaData().getInMovieExperience().style.getBackground().getImage().url);*/
                            fragment.setVideoStatusListener(this);
                            mainMovieListener = fragment;
                            playerActivity.transitMainFragment(fragment);
                            NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_CLIP_SHARE, ((MovieMetaData.AudioVisualItem) headElement).videoId, null);
                        }else {

							IMEDeletedSceneVideoFragment fragment = new IMEDeletedSceneVideoFragment();
							fragment.setShouldAutoPlay(playerActivity.isPlaying() ? false : true);
                            fragment.setShouldShowCloseBtn(true);
							fragment.setExperience(activeObj.imeExperience);
                            /*if (NextGenExperience.getMovieMetaData().getInMovieExperience().style != null)
                                fragment.setBGImageUrl(NextGenExperience.getMovieMetaData().getInMovieExperience().style.getBackground().getImage().url);*/
                            fragment.setAudioVisualItem((MovieMetaData.AudioVisualItem) headElement);
                            fragment.setVideoStatusListener(this);
                            mainMovieListener = fragment;
                            playerActivity.transitMainFragment(fragment);
                            NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_VIDEO, ((MovieMetaData.AudioVisualItem) headElement).videoId, null);
                        }
                    } else if (headElement instanceof MovieMetaData.LocationItem ||
                            (headElement instanceof AVGalleryIMEEngine.IMECombineItem && ((AVGalleryIMEEngine.IMECombineItem)headElement).isLocation() ) ){

                        // TODO: deal with multiple locations at the same timecode later on.
                        if (headElement instanceof AVGalleryIMEEngine.IMECombineItem){
                            headElement = ((AVGalleryIMEEngine.IMECombineItem)headElement).getAllPresentationItems().get(0);
                        }
                        IMEECMapViewFragment fragment = new IMEECMapViewFragment();
                        fragment.setShouldShowCloseBtn(true);
                        fragment.setLocationItem(activeObj.title, (MovieMetaData.LocationItem)headElement);
                        playerActivity.transitMainFragment(fragment);
                        mainMovieListener = null;
                        NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_LOCATION, headElement.getId(), null);
                    } else if (dataObj instanceof MovieMetaData.ShopItem) {

                        ShopItemDetailFragment fragment = new ShopItemDetailFragment();
                        fragment.setContentViewId(R.layout.ime_shop_product_view);
                        fragment.setProduct((MovieMetaData.ShopItem)dataObj);
                        fragment.setShouldShowCloseBtn(true);
                        mainMovieListener = null;
                        playerActivity.transitMainFragment(fragment);
                        NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_PRODUCT, ((MovieMetaData.ShopItem) headElement).getProductReportId(), null);
                    } else if (dataObj instanceof MovieMetaData.TriviaItem){
                        ECTrviaViewFragment fragment = new ECTrviaViewFragment();
                        fragment.setShouldShowCloseBtn(true);
                        fragment.setTriviaItem(activeObj.title, (MovieMetaData.TriviaItem)dataObj);
                        playerActivity.transitMainFragment(fragment);
                        mainMovieListener = null;
                        NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_TRIVIA, headElement.getId(), null);

                    } else if (dataObj instanceof MovieMetaData.TextItem) {
						ECTextViewFragment fragment = new ECTextViewFragment();
						fragment.setShouldShowCloseBtn(true);
						fragment.setTextItem(activeObj.title, (MovieMetaData.TextItem)dataObj);
						playerActivity.transitMainFragment(fragment);
                        mainMovieListener = null;
                        NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_TRIVIA, headElement.getId(), null);
					}
                }
            }
        } else if (activeObj.imeObject instanceof TheTakeProductFrame){
            if (playerActivity != null){
                ShopFrameProductsFragment fragment = new ShopFrameProductsFragment();
                fragment.setShouldShowCloseBtn(true);
                fragment.setTitleText(activeObj.title.toUpperCase());
                fragment.setFrameProductTime(((TheTakeProductFrame)activeObj.imeObject).frameTime);
                mainMovieListener = null;
                playerActivity.transitMainFragment(fragment);
            }
        }
    }

    public void onOrientationChange(int orientation){

        if (orientation == Configuration.ORIENTATION_LANDSCAPE){

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){

        }

    }

    protected int getNumberOfColumns(){
        return TabletUtils.isTablet() ? 2 : 1;
    }

    protected int getListItemCount(){
        return activeIMEs.size();
    }

    protected Object getListItemAtPosition(int i){
        return activeIMEs.get(i);
    }
    protected int getListItemViewId(){      // not using this
        return 0;
    }

    protected int getListItemViewId(int position){
        IMEDisplayObject activeObj = activeIMEs.get(position);

        int retId = R.layout.ime_grid_presentation_item;

        if (activeObj.imeObject instanceof MovieMetaData.IMEElement) {
            Object dataObj = ((MovieMetaData.IMEElement) activeObj.imeObject).imeObject;
            if (dataObj instanceof MovieMetaData.PresentationDataItem) {
                if (dataObj instanceof MovieMetaData.LocationItem ||
                        (dataObj instanceof AVGalleryIMEEngine.IMECombineItem && ((AVGalleryIMEEngine.IMECombineItem)dataObj).isLocation() ) )
                    retId = R.layout.ime_grid_presentation_item;
                else if (dataObj instanceof MovieMetaData.AudioVisualItem &&
                        ((MovieMetaData.AudioVisualItem)dataObj).isShareClip()) {
                    retId = R.layout.ime_grid_share_item;
                } else if (dataObj instanceof MovieMetaData.TextItem) {
					retId = R.layout.ime_grid_text_item;
				} else if (dataObj instanceof MovieMetaData.ShopItem) {
                    retId = R.layout.ime_grid_presentation_item;
                }
            }
        }else if (activeObj.imeObject instanceof TheTakeProductFrame){
            retId = R.layout.ime_grid_shop_item;
        }

        return retId;
    }

    static private int posterWidth = 0, posterHeight= 0;
    protected void fillListRowWithObjectInfo(int position, View rowView, Object item, boolean isSelected){
        IMEDisplayObject activeObj = (IMEDisplayObject)item;

        TextView titleText = (TextView)rowView.findViewById(R.id.ime_title);
        final TextView subText1 = (TextView)rowView.findViewById(R.id.ime_desc_text1);
        final ImageView poster = (ImageView)rowView.findViewById(R.id.ime_image_poster);

        titleText.setText(activeObj.title.toUpperCase());      // set a tag with the linked Experience Id

        if (posterHeight == 0 && posterWidth == 0){
            posterHeight = poster.getHeight() / 2;
            posterWidth = poster.getWidth() / 2;
        }

        if (activeObj.imeObject instanceof MovieMetaData.IMEElement) {
            Object dataObj = ((MovieMetaData.IMEElement) activeObj.imeObject).imeObject;
            if (dataObj instanceof MovieMetaData.PresentationDataItem) {
                Object tag = rowView.getTag(R.id.ime_title);
                //boolean isSameItem = tag != null && tag.equals(((MovieMetaData.PresentationDataItem) dataObj).getId());

                rowView.setTag(R.id.ime_title, ((MovieMetaData.PresentationDataItem) dataObj).getId());
                if (dataObj instanceof MovieMetaData.LocationItem ){

                    MovieMetaData.LocationItem locationItem = (MovieMetaData.LocationItem) dataObj;
                    if (locationItem != null ){
                        poster.setImageDrawable(null);
                        String imageUrl = "";
                        if (posterHeight > 0 && posterWidth > 0){
                            imageUrl = locationItem.getGoogleMapImageUrl(posterWidth, posterHeight);
                        } else {
                            imageUrl = locationItem.getGoogleMapImageUrl(320, 180);

                        }
                        NextGenGlide.load(getActivity(), imageUrl).centerCrop().into(poster);
                        
                    }

                } else if (poster != null) {
                    String imageUrl = ((MovieMetaData.PresentationDataItem) dataObj).getPosterImgUrl();

                    if (!StringHelper.isEmpty(imageUrl)) {
                        NextGenGlide.load(getActivity(), imageUrl).centerCrop()
                                .into(poster);
                        poster.setVisibility(View.VISIBLE);
                    } else {
                        poster.setVisibility(View.GONE);
                    }

                }

                ImageView playbutton = (ImageView)rowView.findViewById(R.id.ime_item_play_logo);
                if (playbutton != null){
                    playbutton.setVisibility((dataObj instanceof MovieMetaData.AudioVisualItem) ? View.VISIBLE : View.INVISIBLE);
                }

                if (subText1 != null && !subText1.getText().equals(((MovieMetaData.PresentationDataItem) dataObj).getGridItemDisplayName())) {
                    subText1.setText(((MovieMetaData.PresentationDataItem) dataObj).getGridItemDisplayName());
                    subText1.setTag(R.id.ime_title, "");
                }
            }
        }else if (activeObj.imeObject instanceof TheTakeProductFrame){
            final int frameTime = ((TheTakeProductFrame) activeObj.imeObject).frameTime;
            if  (rowView.getTag(R.id.ime_title) == null || !rowView.getTag(R.id.ime_title).equals(frameTime))
            {
                rowView.setTag(R.id.ime_title, ((TheTakeProductFrame) activeObj.imeObject).frameTime);
                if (poster != null) {

                    poster.setImageDrawable(null);

                }

                if (subText1 != null && (subText1.getTag(R.id.ime_title) == null || !subText1.getTag(R.id.ime_title).equals(frameTime))) {
                    subText1.setText("");
                    subText1.setTag(R.id.ime_title, frameTime);
                }

                TheTakeApiDAO.getFrameProducts(frameTime, new ResultListener<List<MovieMetaData.ShopItemInterface>>() {
                    @Override
                    public void onResult(final List<MovieMetaData.ShopItemInterface> result) {
                        if (subText1.getTag(R.id.ime_title).equals(frameTime)) {
                            if (result != null && result.size() > 0 && getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        subText1.setText(result.get(0).getProductName());
                                        poster.setBackgroundColor(getResources().getColor(android.R.color.white));
                                        NextGenGlide.load(getActivity(), result.get(0).getProductThumbnailUrl()).into(poster);

                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public <E extends Exception> void onException(E e) {
                        subText1.setText("");

                    }
                });
            }


        }

    }

    public void playbackStatusUpdate(final NextGenPlaybackStatus playbackStatus, final long timecode){
        currentTimeCode = timecode;

        List<IMEDisplayObject> objList = new ArrayList<IMEDisplayObject>();
        for(int i = 0 ; i< imeEngines.size(); i++){
            if (imeGroups.get(i).linkedExperience != null) {
                IMEEngine engine = imeEngines.get(i);
                boolean hasChanged = engine.computeCurrentIMEElement(currentTimeCode);
                List<Object> elements = engine.getCurrentIMEItems();


                if (elements != null && elements.size() > 0) {
                    for (Object element : elements) {
                        objList.add(new IMEDisplayObject(imeGroups.get(i).linkedExperience, element));
                    }
                }
            }
        }
        activeIMEs = objList;

        if (listAdaptor != null)
            listAdaptor.notifyDataSetChanged();

        if (mainMovieListener != null) {
            switch (playbackStatus) {
                case PAUSE:
                    mainMovieListener.onVideoPause();
                    break;
                case RESUME:
                    mainMovieListener.onVideoResume();
                    break;
            }
        }

    }

    protected String getHeaderText(){
        return "";
    }

    protected int getHeaderChildenCount(int header){
        if (header == 0)
            return activeIMEs.size();
        else
            return 0;
    }

    protected int getHeaderCount(){
        return 0;
    }

    protected int getStartupSelectedIndex(){
        return -1;
    }
}
