package com.karim.ater.myexpenses.Helpers;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

import androidx.fragment.app.FragmentActivity;

public class IconsUtility {

    Activity activity;

    public IconsUtility(Activity activity) {
        this.activity = activity;
    }

    public Drawable getIcon(String iconName) {
        InputStream imageStream = null;

        try {
            // get input stream
            imageStream = activity.getAssets().open("icons/"+iconName);
            // load image as Drawable
            Drawable drawable = Drawable.createFromStream(imageStream, null);
            // set image to ImageView
            imageStream.close();
            return drawable;
        } catch (IOException ex) {
            return null;

        }
    }
}
