package com.parallelc.mixfliptool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.parallelc.mixfliptool.ui.theme.MixFlipToolTheme
import kotlinx.coroutines.launch

class AppScaleSettingsActivity : ComponentActivity() {
    private val viewModel: AppScaleSettingsViewModel by viewModels {
        AppScaleSettingsViewModelFactory(this)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        viewModel.handlePermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        actionBar?.hide()
        setContent {
            MixFlipToolTheme {
                MainScreen(viewModel)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.requestPermission.collect { request ->
                    if (request) {
                        permissionLauncher.launch("com.android.permission.GET_INSTALLED_APPS")
                    }
                }
            }
        }

        viewModel.refreshApplications()
    }
}

@Composable
fun ScaleDialog(
    appInfo: AppInfo,
    onDismiss: () -> Unit,
    onScaleChange: (String) -> Unit
) {
    var isScalingEnabled by remember { mutableStateOf(appInfo.scale.isNotEmpty()) }
    var scaleValue by remember { mutableFloatStateOf(if (appInfo.scale.isEmpty() || appInfo.scale.toFloatOrNull() == null) 0.8f else appInfo.scale.toFloat()) }
    var manualScaleInput by remember { mutableStateOf(String.format("%.2f", scaleValue)) }
    var showError by remember { mutableStateOf(false) }

    fun handleDone() {
        if (isScalingEnabled && showError) {
            return
        }
        if (!isScalingEnabled) {
            onScaleChange("")
        } else {
            onScaleChange(manualScaleInput)
        }
        onDismiss()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "${appInfo.appName} 设置缩放比例 ")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("开启")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isScalingEnabled,
                        onCheckedChange = { isChecked ->
                            isScalingEnabled = isChecked
                        }
                    )
                }

                if (isScalingEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Slider(
                        value = scaleValue,
                        onValueChange = { newScale ->
                            scaleValue = newScale
                            manualScaleInput = String.format("%.2f", newScale) // 保留两位小数
                            showError = false
                        },
                        valueRange = 0.01f..1f, // 缩放范围改为 0 到 1
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = manualScaleInput,
                        onValueChange = { input ->
                            manualScaleInput = input
                            val regex = Regex("^(0\\.[1-9]\\d?|1(\\.00?)?)$")
                            if (!regex.matches(input)) {
                                showError = true
                                return@OutlinedTextField
                            }
                            val inputScale = manualScaleInput.toFloatOrNull()
                            if (inputScale == null || inputScale <= 0f || inputScale > 1f) {
                                showError = true
                                return@OutlinedTextField
                            }
                            scaleValue = inputScale
                            showError = false
                        },
                        label = { Text("输入缩放比例 (0.01 - 1.00)") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { handleDone() }),
                        modifier = Modifier.fillMaxWidth(),
                        isError = showError
                    )

                    if (showError) {
                        Text(
                            text = "输入不合法，请输入0.01到1.00之间的两位小数",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        handleDone()
                    }
                ) {
                    Text("完成")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AppScaleSettingsViewModel) {
    val appList by viewModel.appList.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    var text by rememberSaveable { mutableStateOf("") }
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    var showSettingDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SearchBar(
                modifier = Modifier.padding(20.dp, 10.dp),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = { text = it },
                        onSearch = { },
                        expanded = false,
                        onExpandedChange = {},
                        placeholder = { Text("搜索") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showResetDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Restore,
                                    contentDescription = "重置"
                                )
                            }
                        }
                    )
                },
                expanded = false,
                onExpandedChange = {},
            ) {}
        }
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier.padding(padding).fillMaxSize(),
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshApplications() }) {
            var showingAppList = appList
            if (text.isNotEmpty()) {
                showingAppList = showingAppList.filter { app ->
                    app.appName.contains(text) || app.packageName.contains(text)
                }
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(count = showingAppList.size) { i ->
                    val appInfo = showingAppList[i]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable(
                                onClick = {
                                    selectedApp = appInfo
                                    showSettingDialog = true
                                }
                            )
                            .padding(20.dp, 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberDrawablePainter(drawable = appInfo.icon),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = appInfo.appName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (appInfo.scale.isNotEmpty()) {
                                Text(
                                    text = "缩放比例：${appInfo.scale}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Red
                                )
                            }
                            Text(
                                text = appInfo.packageName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (showSettingDialog) {
                selectedApp?.let {
                    ScaleDialog(
                        appInfo = it,
                        onDismiss = { showSettingDialog = false },
                        onScaleChange = { scale ->
                            viewModel.settingAppScale(it.packageName, scale)
                        }
                    )
                }
            }

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = { Text("重置") },
                    text = { Text("确定恢复默认设置？") },
                    confirmButton = {
                        Button(onClick = {
                            showResetDialog = false
                            viewModel.resetAppScale()
                        }) {
                            Text("确认")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showResetDialog = false }) {
                            Text("取消")
                        }
                    }
                )
            }
        }
    }
}