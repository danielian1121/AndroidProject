package com.mmlab.m1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.github.paolorotolo.appintro.AppIntro2;
import com.mmlab.m1.fragment.SampleSlide;

public class IntroActivity extends AppIntro2 {


	@Override
	public void init(@Nullable Bundle savedInstanceState) {
		addSlide(SampleSlide.newInstance(R.layout.intro_1));
		addSlide(SampleSlide.newInstance(R.layout.intro_2));
		addSlide(SampleSlide.newInstance(R.layout.intro_3));
		addSlide(SampleSlide.newInstance(R.layout.intro_4));
		addSlide(SampleSlide.newInstance(R.layout.intro_5));
		addSlide(SampleSlide.newInstance(R.layout.intro_6));
        addSlide(SampleSlide.newInstance(R.layout.intro_7));
        addSlide(SampleSlide.newInstance(R.layout.intro_8));
        addSlide(SampleSlide.newInstance(R.layout.intro_9));
        addSlide(SampleSlide.newInstance(R.layout.intro_10));
		addSlide(SampleSlide.newInstance(R.layout.intro_11));
		addSlide(SampleSlide.newInstance(R.layout.intro_12));
		addSlide(SampleSlide.newInstance(R.layout.intro_13));
		addSlide(SampleSlide.newInstance(R.layout.intro_14));
		addSlide(SampleSlide.newInstance(R.layout.intro_15));
		addSlide(SampleSlide.newInstance(R.layout.intro_16));
		addSlide(SampleSlide.newInstance(R.layout.intro_17));
		addSlide(SampleSlide.newInstance(R.layout.intro_18));
		addSlide(SampleSlide.newInstance(R.layout.intro_19));
		addSlide(SampleSlide.newInstance(R.layout.intro_20));
		addSlide(SampleSlide.newInstance(R.layout.intro_21));
		addSlide(SampleSlide.newInstance(R.layout.intro_22));
		addSlide(SampleSlide.newInstance(R.layout.intro_23));
		addSlide(SampleSlide.newInstance(R.layout.intro_24));
		addSlide(SampleSlide.newInstance(R.layout.intro_25));
		addSlide(SampleSlide.newInstance(R.layout.intro_26));
		addSlide(SampleSlide.newInstance(R.layout.intro_27));
		addSlide(SampleSlide.newInstance(R.layout.intro_28));
		addSlide(SampleSlide.newInstance(R.layout.intro_29));
		addSlide(SampleSlide.newInstance(R.layout.intro_30));
		addSlide(SampleSlide.newInstance(R.layout.intro_31));
		addSlide(SampleSlide.newInstance(R.layout.intro_32));
		addSlide(SampleSlide.newInstance(R.layout.intro_33));
		addSlide(SampleSlide.newInstance(R.layout.intro_34));
		addSlide(SampleSlide.newInstance(R.layout.intro_35));
		addSlide(SampleSlide.newInstance(R.layout.intro_36));
		addSlide(SampleSlide.newInstance(R.layout.intro_37));
		addSlide(SampleSlide.newInstance(R.layout.intro_38));
		addSlide(SampleSlide.newInstance(R.layout.intro_39));
		addSlide(SampleSlide.newInstance(R.layout.intro_40));
		addSlide(SampleSlide.newInstance(R.layout.intro_41));
		addSlide(SampleSlide.newInstance(R.layout.intro_42));
		addSlide(SampleSlide.newInstance(R.layout.intro_43));
		addSlide(SampleSlide.newInstance(R.layout.intro_44));
		addSlide(SampleSlide.newInstance(R.layout.intro_45));
		addSlide(SampleSlide.newInstance(R.layout.intro_46));
		addSlide(SampleSlide.newInstance(R.layout.intro_47));
		addSlide(SampleSlide.newInstance(R.layout.intro_48));
		addSlide(SampleSlide.newInstance(R.layout.intro_49));

       


		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;
		int height = dm.heightPixels;
		getWindow().setLayout((int)(width * 0.95),(int)(height * 0.85));

	}

	private void loadMainActivity(){
		finish();
	}

	@Override
	public void onDonePressed() {
		loadMainActivity();
	}

	@Override
	public void onNextPressed() {

	}

	@Override
	public void onSlideChanged() {

	}
}
