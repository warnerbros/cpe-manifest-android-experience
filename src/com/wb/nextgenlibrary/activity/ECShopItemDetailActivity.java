package com.wb.nextgenlibrary.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.fragment.ShopItemDetailFragment;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.StringHelper;

/**
 * Created by gzcheng on 2/17/17.
 */

public class ECShopItemDetailActivity extends AbstractNGEActivity {

	final static public String SHOP_ITEM_ID = "SHOP_ITEM_ID";

	ShopItemDetailFragment shopItemDetailFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ec_shop_item_activity_view);
		shopItemDetailFragment = (ShopItemDetailFragment) getSupportFragmentManager().findFragmentById(R.id.shop_item_detail_fragment);

		String shopItemId = getIntent().getStringExtra(SHOP_ITEM_ID);
		MovieMetaData.ShopItemInterface shopItem = null;
		if (!StringHelper.isEmpty(shopItemId)){
			shopItem = NextGenExperience.getMovieMetaData().getShopItemByAppId(shopItemId);
		}

		shopItemDetailFragment.setProduct(shopItem);
		shopItemDetailFragment.getProductDetail();
	}

	public String getBackgroundImgUri(){
		return NextGenExperience.getMovieMetaData().getExtraExperience().style.getBackground().getImage().url;
	}
	public String getLeftButtonText(){
		return getResources().getString(R.string.back_button_text);
	}
	public String getRightTitleImageUri(){
		return "";
	}
	public String getRightTitleText(){
		return "";
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (!TabletUtils.isTablet()) {
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				switchFullScreen(true);
			} else {
				switchFullScreen(false);
			}
		}

		//onRequestToggleFullscreen();
	}
	@Override
	public void onStart() {
		super.onStart();
		if (TabletUtils.isTablet())
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		else
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	@Override
	public void switchFullScreen(boolean bFullscreen){
		super.switchFullScreen(bFullscreen);
		if (!bFullscreen && getSupportActionBar() != null) {
			getSupportActionBar().hide();
		}
		shopItemDetailFragment.setFullScreen(bFullscreen);


	}

}
