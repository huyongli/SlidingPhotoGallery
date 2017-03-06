package cn.ittiger.gallery;

import java.util.List;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * ViewPager的基础适配器
 * @author: huylee
 * @time:	2014-11-18下午10:28:50
 * @param <T>
 */
public abstract class PagerAdapterBase<T> extends PagerAdapter {
	private List<T> list;
	private int viewId;
	private LayoutInflater inflater;

	public PagerAdapterBase(Activity activity, int viewId, List<T> list) {
		this.viewId = viewId;
		this.list = list;
		inflater = activity.getLayoutInflater();
	}
	
	/**
	 * 得到适配器(Adapter)数据源的某项数据
	 * @author: huylee
	 * @time:	2014-11-19下午9:50:42
	 * @param position
	 * @return
	 */
	public T getItem(int position) {
		if(position < 0 || position >= getCount()) {
			throw new IndexOutOfBoundsException("超出了数据源的索引");
		}
		return list.get(position);
	}
	
	// 获取要滑动的控件的数量
	@Override
	public int getCount() {
		return list.size();
	}
	
	//当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
	@Override
	public Object instantiateItem(ViewGroup view, int position) {
		View layout = inflater.inflate(viewId, view, false);
		layout.setTag(position);
		
		initPagerItemView(layout, position);
		
		((ViewPager) view).addView(layout, 0);
		return layout;
	}
	
	// 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	// PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public void finishUpdate(View container) {
		
	}
	
	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View container) {
		
	}
	
	@Override
	public int getItemPosition(Object object) {
//		return super.getItemPosition(object);
		return POSITION_NONE;
	}
	
	/**
	 * 初始化ViewPager中的PagerItem选项的视图(View)
	 * @author: huylee
	 * @time:	2014-11-18下午10:26:46
	 * @param view		构造函数中传入的自定义layout对应的view对象
	 * @param position 	pager中的Item项的索引位置
	 */
	public abstract void initPagerItemView(View view, int position);
}
