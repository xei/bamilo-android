package pt.rocket.utils;

import android.text.Editable;
import android.text.TextWatcher;
import pt.rocket.view.R;

/**
* A textwatcher to determine if the text of an edit text is a valid number <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
*
* @author Paulo Carvalho
*
* @version 2.0 
*
* 2012/06/19
* 
*/
public class CheckNumber implements TextWatcher{

    /**
     * fires after the user has changed something on the editext
     */
    public void afterTextChanged(Editable s) {

        try {
            
            if(s.toString().length()==0){
                s.replace(0, s.length(), "1");
              
            } else if(Integer.parseInt(s.toString())<1){
                s.replace(0, s.length(), "1");
            }           
        } catch(NumberFormatException nfe) { }

    }

    /**
     * Fires before the changes made by the user are passed to the edit text 
     */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Not used, details on text just before it changed
        // used to track in detail changes made to text, e.g. implement an undo
    }

    /**
     * Fires during the commit of the changes made by the user on the edit text  
     */
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Not used, details on text at the point change made
    }

}
