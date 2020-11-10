package com.linlinlin.mybanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class MyBanner extends FrameLayout {
    private ViewPager vp;
    private TextView tv_title;
    private LinearLayout ll_point;
    private List<ImageView> mImageViews;

    // 图片资源ID
    private int[] imageIds = {
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.d,
            R.drawable.e
    };

    // 图片标题集合
    private String[] imageDescriptions = {
            "尚硅谷波河争霸赛！",
            "凝聚你我，放飞梦想！",
            "抱歉没座位了！",
            "7月就业名单全部曝光！",
            "平均起薪11345元"
    };

    //上一次高亮显示位置
    private int prePosition = 0;

    //是否已经滚动
    private boolean isDragging = false;

    private final Handler sHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int item = vp.getCurrentItem() + 1;
            vp.setCurrentItem(item);
            sHandler.sendEmptyMessageDelayed(0, 4000);
            return false;
        }
    });

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        sHandler.sendEmptyMessageDelayed(0, 3000);
        Log.i("TAG", "onAttachedToWindow: ");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        sHandler.removeCallbacksAndMessages(null);
        Log.i("TAG", "onDetachedFromWindow: ");
    }

    public MyBanner(@NonNull Context context) {
        this(context, null);
    }

    public MyBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public void setImageIds(int[] imageIds){
        this.imageIds = imageIds;
    }
    public void setImageDescriptions(String[] imageDescriptions){
        this.imageDescriptions = imageDescriptions;
    }

    public MyBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        initData(context);
        setupViewPager();
    }


    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_banner, this, false);
        this.addView(view);
        vp = view.findViewById(R.id.vp);
        tv_title = view.findViewById(R.id.tv_title);
        ll_point = view.findViewById(R.id.ll_point);
    }

    private void initData(Context context) {
        mImageViews = new ArrayList<>();

        for (int i = 0; i < imageIds.length; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(imageIds[i]);
            mImageViews.add(imageView);

            ImageView point = new ImageView(context);
            int px = DensityUtil.dip2px(context, 8f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(px, px);
            point.setBackgroundResource(R.drawable.point_selector);

            if (i == 0) {
                point.setEnabled(true);
            } else {
                point.setEnabled(false);
                params.leftMargin = DensityUtil.dip2px(context, 8);//代码的都是像素
            }
            point.setLayoutParams(params);

            ll_point.addView(point);

        }
        tv_title.setText(imageDescriptions[prePosition]);
    }


    private void setupViewPager() {
        vp.setAdapter(new MyAdapter());
        //设置中间位置
        int item = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mImageViews.size();//保证是imageViews的整数倍
        vp.setCurrentItem(item);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int realPosition = position % mImageViews.size();
                //设置文本
                tv_title.setText(imageDescriptions[realPosition]);
                //上一个高亮设置为灰色
                ll_point.getChildAt(prePosition).setEnabled(false);
                //当前的设置为高亮红色
                ll_point.getChildAt(realPosition).setEnabled(true);
                prePosition = realPosition;

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {//拖拽
                    isDragging = true;
                    sHandler.removeCallbacksAndMessages(null);
                } else if (state == ViewPager.SCROLL_STATE_IDLE && isDragging) {//静止
                    isDragging = false;
                    sHandler.removeCallbacksAndMessages(null);
                    sHandler.sendEmptyMessageDelayed(0, 4000);
                }
                Log.i("state", "onPageScrollStateChanged: " + state);
            }
        });
    }

    private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @SuppressLint("ClickableViewAccessibility")
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int realPosition = position % mImageViews.size();
            ImageView imageView = mImageViews.get(realPosition);
            container.addView(imageView);
            imageView.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sHandler.removeCallbacksAndMessages(null);//消息队列里的都移除了
                        break;
                    case MotionEvent.ACTION_UP:
                        sHandler.removeCallbacksAndMessages(null);
                        sHandler.sendEmptyMessageDelayed(0, 4000);
                        break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                        //up没触发，就执行cancel
                        break;
                }
                return false;
            });
            imageView.setOnClickListener(v -> {
                String description = imageDescriptions[realPosition];
                Toast.makeText(getContext(), description, Toast.LENGTH_SHORT).show();
            });

            return imageView;
        }
    }
}
