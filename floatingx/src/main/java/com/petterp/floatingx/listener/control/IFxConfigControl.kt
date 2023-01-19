package com.petterp.floatingx.listener.control

import com.petterp.floatingx.listener.IFxConfigStorage
import com.petterp.floatingx.listener.IFxViewLifecycle

/**
 * 配置更改接口,使用此接口运行时更改配置层
 * @author petterp
 */
interface IFxConfigControl {

    /** 设置view-lifecycle监听 */
    fun setViewLifecycleListener(listener: IFxViewLifecycle)

    /** 设置允许保存方向 */
    fun setEnableSaveDirection(impl: IFxConfigStorage, isEnable: Boolean = true)

    /** 设置方向保存开关
     * 设置之前,请确保已经设置了方向保存实例
     * */
    fun setEnableSaveDirection(isEnable: Boolean = true)

    /** 清除保存的位置信息 */
    fun clearLocationStorage()
}
