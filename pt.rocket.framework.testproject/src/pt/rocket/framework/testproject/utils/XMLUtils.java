package pt.rocket.framework.testproject.utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import pt.rocket.framework.testproject.helper.XMLReadingConfiguration;
import pt.rocket.framework.testproject.objects.XMLObject;
import android.content.Context;
import android.util.Log;

/**
 * This class encloses all methods related to XML
 * @author josedourado
 *
 */
public class XMLUtils {

    public static String TAG = XMLUtils.class.getSimpleName();
    private static String failedParameterMessage;

    /**
     * Parses a given xml rules file
     * 
     * @param context
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static XMLObject xmlParser(Context context, int id) throws XmlPullParserException, IOException {
        // mastertree created to contain all written rules in the xml
        ArrayList<XMLObject> masterChildrenList = new ArrayList<XMLObject>();
        XMLObject masterTree = new XMLObject("mastertree", masterChildrenList, "", "", "");

        XmlPullParser xpp = context.getResources().getXml(id);
        int eventType = xpp.getEventType();

        int previousDepth;
        int currentDepth;

        // try {
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                // Advances "resources" tag
                if (xpp.getName().equals("resources")) {
                    // if last element is "/resources" breaks the cycle
                    if (eventType == XmlPullParser.END_TAG) {
                        break;
                    }
                    eventType = xpp.next();
                } else if (xpp.getAttributeValue(null, XMLReadingConfiguration.XML_TYPE_TAG) != null) {

                    XMLObject object = new XMLObject(xpp.getName(), null, xpp.getAttributeValue(null, XMLReadingConfiguration.XML_TYPE_TAG),
                            xpp.getAttributeValue(null, XMLReadingConfiguration.XML_METHOD_TAG), xpp.getAttributeValue(null,
                                    XMLReadingConfiguration.XML_EXPECTED_TAG));
                    previousDepth = xpp.getDepth();
                    xpp.next();
                    currentDepth = xpp.getDepth();
                    ArrayList<XMLObject> childrenList = new ArrayList<XMLObject>();
                    childrenList = object.getChildrenNode();

                    while (previousDepth < currentDepth) {

                        object.setHasChildren(true);
                        XMLObject child = new XMLObject(xpp.getName(), null, xpp.getAttributeValue(null, XMLReadingConfiguration.XML_TYPE_TAG),
                                xpp.getAttributeValue(null, XMLReadingConfiguration.XML_METHOD_TAG), xpp.getAttributeValue(null,
                                        XMLReadingConfiguration.XML_EXPECTED_TAG));
                        if (xpp.getAttributeValue(null, XMLReadingConfiguration.XML_CHILDREN_TAG).equals(XMLReadingConfiguration.XML_TRUE_TAG)) {
                            child.setHasChildren(true);
                            depthSearch(child, xpp);
                        }

                        childrenList.add(child);
                        eventType = xpp.next();

                        // Advancing end tags
                        if (eventType == XmlPullParser.END_TAG)
                            xpp.next();

                        // if we find an item with a depth inferior, we stopped
                        // getting children
                        if (xpp.getDepth() < currentDepth) {
                            break;
                        } else {
                            currentDepth = xpp.getDepth();
                        }
                    }
                    object.setChildrenNode(childrenList);
                    Log.i(TAG, "childrenList.size() " + childrenList.size());
                    if (childrenList.size() == 0)
                        eventType = xpp.next();

                    previousDepth = 0;
                    currentDepth = 0;

                    // add object to master tree
                    masterChildrenList.add(object);
                    masterTree.setChildrenNode(masterChildrenList);
                    Log.i(TAG, "MASTER TREE HAS " + masterChildrenList.size());

                }

            } else {
                eventType = xpp.next();
            }

        }

        printXMLTree(masterTree, 0, "");

        return masterTree;
    }

    /**
     * Method to do a depth search in the tree in order to find all successive
     * children
     * 
     * @param childrenList
     * @param xpp
     * @throws IOException
     * @throws XmlPullParserException
     */
    private static void depthSearch(XMLObject parent, XmlPullParser xpp) throws XmlPullParserException, IOException {
        int previousDepth = 0;
        int currentDepth = 0;
        int eventType = xpp.getEventType();
        if (eventType != XmlPullParser.END_DOCUMENT & eventType != XmlPullParser.END_TAG) {
            ArrayList<XMLObject> subChildren = new ArrayList<XMLObject>();
            subChildren = parent.getChildrenNode();
            previousDepth = xpp.getDepth();
            eventType = xpp.next();
            if (eventType != XmlPullParser.END_DOCUMENT && eventType != XmlPullParser.END_TAG) {
                currentDepth = xpp.getDepth();

                while (previousDepth < currentDepth) {
                    XMLObject child = new XMLObject(xpp.getName(), null, xpp.getAttributeValue(null, XMLReadingConfiguration.XML_TYPE_TAG),
                            xpp.getAttributeValue(null, XMLReadingConfiguration.XML_METHOD_TAG), xpp.getAttributeValue(null,
                                    XMLReadingConfiguration.XML_EXPECTED_TAG));
                    if (xpp.getAttributeValue(null, XMLReadingConfiguration.XML_CHILDREN_TAG).equals(XMLReadingConfiguration.XML_TRUE_TAG)) {
                        child.setHasChildren(true);
                        depthSearch(child, xpp);

                    }

                    subChildren.add(child);

                    // Advancing end tags
                    if (xpp.next() == XmlPullParser.END_TAG)
                        xpp.next();

                    currentDepth = xpp.getDepth();
                }
            } else {
                xpp.next();
                eventType = xpp.getEventType();
            }
        }

    }

