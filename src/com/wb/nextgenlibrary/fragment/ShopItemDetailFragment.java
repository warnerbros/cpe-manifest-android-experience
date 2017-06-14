package com.wb.nextgenlibrary.fragment;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.TheTakeData.TheTakeProduct;
import com.wb.nextgenlibrary.data.TheTakeData.TheTakeProductDetail;
import com.wb.nextgenlibrary.network.TheTakeApiDAO;
import com.wb.nextgenlibrary.util.DialogUtils;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.StringHelper;

/**
 * Created by gzcheng on 4/11/16.
 */
public class ShopItemDetailFragment extends AbstractNextGenFragment implements View.OnClickListener{

    MovieMetaData.ShopItemInterface product;
    ImageView productPoster;
    TextView matchStatus, brandText, nameText, priceText;
    Button shopAtTheTakeBtn, sendLinkBtn;
    ECVideoViewFragment productVideoViewFragment;
    View productVideoViewFragmentFrame, productImageFrame, productMetaFrame;

    String titleText = "";

    int contentViewId = R.layout.shop_product_view;

    public void setContentViewId(int viewId){
        contentViewId = viewId;
    }

    public void setFullScreen(boolean bFullScreen){
        if (productImageFrame != null && productMetaFrame != null){
            if (bFullScreen){
                productImageFrame.setVisibility(View.GONE);
                productMetaFrame.setVisibility(View.GONE);
            }else {
                productImageFrame.setVisibility(View.VISIBLE);
                productMetaFrame.setVisibility(View.VISIBLE);


            }
        }
    }

    public void setProduct(MovieMetaData.ShopItemInterface product){
        this.product = product;
        if (productVideoViewFragment != null && product instanceof MovieMetaData.ShopItem && ((MovieMetaData.ShopItem) product).getAVItem() != null) {
            if (productVideoViewFragmentFrame != null)
                productVideoViewFragmentFrame.setVisibility(View.VISIBLE);
            productVideoViewFragment.setShouldAutoPlay(false);
            productVideoViewFragment.setShouldShowFullScreenBtn(true);
            productVideoViewFragment.setAudioVisualItem(((MovieMetaData.ShopItem) product).getAVItem());
        } else if (productVideoViewFragmentFrame != null)
            productVideoViewFragmentFrame.setVisibility(View.GONE);
    }


    @Override
    public int getContentViewId(){
        return contentViewId;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productPoster = (ImageView)view.findViewById(R.id.shop_product_image_full);
        matchStatus = (TextView)view.findViewById(R.id.shop_product_match);
        brandText = (TextView)view.findViewById(R.id.shop_product_brand_name);
        nameText = (TextView)view.findViewById(R.id.shop_product_name);
        priceText = (TextView)view.findViewById(R.id.shop_product_price);

        shopAtTheTakeBtn = (Button)view.findViewById(R.id.shop_at_the_take_button);
        if (shopAtTheTakeBtn != null)
            shopAtTheTakeBtn.setOnClickListener(this);
        sendLinkBtn = (Button)view.findViewById(R.id.send_link_button);
        if (sendLinkBtn != null)
            sendLinkBtn.setOnClickListener(this);

        if (product != null)
            getProductDetail();

        productVideoViewFragment = (ECVideoViewFragment)getChildFragmentManager().findFragmentById(R.id.shop_product_video_fragment);
        productVideoViewFragmentFrame = view.findViewById(R.id.shop_product_video_fragment_frame);

        productImageFrame = view.findViewById(R.id.shop_product_thumbnail_frame);
        productMetaFrame = view.findViewById(R.id.shop_product_meta_frame);

        if (product != null)
            setProduct(product);

    }

    public void onClick(View v){
        if (v.getId() == R.id.shop_at_the_take_button){
            DialogUtils.showLeavingAppDialog(getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NextGenExperience.launchChromeWithUrl(product.getPurchaseLinkUrl());
                }
            });
            NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_SHOP_PRODUCT, product.getProductReportId(), null);
        }else if (v.getId() == R.id.send_link_button){
            if (!StringHelper.isEmpty(product.getShareLinkUrl()))
                NextGenExperience.launchChromeWithUrl(product.getShareLinkUrl());
            else if (!StringHelper.isEmpty(product.getPurchaseLinkUrl())){
                NextGenExperience.launchSocialSharingWithUrl(getActivity(), product.getPurchaseLinkUrl());
            }
            NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SHARE_PRODUCT_LINK, product.getProductReportId(), null);
        }
    }


    @Override
    String getReportContentName(){
        if (product != null)
            return product.getProductName();
        else
            return null;
    }

    private void populateProductDetail(MovieMetaData.ShopItemInterface shopItem){
        shopAtTheTakeBtn.setText(shopItem.getShopItemText(getContext()));
        if (shopItem instanceof  TheTakeProduct) {
            TheTakeProduct product = (TheTakeProduct)shopItem;
            if (product.getProductDetail() != null) {
                NextGenGlide.load(getActivity(),product.getProductDetail().getProductImage()).fitCenter().into(productPoster);

                if (product.isVerified())
                    matchStatus.setText(getActivity().getResources().getString(R.string.exact_match));
                else
                    matchStatus.setText(getActivity().getResources().getString(R.string.close_match));
                brandText.setText(product.getProductDetail().productBrand);
                nameText.setText(product.getProductDetail().productName);
                priceText.setText(product.getProductDetail().productPrice);
                if (!StringHelper.isEmpty(product.getProductDetail().purchaseLink)) {
                    shopAtTheTakeBtn.setVisibility(View.VISIBLE);
                } else {
                    shopAtTheTakeBtn.setVisibility(View.GONE);
                }
                if (!StringHelper.isEmpty(product.getProductDetail().shareUrl)) {
                    sendLinkBtn.setVisibility(View.VISIBLE);
                } else {
                    sendLinkBtn.setVisibility(View.GONE);
                }
            }
        } else {
            matchStatus.setVisibility(View.GONE);
            NextGenGlide.load(getActivity(),product.getProductThumbnailUrl()).fitCenter().into(productPoster);

            /*if (product.isVerified())
                matchStatus.setText(getActivity().getResources().getString(R.string.exact_match));
            else
                matchStatus.setText(getActivity().getResources().getString(R.string.close_match));*/
            brandText.setText(product.getProductBrand());
            nameText.setText(product.getProductName());
            priceText.setText("");
            priceText.setVisibility(View.GONE);
            if (!StringHelper.isEmpty(product.getPurchaseLinkUrl())) {
                shopAtTheTakeBtn.setVisibility(View.VISIBLE);
                sendLinkBtn.setVisibility(View.VISIBLE);
            } else {
                shopAtTheTakeBtn.setVisibility(View.GONE);
                sendLinkBtn.setVisibility(View.GONE);
            }
        }
    }

    public void getProductDetail(){
        if (product instanceof TheTakeProduct) {
            final TheTakeProduct theTakeProduct = (TheTakeProduct)product;

            if (theTakeProduct.getProductDetail() != null)
                populateProductDetail(theTakeProduct);
            else {
                TheTakeApiDAO.getProductDetails(theTakeProduct.productId, new ResultListener<TheTakeProductDetail>() {
                    @Override
                    public void onResult(final TheTakeProductDetail result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                theTakeProduct.setProductDetail(result);
                                populateProductDetail(theTakeProduct);
                            }
                        });
                    }

                    @Override
                    public <E extends Exception> void onException(E e) {

                    }
                });
            }
        } else {
            populateProductDetail(product);
        }
    }
}
