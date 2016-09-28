package ece.course.lab_2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public final static float MAX_GRAVITY = 9.82f;
    private float mX = -100.0f;
    private float mY = -100.0f;
    private float mZ = -100.0f;
    private DisplayView mDisplayView;
    private AccelerometerSensor mAccelerometerSensor;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        mDisplayView = (DisplayView) findViewById(R.id.mDisplayView);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mAccelerometerSensor = new AccelerometerSensor(this, new Handler() {
            public void handleMessage(Message msg) {
                float THRESHOLD = 10.0f;
                float tmpX = msg.getData().getFloat(AccelerometerSensor.TAG_VALUE_DX);
                float tmpY = -msg.getData().getFloat(AccelerometerSensor.TAG_VALUE_DY);
                float tmpZ = msg.getData().getFloat(AccelerometerSensor.TAG_VALUE_DZ);
                if (tmpX - mX > THRESHOLD || tmpX - mX < -THRESHOLD ||
                        tmpY - mY > THRESHOLD || tmpY - mY < -THRESHOLD ||
                        tmpZ - mZ > THRESHOLD || tmpZ - mZ < -THRESHOLD) {
                    mX = tmpX;
                    mY = tmpY;
                    mZ = tmpZ;
                    TextView tvValueX = (TextView) findViewById(R.id.tvValueX);
                    TextView tvValueY = (TextView) findViewById(R.id.tvValueY);
                    TextView tvValueZ = (TextView) findViewById(R.id.tvValueZ);
                    tvValueX.setText("" + tmpX);
                    tvValueY.setText("" + tmpY);
                    tvValueZ.setText("" + tmpZ);
                    mDisplayView.setPtr(tmpX / MAX_GRAVITY, tmpY / MAX_GRAVITY);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menuPtrBall :
                mDisplayView.setPtrType(DisplayView.TYPE_BALL);
                return true;
            case R.id.menuPtrSquare :
                mDisplayView.setPtrType(DisplayView.TYPE_SQUARE);
                return true;
            case R.id.menuPtrDiamond :
                mDisplayView.setPtrType(DisplayView.TYPE_DIAMOND);
                return true;
            case R.id.menuPtrArc :
                mDisplayView.setPtrType(DisplayView.TYPE_ARC);
                return true;
            case R.id.menuPtrRed :
                mDisplayView.setPtrColor(Color.RED);
                return true;
            case R.id.menuPtrBlue :
                mDisplayView.setPtrColor(Color.BLUE);
                return true;
            case R.id.menuPtrGreen :
                mDisplayView.setPtrColor(Color.GREEN);
                return true;
            case R.id.menuPtrWhite :
                mDisplayView.setPtrColor(Color.WHITE);
                return true;
        }
        return false;
    }

    public synchronized void onResume() {
        super.onResume();
        if (mAccelerometerSensor != null) {
            mAccelerometerSensor.startListening();
        }
        mWakeLock.acquire();
    }

    public synchronized void onPause() {
        if (mAccelerometerSensor != null) {
            mAccelerometerSensor.stopListening();
        }
        super.onPause();
        mWakeLock.release();
    }

}
