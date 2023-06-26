# UniappAndroidQrCodeScaner

### HBuilderX 使用本地插件

导入插件参考: [官网](https://nativesupport.dcloud.net.cn/NativePlugin/use/use_local_plugin.html)

插件下载: [download](https://github.com/zhaozhentao/UniappAndroidQrCodeScaner/releases/download/1.0.0/HuaweiScanModule.zip)

### Usage

1. import
```javascript
const module = uni.requireNativePlugin("HuaweiScanModule")
```
2. init

只需要初始化一次。

```javascript
				module.regisResultHandler(
					null,
					ret => {
                        let { action, data } = ret

                        switch (action) {
                            case 'scan_for_single':
                                this.result = data
                                break
                            case 'scan_for_multi':
                                this.result = data
                                break
                            case 'register':
                                // register success
                                modal.toast({ message: '注册成功', duration: 1.5 })
                                break
                            case 'unRegister':
                                // unRegister success
                                modal.toast({ message: '注销成功', duration: 1.5 })
                                break
						}
					}
                )

```

蓝牙状态发生变化时回调，message 可能的值如下。

| message |
| ---- |
| 蓝牙状态未知 |
| 蓝牙重置中 |
| 没有蓝牙权限 |
| 蓝牙关闭 |
| 不支持蓝牙 |
| 蓝牙开启 | 

3. scan
```javascript
bluetoothModule.scan({}, ret => {
  let { name, deviceId } = ret    // your bluetooth data
})
```

4. stopScan
```javascript
bluetoothModule.stop({})
```
### Demo

```javascript
<template>
  <div style="padding: 10px">
    <button style="margin-top: 10px;" type="default" @click="registerResultHandler">
      注册扫描结果回调
    </button>

    <button style="margin-top: 10px;" type="default" @click="unRegisterResultHandler">
      注销扫描结果回调
    </button>

    <button style="margin-top: 10px;" type="default" @click="scanForSingle">
      全屏扫码
    </button>

    <button style="margin-top: 10px;" type="default" @click="scanForMulti">
      半屏扫码
    </button>

    {{ result }}
  </div>
</template>

<script>
// 获取 module
const module = uni.requireNativePlugin("HuaweiScanModule")
const modal = uni.requireNativePlugin('modal')

export default {
  data() {
    return {
      result: null
    }
  },
  methods: {
    registerResultHandler() {
      module.regisResultHandler(
        null,
        ret => {
          let {action, data} = ret

          switch (action) {
            case 'scan_for_single':
              this.result = data
              break
            case 'scan_for_multi':
              this.result = data
              break
            case 'register':
              // register success
              modal.toast({message: '注册成功', duration: 1.5})
              break
            case 'unRegister':
              // unRegister success
              modal.toast({message: '注销成功', duration: 1.5})
              break
          }
        }
      )
    },
    unRegisterResultHandler() {
      module.unRegisterResultHandler()
    },
    scanForSingle() {
      module.scanForSingle()
    },
    scanForMulti() {
      module.scanForMulti()
    }
  }
}
</script>
```
