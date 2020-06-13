package com.codigoj.liciu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroActivity extends Activity {

    private ViewPager viewPager;
    private IntroAdapter viewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnLogin, btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnSignup = (Button) findViewById(R.id.btn_signup);

        layouts = new int[]{
                R.layout.slider1,
                R.layout.slider2,
                R.layout.slider3};

        // adding bottom dots
        addBottomDots(0);

        //load the typeface
        Typeface berlinSansFB= Typeface.createFromAsset(getAssets(),"fonts/BRLNSR.TTF");
        btnLogin.setTypeface(berlinSansFB);
        btnSignup.setTypeface(berlinSansFB);

        viewPagerAdapter = new IntroAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

    }

    public void btnLoginClick(View view) {
        launchLoginScreen();
    }

    public void btnSignupClick(View view) {
        launchSignupScreen();
    }


    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.dot_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.dot_active));
    }



    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchLoginScreen() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    private void launchSignupScreen() {
        startActivity(new Intent(this, SignupActiviy.class));
        finish();
    }

    //Listener of page
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    //Adapter of page
    public class IntroAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        private TextView tvPromt1;
        private TextView tvPromt2;
        private TextView tvPromt3;

        public IntroAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            Typeface berlinSansFB = Typeface.createFromAsset(getAssets(),"fonts/BRLNSR.TTF");
            if (position == 0){
                tvPromt1 = (TextView) view.findViewById(R.id.tvPromt1);
                tvPromt1.setTypeface(berlinSansFB);
            } else if (position == 1){
                tvPromt2 = (TextView) view.findViewById(R.id.tvPromt2);
                tvPromt2.setTypeface(berlinSansFB);
            } else{
                tvPromt3 = (TextView) view.findViewById(R.id.tvPromt3);
                tvPromt3.setTypeface(berlinSansFB);
            }
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
