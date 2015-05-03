package vershitsky.kirill.myapp;

import android.content.Context;
import android.content.res.TypedArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Вершицкий on 30.04.2015.
 */
public class Utils {
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }
    public static JSONObject getJsonParams(ArrayList<String> sexs, String... params){
        JSONObject searchKeys = new JSONObject();
        JSONArray searchArray = new JSONArray();
        for(String sex:sexs){
            JSONArray paramsArray = new JSONArray();
            for(String param:params){
                paramsArray.put(param);
            }
            paramsArray.put(sex);
            searchArray.put(paramsArray);
        }
        try {
            searchKeys = searchKeys.put("keys", searchArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return searchKeys;
    }
}
