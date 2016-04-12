package com.wb.nextgen.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.TheTakeData;
import com.wb.nextgen.data.TheTakeData.TheTakeProduct;
import com.wb.nextgen.data.TheTakeData.TheTakeProductDetail;
import com.wb.nextgen.network.TheTakeApiDAO;
import com.wb.nextgen.util.DialogUtils;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 4/11/16.
 */
public class TheTakeProductDetailFragment extends AbstractNextGenFragment implements View.OnClickListener{

    List<TheTakeProduct> productsList;
    ImageView productPoster;
    TextView matchStatus, brandText, nameText, priceText;
    Button shopAtTheTakeBtn, sendLinkBtn;

    public void setProductList(List<TheTakeProduct> products){
        productsList = products;
    }

    @Override
    public int getContentViewId(){
        return R.layout.the_take_products_view;
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

        if (productsList != null && productsList.size() > 0)
            getProductDetail(productsList.get(0));
    }

    public void onClick(View v){
        if (v.getId() == R.id.shop_at_the_take_button){
            DialogUtils.showLeavingAppDialog(getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NextGenApplication.launchChromeWithUrl(productsList.get(0).getProductDetail().purchaseLink);
                }
            });
        }else if (v.getId() == R.id.send_link_button){
            NextGenApplication.launchChromeWithUrl(productsList.get(0).getProductDetail().shareUrl);
        }
    }

    private void populateProductDetail(TheTakeProduct product){
        if(product.getProductDetail() != null){
            Picasso.with(getActivity()).load(product.getProductDetail().getProductImage()).fit().centerCrop().into(productPoster);

            matchStatus.setText("exact match");
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

    public void getProductDetail(final TheTakeProduct product){
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
