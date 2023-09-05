package com.example.basedplayer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class MyGlobals extends Activity {
    Context mContext;


    public MyGlobals(Context context) {
        this.mContext = context;

    }

    public void showWIP(Context context){
        Toast wip = Toast.makeText(context, "Work in progress", Toast.LENGTH_SHORT);
        wip.show();
    }

    public void showToast(String text) {
        Toast mToast = Toast.makeText(mContext , text , Toast.LENGTH_SHORT);
        if (mToast != null) mToast.cancel();
        mToast.show();
    }

    public void openActivity(Context context, Class className) {
        Intent intent = new Intent(context, className);
        mContext.startActivity(intent);
    }

    public void openActivityNoAnim(Context context, Class className) {
        Intent intent = new Intent(context, className).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivity(intent);
    }

    public void clickEffectDarken(final View button){
        button.setAlpha(0.5f);
        Handler resetHandler = new Handler();
        resetHandler.postDelayed(() -> button.setAlpha(1.0F), 200);
    }

    public void clickEffectResize(final View button , Context context){
        final Animation scaleDown = AnimationUtils.loadAnimation(context, R.anim.button_down);
        final Animation scaleUp = AnimationUtils.loadAnimation(context, R.anim.button_up);
        button.startAnimation(scaleDown);
        Handler resetHandler = new Handler();
        resetHandler.postDelayed(() -> button.startAnimation(scaleUp), 200);
    }



}