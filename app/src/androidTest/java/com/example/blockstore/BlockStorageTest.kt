package com.example.blockstore

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.blockstore.utils.STORAGE_VALUE
import com.example.blockstore.utils.TEST_BUTTON_DELETE_TAG
import com.example.blockstore.utils.TEST_BUTTON_SAVE_TAG
import com.example.blockstore.utils.TEST_TEXT_TAG

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule
import kotlin.time.Duration.Companion.seconds

private val timeoutMillis = 30.seconds.inWholeMilliseconds

@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
class BlockStorageTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testStoreRestore() {
        // Wait for initial text to show up
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText("").and(hasTestTag(TEST_TEXT_TAG)),
            timeoutMillis = timeoutMillis,
        )

        // Click on store button
        composeRule
            .onNodeWithTag(TEST_BUTTON_SAVE_TAG)
            .assertHasClickAction()
            .performClick()

        // Wait until we have successfully stored the value and it shows in the text
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText(STORAGE_VALUE).and(hasTestTag(TEST_TEXT_TAG)),
            timeoutMillis = timeoutMillis,
        )

        // Click on delete button
        composeRule
            .onNodeWithTag(TEST_BUTTON_DELETE_TAG)
            .assertHasClickAction()
            .performClick()

        // Wait until the text disappears
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText("").and(hasTestTag(TEST_TEXT_TAG)),
            timeoutMillis = timeoutMillis,
        )
    }

    @Test
    fun testAppRestart() {
        // Wait for initial text to show up
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText("").and(hasTestTag(TEST_TEXT_TAG)),
            timeoutMillis = timeoutMillis,
        )
        // Click on store button
        composeRule
            .onNodeWithTag(TEST_BUTTON_SAVE_TAG)
            .assertHasClickAction()
            .performClick()

        // Wait until we have successfully stored the value and it shows in the text
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText(STORAGE_VALUE).and(hasTestTag(TEST_TEXT_TAG)),
            timeoutMillis = timeoutMillis,
        )

        // Restart the app
        composeRule.activityRule.scenario.close()
        ActivityScenario.launch(MainActivity::class.java, null)

        // Wait until we get the stored value
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText(STORAGE_VALUE).and(hasTestTag(TEST_TEXT_TAG)),
            timeoutMillis = timeoutMillis,
        )

        // Click on delete button
        composeRule
            .onNodeWithTag(TEST_BUTTON_DELETE_TAG)
            .assertHasClickAction()
            .performClick()

        // Wait until the text disappears
        composeRule.waitUntilExactlyOneExists(
            matcher = hasText("").and(hasTestTag(TEST_TEXT_TAG)),
            timeoutMillis = timeoutMillis,
        )
    }
}