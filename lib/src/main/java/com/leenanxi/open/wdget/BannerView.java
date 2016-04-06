package com.leenanxi.open.wdget;

/**
 * Created by leenanxi on 16/3/5.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class BannerView extends RelativeLayout {
    private static final int DEFAULT_INTERVAL_TIME = 5 * 1000;

    private static final int titleBackgroundColor = Color.parseColor("#44000000");

    private Context mContext;
    private List<BannerItem> mBannerItems = null;
    private int mIntervalTime = DEFAULT_INTERVAL_TIME;
    private boolean isLoopable = true;
    private boolean isTitleEnabled = true;
    private Handler mLoopHandler = null;
    private Runnable mTaskRunnable = null;
    private ViewPager mViewPager = null;
    private LinearLayout mBottomLayout = null;

    private LinearLayout mIndicatorLayout = null;

    private TextView mTextView = null;

    private List<View> mIndicatorViews = null;


    private BannerViewAdapter mBannerAdapter = null;

    private int mViewPagerCurrentItem = 0;


    public BannerView(Context context) {
        super(context);
        initialize(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BannerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }


    public void setTitleEnabled(boolean enable) {
        if (enable) {
            mBottomLayout.setBackgroundColor(titleBackgroundColor);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mBottomLayout.setBackgroundColor(Color.TRANSPARENT);
            mTextView.setVisibility(View.GONE);
        }
    }


    private void initialize(Context context, AttributeSet attrs) {
        this.mContext = context;
        //初始化viewpager和指示器布局并添加到父布局中
        mViewPager = new ViewPager(context);
        mViewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                int realPosition = mBannerAdapter.translateReal2LogicPosition(position);
                mTextView.setText(mBannerItems.get(realPosition).getTitle());
                refreshIndicator(realPosition);
                mViewPagerCurrentItem = position;
                stopLoop();
                startLoop();

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        addView(mViewPager, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mBottomLayout = new LinearLayout(context);
        LayoutParams bottomParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mBottomLayout.setLayoutParams(bottomParams);
        mBottomLayout.setBackgroundColor(titleBackgroundColor);
        int bottomPadding = dip2px(context, 5);
        mBottomLayout.setPadding(bottomPadding, bottomPadding, bottomPadding, bottomPadding);
        mBottomLayout.setOrientation(LinearLayout.VERTICAL);
        mBottomLayout.setGravity(android.view.Gravity.CENTER);

        mTextView = new TextView(context);
        LayoutParams textLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextView.setLayoutParams(textLayoutParams);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setGravity(CENTER_HORIZONTAL);
        mBottomLayout.addView(mTextView);

        mIndicatorLayout = new LinearLayout(context);
        LayoutParams indicatorLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mIndicatorLayout.setPadding(0, dip2px(context, 2), 0, 0);
        mIndicatorLayout.setLayoutParams(indicatorLayoutParams);
        mBottomLayout.addView(mIndicatorLayout);

        addView(mBottomLayout);

        mIndicatorViews = new ArrayList<>();
        mLoopHandler = new Handler();
        mTaskRunnable = new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(getNextItemPosition(mViewPagerCurrentItem), true);
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mBannerAdapter.setOnItemClickListener(l);
    }

    public synchronized void setBannerItems(List<BannerItem> items) {
        if (items == null || items.size() == 0) {
            Log.e("TAG", "banner data is empty!");
            return;
        }
        this.mBannerItems = items;

        if (mBannerAdapter == null) {
            mBannerAdapter = new BannerViewAdapter(items);
        } else {
            mBannerAdapter.setBannerItems(mBannerItems);
        }
        mViewPager.setAdapter(mBannerAdapter);

        //只有当大于一张轮播图的时候才自动轮播
        if (items.size() == 1) {
            isLoopable = false;
            mViewPager.setCurrentItem(0, true);
        } else {
            mViewPager.setCurrentItem(mBannerItems.size() * 10000);
            generateIndicators(mBannerItems, mIndicatorLayout);
        }
    }

    //添加指示器
    private void generateIndicators(List<BannerItem> mBannerItems, LinearLayout mIndicatorLayout) {
        mIndicatorLayout.removeAllViews();
        mIndicatorViews.clear();
        int bannerCount = mBannerItems.size();
        for (int i = 0; i < bannerCount; i++) {
            View dot = new View(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dip2px(mContext, 5), dip2px(mContext, 5));
            layoutParams.setMargins(dip2px(mContext, 3), 0, dip2px(mContext, 3), 0);
            dot.setLayoutParams(layoutParams);
            dot.setBackgroundResource(R.drawable.banner_indicator_selector);
            if (i == 0) {
                dot.setEnabled(true);
            } else {
                dot.setEnabled(false);
            }
            mIndicatorLayout.addView(dot);
            mIndicatorViews.add(dot);
        }
    }


    //刷新指示器显示位置
    private void refreshIndicator(int position) {
        int indicatorCount = mIndicatorViews.size();
        for (int i = 0; i < indicatorCount; i++) {
            if (i == position) {
                mIndicatorViews.get(i).setEnabled(true);
            } else {
                mIndicatorViews.get(i).setEnabled(false);
            }
        }
    }

    //获取下一个需要显示的banner的位置
    private int getNextItemPosition(int currentItem) {
        if (currentItem + 1 == mBannerAdapter.getCount()) {
            return 0;
        } else {
            return currentItem + 1;
        }
    }

    /**
     * 设置轮播时间间隔
     *
     * @param mIntervalTime
     */
    public void setIntervalTime(int mIntervalTime) {
        this.mIntervalTime = mIntervalTime;
    }

    /**
     * 开始轮播
     */
    public void startLoop() {
        if (isLoopable) {
            mLoopHandler.removeCallbacks(mTaskRunnable);
            mLoopHandler.postDelayed(mTaskRunnable, mIntervalTime);
        }
    }

    /**
     * 暂定轮播
     */
    public void stopLoop() {
        mLoopHandler.removeCallbacks(mTaskRunnable);
    }

    private int dip2px(Context context, float dpValue) {
        if (context == null) return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setImageLoadder(ImageLoader imageLoader) {
        mBannerAdapter.setImageLoadder(imageLoader);
    }

    public interface OnItemClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param position The position that was clicked.
         */
        void onClick(int position);
    }

    public interface ImageLoader {
        void loadImage(ImageView imageView, String url);
    }


    public static class BannerItem {
        private String title;
        private String url;

        public BannerItem(String title, String url) {
            this.title = title;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }
    }


    //adapter

    public class BannerViewAdapter extends PagerAdapter {
        public OnItemClickListener mOnItemClickListener;
        private List<BannerItem> mBannerItems;
        private int mBannerCount;
        private ImageLoader mImageLoader;


        public BannerViewAdapter(List<BannerItem> items) {
            this.mBannerItems = items;
            mBannerCount = mBannerItems.size();
        }

        public void setBannerItems(List<BannerItem> items) {
            this.mBannerItems = items;
            mBannerCount = items.size();
        }

        public void setOnItemClickListener(OnItemClickListener l) {
            mOnItemClickListener = l;
        }

        @Override
        public int getCount() {
            return mBannerCount == 1 ? 1 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final int realPosition = translateReal2LogicPosition(position);
            View view = getView(realPosition, container);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onClick(realPosition);
                    }
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        private View getView(int position, ViewGroup container) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (mBannerItems != null && !mBannerItems.isEmpty()) {
                if (mImageLoader != null) {
                    mImageLoader.loadImage(imageView, mBannerItems.get(position).getUrl());
                }
            }
            return imageView;
        }

        public int translateReal2LogicPosition(int position) {
            return position % mBannerCount;
        }

        public void setImageLoadder(ImageLoader imageLoader) {
            this.mImageLoader = imageLoader;
        }
    }

}