    /**
     * Receives a XMLObject representing a master tree and prints its content
     * 
     * @param masterTree
     * @param level
     * @param tab
     */
    private static void printXMLTree(XMLObject masterTree, int level, String tab) {
        ArrayList<XMLObject> children = masterTree.getChildrenNode();
        int size = children.size();
        for (int i = 0; i < size; i++) {
            Log.i("printXMLTree", "  " + level + " " + tab + " <" + children.get(i).getTag() + ">");
            if (children.get(i).getHasChildren()) {
                printXMLTree(children.get(i), level + 1, tab + " ");
            }
        }

    }

    /**
     * Receives a json object, and verifies if this json object complies to a
     * given set of rules
     * 
     * @param jsonObject
     * 
     * @param jsonObject
     * @param generalRules
     * @return
     */
    public static boolean jsonObjectAssertion(JSONObject jsonObject, XMLObject generalRules) {
        // TODO Auto-generated method stub
        boolean jsonIsValid = true;
        failedParameterMessage = "";
        ArrayList<XMLObject> rules = generalRules.getChildrenNode();
        int size = rules.size();

        for (int i = 0; i < size; i++) {
            try {
                Log.i(TAG, " rules.get(i).getTag() " + rules.get(i).getTag());
                Method method = jsonObject.getClass().getMethod((String) rules.get(i).getMethod(), String.class);
                Log.i(TAG, " class => " + method.invoke(jsonObject, rules.get(i).getTag()).getClass().getSimpleName().toLowerCase());
                if (method.invoke(jsonObject, rules.get(i).getTag()) == null) {
                    jsonIsValid = false;
                    failedParameterMessage = "'" + rules.get(i).getTag() + "'" + " doesn't exist";
                    break;
                }
                if (!method.invoke(jsonObject, rules.get(i).getTag()).getClass().getSimpleName().toLowerCase().equals(rules.get(i).getType())) {
                    jsonIsValid = false;
                    failedParameterMessage = "'" + rules.get(i).getTag() + "'" + " doesn't match the expected type";
                    Log.i(TAG, " failedParameterMessage " + failedParameterMessage);
                    break;
                }
                // when an object is of the json object type and has children
                // defined in the rules
                // we need to go deeper in the tree
                if (rules.get(i).getHasChildren() && rules.get(i).getType().equals("jsonobject")) {
                    JSONObject subJSON = (JSONObject) method.invoke(jsonObject, rules.get(i).getTag());
                    jsonIsValid = jsonObjectAssertion(subJSON, rules.get(i));
                }

                if (rules.get(i).getHasChildren() && rules.get(i).getType().equals("jsonarray")) {
                    JSONArray subJSON = (JSONArray) method.invoke(jsonObject, rules.get(i).getTag());
                    JSONObject index;
                    try {
                        index = (JSONObject) subJSON.getJSONObject(0);
                        jsonIsValid = jsonObjectAssertion(index, rules.get(i));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        jsonIsValid = false;
                    }

                }
            } catch (SecurityException e) {
                e.printStackTrace();
                jsonIsValid = false;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                jsonIsValid = false;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                jsonIsValid = false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                jsonIsValid = false;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                jsonIsValid = false;
            }

        }

        Log.i(TAG, " json is valid => " + jsonIsValid);
        return jsonIsValid;

    }
    
    public static String getMessage(){
        return failedParameterMessage;
    }
}
