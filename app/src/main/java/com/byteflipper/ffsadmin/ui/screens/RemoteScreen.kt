package com.byteflipper.ffsadmin.ui.screens

import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.byteflipper.ffsadmin.R
import com.byteflipper.ffsadmin.utils.PreferencesManager
import com.byteflipper.jsoncomposer.JsonViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL

// Data class to represent GitHub repository contents
data class RepositoryItem(
    val name: String,
    val type: String,
    val path: String,
    val downloadUrl: String? = null
)

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun RemoteScreen() {
    val navController = rememberNavController() // NavController to manage navigation

    NavHost(navController = navController, startDestination = "remoteScreen") {
        composable("remoteScreen") {
            RemoteScreenContent(navController)
        }
        composable(
            route = "jsonViewerScreen/{encodedJsonUrl}",
            arguments = listOf(navArgument("encodedJsonUrl") { defaultValue = "" })
        ) { backStackEntry ->
            val encodedJsonUrl = backStackEntry.arguments?.getString("encodedJsonUrl") ?: ""
            val decodedJsonUrl = java.net.URLDecoder.decode(encodedJsonUrl, "UTF-8")
            val jsonUrl = URL(decodedJsonUrl)
            JsonViewerScreen(jsonUrl = jsonUrl)
        }

    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun RemoteScreenContent(navController: NavHostController) {
    val context = LocalContext.current
    var repositoryContents by remember { mutableStateOf<List<RepositoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentPath by remember { mutableStateOf("") }
    val pathStack = remember { mutableStateListOf<String>() }

    LaunchedEffect(currentPath) {
        isLoading = true
        try {
            repositoryContents = fetchRepositoryContents(context, currentPath)
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
        } finally {
            isLoading = false
        }
    }

    BackHandler(enabled = pathStack.isNotEmpty()) {
        currentPath = pathStack.removeLast()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn {
                    items(repositoryContents) { item ->
                        RepositoryItemRow(
                            item = item,
                            onClick = {
                                if (item.type == "dir") {
                                    pathStack.add(currentPath)
                                    currentPath = item.path
                                } else if (item.name.endsWith(".json")) {
                                    navController.navigate("jsonViewerScreen/${java.net.URLEncoder.encode(item.downloadUrl, "UTF-8")}")
                                } else {
                                    Toast.makeText(context, "Selected: ${item.name}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onLongClick = {
                                item.downloadUrl?.let { url ->
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Copied URL", url)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RepositoryItemRow(
    item: RepositoryItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val iconRes = when {
            item.type == "dir" -> R.drawable.folder_24px
            item.name.endsWith(".md", ignoreCase = true) -> R.drawable.markdown_24px
            item.name.endsWith(".json", ignoreCase = true) -> R.drawable.file_json_24px
            item.name.endsWith(".jpg", ignoreCase = true) || item.name.endsWith(".png", ignoreCase = true) -> R.drawable.image_24px
            else -> R.drawable.docs_24px
        }

        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = item.type,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
    }
}

suspend fun fetchRepositoryContents(
    context: Context,
    path: String = ""
): List<RepositoryItem> = withContext(Dispatchers.IO) {
    try {
        val prefsManager = PreferencesManager(context)
        val token = prefsManager.getGitHubToken()
        val owner = prefsManager.getGitHubOwner()
        val repo = prefsManager.getGitHubRepo()

        if (token.isNullOrBlank() || owner.isNullOrBlank() || repo.isNullOrBlank()) {
            throw Exception("GitHub configuration is not set up")
        }

        val fullPath = if (path.isBlank()) "" else "$path/"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.github.com/repos/$owner/$repo/contents/$fullPath")
            .addHeader("Authorization", "token $token")
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("Failed to fetch repository contents")
        }

        val responseBody = response.body?.string() ?: ""
        val jsonArray = JSONArray(responseBody)

        return@withContext (0 until jsonArray.length()).map { index ->
            val item = jsonArray.getJSONObject(index)
            RepositoryItem(
                name = item.getString("name"),
                type = item.getString("type"),
                path = item.getString("path"),
                downloadUrl = item.optString("download_url", null)
            )
        }.sortedWith(compareBy({ it.type }, { it.name }))
    } catch (e: Exception) {
        throw Exception("Error: ${e.localizedMessage}")
    }
}
