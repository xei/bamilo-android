package com.bamilo.android.appmodule.bamiloapp.extlibraries.emarsys;

/**
 * Created by Arash Hassanpour on 4/8/2017.
 */
public enum RequestExecutionType {
    REQUEST_EXEC_IN_BACKGROUND(0),
    REQUEST_EXEC_IN_FOREGROUND(1);

    private int action;

    public int getAction(){
        return this.action;
    }
    RequestExecutionType(int action){
        this.action = action;
    }
}
