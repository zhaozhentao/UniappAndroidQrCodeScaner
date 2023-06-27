package io.tg.minix;

import android.app.Activity;
import android.content.Intent;

import com.alibaba.fastjson.JSONObject;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class HuaweiScanModule extends UniModule {

    public static int REQUEST_SINGLE = 1000;

    public static int REQUEST_MULTI = 2000;

    private UniJSCallback callback;

    @UniJSMethod
    public void registerResultHandler(JSONObject options, UniJSCallback callback) {
        // 不保留之前的 callback
        if (this.callback != null) {
            this.callback.invoke(result("unRegister", null));
        }

        if (callback != null) {
            this.callback = callback;
            callback.invokeAndKeepAlive(result("register", null));
        }
    }

    @UniJSMethod
    public void unRegisterResultHandler(JSONObject options, UniJSCallback callback) {
        if (this.callback != null) {
            this.callback.invoke(result("unRegister", null));
            this.callback = null;
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
    public void scanForMulti() {
        ((Activity) mUniSDKInstance.getContext()).startActivityForResult(
            new Intent(mUniSDKInstance.getContext(), ScanActivity.class),
            REQUEST_MULTI
        );
    }

    private JSONObject result(String action, Object data) {
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
            callback.invokeAndKeepAlive(result("scan_for_multi", ScanActivity.codes));
        }
    }
}
