/**
 * 
 */
package pt.rocket.framework.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.R;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.ProductReviewCommentCreated;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.objects.PurchaseItem;
import pt.rocket.framework.objects.ShoppingCartItem;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import de.akquinet.android.androlog.Log;

/**
 * @author nunocastro
 *
 */
public class MixpanelTracker {
	
	private final static String TAG = MixpanelTracker.class.getSimpleName();
	private final static String SHARED_PREFERENCES = "whitelabel_prefs";
	private final static String KEY_PURCHASECOUNT = "purchase_count";
	
	private static MixpanelAPI mixpanel;
	private static MixpanelAPI.People people;
	private static String MIXPANEL_TOKEN;
	
	private static PackageInfo pInfo = null;
	private static boolean isEnabled;
	private static boolean isStarted;
	private static int mShopId;
	
	private static JSONObject props;
	
	/**
	 * 
	 */
	private MixpanelTracker() {
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("NewApi")
	public static void startup(Context context, int shopId) {
		isEnabled = context.getResources().getBoolean(R.bool.mixpanel_enable);
		if (!isEnabled)
			return;		

		if (isStarted) {
			Log.d(TAG, "startup: is already started - ignoring");
			return;
		}		
		
		MIXPANEL_TOKEN = context.getResources().getString(R.string.mixpanel_token);
		mShopId = shopId;
		people = null;
		
		try {
			//get the package information			
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
        	e.printStackTrace();
        }
		
		// Initialize the library with our Mixpanel project token 
		mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);		
		isStarted = true;
	}
	
	public static void launch(Context context) {
		if (!isEnabled) {
			Log.d(TAG, "mixpanel seems to be disabled - ignoring");
			return;
		}

		props = null;
		if ( null != pInfo ) {
			setProperty(context.getString(R.string.mixprop_appverion), pInfo.versionName + "." + pInfo.versionCode);
		}		
		setProperty(context.getString(R.string.mixprop_shopcountry), "" + mShopId);
		mixpanel.registerSuperProperties(props);
		
		props = null;
		setProperty(context.getString(R.string.mixprop_platform), context.getString(R.string.mixprop_platformmobile));
		mixpanel.registerSuperPropertiesOnce(props);
		
		Log.d(TAG, "launch tracked: event = " + context.getString(R.string.mixlaunch));		
		
		mixpanel.track(context.getString(R.string.mixlaunch), null);
	}
	
	public static void login(Context context, String customerId, String location) {
		if (!isEnabled) {
			Log.d(TAG, "mixpanel seems to be disabled - ignoring");
			return;
		}
		
		people = mixpanel.getPeople();
		people.identify(customerId);
		
		people.increment(context.getString(R.string.mixproppeople_numberlogins), 1);
		people.set(context.getString(R.string.mixproppeople_loginsused), context.getString(R.string.mixprop_loginmethodemail));

		props = null;
		setProperty(context.getString(R.string.mixprop_loginmethod), context.getString(R.string.mixprop_loginmethodemail));
		setProperty(context.getString(R.string.mixprop_loginlocation), location);
		
		Log.d(TAG, "login tracked: event = " + context.getString(R.string.mixlogin) + " customerId = " + customerId);
		
		mixpanel.track(context.getString(R.string.mixlogin), props);
	}
	
	public static void logout(Context context ) {
		if (!isEnabled) {
			Log.d(TAG, "mixpanel seems to be disabled - ignoring");
			return;
		}
		
		if ( null != people ) {
			people.increment(context.getString(R.string.mixproppeople_numberlogouts), 1);
		}

		props = null;
		setProperty(context.getString(R.string.mixprop_logoutlocation), context.getString(R.string.mixprop_loginlocationsidemenu));
		
		Log.d(TAG, "logout tracked: event = " + context.getString(R.string.mixlogout) );
		mixpanel.track(context.getString(R.string.mixlogout), props);
	}	
	
