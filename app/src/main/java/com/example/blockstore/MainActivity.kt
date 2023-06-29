package com.example.blockstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.blockstore.ui.theme.BlockStorageSampleTheme
import com.example.blockstore.utils.BlockStoreHelper
import com.example.blockstore.utils.STORAGE_KEY
import com.example.blockstore.utils.STORAGE_VALUE
import com.example.blockstore.utils.TEST_BUTTON_DELETE_TAG
import com.example.blockstore.utils.TEST_BUTTON_SAVE_TAG
import com.example.blockstore.utils.TEST_TEXT_TAG
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlockStorageExampleApp()
        }
    }
}

@Composable
fun BlockStorageExampleApp() {
    BlockStorageSampleTheme {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()

        val blockStoreHelper = remember { BlockStoreHelper(context) }
        var restoredText by remember { mutableStateOf("") }

        LaunchedEffect(key1 = Unit) {
            restoredText = blockStoreHelper
                .restore(STORAGE_KEY)
                ?.decodeToString()
                .orEmpty()
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().testTag(TEST_TEXT_TAG),
                    text = restoredText,
                )
                Button(
                    modifier = Modifier.fillMaxWidth().testTag(TEST_BUTTON_SAVE_TAG),
                    onClick = {
                        coroutineScope.launch {
                            blockStoreHelper.store(
                                key = STORAGE_KEY,
                                value = STORAGE_VALUE.encodeToByteArray()
                            )

                            restoredText = blockStoreHelper
                                .restore(STORAGE_KEY)
                                ?.decodeToString()
                                .orEmpty()
                        }
                    },
                ) {
                    Text(text = "Store key")
                }

                Button(
                    modifier = Modifier.fillMaxWidth().testTag(TEST_BUTTON_DELETE_TAG),
                    onClick = {
                        coroutineScope.launch {
                            blockStoreHelper.deleteAll()

                            restoredText = blockStoreHelper
                                .restore(STORAGE_KEY)
                                ?.decodeToString()
                                .orEmpty()
                        }
                    },
                ) {
                    Text(text = "Delete all")
                }
            }
        }
    }
}