package com.devtau.ff.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.Fragment
/**
 * Интерфейс хелпера для работы с разрешениями
 * Клиент переопределяет колбэк onRequestPermissionsResult и обрабатывает в нем реакцию пользователя
 */
interface PermissionHelper {

    fun checkGPSPermission(context: Context): Boolean
    fun requestGPSPermission(fragment: Fragment)
    fun requestGPSPermission(activity: Activity, negativeListener: DialogInterface.OnClickListener)

    fun checkCallPermission(context: Context): Boolean
    fun requestCallPermission(fragment: Fragment)
    fun requestCallPermission(activity: Activity)

    fun checkStoragePermission(context: Context): Boolean
    fun requestStoragePermission(fragment: Fragment)
    fun requestStoragePermission(activity: Activity)
}