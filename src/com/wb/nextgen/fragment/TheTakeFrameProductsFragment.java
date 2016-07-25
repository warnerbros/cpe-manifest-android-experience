package com.wb.nextgen.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.TheTakeData.TheTakeProduct;
import com.wb.nextgen.network.BaselineApiDAO;
import com.wb.nextgen.network.TheTakeApiDAO;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;
import com.wb.nextgen.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 4/19/16.
 */
public class TheTakeFrameProductsFragment extends AbstractNextGenFragment {

    TheTakeProductDetailFragment productDetailFragment;
    List<TheTakeProduct> productList = new ArrayList<TheTakeProduct>();
    RecyclerView frameProductsRecyclerView;
    LinearLayoutManager frameProductsLayoutManager;
    FrameProductsAdapter frameProductsAdaptor;
    long frameTime = 0L;
    @Override
    public int getContentViewId(){
        return R.layout.the_take_frame_products_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productDetailFragment = (TheTakeProductDetailFragment)getChildFragmentManager().findFragmentById(R.id.frame_product_detail_fragment);
        productDetailFragment.setContentViewId(R.layout.the_take_product_view);

        frameProductsRecyclerView = (RecyclerView) view.findViewById(R.id.frame_product_list);
        if (frameProductsRecyclerView != null){
            frameProductsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            frameProductsRecyclerView.setLayoutManager(frameProductsLayoutManager);
            frameProductsAdaptor = new FrameProductsAdapter();
            frameProductsRecyclerView.setAdapter(frameProductsAdaptor);
            frameProductsAdaptor.setSelectedIndex(0);
        }
    }

    public void setFrameProductTime(long frameTimestamp){
        frameTime = frameTimestamp;

    }


    @Override
    public void onStart(){
        super.onStart();
        TheTakeApiDAO.getFrameProducts((double) frameTime, new ResultListener<List<TheTakeProduct>>() {
            @Override
            public void onResult(final List<TheTakeProduct> result) {
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

    public void loadProductIntoDetailFragment(TheTakeProduct product){
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
        TheTakeProduct product;
        int position;

        FrameProductViewHolder(View itemView, TheTakeProduct product) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.product_cv);
            productBrand = (TextView)itemView.findViewById(R.id.cv_product_brand_name);
            productName = (TextView)itemView.findViewById(R.id.cv_product_name);
            personPhoto = (ImageView)itemView.findViewById(R.id.cv_product_photo);
            this.product = product;
            itemView.setOnClickListener(this);
        }

        public void setTheTakeProduct(TheTakeProduct product, int position){
            this.position = position;
            this.product = product;
        }

        @Override
        public void onClick(View v) {
            loadProductIntoDetailFragment(product);
            frameProductsAdaptor.setSelectedIndex(position);
            frameProductsAdaptor.notifyDataSetChanged();
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
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.the_take_fram_product_cardview, viewGroup, false);
            FrameProductViewHolder pvh = new FrameProductViewHolder(v, productList.get(i));
            return pvh;
        }
        public void onBindViewHolder(FrameProductViewHolder holder, int position){
            //holder.personName.setText(actorOjbect.actorFilmography[position].title);
            //holder.personAge.setText(actorOjbect.actorFilmography[position].title);


            if (productList != null && productList.size() > position) {
                TheTakeProduct thisProduct = productList.get(position);
                holder.setTheTakeProduct(thisProduct, position);
                if (!StringHelper.isEmpty(thisProduct.getProductThumbnailUrl())) {
                    PicassoTrustAll.loadImageIntoView(getActivity(), thisProduct.getProductThumbnailUrl(), holder.personPhoto);
                    holder.productBrand.setText(thisProduct.productBrand);
                    holder.productName.setText(thisProduct.productName);

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
