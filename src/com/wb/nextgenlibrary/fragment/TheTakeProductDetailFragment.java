package com.wb.nextgenlibrary.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NextGenAnalyticData;
import com.wb.nextgenlibrary.data.TheTakeData.TheTakeProduct;
import com.wb.nextgenlibrary.data.TheTakeData.TheTakeProductDetail;
import com.wb.nextgenlibrary.network.TheTakeApiDAO;
import com.wb.nextgenlibrary.util.DialogUtils;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.StringHelper;

/**
 * Created by gzcheng on 4/11/16.
 */
public class TheTakeProductDetailFragment extends AbstractNextGenFragment implements View.OnClickListener{

    TheTakeProduct product;
    ImageView productPoster;
    TextView matchStatus, brandText, nameText, priceText;
    Button shopAtTheTakeBtn, sendLinkBtn;

    String titleText = "";

    int contentViewId = R.layout.the_take_product_view;

    public void setContentViewId(int viewId){
        contentViewId = viewId;
    }


    public void setProduct(TheTakeProduct product){
        this.product = product;
    }


    @Override
    public int getContentViewId(){
        return contentViewId;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productPoster = (ImageView)view.findViewById(R.id.the_take_product_image_full);
        matchStatus = (TextView)view.findViewById(R.id.the_take_product_match);
        brandText = (TextView)view.findViewById(R.id.the_take_product_brand_name);
        nameText = (TextView)view.findViewById(R.id.the_take_product_name);
        priceText = (TextView)view.findViewById(R.id.the_take_product_price);

        shopAtTheTakeBtn = (Button)view.findViewById(R.id.shop_at_the_take_button);
        if (shopAtTheTakeBtn != null)
            shopAtTheTakeBtn.setOnClickListener(this);
        sendLinkBtn = (Button)view.findViewById(R.id.send_link_button);
        if (sendLinkBtn != null)
            sendLinkBtn.setOnClickListener(this);

        if (product != null)
            getProductDetail();
    }

    public void onClick(View v){
        if (v.getId() == R.id.shop_at_the_take_button){
            DialogUtils.showLeavingAppDialog(getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NextGenExperience.launchChromeWithUrl(product.getProductDetail().purchaseLink);
                }
            });
            //NextGenAnalyticData.reportEvent(getActivity(), TheTakeProductDetailFragment.this, NextGenAnalyticData.AnalyticAction.ACTION_SELECT_SHOPPING, product.productName);
        }else if (v.getId() == R.id.send_link_button){
            NextGenExperience.launchChromeWithUrl(product.getProductDetail().shareUrl);
           // NextGenAnalyticData.reportEvent(getActivity(), TheTakeProductDetailFragment.this, NextGenAnalyticData.AnalyticAction.ACTION_SELECT_SHOPPING, product.productName);
        }
    }


    @Override
    String getReportContentName(){
        if (product != null)
            return product.productName;
        else
            return null;
    }

    private void populateProductDetail(TheTakeProduct product){
        if(product.getProductDetail() != null){
            Picasso.with(getActivity()).load(product.getProductDetail().getProductImage()).fit().centerInside().into(productPoster);

            if (product.verified)
                matchStatus.setText(getActivity().getResources().getString(R.string.exact_match));
            else
                matchStatus.setText(getActivity().getResources().getString(R.string.close_match));
            brandText.setText(product.getProductDetail().productBrand);
            nameText.setText(product.getProductDetail().productName);
            priceText.setText(product.getProductDetail().productPrice);
            if (!StringHelper.isEmpty(product.getProductDetail().purchaseLink)) {
                shopAtTheTakeBtn.setVisibility(View.VISIBLE);
            }else {
                shopAtTheTakeBtn.setVisibility(View.GONE);
            }
            if (!StringHelper.isEmpty(product.getProductDetail().shareUrl)) {
                sendLinkBtn.setVisibility(View.VISIBLE);
            }else {
                sendLinkBtn.setVisibility(View.GONE);
            }
        }
    }

    public void getProductDetail(){
        if (product.getProductDetail() != null)
            populateProductDetail(product);
        else {
            TheTakeApiDAO.getProductDetails(product.productId, new ResultListener<TheTakeProductDetail>() {
                @Override
                public void onResult(final TheTakeProductDetail result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            product.setProductDetail(result);
                            populateProductDetail(product);
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
