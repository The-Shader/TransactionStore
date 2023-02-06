package com.fireblade.transactionstore.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.fireblade.transactionstore.R
import com.fireblade.transactionstore.home.presentation.ErrorStatus
import com.fireblade.transactionstore.home.presentation.HomeViewIntent
import com.fireblade.transactionstore.home.presentation.HomeViewModel
import com.fireblade.transactionstore.home.presentation.HomeViewState
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }

    @Composable
    private fun HomeScreen() {
        val lifecycleOwner = LocalLifecycleOwner.current
        val stateFlowLifecycleAware = remember(viewModel.viewState, lifecycleOwner) {
            viewModel.viewState.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
        }
        val viewState: HomeViewState? by stateFlowLifecycleAware.collectAsState(initial = null)
        viewState?.let { state ->
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 240.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (state.errorStatus) {
                        ErrorStatus.NONE -> Text(
                            text = getString(R.string.result, state.result),
                            style = TextStyle(
                                fontFamily = FontFamily.Default,
                                fontSize = 40.sp,
                                lineHeight = 30.sp,
                                color = Color(0XFF1656B9)
                            )
                        )
                        ErrorStatus.INVALID_REQUEST -> ErrorText(msg = getString(R.string.error_invalid_request))
                        ErrorStatus.GET -> ErrorText(msg = getString(R.string.error_get_value))
                        ErrorStatus.DELETE -> ErrorText(msg = getString(R.string.error_delete_value))
                        ErrorStatus.COMMIT -> ErrorText(msg = getString(R.string.error_commit))
                        ErrorStatus.ROLLBACK -> ErrorText(msg = getString(R.string.error_rollback))
                        ErrorStatus.GENERAL -> ErrorText(msg = getString(R.string.error_general))
                    }

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 24.dp,
                                top = 24.dp,
                                end = 24.dp
                            ),
                        value = state.request,
                        onValueChange = { text ->
                            viewModel.onIntent(
                                HomeViewIntent.BuildRequest(
                                    input = text
                                )
                            )
                        },
                        label = {
                            Text(
                                text = getString(R.string.enter_label),
                                style = TextStyle(
                                    fontFamily = FontFamily.Default,
                                    fontSize = 20.sp,
                                    lineHeight = 30.sp
                                )
                            )
                        }
                    )
                    Button(
                        onClick = {
                              viewModel.onIntent(
                                  HomeViewIntent.SubmitRequest(
                                      rawRequest = state.request
                                  )
                              )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 24.dp,
                                top = 24.dp,
                                end = 24.dp
                            )
                    ) {
                        Text(
                            text = getString(R.string.submit_cta),
                            style = TextStyle(
                                fontFamily = FontFamily.Default,
                                fontSize = 20.sp,
                                lineHeight = 30.sp,
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ErrorText(msg: String) {
        Text(
            text = msg,
            style = TextStyle(
                fontFamily = FontFamily.Default,
                fontSize = 40.sp,
                lineHeight = 30.sp,
                color = Color(0XFFCF1726)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 24.dp,
                    top = 24.dp,
                    end = 24.dp
                )
        )
    }
}
