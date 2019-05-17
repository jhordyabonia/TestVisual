package com.jhordyabonia.tv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RewardedVideoAdListener {

    private int test = 1;
    private RewardedVideoAd mRewardedVideoAd;
    private boolean loading = false;
    private boolean start = false;
    private boolean showAds = false;

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
        TextView t2 = findViewById(R.id.test);
        t2.setMovementMethod(LinkMovementMethod.getInstance());

        findViewById(R.id.policy).setOnClickListener(this);
        findViewById(R.id.more_apps).setOnClickListener(this);
        findViewById(R.id.test).setOnClickListener(this);


        MobileAds.initialize(this, getString(R.string.ad_unit_id));

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        SharedPreferences file = getSharedPreferences("test", MODE_PRIVATE);
        test=file.getInt("test",test);


        ((TextView)findViewById(R.id.test))
                .setText("Consultas: "+test);

        loadRewardedVideoAd(false);
    }
    @Override
    public  void onDestroy() {
        SharedPreferences file = getSharedPreferences("test", MODE_PRIVATE);
        file.edit().putInt("test",test).commit();
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
    @Override
    public void onClick(View v) {
        start = false;
        Intent i = new Intent(Intent.ACTION_VIEW);
        switch (v.getId()){
            case R.id.dummy_button:
                startGame();
                break;
            case R.id.policy:
                i.setData(Uri.parse("https://jhordyabonia.azurewebsites.net/visual_exam/policy/"));
                startActivity(i);
                break;
            case R.id.more_apps:
                i.setData(Uri.parse("https://play.google.com/store/apps/developer?id=Jhordy+Abonia"));
                startActivity(i);
                break;
            case R.id.test:
                if(mRewardedVideoAd.isLoaded())
                    mRewardedVideoAd.show();
                else loadRewardedVideoAd(true);
                break;
        }
    }
    private void startGame(){
        start = true;
        if(test>0) {

            findViewById(R.id.menu).setVisibility(View.GONE);
            findViewById(R.id.content).setVisibility(View.GONE);
            findViewById(R.id.dummy_button).setVisibility(View.GONE);
            findViewById(R.id.bg).setBackgroundResource(R.drawable.image);
            ObjectAnimator in4 = ObjectAnimator
                    .ofFloat(findViewById(R.id.bg), "alpha", 1, 1);
            in4.setDuration(5000);
            in4.setInterpolator(new LinearInterpolator());
            in4.start();
            in4.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator anim) {
                    test--;
                    show();
                }
            });
        }else{
            if(mRewardedVideoAd.isLoaded())
                mRewardedVideoAd.show();
            else loadRewardedVideoAd(false);
        }
    }
    private void show(){

        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.menu).setVisibility(View.VISIBLE);
        findViewById(R.id.content).setVisibility(View.VISIBLE);
        findViewById(R.id.dummy_button).setVisibility(View.VISIBLE);
        findViewById(R.id.bg).setBackgroundResource(R.drawable.blank);

        ((TextView)findViewById(R.id.test))
                .setText("Consultas: "+test);
    }
    private void win(){
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        ObjectAnimator in4 = ObjectAnimator
                .ofFloat(findViewById(R.id.loading), "alpha", 1,1,1,0);
        in4.setDuration(4000);
        in4.setInterpolator(new DecelerateInterpolator());
        in4.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                loadRewardedVideoAd(false);
                show();
                if(start){
                    startGame();
                    return;
                }
            }
        });
        in4.start();

    }

    private void loadRewardedVideoAd(boolean show) {

        showAds = show;
        if(!mRewardedVideoAd.isLoaded()&&!loading) {
            AdRequest.Builder builder = new AdRequest.Builder();
            //builder.addTestDevice("9DF7C46B5A77BF93368E765F9F2D7EBF");
            mRewardedVideoAd.loadAd(getString(R.string.adds),builder.build());

            findViewById(R.id.loading).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.loading))
                    .setText("Cargando...");
            loading = true;
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if(showAds){
            mRewardedVideoAd.show();
            return;
        }
        show();
        loading = false;
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        win();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        int reward = rewardItem.getAmount();
        test += reward;

        ((TextView)findViewById(R.id.test))
                .setText("Consultas: "+test);

        ((TextView)findViewById(R.id.loading))
                .setText("Ganaste: "+reward+" consultas");
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        loadRewardedVideoAd(showAds);
    }

    @Override
    public void onRewardedVideoCompleted() {
       //win();
    }
}
