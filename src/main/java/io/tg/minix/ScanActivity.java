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

public class ScanActivity extends Activity {

    public static final ArrayList<String> codes = new ArrayList<>();

    private RemoteView remoteView;

    private ResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        codes.clear();

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
        // 相机view的高度
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

        // 扫描框中点 300dp / 2 + statusBarHeight
        int scanAreaMiddleY = (int) (300 * density / 2) + statusBarHeight;
        int dy = remoteViewHeight / 2 - scanAreaMiddleY;
        // 移动 remoteView 使 remoteView 中心与扫描框中心相等
        remoteView.setTranslationY(-dy);

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
