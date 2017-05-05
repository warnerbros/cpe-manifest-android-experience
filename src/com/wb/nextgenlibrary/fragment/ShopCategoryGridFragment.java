package com.wb.nextgenlibrary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.ECShopItemDetailActivity;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.TheTakeData;
import com.wb.nextgenlibrary.interfaces.NGEFragmentTransactionInterface;
import com.wb.nextgenlibrary.network.TheTakeApiDAO;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.DotIndicatorImageView;

import java.util.List;

/**
 * Created by gzcheng on 4/11/16.
 */
public class ShopCategoryGridFragment extends AbstractNextGenFragment{
    GridView itemsGridView;
    TheTakeProductsGridViewAdapter itemsGridViewAdaptor;
    TheTakeData.ShopCategory selectedCategory = null;

    public int getContentViewId(){
        return R.layout.shop_category_right_grid;
    }

    public void refreshWithCategory(TheTakeData.ShopCategory category){
        selectedCategory = category;

        if (selectedCategory.products == null) {


            TheTakeApiDAO.getCategoryProducts(selectedCategory.categoryId, new ResultListener<List<MovieMetaData.ShopItemInterface>>() {
                @Override
                public void onResult(List<MovieMetaData.ShopItemInterface> result) {
                    selectedCategory.products = result;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (itemsGridViewAdaptor != null)
                                itemsGridViewAdaptor.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public <E extends Exception> void onException(E e) {

                }
            });
        }else {
            if (itemsGridViewAdaptor != null)
                itemsGridViewAdaptor.notifyDataSetChanged();
        }


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemsGridView = (GridView)view.findViewById(R.id.shop_items_grid);

        if (itemsGridView != null){
            float density = NextGenExperience.getScreenDensity(getActivity());
            int spacing = (int)(10 *density);
            itemsGridView.setNumColumns(TabletUtils.isTablet()?2:1);
            itemsGridView.setHorizontalSpacing(spacing);
            itemsGridView.setVerticalSpacing(spacing);
            itemsGridView.setPadding(spacing, 0, spacing, spacing);

            itemsGridViewAdaptor = new TheTakeProductsGridViewAdapter();
            itemsGridView.setAdapter(itemsGridViewAdaptor);
            itemsGridView.setOnItemClickListener(itemsGridViewAdaptor);

        }
    }


    @Override
    String getReportContentName(){
        if (selectedCategory != null)
            return selectedCategory.categoryName;
        else
            return null;
    }

    @Override
    public void onDestroy(){
        itemsGridViewAdaptor = null;
        if (itemsGridView != null){
            itemsGridView.setAdapter(null);
        }
        super.onDestroy();
    }

    public class TheTakeProductsGridViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        //protected OutOfMovieActivity activity;

        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= getCount() || position < 0){
                return null;
            }


            if (convertView == null  ) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.shop_grid_item, parent, false);


            } else {

            }

            DotIndicatorImageView productThumbnail = (DotIndicatorImageView) convertView.findViewById(R.id.shop_item_thumbnail);
            TextView brandName = (TextView) convertView.findViewById(R.id.shop_brand_name);
            TextView productName = (TextView) convertView.findViewById(R.id.shop_product_name);

            MovieMetaData.ShopItemInterface product = getItem(position);
            if (product != null){
                if (productThumbnail != null){
                    productThumbnail.setKeyCropXY(product.getKeyCropProductX(), product.getKeyCropProductY());
                    NextGenGlide.load(getActivity(), product.getThumbnailUrl()).asBitmap().fitCenter().into(productThumbnail);
                    //PicassoTrustAll.loadImageIntoView(getActivity(), product.getThumbnailUrl(), productThumbnail);
                }
                if (brandName != null) {
                    if (!StringHelper.isEmpty(product.getProductBrand())) {
                        brandName.setVisibility(View.VISIBLE);
                        brandName.setText(product.getProductBrand());
                    } else {
                        brandName.setVisibility(View.GONE);
                    }
                }
                if (productName != null)
                    productName.setText(product.getProductName());
            }



            return convertView;
        }

        public int getCount() {
            if (selectedCategory != null && selectedCategory.products != null)
                return selectedCategory.products.size();
            else
                return 0;
        }

        public MovieMetaData.ShopItemInterface getItem(int position) {
            if (selectedCategory != null && selectedCategory.products != null && position < selectedCategory.products.size())
                return selectedCategory.products.get(position);
            else
                return null;
        }

        public long getItemId(int position) {
            if (selectedCategory != null && selectedCategory.products != null && position < selectedCategory.products.size()) {
                return selectedCategory.products.get(position).getProductId() == -1 ? position : selectedCategory.products.get(position).getProductId();
            }else
                return 0;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            MovieMetaData.ShopItemInterface product = getItem(position);
            NGEAnalyticData.reportEvent(getActivity(), ShopCategoryGridFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_PRODUCT, product.getProductReportId(), null);
            if (TabletUtils.isTablet()) {
                ShopItemDetailFragment fragment = new ShopItemDetailFragment();
                //fragment.setContentViewId(R.layout.shop_single_product_view);

                fragment.setProduct(product);
                if (getActivity() instanceof NGEFragmentTransactionInterface) {
                    ((NGEFragmentTransactionInterface) getActivity()).transitMainFragment(fragment);
                }
            } else {
                Intent intent = new Intent(getActivity(), ECShopItemDetailActivity.class);

                if (product instanceof MovieMetaData.ShopItem)
                    intent.putExtra(ECShopItemDetailActivity.SHOP_ITEM_ID, ((MovieMetaData.ShopItem)product).getId());
                startActivity(intent);
            }
        }

    }
}
