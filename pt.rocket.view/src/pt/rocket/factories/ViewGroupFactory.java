package pt.rocket.factories;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import android.view.View;
import android.view.ViewGroup;

public class ViewGroupFactory{
    // TODO validate if it's better wih SparceArray
    private HashMap<Integer, View> views;
    
    public ViewGroupFactory(ViewGroup viewGroup){
        views = new HashMap<Integer, View>();
        for(int i = 0; i<viewGroup.getChildCount();i++){
            View child = viewGroup.getChildAt(i); 
            views.put(child.getId(), child);
        }
    }
    
    public ViewGroupFactory(View... args){
        for(View child : args){
            views.put(child.getId(), child);
        }
    }
    
    public View setViewVisible(int id){
        View child = null;
        for(Entry<Integer, View> entry : views.entrySet()) {
            int key = entry.getKey();
            View value = entry.getValue();
            if(key == id){
                value.setVisibility(View.VISIBLE);
                child = value;
            } else {
                value.setVisibility(View.GONE);
            }
        }
        return child;
    }
    
    public View getVisibleView(){
        for(Entry<Integer, View> entry : views.entrySet()) {
            View value = entry.getValue();
            if(value.getVisibility() == View.VISIBLE){
                return value;
            }
        }
        return null;
    }
    
    
}
