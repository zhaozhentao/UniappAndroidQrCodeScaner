package io.tg.minix.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static io.tg.minix.data.DataManager.codes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;

import io.tg.minix.R;
import io.tg.minix.adapters.ResultAdapter;
import io.tg.minix.helper.Helper;
import io.tg.minix.modules.HuaweiScanModule;

public class ScanActivity extends Activity {

    private RemoteView remoteView;

    public static ResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        int statusBarHeight = Helper.getStatusBar(this);

        findViewById(R.id.status_bar)
            .setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, statusBarHeight));

        int navigationBarHeight = Helper.getNavigationBarHeight(this);

        //1. Obtain the screen density to calculate the viewfinder's rectangle.
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;

        //2. Obtain the screen size.
        int SCAN_FRAME_SIZE = 200;
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);
        int remoteViewHeight = heightPixels - navigationBarHeight;

        Rect rect = new Rect();
        rect.left = widthPixels / 2 - scanFrameSize / 2;
        rect.right = widthPixels / 2 + scanFrameSize / 2;
        rect.top = remoteViewHeight / 2 - scanFrameSize / 2;
        rect.bottom = rect.top + scanFrameSize;

        //Initialize the RemoteView instance, and set callback for the scanning result.
        remoteView = new RemoteView
            .Builder()
            .setContext(this)
            .setBoundingBox(rect)
            .setFormat(HmsScan.ALL_SCAN_TYPE)
            .build();

        // centerY of scanner's frame equals 300dp / 2 + statusBarHeight
        int scanAreaMiddleY = (int) (300 * density / 2) + statusBarHeight;
        int dy = remoteViewHeight / 2 - scanAreaMiddleY;
        // let remoteView's center equals to scan frame's
        remoteView.setTranslationY(-dy);

        // Subscribe to the scanning result callback event.
        remoteView.setOnResultCallback(result -> {
            //Check the result.
            boolean getResult = result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue());

            if (!getResult) return;

            boolean exits = codes.stream().anyMatch(i -> i.sn.equals(result[0].showResult));

            if (exits) return;

            HuaweiScanModule.invoke("code", result[0].showResult);

            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator()) {
                long[] pattern = {0, 50};
                vibrator.vibrate(pattern, -1);
            }
        });

        // Load the customized view to the activity.
        remoteView.onCreate(savedInstanceState);

        ((FrameLayout) findViewById(R.id.rim))
            .addView(remoteView, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter = new ResultAdapter(this, codes));

        findViewById(R.id.back_img).setOnClickListener(v -> finish());

        findViewById(R.id.finish).setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        remoteView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        remoteView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        remoteView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
        adapter = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
    }
}
