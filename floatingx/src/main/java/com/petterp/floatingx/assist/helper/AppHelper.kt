package com.petterp.floatingx.assist.helper

import android.app.Activity
import com.petterp.floatingx.listener.IFxProxyTagActivityLifecycle
import com.petterp.floatingx.util.FxScopeEnum
import com.petterp.floatingx.util.navigationBarHeight
import com.petterp.floatingx.util.statusBarHeight

/** AppHelper构建器 */
class AppHelper(
    val blackFilterList: MutableList<Class<*>>,
    val whiteInsertList: MutableList<Class<*>>,
    val isAllInstall: Boolean,
    val fxLifecycleExpand: IFxProxyTagActivityLifecycle?
) : BasisHelper() {

    @JvmSynthetic
    internal fun updateNavigationBar(activity: Activity?) {
        navigationBarHeight = activity?.navigationBarHeight ?: navigationBarHeight
        fxLog?.v("system-> navigationBar-$navigationBarHeight")
    }

    @JvmSynthetic
    internal fun updateStatsBar(activity: Activity?) {
        statsBarHeight = activity?.statusBarHeight ?: statsBarHeight
        fxLog?.v("system-> statusBarHeight-$statsBarHeight")
    }

    class Builder : BasisHelper.Builder<Builder, AppHelper>() {
        private var whiteInsertList: MutableList<Class<*>> = mutableListOf()
        private var blackFilterList: MutableList<Class<*>> = mutableListOf()
        private var fxLifecycleExpand: IFxProxyTagActivityLifecycle? = null
        private var isEnableAllInstall: Boolean = true

        /**
         * 设置显示悬浮窗的Activity生命周期回调
         *
         * @param tagActivityLifecycle 生命周期实现类回调
         * @sample
         *     [com.petterp.floatingx.impl.lifecycle.FxTagActivityLifecycleImpl]
         *     空实现,便于直接object:XXX
         */
        fun setTagActivityLifecycle(tagActivityLifecycle: IFxProxyTagActivityLifecycle): Builder {
            this.fxLifecycleExpand = tagActivityLifecycle
            return this
        }

        /**
         * 添加禁止显示悬浮窗的activity
         *
         * @param c 禁止显示的activity
         *
         * [setEnableAllBlackClass(true)] 时,此方法生效
         */
        fun addInstallBlackClass(vararg c: Class<out Activity>): Builder {
            blackFilterList.addAll(c)
            return this
        }

        /**
         * 允许显示浮窗的activity
         *
         * @param c 允许显示的activity
         *
         * [setEnableAllBlackClass(false)] 时,此方法生效
         */
        fun addInstallWhiteClass(vararg c: Class<out Activity>): Builder {
            whiteInsertList.addAll(c)
            return this
        }

        /**
         * 是否允许给所有浮窗安装悬浮窗
         *
         * @param isEnable 是否允许,默认true
         */
        fun setEnableAllInstall(isEnable: Boolean): Builder {
            isEnableAllInstall = isEnable
            return this
        }

        override fun buildHelper(): AppHelper =
            AppHelper(
                blackFilterList,
                whiteInsertList,
                isEnableAllInstall,
                fxLifecycleExpand
            )

        override fun build(): AppHelper {
            val helper = super.build()
            helper.initLog(FxScopeEnum.APP_SCOPE.tag)
            return helper
        }
    }

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }
}