	public static void signup(Context context, Customer customer, String location) {
		if (!isEnabled)
			return;

		props = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		PreInstallController ctrl = new PreInstallController();
		
		setProperty(context.getString(R.string.mixprop_createdate), currentDateandTime); 
		setProperty(context.getString(R.string.mixprop_preinstall), "" + ctrl.isPreInstalled(context));
		setProperty(context.getString(R.string.mixprop_signupplatform), context.getString(R.string.mixprop_platformmobile));
		Calendar calendar = Calendar.getInstance();
		if(customer.getBirthday() != null){
			calendar.setTime(customer.getBirthday());
			setProperty(context.getString(R.string.mixprop_age), "" + calendar.get(Calendar.YEAR));
		}
		setProperty(context.getString(R.string.mixprop_gender), "" + customer.getGender());
		setProperty(context.getString(R.string.mixprop_email), "" + customer.getEmail());
		setProperty(context.getString(R.string.mixprop_country), "" + mShopId);
		mixpanel.registerSuperProperties(props);
		
		if ( null != people ) {			
			people.set(context.getString(R.string.mixproppeople_created), currentDateandTime);
			people.set(context.getString(R.string.mixproppeople_preinstall), "" + ctrl.isPreInstalled(context));
			people.set(context.getString(R.string.mixproppeople_signupplatform), currentDateandTime);
			people.set(context.getString(R.string.mixproppeople_source), location);
			people.set(context.getString(R.string.mixproppeople_firstname), customer.getFirstName());
			people.set(context.getString(R.string.mixproppeople_lastname), customer.getLastName());
		    calendar.setTime(customer.getBirthday());
		    Log.i(TAG, "code1date "+calendar.get(Calendar.YEAR));
			people.set(context.getString(R.string.mixproppeople_age), calendar.get(Calendar.YEAR));
			people.set(context.getString(R.string.mixproppeople_gender), customer.getGender());
			people.set(context.getString(R.string.mixproppeople_email), customer.getEmail());
			people.set(context.getString(R.string.mixproppeople_country), "" + mShopId);
			people.set(context.getString(R.string.mixproppeople_shopcountry), "" + mShopId);
		}
		
		props = null;
		setProperty(context.getString(R.string.mixprop_registrationlocation), location);
		setProperty(context.getString(R.string.mixprop_registrationmethod), context.getString(R.string.mixprop_loginmethodemail));
		
		Log.d(TAG, "signup tracked: event = " + context.getString(R.string.mixsignup) + " customerId = " + customer.getIdAsString());
		mixpanel.track(context.getString(R.string.mixsignup), props);
	}
	
	public static void search(Context context, String criteria, long results) {
		if (!isEnabled)
			return;

		if ( null != people ) {
			people.increment(context.getString(R.string.mixproppeople_numbersearches), 1);
		}
		
		props = null;
		setProperty(context.getString(R.string.mixprop_searchtearm), criteria);
		setProperty(context.getString(R.string.mixprop_numberresults), "" + results);
		
		Log.d(TAG, "signup tracked: event = " + context.getString(R.string.mixsearch) + " criteria = " + criteria + " #results = " + results);
		mixpanel.track(context.getString(R.string.mixsearch), props);
	}
	
