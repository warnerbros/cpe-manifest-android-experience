package com.wb.nextgenlibrary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.wb.nextgenlibrary.R;

import co.aetherinc.aetherplayer.PlayerFragment;

public class FullscreenPlayerActivity extends AppCompatActivity {

    public static final String ARG_CONTENT_ADDRESS = "content_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.fullscreen_player_activity);

        Bundle arguments = new Bundle();
        arguments.putString(PlayerFragment.ARG_CONTENT_ADDRESS, getIntent().getStringExtra(ARG_CONTENT_ADDRESS));
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.children, fragment)
                .commit();
    }

}
