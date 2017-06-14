package com.wb.nextgenlibrary.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.TheTakeData.ShopCategory;
import com.wb.nextgenlibrary.fragment.ShopCategoryGridFragment;
import com.wb.nextgenlibrary.fragment.ShopItemDetailFragment;
import com.wb.nextgenlibrary.interfaces.ContentViewFullscreenRequestInterface;
import com.wb.nextgenlibrary.interfaces.NGEFragmentTransactionInterface;
import com.wb.nextgenlibrary.network.TheTakeApiDAO;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NGEFragmentTransactionEngine;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gzcheng on 4/8/16.
 */
public class ShopCategoryActivity extends AbstractNGEActivity implements NGEFragmentTransactionInterface, ContentViewFullscreenRequestInterface {

    List<ShopCategory> categories = new ArrayList<ShopCategory>();

    ExpandableListView categoryListView;
    ShopCategoryExpandableListAdapter categoryListAdaptor;
    ShopCategoryGridFragment gridFrament;

    FrameLayout leftFrame;
    NGEFragmentTransactionEngine fragmentTransactionEngine;

    String titleText = null;

    MovieMetaData.ExperienceData ecGroupData;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.shop_category_view);

        String groupId = getIntent().getStringExtra(F.ID);
        if (!StringHelper.isEmpty(F.ID)){
            ecGroupData = NextGenExperience.getMovieMetaData().findExperienceDataById(groupId);
        }

        gridFrament = new ShopCategoryGridFragment();

        categoryListView = (ExpandableListView)findViewById(R.id.the_take_category_list);
        if (categoryListView != null) {

            categoryListAdaptor = new ShopCategoryExpandableListAdapter();
            categoryListView.setAdapter(categoryListAdaptor);
            categoryListView.setOnChildClickListener(categoryListAdaptor);
            categoryListView.setOnGroupCollapseListener(categoryListAdaptor);
            categoryListView.setOnGroupExpandListener(categoryListAdaptor);
            categoryListView.setOnGroupClickListener(categoryListAdaptor);
            requestCategoryList();
        }

        leftFrame = (FrameLayout)findViewById(R.id.category_left_frame);



        fragmentTransactionEngine = new NGEFragmentTransactionEngine(this);
        transitLeftFragment(gridFrament);
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
    public void onDestroy(){
        categoryListAdaptor = null;

        if (fragmentTransactionEngine != null)
            fragmentTransactionEngine.onDestroy();
        super.onDestroy();
    }

    private void requestCategoryList(){
        if (ecGroupData != null && ecGroupData.getECGroupType() == MovieMetaData.ECGroupType.SHOP){
            HashMap<String, ShopCategory> shopCategoryHashMap = new HashMap<>();
            if (categories.size() == 0){
                for (MovieMetaData.ExperienceData child : ecGroupData.getChildrenContents()){
                    buildCategorieyFromExperience(child, shopCategoryHashMap);
                }
            }
            if (categoryListAdaptor != null)
                categoryListAdaptor.notifyDataSetChanged();
            categoryListAdaptor.setItemChecked(0);
        }else {

            TheTakeApiDAO.prefetchProductCategories(new ResultListener<List<ShopCategory>>() {
                @Override
                public void onResult(List<ShopCategory> result) {
                    categories = result;
                    if (categories != null && categories.size() > 0) {
                        if (!categories.get(0).categoryName.equals(getResources().getString(R.string.label_all))) {
                            ShopCategory rootCate = new ShopCategory();
                            rootCate.categoryId = 0;
                            rootCate.categoryName = getResources().getString(R.string.label_all);
                            categories.add(0, rootCate);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (categoryListAdaptor != null)
                                categoryListAdaptor.notifyDataSetChanged();
                            categoryListAdaptor.setItemChecked(0, -1);
                        }
                    });
                }

                @Override
                public <E extends Exception> void onException(E e) {

                }
            });
        }
    }

    private void buildCategorieyFromExperience(MovieMetaData.ExperienceData experienceData, HashMap<String, ShopCategory> shopCategoryHashMap){

        if (experienceData != null){
            if (experienceData.getChildrenContents() != null && experienceData.getChildrenContents().size() > 0){
                for (MovieMetaData.ExperienceData child : experienceData.getChildrenContents()){
                    buildCategorieyFromExperience(child, shopCategoryHashMap);
                }

            }
            if ( categories.size() == 0){
                ShopCategory allCategory = new ShopCategory();
                allCategory.categoryId = 0;
                allCategory.categoryName = getResources().getString(R.string.label_all);
                allCategory.products = new ArrayList<>();
                categories.add(allCategory);
            }

            if (experienceData.shopItems.size() > 0){
                for (MovieMetaData.ShopItem shopItem : experienceData.shopItems){
                    if (shopItem.categoryType != null){
                        String id = shopItem.categoryType.getContentID();
                        ShopCategory category = shopCategoryHashMap.get(id);
                        if (category == null){
                            category = new ShopCategory();
                            category.categoryId = categories.size();
                            category.categoryName = MovieMetaData.getMatchingLocalizableObject(shopItem.categoryType.getBasicMetadata().getLocalizedInfo(), NextGenExperience.getClientLocale()).getTitleDisplayUnlimited();
                            categories.add(category);
                            category.products = new ArrayList<>();
                            shopCategoryHashMap.put(id, category);
                        }
                        category.products.add(shopItem);
                        categories.get(0).products.add(shopItem);   // add to "All" category
                    }
                }
            }
        }

    }

    public class ShopCategoryExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener,
            ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupClickListener {


        int[] selectedItemIndexes;
        int selectedFlatIndex = -1;

        @Override
        public ShopCategory getChild(int groupPosition, int childPosititon) {
            if (childPosititon == 0)
                return categories.get(groupPosition);
            return categories.get(groupPosition).childCategories.get(childPosititon-1);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return getChild(groupPosition, childPosition).categoryId;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = getChild(groupPosition, childPosition).categoryName;

            if (convertView == null) {
                LayoutInflater infalInflater = getLayoutInflater();
                convertView = infalInflater.inflate(R.layout.shop_list_item_row, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            if (childPosition == 0)
                txtListChild.setText(getResources().getString(R.string.label_all));
            else
                txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition >= categories.size())
                return 0;
            ShopCategory category = categories.get(groupPosition);
            if (category != null && category.childCategories != null)
                return categories.get(groupPosition).childCategories.size() + 1;
            else
                return 0;
        }

        @Override
        public ShopCategory getGroup(int groupPosition) {
            return categories.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return categories.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return categories.get(groupPosition).categoryId;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = getGroup(groupPosition).categoryName;
            if (convertView == null) {
                LayoutInflater infalInflater = getLayoutInflater();
                convertView = infalInflater.inflate(R.layout.shop_list_group_row, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            setItemChecked(groupPosition, childPosition);
            return false;
        }

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id){
            if (groupPosition < categories.size()) {
                ShopCategory selectedCategory = categories.get(groupPosition);
                if (selectedCategory.childCategories == null || selectedCategory.childCategories.size() == 0){
                    setItemChecked(groupPosition, -1);
                    NGEAnalyticData.reportEvent(ShopCategoryActivity.this, null, NGEAnalyticData.AnalyticAction.ACTION_SELECT_CATEGORY, selectedCategory.categoryName, null);
                    return true;
                }
            }
            return false;
        }

        public void setItemChecked(int... positions) {

            List currentLevelList = categories;
            for (int i = 0; i < positions.length; i++){
                int selectedIndex = positions[i];
                if (selectedIndex != -1){
                    Object currentItem = currentLevelList.get(selectedIndex);
                    categoryListView.setItemChecked(selectedIndex, false);
                    if (currentItem instanceof ShopCategory){
                        if (((ShopCategory)currentItem).childCategories != null && ((ShopCategory)currentItem).childCategories.size() > 0) {
                            currentLevelList = ((ShopCategory)currentItem).childCategories;

                        } else if (((ShopCategory)currentItem).products != null && ((ShopCategory)currentItem).products.size() > 0){
                            Fragment f = getSupportFragmentManager().findFragmentByTag(ShopItemDetailFragment.class.toString());
                            if (f != null)
                                getSupportFragmentManager().popBackStack();
                            gridFrament.refreshWithCategory((ShopCategory)currentItem);
                            categoryListView.setItemChecked(selectedIndex,true);
                        }
                    } else if (currentItem instanceof MovieMetaData.ShopItemInterface){

                    }
                }


            }
/*
            if (groupPosition >= categories.size()) {

            }else if (groupPosition != -1) {
                if (selectedGroupIndex != groupPosition)
                    selectedGroupIndex = groupPosition;

                if (childPosition != -1) {
                     if (selectedItemIndex != childPosition) {
                        selectedItemIndex = childPosition;
                        if (!categoryListView.isGroupExpanded(selectedGroupIndex)) {
                            categoryListView.expandGroup(selectedGroupIndex);
                        }

                    }
                } else {
                    selectedItemIndex = -1;
                }
                reComputeCheckedItem();
                selectedCategory = getSelectedCategory();
            }

            if (selectedCategory != null){
                Fragment f = getSupportFragmentManager().findFragmentByTag(ShopItemDetailFragment.class.toString());
                if (f != null)
                    getSupportFragmentManager().popBackStack();
                gridFrament.refreshWithCategory(selectedCategory);
            }*/


        }

        public ShopCategory getSelectedCategory(){
            /*if (selectedGroupIndex != -1){
                if (selectedItemIndex != -1) {
                    return getChild(selectedGroupIndex, selectedItemIndex);
                }else if (categories != null && selectedItemIndex < categories.size()){
                    return categories.get(selectedGroupIndex);
                }

            }*/
            return null;
        }

        public void reComputeCheckedItem(){
            /*if (selectedGroupIndex != -1){
                ShopCategory selectedCategory = categories.get(selectedGroupIndex);
                if (selectedCategory.childCategories == null || selectedCategory.childCategories.size() == 0) {
                    selectedFlatIndex = categoryListView.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(selectedGroupIndex));
                    categoryListView.setItemChecked(selectedFlatIndex, true);
                }else if (selectedItemIndex != -1) {
                    if (categoryListView.isGroupExpanded(selectedGroupIndex)) {

                        selectedFlatIndex = categoryListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(selectedGroupIndex, selectedItemIndex));
                        categoryListView.setItemChecked(selectedFlatIndex, true);
                    } else if (selectedFlatIndex != -1) {
                        categoryListView.setItemChecked(selectedFlatIndex, false);
                    }
                }
            }*/
        }

        @Override
        public void onGroupCollapse(int groupPosition){
            reComputeCheckedItem();
        }

        @Override
        public void onGroupExpand(int groupPosition){
            reComputeCheckedItem();
        }
    }

    @Override
    public int getLeftButtonLogoId(){
        return R.drawable.back_logo;
    }

    @Override
    public String getBackgroundImgUri(){
        return NextGenExperience.getMovieMetaData().getExtraExperience().style.getBackground().getImage().url;
    }

    @Override
    public String getLeftButtonText(){
        return getResources().getString(R.string.back_button_text);
    }

    @Override
    public String getRightTitleImageUri(){
        return "";
    }

    //*************** NGEFragmentTransactionInterface ***************
    @Override
    public void transitRightFragment(Fragment nextFragment){
        fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.category_left_frame, nextFragment);

    }

    @Override
    public void transitLeftFragment(Fragment nextFragment){
        fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.category_left_frame, nextFragment);

    }

    @Override
    public void transitMainFragment(Fragment nextFragment){
        fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.category_left_frame, nextFragment);
    }

    @Override
    public void resetUI(boolean bIsRoot){

        setBackButtonLogo(R.drawable.back_logo);
        setBackButtonText(getResources().getString(R.string.back_button_text) );

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() == 0 )
            finish();
    }


    @Override
    public String getRightTitleText(){
        if (titleText == null && getIntent() != null) {
            if (getIntent() != null)
                titleText = getIntent().getStringExtra("title");
            else
                titleText = "";
        }
        return titleText;
    }

    boolean isContentFullScreen = false;

    public void onRequestToggleFullscreen(){
        switchFullScreen(!isContentFullScreen);
        isContentFullScreen = !isContentFullScreen;
        if (categoryListView != null) {

            ActionBar bar = getSupportActionBar();
            if (isContentFullScreen){    // make it full screen

                if (bar != null)
                    bar.hide();
                if (backgroundImageView != null)
                    backgroundImageView.setImageDrawable(null);


            } else {                     // shrink it

                if (bar != null)
                    bar.show();
                loadBGImage();
            }
            if (isContentFullScreen) {

                categoryListView.setVisibility(View.GONE);
            }else
                categoryListView.setVisibility(View.VISIBLE);
        }
        Fragment f = getSupportFragmentManager().findFragmentByTag(ShopItemDetailFragment.class.toString());
        if (f != null){
            ((ShopItemDetailFragment)f).setFullScreen(isContentFullScreen);
        }

    }
}