	public static void trackSale(Context context, String cartValue, List<PurchaseItem> listItems ) {
		if (!isEnabled)
			return;

		props = null;
        SharedPreferences sharedPrefs = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int count = sharedPrefs.getInt(KEY_PURCHASECOUNT, 0);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(KEY_PURCHASECOUNT, ++count);
        editor.commit();
        setProperty(context.getString(R.string.mixprop_purchacecount), "" + count);
        mixpanel.registerSuperProperties(props);

		String category = "";
		for ( PurchaseItem item : listItems ) {
			category += item.category + "; ";
		}
        
		if ( null != people ) {
			people.increment(context.getString(R.string.mixproppeople_totalnumpurchaces), 1);
			
			people.set(context.getString(R.string.mixproppeople_itemscategory), category);
			people.set(context.getString(R.string.mixproppeople_transations), cartValue);
		}
        
		props = null;
		//setProperty(context.getString(R.string.mixprop_paymentmethod), method);
		setProperty(context.getString(R.string.mixprop_itemcategory), category);
		setProperty(context.getString(R.string.mixprop_totalitems), "" + listItems.size());
		setProperty(context.getString(R.string.mixprop_totalcart), cartValue);
		//setProperty(context.getString(R.string.mixprop_voucheramout), voucherValue);
		//setProperty(context.getString(R.string.mixprop_usevoucher), "" + useVoucher);
		
		Log.d( TAG, "trackSale: cartValue = " + cartValue  );
		mixpanel.track(context.getString(R.string.mixsale), props);
		
	}
	
	public static void changeShop(Context context, int shopId) {
		if (!isEnabled) {
			Log.d(TAG, "mixpanel seems to be disabled - ignoring");
			return;
		}

		mShopId = shopId;
		
		props = null;
		setProperty(context.getString(R.string.mixprop_shopcountry), "" + mShopId);
		mixpanel.registerSuperProperties(props);
		
		Log.d(TAG, "Shop changed tracked: event = " + context.getString(R.string.mixshop));		

		mixpanel.track(context.getString(R.string.mixshop), null);
	}
	
	public static void setPushNotification(Context context, boolean enabled) {
		if (!isEnabled)
			return;
		
		props = null;
		setProperty(context.getString(R.string.mixprop_pushnotifications), "" + enabled);
		mixpanel.registerSuperProperties(props);
		
		Log.d( TAG, "setPushNotification: notificationsEnabled = " + enabled );
		mixpanel.track(context.getString(R.string.mixpushnotification), null);
	}
	

	public static void product(Context context, CompleteProduct product) {
		if (!isEnabled)
			return;
		
		if ( null != people ) {
			people.increment(context.getString(R.string.mixproppeople_numberitems), 1);			
		}
		
		props = null;
		setProperty(context.getString(R.string.mixprop_itemid), product.getSku());
		setProperty(context.getString(R.string.mixprop_itemname), product.getName());
		setProperty(context.getString(R.string.mixprop_itembrand), product.getBrand());
		setProperty(context.getString(R.string.mixprop_itemcategory), product.getCategories().size() > 0 ? product.getCategories().get(0) : "" );
		setProperty(context.getString(R.string.mixprop_itemsubcategory), "");
		setProperty(context.getString(R.string.mixprop_itemprice), getPriceRange(product.getPriceAsDouble()) );
		
		Log.d( TAG, "product tracking: productSKU = " + product.getSku() );
		mixpanel.track(context.getString(R.string.mixproduct), props);
	}

	public static void productAddedToCart(Context context, CompleteProduct product, ProductSimple simple, Double price, String location) {
		if (!isEnabled)
			return;
		
		props = null;
		setProperty(context.getString(R.string.mixprop_itemid), simple.getAttributeByKey(ProductSimple.SKU_TAG));
		setProperty(context.getString(R.string.mixprop_itemname), product.getName());
		setProperty(context.getString(R.string.mixprop_itembrand), product.getBrand());
		setProperty(context.getString(R.string.mixprop_itemcategory), product.getCategories().size() > 0 ? product.getCategories().get(0) : "" );
		setProperty(context.getString(R.string.mixprop_itemsubcategory), "");
		setProperty(context.getString(R.string.mixprop_itemprice), getPriceRange(price) );
		setProperty(context.getString(R.string.mixprop_itemquantity), "1" );
		setProperty(context.getString(R.string.mixprop_itemlocation), location );
		
		Log.d( TAG, "cart tracking: productSKU = " + product.getSku() );
		mixpanel.track(context.getString(R.string.mixcart), props);
	}

    
	public static void checkout(Context context, List<ShoppingCartItem> items) {
		if (!isEnabled)
			return;
		
		Double actItemPrice = 0.0;
		Double cart_price = 0.0; 
		
		for ( ShoppingCartItem item : items ) {
	        if ( item.getPrice().equals( item.getSpecialPrice())) {
	            actItemPrice = item.getPriceVal();
	        } else {
	            actItemPrice = item.getSpecialPriceVal();
	        }
	        
	        cart_price += actItemPrice * item.getQuantity();
		}

		props = null;
		setProperty(context.getString(R.string.mixprop_cartcategories), "");
		setProperty(context.getString(R.string.mixprop_cartnumber), "" + items.size());
		setProperty(context.getString(R.string.mixprop_carttotal), "" + cart_price);
		
		Log.d( TAG, "checkout tracking:  " + context.getString(R.string.mixcheckout) );
		mixpanel.track(context.getString(R.string.mixcheckout), props);
	}

