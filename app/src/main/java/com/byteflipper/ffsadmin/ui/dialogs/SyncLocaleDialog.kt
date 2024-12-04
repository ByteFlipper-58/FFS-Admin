package com.byteflipper.ffsadmin.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.byteflipper.ffsadmin.R

@Composable
fun SyncLocaleDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(painter = painterResource(id = R.drawable.cloud_download_24px), contentDescription = "Example Icon")
        },
        title = {
            Text(text = "Sync Locale")
        },
        text = {
            Text(text = "Are you sure you want to sync the locale data from the remote server?")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}