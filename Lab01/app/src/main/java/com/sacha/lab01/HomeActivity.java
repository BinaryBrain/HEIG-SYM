package com.sacha.lab01;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private TextView mEmailView;
    private TextView mIMEIView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        String mEmail = getIntent().getExtras().getString("email");
        mEmailView = (TextView) findViewById(R.id.emailView);
        mEmailView.setText(mEmail);

        mIMEIView = (TextView) findViewById(R.id.IMEIView);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();
        mIMEIView.setText(telephonyManager.getDeviceId());

        Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/DCIM/SYM/" + mEmail + ".jpg");
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageBitmap(bmp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
