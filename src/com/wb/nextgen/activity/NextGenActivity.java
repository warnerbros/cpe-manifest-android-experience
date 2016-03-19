package com.wb.nextgen.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;

import com.wb.nextgen.fragment.NextGenNavigationDrawerFragment;
//import com.wb.nextgen.parser.manifest.schema.v1_4.MediaManifestType;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.TabletUtils;

import net.flixster.android.localization.Localizer;
import net.flixster.android.localization.constants.KEYS;

import java.io.File;
/*
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
*/
/**
 * Created by gzcheng on 1/7/16.
 */
public class NextGenActivity extends FragmentActivity {
    // wrapper of ProfileViewFragment


    private RelativeLayout leftdrawer;
    private DrawerLayout mDrawerLayout;
    private NextGenNavigationDrawerFragment mDrawerFragment;
    private NextGenActionBarDrawerToggle mDrawerToggle;
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.next_gen_drawer_view);

        // Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerFragment = (NextGenNavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.drawer_list);
        //mDrawerFragment.setListener(this);
        leftdrawer = (RelativeLayout) findViewById(R.id.left_drawer);

        ImageView menuBG = (ImageView)findViewById(R.id.menu_bg_image_view);
        if (menuBG != null){
            String bgImageUri = "android.resource://com.wb.nextgen/" + R.drawable.man_of_steel_menu;
            PicassoTrustAll.getInstance(this).load(bgImageUri).fit().into(menuBG);
            //PicassoTrustAll.loadImageIntoView(NextGenApplication.getContext(), bgImageUri, menuBG);
        }

        mDrawerToggle = new NextGenActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        /*
        try {
            JAXBContext jc = JAXBContext.newInstance(MediaManifestType.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<MediaManifestType> unmarshalledRoot = unmarshaller.unmarshal(new StreamSource(new File("/data/mos_hls_manifest_v3.xml")), MediaManifestType.class);
            MediaManifestType mani jifest = unmarshalledRoot.getValue();

            System.out.println(manifest.getInventory().getMetadata().get(0).getBasicMetadata().getLocalizedInfo().get(0).getOriginalTitle());
        } catch (JAXBException ex){

        }*/
    }

    private class NextGenActionBarDrawerToggle extends ActionBarDrawerToggle {

        public NextGenActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes,
                                             int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        private float lastTranslate = 0.0f;
        /**
         * Swap fragment on drawer closed for better performance
         */
        @Override
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);




        }

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            mDrawerFragment.resetDrawer();
            //setTitle(R.string.app_name);
        }

        //@SuppressLint("NewApi")
        public void onDrawerSlide(View drawerView, float slideOffset)
        {
            float moveFactor = (leftdrawer.getWidth() * slideOffset);

            FrameLayout frame = (FrameLayout) findViewById(R.id.content_frame);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            {

                frame.setTranslationX(moveFactor);
            }
            else
            {
                TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                anim.setDuration(0);
                anim.setFillAfter(true);
                frame.startAnimation(anim);

                lastTranslate = moveFactor;
            }
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideShowNextGenView();
    }

    private void hideShowNextGenView(){
/*        if (TabletUtils.isTablet()) {
            View nextGenView = findViewById(R.id.nextgen_portrait_bottom);
            if (nextGenView == null)
                return;
            switch (this.getResources().getConfiguration().orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    nextGenView.setVisibility(View.VISIBLE);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    nextGenView.setVisibility(View.GONE);
            }
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case android.R.id.home:
                if (mDrawerLayout.isDrawerVisible(leftdrawer))
                    mDrawerLayout.closeDrawer(leftdrawer);
                else
                    mDrawerLayout.openDrawer(leftdrawer);
                return true;

            default:
                return true;

        }
    }

    public void onResume() {
        super.onResume();
        hideShowNextGenView();
    }
}
