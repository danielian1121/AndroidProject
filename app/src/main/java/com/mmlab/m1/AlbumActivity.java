package com.mmlab.m1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.liuguangqiang.progressbar.CircleProgressBar;
import com.mmlab.m1.adapter.ImageAdapter;
import com.mmlab.m1.constant.PLAYBACK;

import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    private CircleProgressBar progressBar;
    private ArrayList<String> pics;
    private TextView picCaption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle bundle = getIntent().getExtras();
        pics = (ArrayList<String>) bundle.getSerializable("Pics");

        if (pics.isEmpty()) {
            pics = new ArrayList<>();
            pics.add("http://thespiritscience.net/wp-content/uploads/2015/08/eat-an-apple.jpg");
            pics.add("http://thespiritscience.net/wp-content/uploads/2015/08/eat-an-apple.jpg");
            pics.add("http://wac.450f.edgecastcdn.net/80450F/hudsonvalleycountry.com/files/2015/01/cat4.jpg");
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ImageAdapter(this, pics));
        picCaption = (TextView) findViewById(R.id.imageNum);
        picCaption.setText(1 + " / " + pics.size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                picCaption.setText(position + 1 + " / " + pics.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        progressBar = (CircleProgressBar) findViewById(R.id.progressbar1);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


}
