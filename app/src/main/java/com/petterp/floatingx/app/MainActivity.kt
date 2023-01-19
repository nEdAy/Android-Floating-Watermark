package com.petterp.floatingx.app

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.petterp.floatingx.FloatingX

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createLinearLayoutToParent {
            addNestedScrollView {
                addLinearLayout {
                    addItemView("显示全局悬浮窗") {
                        FloatingX.control().show()
                    }
                    addItemView("隐藏全局悬浮窗") {
                        FloatingX.control().hide()
                    }
                    addItemView("进入黑名单页面(禁止显示浮窗)") {
                        BlackActivity::class.java.start(context)
                    }
                    addItemView("更新当前全局浮窗显示View-(layoutId)") {
                        FloatingX.control().apply {
                            updateView(R.layout.item_floating)
                            this.updateViewContent {
                                it.setText(R.id.tvItemFx, nowDateFormatText())
                            }
                        }.show()
                    }
                    addItemView("更新当前全局浮窗显示View-(layoutView)") {
                        FloatingX.control().updateView {
                            TextView(it).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                text = nowDateFormatText()
                                textSize = 15f
                                setBackgroundColor(Color.GRAY)
                                setPadding(10, 10, 10, 10)
                            }
                        }
                        FloatingX.control().show()
                    }
                    addItemView("调整到无状态栏页面-(测试状态栏影响)") {
                        ImmersedActivity::class.java.start(context)
                    }
                    addItemView("跳转到局部悬浮窗页面-(测试api功能)") {
                        ScopeActivity::class.java.start(context)
                    }
                }
            }
        }
    }
}
