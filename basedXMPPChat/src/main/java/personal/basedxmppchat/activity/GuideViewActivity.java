package personal.basedxmppchat.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import java.util.ArrayList;

import personal.basedxmppchat.R;

/**
 * Android实现左右滑动指引效果
 *
 * @Description: Android实现左右滑动指引效果
 * @Author shimiso
 */
public class GuideViewActivity extends ActivitySupport {

    private ViewPager viewPager;
    private ArrayList<View> pageViews;
    private ImageView imageView;
    private ImageView[] imageViews;
    //包裹滑动图片LinearLayout
    private ViewGroup main;
    //包裹小圆点的LinearLayout
    private ViewGroup group;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getEimApplication().addActivity(this);
        LayoutInflater inflater = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.item01, null));
        pageViews.add(inflater.inflate(R.layout.item02, null));
        pageViews.add(inflater.inflate(R.layout.item03, null));
        pageViews.add(inflater.inflate(R.layout.item04, null));
        pageViews.add(inflater.inflate(R.layout.item05, null));
        pageViews.add(inflater.inflate(R.layout.item06, null));
        View view = new View(this);
        view.setBackgroundResource(R.color.white);
        pageViews.add(view);

        imageViews = new ImageView[pageViews.size()];
        main = (ViewGroup) inflater.inflate(R.layout.guide_view, null);

        group = (ViewGroup) main.findViewById(R.id.viewGroup);
        viewPager = (ViewPager) main.findViewById(R.id.guidePages);

        for (int i = 0; i < pageViews.size() - 1; i++) {
            imageView = new ImageView(GuideViewActivity.this);
            imageView.setLayoutParams(new ActionBar.LayoutParams(20, 20));
            imageView.setPadding(20, 0, 20, 0);
            imageViews[i] = imageView;

            if (i == 0) {
                // 默认选中第一张图片
                imageViews[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.page_indicator);
            }

            group.addView(imageViews[i]);
        }

        setContentView(main);

        viewPager.setAdapter(new GuidePageAdapter());
        viewPager.addOnPageChangeListener(new GuidePageChangeListener());
    }

    // 指引页面数据适配器
    class GuidePageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(pageViews.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager)container).addView(pageViews.get(position));
            return pageViews.get(position);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

    // 指引页面更改事件监听器
    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == imageViews.length - 1) {
                GuideViewActivity.this.startActivity(new Intent(
                        GuideViewActivity.this, MainActivity.class));
            } else {
                for (int i = 0; i < imageViews.length - 1; i++) {
                    imageViews[arg0]
                            .setBackgroundResource(R.drawable.page_indicator_focused);

                    if (arg0 != i) {
                        imageViews[i]
                                .setBackgroundResource(R.drawable.page_indicator);
                    }
                }
            }
        }
    }
}
