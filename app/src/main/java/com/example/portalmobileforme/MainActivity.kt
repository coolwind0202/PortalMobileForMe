package com.example.portalmobileforme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.portalmobileforme.ui.theme.PortalMobileForMeTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PortalMobileForMeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text(LocalContext.current.getString(R.string.navigation_text_home))
                                },
                                onClick = {},
                                selected = true
                            )
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text(LocalContext.current.getString(R.string.navigation_text_settings))
                                },
                                onClick = {},
                                selected = false
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        CurrentLecture(
                            title = "キャリア形成B2",
                            period = LecturePeriod(1),
                            onPasswordEntered = {},
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

class LecturePeriod(private val period: Int) {
    init {
        if (period < 0 || MAX_LECTURE_PERIOD < period)
            throw IllegalArgumentException("Invalid period: $period")
    }

    companion object {
        const val MAX_LECTURE_PERIOD = 5
        const val MINUTE_PERIOD: Long = 90
    }

    val startsAt: Result<LocalTime>
        get() {
            val localTime = when (period) {
                1 -> LocalTime.of(9, 0)
                2 -> LocalTime.of(10, 45)
                3 -> LocalTime.of(13, 15)
                4 -> LocalTime.of(15, 0)
                5 -> LocalTime.of(16, 15)
                else -> null
            }

            if (localTime == null)
                return Result.failure(IllegalArgumentException("Invalid period: $period"))
            return Result.success(localTime)
        }
    val endsAt: Result<LocalTime>
        get() = startsAt.map { it.plusMinutes(MINUTE_PERIOD) }
}

fun formatLecturePeriodTime(localTimeResult: Result<LocalTime>): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return localTimeResult.fold({ return it.format(formatter) }, { return "??:??" })
}

@Composable
fun LecturePeriodText(period: LecturePeriod) {
    val content = LocalContext.current.getString(
        R.string.lecture_period_duration,
        formatLecturePeriodTime(period.startsAt),
        formatLecturePeriodTime(period.endsAt)
    )

    Text(text = content)
}

@Preview
@Composable
fun LecturePeriodTextPreview() {
    LecturePeriodText(period = LecturePeriod(1))
}

@Composable
fun LectureTitleText(title: String) {
    val lectureTitleDescription = LocalContext.current.getString(R.string.lecture_title_description)

    if (title.isEmpty())
        Text(
            text = LocalContext.current.getString(R.string.no_lecture_title),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.semantics { contentDescription = lectureTitleDescription }
        )
    else Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.semantics { contentDescription = lectureTitleDescription }
    )
}

@Preview
@Composable
fun LecturePasswordInputPreview() {
    LecturePasswordInput {}
}

@Composable
fun LecturePasswordInput(onPasswordEntered: (String) -> Unit) {
    val unEnteredPassword = remember { mutableStateOf("") }

    val passwordLengthIsValid = unEnteredPassword.value.length == 4

    TextField(
        value = unEnteredPassword.value,
        onValueChange = { unEnteredPassword.value = it },
        label = { Text(text = LocalContext.current.getString(R.string.lecture_password_description)) },
        trailingIcon = {
            IconButton(onClick = { onPasswordEntered(unEnteredPassword.value) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = LocalContext.current.getString(R.string.lecture_password_send_description)
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
        singleLine = true,
        isError = !passwordLengthIsValid,
        supportingText = { Text(LocalContext.current.getString(R.string.lecture_password_validate_rule)) }
    )
}

@Preview
@Composable
fun CurrentLecturePreview() {
    CurrentLecture(
        title = "キャリア形成B2",
        period = LecturePeriod(1),
        onPasswordEntered = {},
        modifier = Modifier
    )
}

@Composable
fun CurrentLecture(
    title: String,
    period: LecturePeriod,
    onPasswordEntered: (String) -> Unit,
    modifier: Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            LectureTitleText(title = title)
            LecturePeriodText(period = period)
            LecturePasswordInput(onPasswordEntered)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PortalMobileForMeTheme {
        Greeting("Android")
    }
}