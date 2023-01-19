package com.petterp.floatingx.app.kotlin

import android.app.Application
import com.petterp.floatingx.FloatingX
import com.petterp.floatingx.app.*
import com.petterp.floatingx.listener.IFxViewLifecycle

/** Kotlin-Application */
class CustomKtApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FloatingX.init {
            setLayout(R.layout.item_floating)

            // 传递自定义的View
//            setLayoutView(
//                TextView(applicationContext).apply {
//                    text = "App"
//                    textSize = 15f
//                    width = 300
//                    height = 4000
//                    setBackgroundColor(Color.GRAY)
//                    setPadding(10, 10, 10, 10)
//                }
//            )

            /** 指定浮窗可显示的activity方式 */
            // 1.设置是否允许所有activity都进行显示,默认true
            setEnableAllInstall(true)
            // 2.禁止插入Activity的页面, setEnableAllBlackClass(true)时,此方法生效
            addInstallBlackClass(BlackActivity::class.java)
            // 3.允许插入Activity的页面, setEnableAllBlackClass(false)时,此方法生效
//            addInstallWhiteClass(
//                MainActivity::class.java,
//                ImmersedActivity::class.java,
//                ScopeActivity::class.java
//            )

            // 增加生命周期监听
            setViewLifecycle(object : IFxViewLifecycle {
                override fun postAttach() {
                    FloatingX.control().apply {
                        updateView(R.layout.item_floating)
                        this.updateViewContent {
                            it.setText(R.id.tvItemFx, nowDateFormatText())
                        }
                    }.show()
                }
            })
            // 设置是否启用日志
            setEnableLog(BuildConfig.DEBUG)
            // 只有调用了enableFx,默认才会启用fx,否则fx不会自动插入activity
            // ps: 这里的只有调用了enableFx仅仅只是配置工具层的标记,后续使用control.show()也会默认启用
            enableFx()
        }
    }
}
