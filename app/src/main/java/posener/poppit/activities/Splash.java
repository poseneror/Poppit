package posener.poppit.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import posener.poppit.R;
import posener.poppit.activities.LoginActivity;


public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread startTimer = new Thread(){
            public void run(){
                try {

                    sleep(2000);
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally {
                    finish();
                }

            }
        };
        startTimer.start();
    }
}
