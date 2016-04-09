package com.wb.nextgen.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.data.TheTakeData.TheTakeProduct;
import com.wb.nextgen.data.TheTakeData.TheTakeCategory;
import com.wb.nextgen.fragment.NextGenActorListFragment;
import com.wb.nextgen.fragment.NextGenExtraMainTableFragment;
import com.wb.nextgen.network.TheTakeApiDAO;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 4/8/16.
 */
public class TheTakeShopCategoryActivity extends AbstractNextGenActivity {

    List<TheTakeCategory> categories = new ArrayList<TheTakeCategory>();

    ExpandableListView categoryListView;
    TheTakeExpandableListAdapter categoryListAdaptor;
    GridView itemsGridView;
    TheTakeProductsGridViewAdapter itemsGridViewAdaptor;


    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.the_take_shop_category_view);
        categoryListView = (ExpandableListView)findViewById(R.id.the_take_category_list);
        if (categoryListView != null) {

            categoryListAdaptor = new TheTakeExpandableListAdapter();
            categoryListView.setAdapter(categoryListAdaptor);
            categoryListView.setOnChildClickListener(categoryListAdaptor);
            categoryListView.setOnGroupCollapseListener(categoryListAdaptor);
            categoryListView.setOnGroupExpandListener(categoryListAdaptor);
            requestCategoryList();

        }
        itemsGridView = (GridView)findViewById(R.id.the_take_items_grid);

        if (itemsGridView != null){
            float density = NextGenApplication.getScreenDensity(this);
            int spacing = (int)(10 *density);
            itemsGridView.setNumColumns(3);
            itemsGridView.setHorizontalSpacing(spacing);
            itemsGridView.setVerticalSpacing(spacing);
            itemsGridView.setPadding(spacing, 0, spacing, spacing);

            itemsGridViewAdaptor = new TheTakeProductsGridViewAdapter();
            itemsGridView.setAdapter(itemsGridViewAdaptor);
            itemsGridView.setOnItemClickListener(itemsGridViewAdaptor);

        }
    }

    private void requestCategoryList(){
        TheTakeApiDAO.prefetchProductCategories(new ResultListener<List<TheTakeCategory>>() {
            @Override
            public void onResult(List<TheTakeCategory> result) {
                categories = result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (categoryListAdaptor != null)
                            categoryListAdaptor.notifyDataSetChanged();
                        categoryListAdaptor.setItemChecked(0,0);
                    }
                });
            }

            @Override
            public <E extends Exception> void onException(E e) {

            }
        });
    }

    public class TheTakeExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener,
            ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener {


        int selectedGroupIndex = -1;
        int selectedItemIndex = -1;
        int selectedFlatIndex = -1;

        @Override
        public TheTakeCategory getChild(int groupPosition, int childPosititon) {
            return categories.get(groupPosition).childCategories.get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return categories.get(groupPosition).childCategories.get(childPosition).categoryId;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = getChild(groupPosition, childPosition).categoryName;

            if (convertView == null) {
                LayoutInflater infalInflater = getLayoutInflater();
                convertView = infalInflater.inflate(R.layout.the_take_list_item_row, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (groupPosition >= categories.size())
                return 0;
            TheTakeCategory category = categories.get(groupPosition);
            if (category != null && category.childCategories != null)
                return categories.get(groupPosition).childCategories.size();
            else
                return 0;
        }

        @Override
        public TheTakeCategory getGroup(int groupPosition) {
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
                convertView = infalInflater.inflate(R.layout.the_take_list_group_row, null);
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

        public void setItemChecked(int groupPosition, int childPosition) {
            if (groupPosition >= categories.size() || childPosition >= categories.get(groupPosition).childCategories.size())
                return;
            else if (groupPosition != -1 && childPosition != -1) {
                if (selectedGroupIndex != groupPosition || selectedItemIndex != childPosition) {
                    selectedGroupIndex = groupPosition;
                    selectedItemIndex = childPosition;
                    if (!categoryListView.isGroupExpanded(selectedGroupIndex)){
                        categoryListView.expandGroup(selectedGroupIndex);
                    }
                    reComputeCheckedItem();
                    final TheTakeCategory selectedCategory = getSelectedCategory();
                    if (selectedCategory.products != null){

                    }
                    TheTakeApiDAO.getCategoryProducts(selectedCategory.categoryId, new ResultListener<List<TheTakeProduct>>() {
                        @Override
                        public void onResult(List<TheTakeProduct> result) {
                            selectedCategory.products = result;
                            TheTakeShopCategoryActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    itemsGridViewAdaptor.notifyDataSetChanged();
                                }
                            });
                        }

                        @Override
                        public <E extends Exception> void onException(E e) {

                        }
                    });
                }
            }
        }

        public TheTakeCategory getSelectedCategory(){
            if (selectedGroupIndex != -1 && selectedItemIndex != -1){
                return categories.get(selectedGroupIndex).childCategories.get(selectedItemIndex);
            }else
                return null;
        }

        public void reComputeCheckedItem(){
            if (selectedGroupIndex != -1 && selectedItemIndex != -1){


                if (categoryListView.isGroupExpanded(selectedGroupIndex)) {

                    selectedFlatIndex = categoryListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(selectedGroupIndex, selectedItemIndex));
                    categoryListView.setItemChecked(selectedFlatIndex, true);
                }else if (selectedFlatIndex != -1){
                    categoryListView.setItemChecked(selectedFlatIndex, false);
                }
            }
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

    public class TheTakeProductsGridViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        //protected NextGenExtraActivity activity;

        TheTakeCategory selectedCategory = null;

        @Override
        public void notifyDataSetChanged(){
            selectedCategory = categoryListAdaptor.getSelectedCategory();
            super.notifyDataSetChanged();
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= getCount() || position < 0){
                return null;
            }


            if (convertView == null  ) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.the_take_grid_item, parent, false);


            } else {

            }

            ImageView productThumbnail = (ImageView)convertView.findViewById(R.id.the_take_item_thumbnail);
            TextView brandName = (TextView) convertView.findViewById(R.id.the_take_brand_name);
            TextView productName = (TextView) convertView.findViewById(R.id.the_take_product_name);

            TheTakeProduct product = getItem(position);
            if (product != null){
                if (productThumbnail != null){
                    PicassoTrustAll.loadImageIntoView(TheTakeShopCategoryActivity.this, product.getThumbnailUrl(), productThumbnail);
                }
                if (brandName != null)
                    brandName.setText(product.productBrand);
                if (productName != null)
                    productName.setText(product.productName);
            }



            return convertView;
        }

        public int getCount() {
            if (selectedCategory != null && selectedCategory.products != null)
                return selectedCategory.products.size();
            else
                return 0;
        }

        public TheTakeProduct getItem(int position) {
            if (selectedCategory != null && selectedCategory.products != null && position < selectedCategory.products.size())
                return selectedCategory.products.get(position);
            else
                return null;
        }

        public long getItemId(int position) {
            if (selectedCategory != null && selectedCategory.products != null && position < selectedCategory.products.size())
                return selectedCategory.products.get(position).productId;
            else
                return 0;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

        }

    }

    @Override
    public int getLeftButtonLogoId(){
        return R.drawable.back_logo;
    }

    @Override
    public String getBackgroundImgUri(){
        return DemoData.getExtraBackgroundUrl();
    }

    @Override
    public String getLeftButtonText(){
        return getResources().getString(R.string.back_button_text);
    }

    @Override
    public String getRightTitleImageUri(){
        return DemoData.getExtraRightTitleImageUrl();
    }
}
