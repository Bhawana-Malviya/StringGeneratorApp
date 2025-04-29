package com.example.randomstringgeneratorapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import com.example.randomstringgeneratorapp.data.model.RandomStringData
import com.example.randomstringgeneratorapp.ui.state.RandomStringUiState
import com.example.randomstringgeneratorapp.viewmodel.RandomStringViewModel
import androidx.compose.ui.res.stringResource
import com.example.randomstringgeneratorapp.R

@Composable
fun RandomStringScreen(
    viewModel: RandomStringViewModel = viewModel()
) {

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    var lengthInput by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isLoading = when (uiState) {
        is RandomStringUiState.Loading -> true
        is RandomStringUiState.Success -> uiState.isLoading
        else -> false
    }

    // Disable the Button when no text is present or when loading
    val isButtonEnabled = lengthInput.text.isNotEmpty() && !isLoading
    // Determine if the Clear All button is enabled
    val isClearEnabled = uiState is RandomStringUiState.Success &&
            uiState.randomStrings.isNotEmpty()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                WindowInsets.systemBars
                    .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    .asPaddingValues()
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.random_string_generator),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top=4.dp,bottom = 20.dp)
        )

        InputField(
            value = lengthInput,
            onValueChange = { lengthInput = it },
            enabled = !isLoading
        )

        AppButton(
            text = stringResource(id = R.string.generate),
            onClick = {
                val length = lengthInput.text.toIntOrNull()
                keyboardController?.hide()
                if (length != null) {
                    viewModel.generateRandomString(length)
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.please_enter_valid_number),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            enabled = isButtonEnabled
        )

        // Content Box with weight
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (uiState) {
                is RandomStringUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is RandomStringUiState.Error -> {
                    val errorPrefix = stringResource(id = R.string.error)
                    Text(
                        text = "$errorPrefix ${uiState.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is RandomStringUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.randomStrings.asReversed()) { randomString ->
                            RandomStringItem(
                                randomString = randomString,
                                onDelete = { viewModel.deleteString(it) }
                            )
                        }
                    }
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is RandomStringUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(id = R.string.no_random_strings_generated_yet))
                    }
                }
            }
        }

        // Clear All button
        AppButton(
            text = stringResource(id = R.string.clear_all),
            onClick = { viewModel.clearAllStrings() },
            enabled = isClearEnabled
        )
    }

    // Clear text field after loading completes
    LaunchedEffect(uiState) {
        if (uiState is RandomStringUiState.Success || uiState is RandomStringUiState.Error) {
            lengthInput = TextFieldValue("")
        }
    }
}


@Composable
fun RandomStringItem(
    randomString: RandomStringData,
    onDelete: (RandomStringData) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${stringResource(id = R.string.value)} ${randomString.value}")
                Text(text = "${stringResource(id = R.string.length)} ${randomString.length}")
                Text(text = "${stringResource(id = R.string.created_at)} ${randomString.createdAt}")
            }

            IconButton(onClick = { onDelete(randomString) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete)
                )
            }
        }
    }
}

@Composable
fun InputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean
) {
    val hint = stringResource(id = R.string.enter_number_hint)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
            .padding(4.dp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp), // ensures cursor & hint align
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.text.isEmpty()) {
                    Text(
                        text = hint,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        }
    )
}


@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.DarkGray
        )
    ) {
        Text(text)
    }
}



