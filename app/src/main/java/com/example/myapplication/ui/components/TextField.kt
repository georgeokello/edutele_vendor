package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MainBlack

@Composable
fun AppTextField(amount: String, onAmountChanged: (String) -> Unit, modifier: Modifier, placeholder: String ){

    OutlinedTextField(
        value = amount,
        onValueChange = {text -> onAmountChanged(text)},
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MainBlack,
            focusedBorderColor = MainBlack,
        ),
        placeholder = { Text(text = placeholder) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.padding(20.dp)

    )
}