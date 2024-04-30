package extrydev.app.tombalam

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import extrydev.app.tombalam.database.UserDatabase
import extrydev.app.tombalam.service.MessageService.Companion.CHANNEL_ID
import extrydev.app.tombalam.ui.theme.TombalaTheme
import extrydev.app.tombalam.util.LocalWebSocketAppState
import extrydev.app.tombalam.view.EditProfileScreen
import extrydev.app.tombalam.view.FaqScreen
import extrydev.app.tombalam.view.ForgotPasswordScreen
import extrydev.app.tombalam.view.GameScreen
import extrydev.app.tombalam.view.HelpScreen
import extrydev.app.tombalam.view.HomeScreen
import extrydev.app.tombalam.view.LoginScreen
import extrydev.app.tombalam.view.OnBoardingScreen
import extrydev.app.tombalam.view.PaymentInScreen
import extrydev.app.tombalam.view.PaymentOutScreen
import extrydev.app.tombalam.view.PaymentScreen
import extrydev.app.tombalam.view.RegisterScreen
import extrydev.app.tombalam.view.SendHelpRequestScreen
import extrydev.app.tombalam.view.ShowPaymentScreen
import extrydev.app.tombalam.websocket.WebSocketAppState
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var db: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            TombalaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                ) {
                    createNotificationChannel()
                    val webSocketAppState = remember { WebSocketAppState() }
                    val viewModel: MainViewModel by viewModels()
                    val vmDes by viewModel.getStartDestination().collectAsState(initial = null) //onBoarding completed ise true
                    val userDao = db.userDao()
                    val startDes = remember { mutableStateOf<String?>(null) }
                    LaunchedEffect(key1 = userDao, key2 = vmDes) {
                        var token: String? = null
                        if (userDao.anyData() != 0) {
                            token = userDao.getUser().jwtToken
                        }
                        else{
                            Log.d("tombala","kullanıcı verisi bulunamadı, main activity")
                        }
                        startDes.value =
                            if(vmDes == "true" && !token.isNullOrEmpty()){
                                "homeScreen"
                            }
                            else if(vmDes == "false" && token.isNullOrEmpty()){
                                "onBoardingScreen"
                            }
                            else if(vmDes == "true" && token.isNullOrEmpty()){
                                "loginScreen"
                            }
                            else if(vmDes == "false" && !token.isNullOrEmpty()){
                                "onBoardingScreen"
                            }
                            else {
                                "registerScreen"
                            }
                    }
                    CompositionLocalProvider(LocalWebSocketAppState provides webSocketAppState) {
                        if(startDes.value != null){
                            val navController = rememberNavController()
                            NavHost(
                                navController = navController,
                                startDestination = startDes.value!!
                            )
                            {
                                composable("onBoardingScreen")
                                {
                                    OnBoardingScreen(navController)
                                }
                                composable("forgotPasswordScreen")
                                {
                                    ForgotPasswordScreen(navController)
                                }
                                composable("registerScreen")
                                {
                                    RegisterScreen(navController)
                                }
                                composable("homeScreen")
                                {
                                    HomeScreen(navController)
                                }
                                composable("loginScreen")
                                {
                                    LoginScreen(navController)
                                }
                                composable("editProfileScreen")
                                {
                                    EditProfileScreen(navController)
                                }
                                composable("paymentOutScreen")
                                {
                                    PaymentOutScreen(navController)
                                }
                                composable("paymentInScreen")
                                {
                                    PaymentInScreen(navController)
                                }
                                composable("helpScreen")
                                {
                                    HelpScreen(navController)
                                }
                                composable("faqScreen")
                                {
                                    FaqScreen(navController)
                                }
                                composable("paymentScreen")
                                {
                                    PaymentScreen(navController)
                                }
                                composable("showPaymentScreen")
                                {
                                    ShowPaymentScreen(navController)
                                }
                                composable("sendHelpRequestScreen")
                                {
                                    SendHelpRequestScreen(navController)
                                }
                                composable("gameScreen/{roomId}", arguments = listOf(
                                    navArgument("roomId"){
                                        type = NavType.StringType
                                    }))
                                {
                                    val roomId = remember { it.arguments!!.getString("roomId")}
                                    GameScreen(navController, roomId!!)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.channel_name)
            val channelDescription = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
