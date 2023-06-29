package io.tg.minix.modules;

import static io.tg.minix.data.DataManager.codes;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;
import io.tg.minix.data.Record;
import io.tg.minix.activities.ScanActivity;

public class HuaweiScanModule extends UniModule {

    private static UniJSCallback callback;

    public static int REQUEST_SINGLE = 1000;

    public static int REQUEST_MULTI = 2000;

    @UniJSMethod
    public void registerResultHandler(JSONObject options, UniJSCallback callback) {
        // 不保留之前的 callback
        if (HuaweiScanModule.callback != null) {
            HuaweiScanModule.callback.invoke(result("unRegister", null));
        }

        if (callback != null) {
            HuaweiScanModule.callback = callback;
            callback.invokeAndKeepAlive(result("register", null));
        }
    }

    @UniJSMethod
    public void unRegisterResultHandler(JSONObject options, UniJSCallback callback) {
        if (HuaweiScanModule.callback != null) {
            HuaweiScanModule.callback.invoke(result("unRegister", null));
            HuaweiScanModule.callback = null;
        }
    }

    @UniJSMethod
    public void scanForSingle() {
        if (mUniSDKInstance != null && mUniSDKInstance.getContext() instanceof Activity) {
            ScanUtil.startScan(
                (Activity) mUniSDKInstance.getContext(),
                REQUEST_SINGLE,
                new HmsScanAnalyzerOptions.Creator().create()
            );
        }
    }

    @UniJSMethod
    public void scanForMulti(JSONArray array) {
        codes.clear();

        if (array.size() != 0) {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);

                Record record = new Record(obj.getString("sn"), obj.getString("code"));

                codes.add(record);
            }
        }

        ((Activity) mUniSDKInstance.getContext()).startActivityForResult(
            new Intent(mUniSDKInstance.getContext(), ScanActivity.class),
            REQUEST_MULTI
        );
    }

    @UniJSMethod
    public void addRecord(JSONObject options, UniJSCallback callback) {
        Record record = new Record(options.getString("sn"), options.getString("code"));

        codes.add(record);

        if (ScanActivity.adapter != null) {
            ScanActivity.adapter.notifyDataSetChanged();
        }
    }

    public static void invoke(String action, Object data) {
        if (callback == null) {
            Log.e("异常", "未注册处理器");
            return;
        }
        callback.invokeAndKeepAlive(result(action, data));
    }

    private static JSONObject result(String action, Object data) {
        JSONObject result = new JSONObject();

        result.put("action", action);

        if (data != null) {
            result.put("data", data);
        }

        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_SINGLE) {
            HmsScan result = data.getParcelableExtra(ScanUtil.RESULT);
            callback.invokeAndKeepAlive(result("scan_for_single", result.showResult));
        } else if (requestCode == REQUEST_MULTI) {
            callback.invokeAndKeepAlive(result("scan_for_multi", codes));
        }
    }
}
