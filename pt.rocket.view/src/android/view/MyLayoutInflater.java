/**
 * @author Michael Kroez/Smart Mobile Factory
 * 
 * @version 1.00
 * 
 * 2013/02/26
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package android.view;

import org.holoeverywhere.FontLoader;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Class that implements a custom layout inflater that
 * loads the roboto font for each inflated view hierarchy.
 * This is done via wrapping the system default layout inflater.
 * 
 * @author Michael Kroez
 *
 */
public class MyLayoutInflater extends LayoutInflater {

	LayoutInflater mLayoutInflater;
	
	public MyLayoutInflater( Context context, LayoutInflater inflater ) {
		super( context );
		mLayoutInflater = inflater;
	}
	
	public MyLayoutInflater(Context context) {
		super(context);
		mLayoutInflater = LayoutInflater.from( context );
 	}
		
	/**
	 * (non-Javadoc)
	 * @see android.view.LayoutInflater#cloneInContext(android.content.Context)
	 */
	@Override
	public MyLayoutInflater cloneInContext(Context newContext) {
		return new MyLayoutInflater( newContext, mLayoutInflater.cloneInContext(newContext));
	}

	/**
	 * (non-Javadoc)
	 * @see android.view.LayoutInflater#getContext()
	 */
	@Override
	public Context getContext() {
		return mLayoutInflater.getContext();
	}

	/**
	 * (non-Javadoc)
	 * @see android.view.LayoutInflater#getFilter()
	 */
	@Override
	public Filter getFilter() {
		return mLayoutInflater.getFilter();
	}

	/**
	 * Inflates and loads the Roboto Font
	 * 
	 * (non-Javadoc)
	 * @see android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)
	 */
	@Override
	public View inflate(int resource, ViewGroup root, boolean attachToRoot) {
		View v = mLayoutInflater.inflate( resource,  root, attachToRoot );
		FontLoader.apply( v, FontLoader.ROBOTO_REGULAR);
		return v;
	}

	/**
	 * Inflates and loads the Roboto Font
	 * 
	 * (non-Javadoc)
	 * @see android.view.LayoutInflater#inflate(int, android.view.ViewGroup)
	 */
	@Override
	public View inflate(int resource, ViewGroup root) {
		View v = mLayoutInflater.inflate(resource,  root );
		FontLoader.apply( v, FontLoader.ROBOTO_REGULAR);
		return v;
	}

	/**
	 * Inflates and loads the Roboto Font
	 * 
	 *  @see android.view.LayoutInflater#inflate(XMlPullParser, android.view.ViewGroup, boolean)
	 */
	@Override
	public View inflate(XmlPullParser parser, ViewGroup root,
			boolean attachToRoot) {
		View v = mLayoutInflater.inflate( parser, root, attachToRoot );
		FontLoader.apply( v, FontLoader.ROBOTO_REGULAR);
		return v;
	}

	/**
	 * Inflates and loads the Roboto Font
	 * 
	 *  @see android.view.LayoutInflater#inflate(XMlPullParser, android.view.ViewGroup)
	 */
	@Override
	public View inflate(XmlPullParser parser, ViewGroup root) {
		View v = mLayoutInflater.inflate( parser, root );
		FontLoader.apply( v, FontLoader.ROBOTO_REGULAR);
		return v;
	}

	/**
	 * Wraps the default onCreateView
	 * 
	 *  @see android.view.LayoutInflater#onCreateView( String, AttributeSet)
	 */
	@Override
	protected View onCreateView(String name, AttributeSet attrs)
			throws ClassNotFoundException {
		return mLayoutInflater.onCreateView(name,  attrs );
	}

	/**
	 * Wraps the default onCreateView
	 * 
	 * @see android.view.LayoutInflater#onCreateView( android.view.View, String, AttributeSet)
	 */
	@Override
	protected View onCreateView(View parent, String name, AttributeSet attrs)
			throws ClassNotFoundException {
		return mLayoutInflater.onCreateView( parent, name, attrs );
	}

	/**
	 * Wraps the default setFactory
	 * 
	 * @see android.view.LayoutInflater#setFactory( Factory )
	 */
	@Override
	public void setFactory(Factory factory) {
		mLayoutInflater.setFactory(factory );
	}

	/**
	 * Wraps the default setFactory2
	 * 
	 *  @see android.view.LayoutInflater#setFactory2( Factory2 )
	 */
	@Override
	public void setFactory2(Factory2 factory) {
		mLayoutInflater.setFactory2( factory );
	}

	/**
	 * Wraps the default setFilter
	 * 
	 * @see android.view.LayoutInflater#setFilter( Filter )
	 */
	@Override
	public void setFilter(Filter filter) {
		mLayoutInflater.setFilter(filter);
	}
	
	
}
