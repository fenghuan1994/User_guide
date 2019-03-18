package com.hct.userguide;

import android.content.res.Resources;
import android.os.Bundle;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager mViewPager;
    private List<String> data = new ArrayList<>();
    private List<View> viewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        String[] drawString = getResources().getStringArray(R.array.display_in_viewpager);
        for (String s : drawString) {
            data.add(s);
        }
        for (int i = 0; i < 4; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(getDrawResourceID(data.get(i)));
            viewList.add(imageView);
        }
        mViewPager.setAdapter(new AdapterViewpager(viewList));

    }

    public int getDrawResourceID(String resourceName) {
        Resources res = getResources();
        int picid = res.getIdentifier(resourceName, "drawable", getPackageName());
        return picid;
    }

    public class AdapterViewpager extends PagerAdapter {
        private List<View> mViewList;

        public AdapterViewpager(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);

            int i = position % 4;
            final ImageView imageView = (ImageView) mViewList.get(i);
            imageView.setLayoutParams(params);

            final String url = data.get(position);
            int resId = getDrawResourceID(url);
            imageView.setBackgroundResource(resId);
            ViewGroup parent = (ViewGroup) imageView.getParent();

            if (parent != null){
                parent.removeView(imageView);
            }

            ViewGroup.LayoutParams lp = imageView.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            container.addView(imageView,lp);

            return imageView;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int i = position % 4;
            ImageView imageView = (ImageView) mViewList.get(i);
            container.removeView(imageView);
        }

        @Override
        public int getCount() {
            //必须实现
            return data.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            //必须实现
            return view == object;
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}