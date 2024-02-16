/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.zenithrajbhandari_a2

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zenithrajbhandari_a2.Data.HealthConnectManager
import com.example.zenithrajbhandari_a2.ViewModel.InputReadingsViewModel
import com.example.zenithrajbhandari_a2.ViewModel.InputReadingsViewModelFactory
import com.example.zenithrajbhandari_a2.ui.theme.ZenithRajbhandari_A2Theme
import kotlinx.coroutines.launch

@Composable
fun InputReadingsScreen(
  permissionsGranted: Any,
  permissions: Set<String>,
  uiState: InputReadingsViewModel.UiState,
  onInsertClick: Any,
  readingsList: Any,
  onError: Any,
  onPermissionsResult: () -> Unit,
  onPermissionsLaunch: Any
) {

}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HealthConnectApp(healthConnectManager: HealthConnectManager) {
  ZenithRajbhandari_A2Theme {
    val viewModel: InputReadingsViewModel = viewModel(
      factory = InputReadingsViewModelFactory(
        healthConnectManager = healthConnectManager
      )
    )
    val permissionsGranted by viewModel.permissionsGranted
    val readingsList by viewModel.readingsList
    val permissions = viewModel.permissions
    val onPermissionsResult = { viewModel.initialLoad() }
    val permissionsLauncher =
      rememberLauncherForActivityResult(viewModel.permissionsLauncher) {
        onPermissionsResult()
      }
    InputReadingsScreen(
      permissionsGranted = permissionsGranted,
      permissions = permissions,

      uiState = viewModel.uiState,
      onInsertClick = {},
      readingsList = readingsList,
      onError = {
      },
      onPermissionsResult = {
        viewModel.initialLoad()
      },
      onPermissionsLaunch = {
      }
    )
}
}
