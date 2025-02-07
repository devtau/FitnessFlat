package com.devtau.ironHeroes.util

import android.Manifest.permission.*
import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.devtau.ironHeroes.R

class PermissionHelperImpl: PermissionHelper {

    override fun checkGPSPermission(context: Context): Boolean =
        if (!isPermissionDynamic()) true
        else isPermissionGranted(context, ACCESS_COARSE_LOCATION) && isPermissionGranted(context, ACCESS_FINE_LOCATION)

    override fun requestGPSPermission(fragment: Fragment) {
        fragment.activity ?: return
        if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.activity!!, ACCESS_FINE_LOCATION)) {
            val explanationText = getExplanationText(fragment.context, R.string.permission_explanation_gps)
            val declinedText = fragment.getString(R.string.permission_cancelled_msg_gps)
            showExplanationDialog(fragment, explanationText, declinedText, ACCESS_FINE_LOCATION, GPS_REQUEST_CODE)
        } else {
            fragment.requestPermissions(arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), GPS_REQUEST_CODE)
        }
    }

    @TargetApi(23)
    override fun requestGPSPermission(activity: AppCompatActivity, negativeListener: DialogInterface.OnClickListener) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)) {
            val explanationText = getExplanationText(activity, R.string.permission_explanation_gps)
            showExplanationDialog(activity, explanationText, ACCESS_FINE_LOCATION, GPS_REQUEST_CODE, negativeListener)
        } else {
            activity.requestPermissions(arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), GPS_REQUEST_CODE)
        }
    }


    override fun checkCallPermission(context: Context): Boolean =
        if (!isPermissionDynamic()) true else isPermissionGranted(context, CALL_PHONE)

    override fun requestCallPermission(fragment: Fragment) {
        fragment.activity ?: return
        if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.activity!!, CALL_PHONE)) {
            val explanationText = getExplanationText(fragment.context, R.string.permission_explanation_call)
            val declinedText = fragment.getString(R.string.permission_cancelled_msg_call)
            showExplanationDialog(fragment, explanationText, declinedText, CALL_PHONE, CALL_REQUEST_CODE)
        } else {
            fragment.requestPermissions(arrayOf(CALL_PHONE), CALL_REQUEST_CODE)
        }
    }

    @TargetApi(23)
    override fun requestCallPermission(activity: AppCompatActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, CALL_PHONE)) {
            val explanationText = getExplanationText(activity, R.string.permission_explanation_call)
            val declinedText = activity.getString(R.string.permission_cancelled_msg_call)
            showExplanationDialog(activity, explanationText, declinedText, CALL_PHONE, CALL_REQUEST_CODE)
        } else {
            activity.requestPermissions(arrayOf(CALL_PHONE), CALL_REQUEST_CODE)
        }
    }


    override fun checkStoragePermission(context: Context): Boolean =
        if (!isPermissionDynamic()) true else isPermissionGranted(context, WRITE_EXTERNAL_STORAGE)

    @TargetApi(23)
    override fun requestStoragePermission(fragment: Fragment) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(fragment.activity!!, WRITE_EXTERNAL_STORAGE)) {
            val explanationText = getExplanationText(fragment.context, R.string.permission_explanation_storage)
            val declinedText = fragment.getString(R.string.permission_cancelled_msg_storage)
            showExplanationDialog(fragment, explanationText, declinedText, WRITE_EXTERNAL_STORAGE, STORAGE_REQUEST_CODE)
        } else {
            fragment.requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), STORAGE_REQUEST_CODE)
        }
    }

    @TargetApi(23)
    override fun requestStoragePermission(activity: AppCompatActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, WRITE_EXTERNAL_STORAGE)) {
            val explanationText = getExplanationText(activity, R.string.permission_explanation_storage)
            val declinedText = activity.getString(R.string.permission_cancelled_msg_storage)
            showExplanationDialog(activity, explanationText, declinedText, WRITE_EXTERNAL_STORAGE, STORAGE_REQUEST_CODE)
        } else {
            activity.requestPermissions(arrayOf(WRITE_EXTERNAL_STORAGE), STORAGE_REQUEST_CODE)
        }
    }


    private fun getExplanationText(context: Context?, stringResId: Int): String {
        context ?: return ""
        val formatter = context.getString(R.string.permission_explanation_formatter)
        return String.format(formatter,
            context.getString(stringResId),
            context.getString(R.string.to_disable_press_further))
    }

    private fun isPermissionDynamic(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        return try {
            val selfPermission = PermissionChecker.checkSelfPermission(context, permission)
            if (selfPermission == PackageManager.PERMISSION_GRANTED) {
                Logger.d(LOG_TAG, "Permission: $permission is granted")
                true
            } else {
                Logger.d(LOG_TAG, "Permission: $permission is denied")
                false
            }
        } catch (e: Exception) {
            e.message?.let { Logger.e(LOG_TAG, it) }
            false
        }
    }

    private fun showExplanationDialog(fragment: Fragment, explanationText: String, declinedText: String,
                                      permission: String, requestCode: Int) {
        showExplanationDialog(fragment.context, explanationText, permission,
            DialogInterface.OnClickListener { _, _ -> fragment.requestPermissions(arrayOf(permission), requestCode) },
            DialogInterface.OnClickListener { _, _ -> fragment.requestPermissions(arrayOf(permission), requestCode) },
            DialogInterface.OnClickListener { _, _ ->
                if (fragment.context != null) fragment.context?.toastLong(declinedText)
            })

    }

    @TargetApi(23)
    private fun showExplanationDialog(activity: AppCompatActivity, explanationText: String, declinedText: String,
                                      permission: String, requestCode: Int) {
        showExplanationDialog(activity, explanationText, permission,
            DialogInterface.OnClickListener { _, _ -> activity.requestPermissions(arrayOf(permission), requestCode) },
            DialogInterface.OnClickListener { _, _ -> activity.requestPermissions(arrayOf(permission), requestCode) },
            DialogInterface.OnClickListener { _, _ -> activity.toastLong(declinedText) })
    }

    @TargetApi(23)
    private fun showExplanationDialog(activity: AppCompatActivity, explanationText: String, permission: String,
                                      requestCode: Int, negativeListener: DialogInterface.OnClickListener) {
        showExplanationDialog(activity, explanationText, permission,
            DialogInterface.OnClickListener { _, _ -> activity.requestPermissions(arrayOf(permission), requestCode) },
            DialogInterface.OnClickListener { _, _ -> activity.requestPermissions(arrayOf(permission), requestCode) },
            negativeListener)
    }

    private fun showExplanationDialog(context: Context?, explanationText: String, permission: String,
                                      positiveListener: DialogInterface.OnClickListener,
                                      neutralListener: DialogInterface.OnClickListener,
                                      negativeListener: DialogInterface.OnClickListener) {
        context ?: return
        Logger.d(LOG_TAG, "Showing explanation dialog for permission=$permission, context=$context")
        try {
            AlertDialog.Builder(context)
                .setTitle(R.string.permission_needed)
                .setMessage(explanationText)
                .setPositiveButton(android.R.string.yes, positiveListener)
                .setNeutralButton(R.string.further, neutralListener)
                .setNegativeButton(android.R.string.no, negativeListener)
                .show()
        } catch (e: Exception) {
        }
    }


    companion object {
        const val GPS_REQUEST_CODE = 5748
        const val CALL_REQUEST_CODE = 5749
        const val STORAGE_REQUEST_CODE = 5751
        private const val LOG_TAG = "PermissionHelper"
    }
}