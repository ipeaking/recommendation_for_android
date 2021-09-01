package com.bo.bonews;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Stack;

/**
 */
public class AppManager {

    private Stack<Activity> mActivities = new Stack<>();

    private static class Holder {
        private static final AppManager INSTANCE = new AppManager();
    }

    public static AppManager getInstance() {
        return Holder.INSTANCE;
    }

    public void addActivity(Activity activity) {
        mActivities.add(activity);
    }

    public void removeActivity(Activity activity) {
        hideSoftKeyBoard(activity);
        activity.finish();
        mActivities.remove(activity);
    }

    public void removeAllActivity() {
        for (Activity activity : mActivities) {
            hideSoftKeyBoard(activity);
            activity.finish();
        }
        mActivities.clear();
    }

    public <T extends Activity> boolean hasActivity(Class<T> tClass) {
        for (Activity activity : mActivities) {
            if (tClass.getName().equals(activity.getClass().getName())) {
                return !activity.isDestroyed() || !activity.isFinishing();
            }
        }
        return false;
    }

    public void hideSoftKeyBoard(Activity activity) {
        View localView = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (localView != null && imm != null) {
            imm.hideSoftInputFromWindow(localView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
