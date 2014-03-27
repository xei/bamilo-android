package pt.rocket.framework.testproject.objects;

import java.util.ArrayList;

public class XMLObject {

    private String mTag;
    private ArrayList<XMLObject> mChildrenNode;
    private String mMethod;
    private String mType;
    private boolean mOptional;
    private String mExpectedResult;
    private boolean hasChildren;

    public XMLObject(String tag, ArrayList<XMLObject> childrenNode, String type,String method, String expectedResult, boolean optional) {
        mTag = tag;
        mChildrenNode = childrenNode;
        mType = type;
        mMethod = method;
        mExpectedResult = expectedResult;
        mOptional = optional;
        
    }

    public void setChildrenNode(ArrayList<XMLObject> childreNode) {
        mChildrenNode = childreNode;
    }

    public ArrayList<XMLObject> getChildrenNode() {
        if (mChildrenNode == null) {
            mChildrenNode = new ArrayList<XMLObject>();
        }
        return mChildrenNode;
    }

    public void setHasChildren(Boolean status){
        hasChildren = status;
    }
    
    public boolean getHasChildren(){
        return hasChildren;
    }
    
    public String getTag(){
        return mTag;
    }
    
    public String getType(){
        return mType;
    }
    
    public String getMethod(){
        return mMethod;
    }
    
    public boolean isOptional(){
    	return mOptional;
    }
    
    public String getExpectedResult(){
    	return mExpectedResult;
    }
    
    
}