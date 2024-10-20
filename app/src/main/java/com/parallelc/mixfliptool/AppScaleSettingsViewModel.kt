package com.parallelc.mixfliptool

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppInfo(
    val icon: Drawable,
    val appName: String,
    val packageName: String,
    var scale: String
)

class AppScaleSettingsViewModel(private val context: Context) : ViewModel() {
    private val appScaleManager = AppScaleManager(context)

    private val _appList = MutableStateFlow<List<AppInfo>>(emptyList())
    val appList: StateFlow<List<AppInfo>> = _appList

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _requestPermission = MutableStateFlow(false)
    val requestPermission: StateFlow<Boolean> = _requestPermission

    fun refreshApplications() {
        try {
            val permissionInfo = context.packageManager.getPermissionInfo("com.android.permission.GET_INSTALLED_APPS", 0)
            if (permissionInfo != null && context.checkSelfPermission("com.android.permission.GET_INSTALLED_APPS") != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                _isRefreshing.value = true
                _requestPermission.value = true
                return
            }
        } catch (e : PackageManager.NameNotFoundException) {
            Log.e("mixfliptool", e.stackTraceToString())
        }
        loadApplications()
    }

    fun handlePermissionResult(isGranted: Boolean) {
        _requestPermission.value = false
        if (isGranted) {
            loadApplications()
        } else {
            _isRefreshing.value = false
            Toast.makeText(context, "获取应用列表失败！", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadApplications() {
        viewModelScope.launch {
            _isRefreshing.value = true
            delay(500)
            val pm = context.packageManager
            val apps = withContext(Dispatchers.IO) {
                val preferences = appScaleManager.getAllPreferences()
                pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0L)).map { appInfo ->
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    val appIcon = pm.getApplicationIcon(appInfo.packageName)
                    AppInfo(
                        icon = appIcon,
                        appName = appName,
                        packageName = appInfo.packageName,
                        scale = preferences[appInfo.packageName] ?: ""
                    )
                }.sortedWith(compareBy({ it.scale.isEmpty() }, { it.appName } ))
            }
            if (apps.size <= 1) {
                Toast.makeText(context, "获取应用列表失败！", Toast.LENGTH_SHORT).show()
            } else {
                _appList.value = apps
            }
            _isRefreshing.value = false
        }
    }

    fun settingAppScale(packageName: String, scale: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                appScaleManager.saveAppScale(packageName, scale)
            }
            _appList.value = _appList.value.map { app ->
                if (app.packageName == packageName) {
                    app.copy(scale = scale)
                } else {
                    app
                }
            }.sortedWith(compareBy({ it.scale.isEmpty() }, { it.appName } ))
        }
    }

    fun resetAppScale() {
        viewModelScope.launch {
            val preferences = withContext(Dispatchers.IO) {
                appScaleManager.resetDefaultPreferences()
                appScaleManager.getAllPreferences()
            }
            _appList.value = _appList.value.map { app ->
                app.copy(scale = preferences[app.packageName] ?: "")
            }.sortedWith(compareBy({ it.scale.isEmpty() }, { it.appName } ))
        }
    }
}

class AppScaleSettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppScaleSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppScaleSettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}