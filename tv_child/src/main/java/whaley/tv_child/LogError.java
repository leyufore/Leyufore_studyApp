package whaley.tv_child;

import android.util.Log;

/**
 * Created by wenrule on 16/3/28.
 */
public class LogError {
    public static void error(String value){
        Log.e("tv_child",value);
    }
    public static void error(int  value){
        Log.e("tv_child","" + value);
    }
}
