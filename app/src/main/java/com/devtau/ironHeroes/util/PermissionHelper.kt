package com.devtau.ironHeroes.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * Интерфейс хелпера для работы с разрешениями
 * Клиент переопределяет колбэк onRequestPermissionsResult и обрабатывает в нем реакцию пользователя
 */
interface PermissionHelper {

    fun checkGPSPermission(context: Context): Boolean
    fun requestGPSPermission(fragment: Fragment)
    fun requestGPSPermission(activity: AppCompatActivity, negativeListener: DialogInterface.OnClickListener)

    fun checkCallPermission(context: Context): Boolean
    fun requestCallPermission(fragment: Fragment)
    fun requestCallPermission(activity: AppCompatActivity)

    fun checkStoragePermission(context: Context): Boolean
    fun requestStoragePermission(fragment: Fragment)
    fun requestStoragePermission(activity: AppCompatActivity)
}