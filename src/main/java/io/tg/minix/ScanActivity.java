package io.tg.minix;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.ml.scan.HmsScan;

import java.util.ArrayList;

import minix.R;

public class ScanActivity extends Activity {

    public static final ArrayList<String> codes = new ArrayList<>();

    private RemoteView remoteView;

    private ResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        codes.clear();

        int statusBarHeight = initStatusBar();

        //1. Obtain the screen density to calculate the viewfinder's rectangle.
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float density = dm.density;

        //2. Obtain the screen size.
        int mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        int SCAN_FRAME_SIZE = 200;
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * density);

        Rect rect = new Rect();
        rect.left = mScreenWidth / 2 - scanFrameSize / 2;
        rect.right = mScreenWidth / 2 + scanFrameSize / 2;
        rect.top = (int) (0 + (300 - SCAN_FRAME_SIZE) * density) + statusBarHeight;
        rect.bottom = rect.top + scanFrameSize;

        //Initialize the RemoteView instance, and set callback for the scanning result.
        remoteView = new RemoteView
            .Builder()
            .setContext(this)
            .setBoundingBox(rect)
            .setFormat(HmsScan.ALL_SCAN_TYPE)
            .build();

        // Subscribe to the scanning result callback event.
        remoteView.setOnResultCallback(result -> {
            //Check the result.
            boolean getResult = result != null && result.length > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue());

            if (!getResult || codes.contains(result[0].showResult)) return;

            codes.add(result[0].showResult);

            adapter.notifyDataSetChanged();
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

    public int initStatusBar() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        findViewById(R.id.status_bar).setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, statusBarHeight));

        return statusBarHeight;
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
    }
}
