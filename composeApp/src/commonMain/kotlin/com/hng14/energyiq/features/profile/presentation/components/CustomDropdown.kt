package com.hng14.energyiq.features.profile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hng14.energyiq.core.theme.dmSansFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    label : String,
    placeHolder: String,
    value: String,
    enabled: Boolean,
    options: List<String>,
    onSelected: (String) -> Unit,
    onValueChange: (String)->Unit
) {
    val dmSans = dmSansFontFamily()
    var expanded by remember { mutableStateOf(false) }
    var isOther by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isOther){
        if(isOther) focusRequester.requestFocus()
    }

    Column (modifier = Modifier.fillMaxWidth()){
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = dmSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
            ),
            color = Color(0xFF111827),
        )
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { if (enabled && !isOther) expanded = !expanded }
        ) {
            val fillMaxWidth = Modifier
                .fillMaxWidth()
            OutlinedTextField(
                modifier = fillMaxWidth.menuAnchor(
                    ExposedDropdownMenuAnchorType.PrimaryEditable,
                    enabled
                ), // <- this anchors the dropdown to “Select business type”
                value = value,
                shape = RoundedCornerShape(10.dp),
                onValueChange = {if(isOther) onValueChange(it)},
                readOnly = !isOther,
                enabled = enabled,
                placeholder = { Text(if(isOther) "Type here...."  else placeHolder) },
                trailingIcon = {
                    IconButton(enabled= enabled,
                        onClick = {expanded = true}){
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    }
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color(0xFFFFFFFF)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            expanded = false
                           if(option.equals("Other", ignoreCase = true)){
                               isOther = true
                              onValueChange("")
                           }else{
                               onValueChange(option)
                           }

                        }
                    )
                }
            }
        }
    }
}