	public static void share(Context context, Intent intent) {
		if (!isEnabled)
			return;
		
		String provider = getShareProviderName(context, intent);
		
		if ( null != people ) {
			people.increment(context.getString(R.string.mixproppeople_numbershares), 1);
			people.set(context.getString(R.string.mixproppeople_sharenetwork), provider);
		}
		
		props = null;
		try {
			setProperty(context.getString(R.string.mixprop_sharenetwork), provider);
			setProperty(context.getString(R.string.mixprop_sharelocation), intent.getExtras().getString(context.getString(R.string.mixprop_sharelocation)));
			setProperty(context.getString(R.string.mixprop_sharecategory), intent.getExtras().getString(context.getString(R.string.mixprop_sharecategory)));
			setProperty(context.getString(R.string.mixprop_sharename), intent.getExtras().getString(context.getString(R.string.mixprop_sharename)));
			setProperty(context.getString(R.string.mixprop_sharebrand), intent.getExtras().getString(context.getString(R.string.mixprop_sharebrand)));
			setProperty(context.getString(R.string.mixprop_shareprice), intent.getExtras().getString(context.getString(R.string.mixprop_shareprice)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d( TAG, "share tracking: " +  context.getString(R.string.mixshare));
		mixpanel.track(context.getString(R.string.mixshare), props);
	}
	
	public static void listCategory(Context context, String category, int page) {
		if (!isEnabled)
			return;
		
		props = null;
		setProperty(context.getString(R.string.mixprop_category), category);
		setProperty(context.getString(R.string.mixprop_page), "" + page);		
		
		Log.d( TAG, "listcategory tracking: " + context.getString(R.string.mixviewcategory) );
		mixpanel.track(context.getString(R.string.mixviewcategory), props);
	}
	
	public static void viewCart(Context context, int numberItems) {
		if (!isEnabled)
			return;

		props = null;		
		setProperty(context.getString(R.string.mixprop_cartitems), "" + numberItems);
		
		Log.d( TAG, "checkout tracking:  " + context.getString(R.string.mixviewcart) );
		mixpanel.track(context.getString(R.string.mixviewcart), props);
	}
	
	public static void rate(Context context, CompleteProduct product, ProductReviewCommentCreated review, float ratePrice, float rateApperance, float rateQuality) {
		if (!isEnabled)
			return;
		
		if ( null != people ) {
			people.increment(context.getString(R.string.mixproppeople_rateitemnumber), 1);			
		}
		
		props = null;
		setProperty(context.getString(R.string.mixprop_rateid), product.getSku());
		setProperty(context.getString(R.string.mixprop_ratename), product.getName());
		setProperty(context.getString(R.string.mixprop_ratebrand), product.getBrand());
		setProperty(context.getString(R.string.mixprop_ratecategory), product.getCategories().size() > 0 ? product.getCategories().get(0) : "" );
		setProperty(context.getString(R.string.mixprop_ratesubcategory), "");
		setProperty(context.getString(R.string.mixprop_rateprice), getPriceRange(product.getPriceAsDouble()));
		setProperty(context.getString(R.string.mixprop_ratingprice), "" + ratePrice );
		setProperty(context.getString(R.string.mixprop_ratingappearance), "" + rateApperance );
		setProperty(context.getString(R.string.mixprop_ratingquality), "" + rateQuality );
		
		Log.d( TAG, "rate tracking: productSKU = " + product.getSku() );
		mixpanel.track(context.getString(R.string.mixrate), props);
	}

	public static void viewRate(Context context, CompleteProduct product, float rate) {
		if (!isEnabled)
			return;
		
		if ( null != people ) {
			people.increment(context.getString(R.string.mixproppeople_rateitemnumber), 1);			
		}
		
		props = null;
		setProperty(context.getString(R.string.mixprop_rateid), product.getSku());
		setProperty(context.getString(R.string.mixprop_ratename), product.getName());
		setProperty(context.getString(R.string.mixprop_ratebrand), product.getBrand());
		setProperty(context.getString(R.string.mixprop_ratecategory), product.getCategories().size() > 0 ? product.getCategories().get(0) : "" );
		setProperty(context.getString(R.string.mixprop_ratesubcategory), "");
		setProperty(context.getString(R.string.mixprop_rateprice), getPriceRange(product.getPriceAsDouble()));
		setProperty(context.getString(R.string.mixprop_ratingaverage), "" + rate );
		
		Log.d( TAG, "view rate tracking: productSKU = " + product.getSku() );
		mixpanel.track(context.getString(R.string.mixviewrate), props);
	}
	
	public static void flush (){
		mixpanel.flush();
	}
	
	/**
	 * Sets Mixpanel properties to use with the register properties function 
	 * 
	 * @param name the name of the property to set
	 * @param value the value of the property to set
	 */
	private static void setProperty(String name, String value ) {
		if (null == props ) {
			props = new JSONObject();
		}
		
		try {
			props.put(name, value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private static String getPriceRange( Double price ) {
		double prc = price;
		String range = "";

		if (prc < 20 ) {
			range = "0-20"; 
		} else if ( prc >= 20 && prc < 50  ) {
			range = "20-50";
		} else if ( prc >= 50 && prc < 100  ) {
			range = "50-100";
		} else if ( prc >= 100 && prc < 200  ) {
			range = "100-200";
		} else if ( prc >= 200 && prc < 500  ) {
			range = "200-500";
		} else if ( prc >= 500 ) {
			range = "500+";
		}
		
		return range;
	}
	
	
	private static String getShareProviderName( Context context, Intent intent ) {
		String name = "";		
		final PackageManager pm = context.getPackageManager();
		ActivityInfo ai = intent.resolveActivityInfo(pm, 0);

		if (ai != null) {            
	            if (ai.labelRes != 0) {
					Resources res;
					try {
						res = pm.getResourcesForApplication(ai.applicationInfo);
						name = res.getString(ai.labelRes);
					} catch (NameNotFoundException e) {						
						e.printStackTrace();
						name = ai.applicationInfo.loadLabel(pm).toString();
					}
	            } else {
	                name = ai.applicationInfo.loadLabel(pm).toString();
	            }
        }		
		
//		//final Intent intent = new Intent(Intent.ACTION_SEND);
//		List<ResolveInfo> riList = pm.queryIntentActivities(intent, 0);
//		for (ResolveInfo ri : riList) {
//		    ai = ri.activityInfo;
//		    
//		    ai.
//		    
//		    String pkg = ai.packageName;
//		    if (pkg.equals("com.facebook.katana") || pkg.equals("com.twitter.android")) {
//
//		        // Add to the list of accepted activities.
//
//		        // There's a lot of info available in the
//		        // ResolveInfo and ActivityInfo objects: the name, the icon, etc.
//
//		        // You could get a component name like this:
//		        ComponentName cmp = new ComponentName(ai.packageName, ai.name);
//		    }
//		}		
		
		return name;
	}
	
}
