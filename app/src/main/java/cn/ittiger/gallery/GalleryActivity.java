package cn.ittiger.gallery;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ylhu on 17-2-24.
 */
public class GalleryActivity extends AppCompatActivity implements GalleryViewPager.MovingListener {
    private GalleryViewPager mViewPager;
    private TextView mTextView;
    private Integer[] mImages = {R.drawable.girl1, R.drawable.girl2, R.drawable.girl3, R.drawable.girl4, R.drawable.girl5, R.drawable.girl6};
    private MyViewPagerAdapter mAdapter;
    private View mRoot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mViewPager = (GalleryViewPager) findViewById(R.id.viewPager);
        mTextView = (TextView)findViewById(R.id.textView);
        mRoot = findViewById(R.id.blackView);
        mAdapter = new MyViewPagerAdapter(this, R.layout.page_view_item, Arrays.asList(mImages));
        mViewPager.setAdapter(mAdapter);
        mViewPager.setMovingListener(this);

        mTextView.setText("1/" + mAdapter.getCount());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                mTextView.setText((position + 1) + "/" + mAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class MyViewPagerAdapter extends PagerAdapterBase<Integer> {

        public MyViewPagerAdapter(Activity activity, int viewId,
                                  List<Integer> list) {
            super(activity, viewId, list);
        }

        @Override
        public void initPagerItemView(View view, final int position) {
            final ImageView iv = (ImageView) view.findViewById(R.id.image);
            iv.setImageResource(getItem(position));
        }
    }

    @Override
    public void onMoving(float percent) {

        if(mTextView.getVisibility() == View.VISIBLE) {
            mTextView.setVisibility(View.GONE);
        }
        mRoot.setAlpha(1 - percent / 2);
        if(percent == 1) {
            onBackPressed();
        }
//        Log.d("Gallery", percent + "");
    }
}
