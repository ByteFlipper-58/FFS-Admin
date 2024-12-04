package com.byteflipper.ffsadmin.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.byteflipper.ffsadmin.services.GitHubService
import com.byteflipper.ffsadmin.utils.PreferencesManager

@Composable
@Preview(showBackground = true)
fun SetupScreen(
    onSetupComplete: () -> Unit = {}
) {
    val context = LocalContext.current

    // State variables for GitHub configuration
    var githubToken by rememberSaveable { mutableStateOf("") }
    var githubOwner by rememberSaveable { mutableStateOf("") }
    var githubRepo by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load saved values
    LaunchedEffect(Unit) {
        val prefs = PreferencesManager(context)
        githubToken = prefs.getGitHubToken() ?: ""
        githubOwner = prefs.getGitHubOwner() ?: ""
        githubRepo = prefs.getGitHubRepo() ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to FFS Admin", style = MaterialTheme.typography.headlineMedium)
        Text("Let's set up your GitHub configuration", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // GitHub Token Input
        OutlinedTextField(
            value = githubToken,
            onValueChange = { githubToken = it },
            label = { Text("GitHub Token") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Button to open GitHub token creation page with pre-selected repo scopes
        Button(
            onClick = {
                openGitHubTokenCreationPage(context)
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Create GitHub Token")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GitHub Repository Owner Input
        OutlinedTextField(
            value = githubOwner,
            onValueChange = { githubOwner = it },
            label = { Text("GitHub Repository Owner") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // GitHub Repository Name Input
        OutlinedTextField(
            value = githubRepo,
            onValueChange = { githubRepo = it },
            label = { Text("GitHub Repository Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Error Message
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Complete Setup Button
        Button(
            onClick = {
                // Validate inputs
                if (githubToken.isBlank() || githubOwner.isBlank() || githubRepo.isBlank()) {
                    errorMessage = "Please fill in all GitHub configuration fields"
                    return@Button
                }

                // Attempt to validate GitHub configuration
                try {
                    // Create a GitHub service to test the configuration
                    val githubService = GitHubService(githubToken, githubOwner, githubRepo)

                    // Save configuration
                    val prefs = PreferencesManager(context)
                    prefs.saveGitHubConfig(githubToken, githubOwner, githubRepo)

                    // Clear any previous error
                    errorMessage = null

                    // Proceed with setup
                    onSetupComplete()
                } catch (e: Exception) {
                    errorMessage = "Failed to validate GitHub configuration: ${e.localizedMessage}"
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Complete Setup")
        }
    }
}

// Separate function to open GitHub token creation page
fun openGitHubTokenCreationPage(context: Context) {
    val githubTokenUrl = Uri.parse("https://github.com/settings/tokens/new").buildUpon()
        .appendQueryParameter("scopes", "repo")
        .appendQueryParameter("description", "FFS Admin App Token")
        .build()

    val intent = Intent(Intent.ACTION_VIEW, githubTokenUrl)
    context.startActivity(intent)
}