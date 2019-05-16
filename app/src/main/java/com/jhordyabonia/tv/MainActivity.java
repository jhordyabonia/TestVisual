package com.jhordyabonia.tv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        findViewById(R.id.dummy_button).setOnClickListener(this);
        TextView t2 = findViewById(R.id.policy);
        t2.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.policy).setOnClickListener(this);
        findViewById(R.id.more_apps).setOnClickListener(this);
        findViewById(R.id.ours).setOnClickListener(this);


        interstitialAd = new InterstitialAd(this);
        ads();
    }
    @Override
    public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        switch (v.getId()){
            case R.id.dummy_button:
                if(interstitialAd.isLoaded())
                    interstitialAd.show();
                else loadInterstitial();
                break;
            case R.id.policy:
                i.setData(Uri.parse("https://jhordyabonia.azurewebsites.net/visual_exam/policy/"));
                startActivity(i);
                break;
            case R.id.more_apps:
                i.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Jhordy+Abonia"));
                startActivity(i);
                break;
            case R.id.ours:
                i.setData(Uri.parse("https://jhordyabonia.azurewebsites.net/ours/"));
                startActivity(i);
                break;
        }
    }
    private void startGame(){
        findViewById(R.id.content).setVisibility(View.GONE);
        findViewById(R.id.dummy_button).setVisibility(View.GONE);
        findViewById(R.id.bg).setBackgroundResource(R.drawable.image);
        ObjectAnimator in4 = ObjectAnimator
                .ofFloat(findViewById(R.id.bg),"alpha",1,1);
        in4.setDuration(5000);
        in4.setInterpolator(new LinearInterpolator());
        in4.start();
        in4.addListener( new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator anim){
                show();
            }
        });
    }
    private void loaded(){
        findViewById(R.id.loading).setVisibility(View.GONE);
    }
    private void show(){
        findViewById(R.id.content).setVisibility(View.VISIBLE);
        findViewById(R.id.dummy_button).setVisibility(View.VISIBLE);
        findViewById(R.id.bg).setBackgroundResource(R.drawable.blank);
    }
    private void ads(){
        MobileAds.initialize(this, getString(R.string.ad_unit_id));
        interstitialAd.setAdUnitId(getString(R.string.adds));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed(){startGame();}
            @Override
            public void onAdLoaded(){loaded();}
            @Override
            public void onAdFailedToLoad(int errorCode)
            { loadInterstitial();}
        });
        loadInterstitial();
    }
    private void loadInterstitial()
    {
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()){
            AdRequest.Builder builder = new AdRequest.Builder();
            builder.addTestDevice("9DF7C46B5A77BF93368E765F9F2D7EBF");
            interstitialAd.loadAd(builder.build());
        }
    }
}
