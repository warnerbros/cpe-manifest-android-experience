package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.network.TheTakeApiDAO;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 4/19/16.
 */
public class ShopFrameProductsFragment extends AbstractNextGenFragment {

    ShopItemDetailFragment productDetailFragment;
    List<MovieMetaData.ShopItemInterface> productList = new ArrayList<MovieMetaData.ShopItemInterface>();
    RecyclerView frameProductsRecyclerView;
    LinearLayoutManager frameProductsLayoutManager;
    FrameProductsAdapter frameProductsAdaptor;

    TextView titleTextView;
    String titleText = "";

    long frameTime = 0L;
    @Override
    public int getContentViewId(){
        return R.layout.shop_frame_products_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleTextView = (TextView)view.findViewById(R.id.ec_title_name);

        setTitleText(titleText);
        productDetailFragment = (ShopItemDetailFragment)getChildFragmentManager().findFragmentById(R.id.frame_product_detail_fragment);
        productDetailFragment.setContentViewId(R.layout.shop_product_view);

        frameProductsRecyclerView = (RecyclerView) view.findViewById(R.id.frame_product_list);
        if (frameProductsRecyclerView != null){
            frameProductsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            frameProductsRecyclerView.setLayoutManager(frameProductsLayoutManager);
            frameProductsAdaptor = new FrameProductsAdapter();
            frameProductsRecyclerView.setAdapter(frameProductsAdaptor);
            frameProductsAdaptor.setSelectedIndex(0);
        }
    }

    public void setTitleText(String title){
        titleText = title;
        if (titleTextView != null)
            titleTextView.setText(titleText);
    }
    @Override
    String getReportContentName(){
        return titleText;
    }

    @Override
    public void onDestroy(){
        frameProductsAdaptor = null;
        if (frameProductsRecyclerView != null)
            frameProductsRecyclerView.setAdapter(null);
        super.onDestroy();
    }

    public void setFrameProductTime(long frameTimestamp){
        frameTime = frameTimestamp;

    }


    @Override
    public void onStart(){
        super.onStart();
        TheTakeApiDAO.getFrameProducts((double) frameTime, new ResultListener<List<MovieMetaData.ShopItemInterface>>() {
            @Override
            public void onResult(final List<MovieMetaData.ShopItemInterface> result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        productList = result;
                        if (productList != null && productList.size() > 0) {
                            loadProductIntoDetailFragment(productList.get(0));
                        }
                        frameProductsAdaptor.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public <E extends Exception> void onException(E e) {

            }
        });
    }

    public void loadProductIntoDetailFragment(MovieMetaData.ShopItemInterface product){
        if (productDetailFragment != null) {
            productDetailFragment.setProduct(product);
            productDetailFragment.getProductDetail();
        }
    }

    public class FrameProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView productBrand;
        TextView productName;
        ImageView personPhoto;
        MovieMetaData.ShopItemInterface product;
        int position;

        FrameProductViewHolder(View itemView, MovieMetaData.ShopItemInterface product) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.product_cv);
            productBrand = (TextView)itemView.findViewById(R.id.cv_product_brand_name);
            productName = (TextView)itemView.findViewById(R.id.cv_product_name);
            personPhoto = (ImageView)itemView.findViewById(R.id.cv_product_photo);
            this.product = product;
            itemView.setOnClickListener(this);
        }

        public void setTheTakeProduct(MovieMetaData.ShopItemInterface product, int position){
            this.position = position;
            this.product = product;
        }

        @Override
        public void onClick(View v) {
            loadProductIntoDetailFragment(product);
            frameProductsAdaptor.setSelectedIndex(position);
            frameProductsAdaptor.notifyDataSetChanged();
            //NGEAnalyticData.reportEvent(getActivity(), ShopFrameProductsFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_SHOPPING, product.productName);
        }
    }

    public class FrameProductsAdapter extends RecyclerView.Adapter<FrameProductViewHolder>{


        int lastloadingIndex = -1;
        int selectedIndex = 0;
        static final int PAGEITEMCOUNT = 6;

        public void reset(){
            lastloadingIndex = -1;
        }

        @Override
        public FrameProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shop_frame_product_cardview, viewGroup, false);
            FrameProductViewHolder pvh = new FrameProductViewHolder(v, productList.get(i));
            return pvh;
        }
        public void onBindViewHolder(FrameProductViewHolder holder, int position){
            //holder.personName.setText(actorOjbect.actorFilmography[position].title);
            //holder.personAge.setText(actorOjbect.actorFilmography[position].title);


            if (productList != null && productList.size() > position) {
                MovieMetaData.ShopItemInterface thisProduct = productList.get(position);
                holder.setTheTakeProduct(thisProduct, position);
                if (!StringHelper.isEmpty(thisProduct.getProductThumbnailUrl())) {
                    NextGenGlide.load(getActivity(), thisProduct.getProductThumbnailUrl()).into(holder.personPhoto);
                    //PicassoTrustAll.loadImageIntoView(getActivity(), thisProduct.getProductThumbnailUrl(), holder.personPhoto);
                    holder.productBrand.setText(thisProduct.getProductBrand());
                    holder.productName.setText(thisProduct.getProductName());

                }else if (position < lastloadingIndex) {
                    //holder.personPhoto.setImageResource(R.drawable.poster_blank);
                    holder.personPhoto.setImageDrawable(null);
                    holder.productBrand.setText("");
                    holder.productName.setText("");
                } else {
                    holder.personPhoto.setImageDrawable(null);
                    holder.productBrand.setText("");
                    holder.productName.setText("");
                    final int requestStartIndex = position;
                    lastloadingIndex = requestStartIndex + PAGEITEMCOUNT;

                }
            }
            holder.itemView.setSelected(position == selectedIndex);
            //holder.personPhoto.setImageResource(persons.get(i).photoId);
        }


        public void setSelectedIndex(int index){
            selectedIndex = index;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount(){
            if (productList != null )
                return productList.size();
            else
                return 0;
        }

    }

}
