package com.example.zenithrajbhandari_a2.ViewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import com.example.zenithrajbhandari_a2.R
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputReadingScreen(permissions: Set<String>,
                       permissionsGranted: Boolean,
                       readingsList: List<HeartRateRecord>,
                       uiState: InputReadingsViewModel.UiState,
                       onInsertClick: (Double) -> Unit = {},
                       onError: (Throwable?) -> Unit = {},
                       onPermissionsResult: () -> Unit = {},
                       weeklyAvg: Mass?,
                       onPermissionsLaunch: (Set<String>) -> Unit = {},) {

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is InputReadingsViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [InputReadingsScreenViewModel.UiState] provides details of whether the last action
        // was a success or resulted in an error. Where an error occurred, for example in reading
        // and writing to Health Connect, the user is notified, and where the error is one that can
        // be recovered from, an attempt to do so is made.
        if (uiState is InputReadingsViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    var heartRateInput by remember { mutableStateOf("") }

    // Check if the input value is a valid weight
    fun hasValidDoubleInRange(weight: String): Boolean {
        val tempVal = weight.toDoubleOrNull()
        return if (tempVal == null) {
            false
        } else tempVal <= 1000
    }

    if (uiState != InputReadingsViewModel.UiState.Uninitialized) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!permissionsGranted) {
                item {
                    Button(
                        onClick = { onPermissionsLaunch(permissions) }
                    ) {
                        Text(text = "button 1")
                    }
                }
            } else {
                item {
                    OutlinedTextField(
                        value = heartRateInput,
                        onValueChange = {
                            heartRateInput = it
                        },

                        label = {
                            Text("input")
                        },
                        isError = !hasValidDoubleInRange(heartRateInput),
                        keyboardActions = KeyboardActions { !hasValidDoubleInRange(heartRateInput) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    if (!hasValidDoubleInRange(heartRateInput)) {
                        Text(
                            text = "adsfsdf",
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Button(
                        enabled = hasValidDoubleInRange(heartRateInput),
                        onClick = {
                            onInsertClick(heartRateInput.toDouble())
                            // clear TextField when new weight is entered
                            heartRateInput = ""
                        },

                        modifier = Modifier.fillMaxHeight()

                    ) {
                        Text(text = "adsfsdf")
                    }

                    Text(
                        text = "adsfsdf",
                        fontSize = 24.sp,
                    )
                }
                items(readingsList) { reading ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // show local date and time
                        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        Text(
                            text = "${reading.samples}" + " ",
                        )
                        Text(text = "asdfsd")
                    }
                }
                item {
                    Text(
                        text = "asdas", fontSize = 24.sp,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
//                    if (weeklyAvg == null) {
//                        Text(text = "0.0" + stringResource(id = R.string.kilograms))
//                    } else {
//                        Text(text = "$weeklyAvg".take(5) + stringResource(id = R.string.kilograms))
//                    }
                }
                item {
                    Text("Assignment 2", fontSize = 24.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
                item {
                    // Heart Rate input field
                    OutlinedTextField(
                        value = heartRateInput,
                        onValueChange = {
                            heartRateInput = it
                        },
                        label = {
                            Text("Enter Heart Rate")
                        },
                        isError = !heartRateInput.isDigitsOnly(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    if (!heartRateInput.isDigitsOnly()) {
                        Text(
                            text = "fdsf",
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }


                    // Load and Save buttons
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                // Implement Load button functionality
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Load")
                        }

                        Button(
                            enabled = hasValidDoubleInRange(heartRateInput) && heartRateInput.isDigitsOnly(),
                            onClick = {
                                onInsertClick(heartRateInput.toDouble())
                                // clear TextField when new weight is entered
                                heartRateInput = ""
                                heartRateInput = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Add")
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(vertical = 20.dp)
                    ) {
                        Text(
                            text = "Hello",
                            fontSize = 24.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
                item {
                    Text(
                        "Zenith Rajbhandari",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Text(
                        "Student Id : 301331752",
                        fontSize = 24.sp,
                    )
                }
            }
        }
    }
}
