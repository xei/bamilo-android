package com.mobile.extlibraries.emarsys;

import java.util.ArrayList;

/**
 * Created by Arash Hassanpour on 4/5/2017.
 */
interface RequestCompletion {

    void completion(int status, Object response, ArrayList<String> errors);

}
