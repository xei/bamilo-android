package pt.rocket.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.components.customfontviews.HoloFontLoader;
import pt.rocket.components.absspinner.IcsAdapterView;
import pt.rocket.components.absspinner.IcsAdapterView.OnItemSelectedListener;
import pt.rocket.components.absspinner.IcsSpinner;
import pt.rocket.controllers.PickupStationsAdapter;
import pt.rocket.framework.objects.IJSONSerializable;
import pt.rocket.framework.objects.PickUpStationObject;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * Class that represents an Shipping Method Sub form.
 * 
 * @author Manuel Silva
 * 
 */
public class ShippingMethodSubForm implements IJSONSerializable, Parcelable {
	private final static String TAG = LogTagHelper.create( ShippingMethodSubForm.class );

	private int lastID = 0x7f096000;
	private IcsAdapterView.OnItemSelectedListener spinnerSelectedListener;
	
    public String key;
    public String scenario;
    public ArrayList<PickUpStationObject> options;
    public String type;
    public boolean required;
    public String value;
    public String name;
    public String id;
    public String label;
    
    public View dataControl;
    
    public IcsSpinner icsSpinner;

    public ListView pickup_stations_list_view;
    /**
     * Form empty constructor.
     */
    public ShippingMethodSubForm() {
        this.key = "";
        this.scenario = "";
        this.options = new ArrayList<PickUpStationObject>();
        this.type = "";
        this.required = false;
        this.value = "";
        this.id = "";
        this.name = "";
        this.label = "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.optString(RestConstants.JSON_ID_TAG);
            name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
            key = jsonObject.optString(RestConstants.JSON_KEY_TAG);
            scenario = jsonObject.optString(RestConstants.JSON_SCENARIO_TAG);
            if(jsonObject.has(RestConstants.JSON_TYPE_TAG)){
                type = jsonObject.optString(RestConstants.JSON_TYPE_TAG);    
            } else {
                type = "metadata";
            }
            
            value = jsonObject.optString(RestConstants.JSON_VALUE_TAG);
            label = jsonObject.optString(RestConstants.JSON_LABEL_TAG);
            
            /**
             * TODO: Verify if on SubForm can be required for more then one Form
             * jsonObject.getJSONObject(RestConstants.JSON_VALIDATION_TAG);
             */
            
            required = true;
            if(jsonObject.has(RestConstants.JSON_OPTIONS_TAG)){
                JSONArray optionsArray = jsonObject.getJSONArray(RestConstants.JSON_OPTIONS_TAG);
                for (int i = 0; i < optionsArray.length(); i++) {
                    PickUpStationObject mPickUpStationObject = new PickUpStationObject();
                    mPickUpStationObject.initialize(optionsArray.getJSONObject(i));
                    options.add(mPickUpStationObject);
                }
            }
            
        } catch (JSONException e) {
        	Log.e(TAG, "initialize: error parsing jsonobject", e );
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    /**
     * Gets the next available id for the creation of controls
     * 
     * @return The next ID
     */
    public int getNextId() {
        return ++lastID;
    }

    public View generateForm(final Context context) {
        // Generate LayoutParams for Spinner
        final LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mParams.setMargins(0, context.getResources().getDimensionPixelSize(R.dimen.form_top_margin), 0, context.getResources().getDimensionPixelSize(R.dimen.rounded_margin_mid));

        this.dataControl = View.inflate(context, R.layout.form_icsspinner_shipping, null);
        this.dataControl.setId(getNextId());
        this.dataControl.setLayoutParams(mParams);
//        IcsSpinner icsSpinner = (IcsSpinner)dataControl;
        
        icsSpinner = (IcsSpinner)dataControl.findViewById(android.R.id.custom);
        
        final HashMap<String, ArrayList<PickUpStationObject>> pickupStationByRegion = new HashMap<String, ArrayList<PickUpStationObject>>();
        if (this.options.size() > 0) {
            ArrayList<String> mSpinnerOptions = new ArrayList<String>();
            
            for(int i = 0; i< this.options.size(); i++){
                if(!mSpinnerOptions.contains(this.options.get(i).getRegions().get(0).getName())){
                    mSpinnerOptions.add(this.options.get(i).getRegions().get(0).getName());
                }
                if(!pickupStationByRegion.containsKey(this.options.get(i).getRegions().get(0).getName())){
                    pickupStationByRegion.put(this.options.get(i).getRegions().get(0).getName(), new ArrayList<PickUpStationObject>());
                } 
                pickupStationByRegion.get(this.options.get(i).getRegions().get(0).getName()).add(this.options.get(i));
                
//                mSpinnerOptions.add(this.options.get(i).getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.form_spinner_item, new ArrayList<String>(mSpinnerOptions));
            adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
            icsSpinner.setAdapter(adapter);
            icsSpinner.setPrompt(this.label);
            
//            icsSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                    // TODO Auto-generated method stub
//                    
//                }
//            });
//            
        }
        else {
           return null;
        }

        // sets the spinner value
        int position = -1;
        icsSpinner.setSelection(position);
//        if (null != this.entry.getValue() && !this.entry.getValue().trim().equals("")) {
//            for (String item : new ArrayList<String>(this.entry.getDataSet().values())) {
//                if (item.equals(this.entry.getValue())) {
//                    ((IcsSpinner) this.dataControl).setSelection(position);
//                    break;
//                }
//            }
//            position++;
//        }

        this.dataControl.setVisibility(View.GONE);


        HoloFontLoader.applyDefaultFont(icsSpinner);
        // Listeners
        icsSpinner
                .setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {
                    
                    @Override
                    public void onItemSelected(IcsAdapterView<?> parent, View view, int position,
                            long id) {
                            
                            pickup_stations_list_view = (ListView)dataControl.findViewById(R.id.pickup_stations_list_view);
                            if(pickupStationByRegion.get(icsSpinner.getItemAtPosition(position)).size()>0)
                            {
                                pickup_stations_list_view.setVisibility(View.VISIBLE);
                                pickup_stations_list_view.setAdapter(new PickupStationsAdapter(context, pickupStationByRegion.get(icsSpinner.getItemAtPosition(position))));
                                
                            } else {
                                pickup_stations_list_view.setVisibility(View.GONE);   
                            }
                            
                        if (required) {
//                            mandatoryControl
//                                    .setVisibility(position == Spinner.INVALID_POSITION ? View.VISIBLE
//                                            : View.GONE);
                            
//                            Toast.makeText(context, "please fill all the fields", Toast.LENGTH_LONG).show();
                        }

                        if (null != spinnerSelectedListener) {
                            spinnerSelectedListener.onItemSelected(parent, view, position, id);
                        }
                    }

                    @Override
                    public void onNothingSelected(IcsAdapterView<?> parent) {

                        if (required) {
//                            mandatoryControl.setVisibility(View.VISIBLE);
//                            Toast.makeText(context, "please fill all the fields 2", Toast.LENGTH_LONG).show();
                        }

                        if (null != spinnerSelectedListener) {
                            spinnerSelectedListener.onNothingSelected(parent);
                        }
                    }
                });


        return this.dataControl;
    }
    
//    public void
    
    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(scenario);
        dest.writeList(options);
        dest.writeString(type);
        dest.writeBooleanArray(new boolean[] {required});
        dest.writeString(value);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(label);
        
    }
    
    /**
     * Parcel constructor
     * @param in
     */
    private ShippingMethodSubForm(Parcel in) {
        key = in.readString();
        scenario = in.readString();
        options = new ArrayList<PickUpStationObject>();
        in.readArrayList(PickUpStationObject.class.getClassLoader());
        type = in.readString();
        in.readBooleanArray(new boolean[] {required});
        value = in.readString();
        id = in.readString();
        name = in.readString();
        label = in.readString();
        
    }
    
    /**
     * Create parcelable 
     */
    public static final Parcelable.Creator<ShippingMethodSubForm> CREATOR = new Parcelable.Creator<ShippingMethodSubForm>() {
        public ShippingMethodSubForm createFromParcel(Parcel in) {
            return new ShippingMethodSubForm(in);
        }

        public ShippingMethodSubForm[] newArray(int size) {
            return new ShippingMethodSubForm[size];
        }
    };

}
