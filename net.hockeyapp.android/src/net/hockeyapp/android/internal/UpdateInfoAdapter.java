package net.hockeyapp.android.internal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import net.hockeyapp.android.Constants;
import net.hockeyapp.android.UpdateInfoListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * <h4>Description</h4>
 * 
 * Internal helper class. Provides views and data for the list views
 * in UpdateActivity and UpdateFragment. 
 * 
 * <h4>License</h4>
 * 
 * <pre>
 * Copyright (c) 2012 Codenauts UG
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * </pre>
 *
 * @author Thomas Dohmke
 **/
public class UpdateInfoAdapter extends BaseAdapter {
  Activity activity;
  ArrayList<JSONObject> sortedVersions;
  JSONObject newest;
  UpdateInfoListener listener;
  
  public UpdateInfoAdapter(Activity activity, String infoJSON, UpdateInfoListener listener) {
    super();

    this.activity = activity;
    this.listener = listener;

    loadVersions(infoJSON);
    sortVersions();
  }
  
  private void loadVersions(String infoJSON) {
    this.newest = new JSONObject();

    try {
      JSONArray versions = new JSONArray(infoJSON);
      this.sortedVersions = new ArrayList<JSONObject>();
      
      int versionCode = listener.getCurrentVersionCode();
      for (int index = 0; index < versions.length(); index++) {
        JSONObject entry = versions.getJSONObject(index);
        if (entry.getInt("version") > versionCode) {
          newest = entry;
          versionCode = entry.getInt("version");
        }
        sortedVersions.add(entry);
      }
    }
    catch (JSONException e) {
    }
  }

  private void sortVersions() {
    Collections.sort(sortedVersions, new Comparator<JSONObject>() {
      public int compare(JSONObject object1, JSONObject object2) {
        try {
          if (object1.getInt("version") > object2.getInt("version")) {
            return 0;
          }
        }
        catch (JSONException e) {
        }

        return 0;
      }
    });
  }

  public int getCount() {
    return 2 * sortedVersions.size();
  }

  public Object getItem(int position) {
    int currentVersionCode = listener.getCurrentVersionCode();

    JSONObject version = sortedVersions.get(position / 2);
    int versionCode = 0;
    String versionName= "";
    try { 
      versionCode = version.getInt("version");
      versionName = version.getString("shortversion");
    }
    catch (JSONException e) {
    }
    
    String item = null;
    switch (position % 2) {
    case 0:
      item = (position == 0 ? "Release Notes:" : "Version " + versionName + " (" + versionCode + "): " + (versionCode == currentVersionCode ? "[INSTALLED]" : ""));
      break;
    case 1:
      item = failSafeGetStringFromJSON(version, "notes", "");
      break;
    case 2:
    }
    return item;
  }
  
  public String getVersionString() {
    return failSafeGetStringFromJSON(newest, "shortversion", "") + " (" + failSafeGetStringFromJSON(newest, "version", "") + ")";
  }
  
  public String getFileInfoString() {
    int appSize = failSafeGetIntFromJSON(newest, "appsize", 0);
    long timestamp = failSafeGetIntFromJSON(newest, "timestamp", 0);
    Date date = new Date(timestamp * 1000);
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    return dateFormat.format(date) + " - " + String.format("%.2f", appSize / 1024F / 1024F) + " MB";
  }
  
  private static String failSafeGetStringFromJSON(JSONObject json, String name, String defaultValue) {
    try {
      return json.getString(name);
    }
    catch (JSONException e) {
      return defaultValue;
    }
  }
  
  private static int failSafeGetIntFromJSON(JSONObject json, String name, int defaultValue) {
    try {
      return json.getInt(name);
    }
    catch (JSONException e) {
      return defaultValue;
    }
  }

  public long getItemId(int position) {
    return new Integer(position).hashCode();
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    switch (position % 2) {
    case 0:
      return getSimpleView(position, convertView, parent);
    case 1:
      return getWebView(position, convertView, parent);
    default:
      return null;
    }
  }

  private View getSimpleView(int position, View convertView, ViewGroup parent) {
    View row = convertView;
    if (!(row instanceof TextView)) {
      LayoutInflater inflater = activity.getLayoutInflater();
      row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
    }
      
    String item = (String)getItem(position);
    
    TextView textView = (TextView)row.findViewById(android.R.id.text1);
    float scale = activity.getResources().getDisplayMetrics().density;
    boolean leftPadding = false; //(parent.getTag().equals("right"));
    boolean topPadding = (position == 0);
    textView.setPadding((int)(20 * scale) * (leftPadding ? 2 : 1), (int)(20 * scale) * (!leftPadding && topPadding ? 1 : 0), (int)(20 * scale), 0);
    textView.setText(item);
    textView.setTextColor(Color.BLACK);
    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    
    return row;
  }

  private View getWebView(int position, View convertView, ViewGroup parent) {
    View row = convertView;
    if ((row == null) || (row.findViewById(1337) == null)) {
      RelativeLayout layout = new RelativeLayout(activity);
      layout.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
      row = layout;
      
      WebView webView = new WebView(activity);
      webView.setId(1337);
      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); 
      float scale = activity.getResources().getDisplayMetrics().density;
      boolean leftPadding = false; //(parent.getTag().equals("right"));
      params.setMargins((int)(20 * scale) * (leftPadding ? 2 : 1), (int)(0 * scale), (int)(20 * scale), 0);
      webView.setLayoutParams(params);
      layout.addView(webView);
    }
      
    WebView webView = (WebView)row.findViewById(1337);

    String item = (String)getItem(position);
    if (item.trim().length() == 0) {
      webView.loadData("<em>No information.</em>", "text/html", "utf-8");
    }
    else {
      webView.loadDataWithBaseURL(Constants.BASE_URL, item, "text/html", "utf-8", null);
    }
    
    return row;
  }
}
