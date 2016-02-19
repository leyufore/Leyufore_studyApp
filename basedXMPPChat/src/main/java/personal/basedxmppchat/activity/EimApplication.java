package personal.basedxmppchat.activity;

import android.app.Activity;
import android.app.Application;

import org.jivesoftware.smack.android.AndroidSmackInitializer;
import org.jivesoftware.smack.util.stringencoder.android.AndroidBase64Encoder;

import java.util.LinkedList;
import java.util.List;

import personal.basedxmppchat.manager.XmppConnectionManager;

/**
 *
 * 完整的退出应用.
 *
 * @author shimiso
 * Created by wenrule on 16/1/26.
 */
public class EimApplication extends Application{
    private List<Activity> activityList = new LinkedList<Activity>();


    // 添加Activity到容器中
    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    // 遍历所有Activity并finish
    public void exit(){
        XmppConnectionManager.getInstance().disconnect();
        for(Activity activity:activityList){
            activity.finish();
        }
    }

}
