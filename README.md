# UniappAndroidQrCodeScaner

### HBuilderX 使用本地插件

导入插件参考: [官网](https://nativesupport.dcloud.net.cn/NativePlugin/use/use_local_plugin.html)

插件下载: [download](https://github.com/zhaozhentao/UniappAndroidQrCodeScaner/releases/download/1.0.0/HuaweiScanModule.zip)

### Usage

1. import
```javascript
const module = uni.requireNativePlugin("HuaweiScanModule-HuaweiScanModule")
```
2. 注册接收结果处理器，`action` 可能的取值如下。

```javascript
module.registerResultHandler(
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
      case 'code':
        // 半屏扫码中每个扫描到的 code 在这里获取，通过 addRecord 通知扫码页面添加记录
        module.addRecord({sn: data, code: "ignore"})
        break
    }
  }
)
```

3. 全屏扫描
```javascript
module.scanForSingle()
```

4. 半屏扫描

　　参数格式传入格式 [{sn: 'xx', code: 'yy'}]，没有数据时传入 []

```javascript
module.scanForMulti([{sn: "sn", "code": "ignore"}])
```

5. 注销接收结果处理器

　　页面关闭，不再需要扫码时，需要注销处理器。
  
```javascript
module.unRegisterResultHandler()
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
const module = uni.requireNativePlugin("HuaweiScanModule-HuaweiScanModule")
const modal = uni.requireNativePlugin('modal')

export default {
  data() {
    return {
      result: null
    }
  },
  methods: {
    registerResultHandler() {
      module.registerResultHandler(
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
            case 'code':
              module.addRecord({sn: data, code: "ignore"})
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
      module.scanForMulti([{sn: "sn", "code": "ignore"}])
    }
  }
}
</script>
```

### ScreenShot

<img src="https://github.com/zhaozhentao/UniappAndroidQrCodeScaner/blob/main/screenshot/screenshot.jpg?raw=true" style="height: 600px;" />

