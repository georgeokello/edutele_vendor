package com.example.myapplication

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.myapplication.ui.screens.homescreen.HomeScreen
import com.example.myapplication.ui.screens.login.LoginScreen
import com.example.myapplication.ui.screens.scancards.NfcManager
import com.example.myapplication.ui.screens.scancards.NfcScreen

import com.example.myapplication.ui.screens.scanqr.ScanQRScreen
import com.example.myapplication.ui.screens.transaction.TransactionScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.util.debug.NfcDebug

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        setContent {
            MyApplicationTheme {
                SetStatusBarColor()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        modifier = Modifier
                            .padding(innerPadding)

                    )
                }
            }
        }
    }

    // ✅ CALLED ONLY FROM NFC SCREEN
    fun enableNfc() {
        val intent = Intent(this, javaClass)
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        nfcAdapter?.enableForegroundDispatch(
            this,
            pendingIntent,
            null,
            null
        )
    }

    fun disableNfc() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        NfcDebug.lastMessage = "Intent received: ${intent.action}"

        // 1. Validate NFC intent action
        if (intent.action != NfcAdapter.ACTION_TAG_DISCOVERED &&
            intent.action != NfcAdapter.ACTION_TECH_DISCOVERED &&
            intent.action != NfcAdapter.ACTION_NDEF_DISCOVERED
        ) {
            NfcDebug.lastMessage = "Ignored non-NFC intent: ${intent.action}"
            return
        }

        // 2. Extract tag safely (API-aware)
        val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        }

        if (tag == null) {
            NfcDebug.lastMessage = "NFC Tag is NULL"
            return
        }

        // 3. Process tag
        val uid = formatUid(tag.id)

        NfcDebug.lastMessage = "Tag detected: $uid"

        // 4. Trigger state update only after validation
        NfcManager.updateData(uid)

        NfcManager.setTrue()

    }

    private fun formatUid(bytes: ByteArray): String {
        return bytes.joinToString(":") { byte ->
            "%02X".format(byte)
        }
    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(modifier: Modifier){
    val navController = rememberNavController()
    val rootNavController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "login"
    ){
        composable(route = "login"){
            LoginScreen(onLoginSuccess = {
                navController.navigate("home"){
                    popUpTo("login"){inclusive = true} // remove login from back stack
                }
            })
        }
        composable("home"){
            HomeScreen( navController = navController)
        }
        composable("scan_qr"){
            ScanQRScreen(modifier = Modifier, navController)
        }
        composable("history") {
            TransactionScreen(navController = navController)
        }
        composable("nfc_Scan") {
            NfcScreen(navController)
        }
    }
}

@Composable
fun SetStatusBarColor() {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            //  Change this to your desired color
            window.statusBarColor = Color(0xFF012A56).toArgb()

            // ⚪ false = white icons, true = dark icons
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false
        }
    }
}

