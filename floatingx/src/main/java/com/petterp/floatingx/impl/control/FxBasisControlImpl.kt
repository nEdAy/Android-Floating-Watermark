package com.petterp.floatingx.impl.control

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import com.petterp.floatingx.assist.helper.BasisHelper
import com.petterp.floatingx.listener.IFxConfigStorage
import com.petterp.floatingx.listener.IFxViewLifecycle
import com.petterp.floatingx.listener.control.IFxConfigControl
import com.petterp.floatingx.listener.control.IFxControl
import com.petterp.floatingx.listener.provider.IFxContextProvider
import com.petterp.floatingx.listener.provider.IFxHolderProvider
import com.petterp.floatingx.util.lazyLoad
import com.petterp.floatingx.view.FxManagerView
import com.petterp.floatingx.view.FxViewHolder
import java.lang.ref.WeakReference

/** 基础控制器实现 */
open class FxBasisControlImpl(private val helper: BasisHelper) : IFxControl, IFxConfigControl {
    private var managerView: FxManagerView? = null
    private var viewHolder: FxViewHolder? = null
    private var mContainer: WeakReference<ViewGroup>? = null
    private val cancelAnimationRunnable by lazyLoad { Runnable { reset() } }
    private val hideAnimationRunnable by lazyLoad { Runnable { detach() } }

    /*
    * 控制接口相关实现
    * */
    override val configControl: IFxConfigControl get() = this

    override fun show() {
        if (!helper.enableFx) helper.enableFx = true
    }

    override fun cancel() {
        if (managerView == null && viewHolder == null) return
        reset()
    }

    override fun hide() {
        if (helper.enableFx) helper.enableFx = false
        if (!isShow()) return
        detach()
    }

    override fun isShow(): Boolean =
        managerView != null && ViewCompat.isAttachedToWindow(managerView!!) && managerView!!.visibility == View.VISIBLE

    override fun getView(): View? = managerView?.childFxView

    override fun getViewHolder(): FxViewHolder? = viewHolder

    override fun getManagerView(): FxManagerView? = managerView

    override fun updateView(@LayoutRes resource: Int) {
        if (resource == 0) throw IllegalArgumentException("resource cannot be 0!")
        helper.layoutView = null
        updateMangerView(resource)
    }

    override fun updateView(view: View) {
        helper.layoutView = view
        updateMangerView(0)
    }

    override fun updateView(provider: IFxContextProvider) {
        val view = provider.build(context())
        updateView(view)
    }

    override fun updateViewContent(provider: IFxHolderProvider) {
        provider.apply(viewHolder ?: return)
    }

    override fun setViewLifecycleListener(listener: IFxViewLifecycle) {
        helper.iFxViewLifecycle = listener
    }

    override fun setEnableSaveDirection(impl: IFxConfigStorage, isEnable: Boolean) {
        helper.iFxConfigStorage = impl
        helper.enableSaveDirection = isEnable
    }

    override fun setEnableSaveDirection(isEnable: Boolean) {
        helper.enableSaveDirection = isEnable
    }

    override fun clearLocationStorage() {
        helper.iFxConfigStorage?.clear()
    }

    /*
    * 以下方法作为基础实现,供子类自行调用
    * */
    protected open fun updateMangerView(@LayoutRes layout: Int = 0) {
        helper.layoutId = layout
        if (getContainerGroup() == null) throw NullPointerException("FloatingX window The parent container cannot be null!")
        val isShow = isShow()
        if (helper.iFxConfigStorage?.hasConfig() != true) {
            val x = managerView?.x ?: 0f
            val y = managerView?.y ?: 0f
            initManagerView()
            managerView?.updateLocation(x, y)
        } else {
            initManagerView()
        }
        // 如果当前显示,再添加到parent里
        if (isShow) {
            getContainerGroup()?.addView(managerView)
        }
    }

    protected fun initManagerView() {
        if (helper.layoutId == 0 && helper.layoutView == null) throw RuntimeException("The layout id cannot be 0 ,and layoutView==null")
        getContainerGroup()?.removeView(managerView)
        initManager()
    }

    protected open fun initManager() {
        managerView = FxManagerView(context()).init(helper)
        val fxContentView = managerView?.childFxView ?: return
        viewHolder = FxViewHolder(fxContentView)
    }

    protected fun getContainerGroup(): ViewGroup? {
        return mContainer?.get()
    }

    protected fun setContainerGroup(viewGroup: ViewGroup) {
        mContainer = WeakReference(viewGroup)
    }

    protected open fun detach(container: ViewGroup?) {
        if (managerView != null && container != null) {
            helper.fxLog?.d("fxView-lifecycle-> code->removeView")
            helper.iFxViewLifecycle?.postDetached()
            container.removeView(managerView)
        }
    }

    protected fun detach() {
        val containerGroup = getContainerGroup() ?: return
        detach(containerGroup)
    }

    protected open fun context(): Context {
        if (mContainer?.get()?.context == null) {
            throw NullPointerException("context cannot be null")
        }
        return mContainer?.get()?.context!!
    }

    protected fun clearContainer() {
        mContainer?.clear()
        mContainer = null
    }

    @JvmSynthetic
    protected fun FxManagerView.show() {
        helper.enableFx = true
        visibility = View.VISIBLE
    }

    protected open fun reset() {
        managerView?.removeCallbacks(hideAnimationRunnable)
        managerView?.removeCallbacks(cancelAnimationRunnable)
        detach(mContainer?.get())
        managerView = null
        viewHolder = null
        helper.clear()
        clearContainer()
        helper.fxLog?.d("fxView-lifecycle-> code->cancelFx")
    }
}
