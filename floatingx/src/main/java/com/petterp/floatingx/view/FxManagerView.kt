package com.petterp.floatingx.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.petterp.floatingx.assist.FxGravity
import com.petterp.floatingx.assist.helper.AppHelper
import com.petterp.floatingx.assist.helper.BasisHelper
import com.petterp.floatingx.util.FX_GRAVITY_BOTTOM
import com.petterp.floatingx.util.FX_GRAVITY_TOP
import com.petterp.floatingx.util.topActivity

/** 基础悬浮窗View */
@SuppressLint("ViewConstructor")
class FxManagerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private lateinit var helper: BasisHelper
    private var mParentWidth = 0f
    private var mParentHeight = 0f

    private var _childFxView: View? = null
    val childFxView: View? get() = _childFxView

    fun init(config: BasisHelper): FxManagerView {
        this.helper = config
        initView()
        return this
    }

    private fun initView() {
        _childFxView = inflateLayoutView() ?: inflateLayoutId()
        checkNotNull(_childFxView) { "initFxView -> Error,check your layoutId or layoutView." }
        initLocation()
        isClickable = true
        helper.iFxViewLifecycle?.initView(this)
        // 注意这句代码非常重要,可以避免某些情况下View被隐藏掉
        setBackgroundColor(Color.TRANSPARENT)
    }

    private fun inflateLayoutView(): View? {
        val view = helper.layoutView ?: return null
        helper.fxLog?.d("fxView-->init, way:[layoutView]")
        val lp = layoutParams ?: LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addViewInLayout(view, -1, lp, true)
        return view
    }

    private fun inflateLayoutId(): View? {
        if (helper.layoutId == 0) return null
        helper.fxLog?.d("fxView-->init, way:[layoutId]")
        return inflate(context, helper.layoutId, this)
    }

    private fun initLocation() {
        // 初始化lp
        val configImpl = helper.iFxConfigStorage
        val hasConfig = configImpl?.hasConfig() ?: false
        val lp = helper.layoutParams ?: LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        // 不存在历史坐标时,设置gravity,默认左上角
        if (!hasConfig) lp.gravity = helper.gravity.value
        layoutParams = lp

        // 获得浮窗的位置
        // 存在历史位置 || 根据配置去获取
        val (initX, initY) = if (hasConfig) configImpl!!.getX() to configImpl.getY()
        else initDefaultXY()
        if (initX != -1F) x = initX
        if (initY != -1F) y = initY
        helper.fxLog?.d("fxView->initLocation,isHasConfig-($hasConfig),defaultX-($initX),defaultY-($initY)")
    }

    private fun initDefaultXY(): Pair<Float, Float> {
        // 非辅助定位&&非默认位置,此时x,y不可信
        if (!helper.enableAssistLocation && !helper.gravity.isDefault()) {
            helper.fxLog?.e(
                "fxView--默认坐标可能初始化异常,如果显示位置异常,请检查您的gravity是否为默认配置，当前gravity:${helper.gravity}。\n" +
                        "如果您要配置gravity,建议您启用辅助定位setEnableAssistDirection(),此方法将更便于定位。"
            )
        }
        return helper.defaultX to checkDefaultY(helper.defaultY)
    }

    private fun checkDefaultY(y: Float): Float {
        // 单独处理状态栏和底部导航栏
        var defaultY = y
        when (helper.gravity.scope) {
            FX_GRAVITY_TOP -> defaultY += helper.statsBarHeight
            FX_GRAVITY_BOTTOM -> defaultY -= helper.navigationBarHeight
            else -> {}
        }
        return defaultY
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        helper.iFxViewLifecycle?.attach()
        helper.fxLog?.d("fxView-lifecycle-> onAttachedToWindow")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        helper.iFxViewLifecycle?.detached()
        helper.fxLog?.d("fxView-lifecycle-> onDetachedFromWindow")
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        helper.iFxViewLifecycle?.windowsVisibility(visibility)
        helper.fxLog?.d("fxView-lifecycle-> onWindowVisibilityChanged")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        helper.fxLog?.d("fxView--lifecycle-> onConfigurationChanged--")
        val parentGroup = (parent as? ViewGroup) ?: return
        // 对于全局的处理
        if (helper is AppHelper) {
            (helper as AppHelper).updateNavigationBar(topActivity)
        }

        // 如果视图大小改变,则更新位置
        parentGroup.post {
            updateWidgetSize()
        }
    }

    private fun updateWidgetSize(): Boolean {
        // 如果此时浮窗被父布局移除,parent将为null,此时就别更新位置了,没意义
        val parentGroup = (parent as? ViewGroup) ?: return false
        // 这里先减掉自身大小可以避免后期再重复减掉
        val parentWidth = (parentGroup.width - this@FxManagerView.width).toFloat()
        val parentHeight = (parentGroup.height - this@FxManagerView.height).toFloat()
        if (mParentHeight != parentHeight || mParentWidth != parentWidth) {
            helper.fxLog?.d("fxView->updateContainerSize: oldW-($mParentWidth),oldH-($mParentHeight),newW-($parentWidth),newH-($parentHeight)")
            mParentWidth = parentWidth
            mParentHeight = parentHeight
            return true
        }
        return false
    }

    @JvmSynthetic
    internal fun updateLocation(x: Float, y: Float) {
        (layoutParams as LayoutParams).gravity = FxGravity.DEFAULT.value
        this.x = x
        this.y = y
        helper.fxLog?.d("fxView-updateManagerView-> RestoreLocation  x->$x,y->$y")
    }
}
