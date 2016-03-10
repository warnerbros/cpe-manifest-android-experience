package com.wb.nextgen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.DemoData;
import com.wb.nextgen.fragment.ECViewLeftListFragment;
import com.wb.nextgen.fragment.NextGenExtraLeftListFragment;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.F;

/**
 * Created by gzcheng on 2/25/16.
 */
public abstract class AbstractECView extends AbstractNextGenActivity {

    protected DemoData.ECGroupData ecGroupData ;
    protected ECViewLeftListFragment listFragment;

    public abstract void onLeftListItemSelected(DemoData.ECContentData ecContentData);

    public abstract int getContentViewId();

    public DemoData.ECGroupData getECGroupData(){
        return ecGroupData;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String groupId = intent.getStringExtra(F.ID);
        ecGroupData = DemoData.findECGroupDataById(groupId);

        setContentView(getContentViewId());

        float density = NextGenApplication.getScreenDensity(this);
        int spacing = (int)(10 *density);

        listFragment = (ECViewLeftListFragment) getSupportFragmentManager().findFragmentById(R.id.ec_fragment_list);

    }

    public void onStart() {
        super.onStart();
        if (listFragment != null){
            listFragment.onListItemClick(ecGroupData.ecContents.get(0));
            listFragment.scrollToTop();
        }
    }

    public abstract int getListItemViewLayoutId();

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
