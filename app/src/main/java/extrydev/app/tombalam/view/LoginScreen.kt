package extrydev.app.tombalam.view

import android.content.Context
import androidx.compose.ui.Alignment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.room.Room
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.regions.Regions
import com.google.firebase.messaging.FirebaseMessaging
import extrydev.app.tombalam.database.UserDatabase
import extrydev.app.tombalam.database.UserInfo
import extrydev.app.tombalam.service.AuthInterceptor
import extrydev.app.tombalam.service.TokenManager
import extrydev.app.tombalam.util.Constants.CLIENT_ID
import extrydev.app.tombalam.util.Constants.USER_POOL_ID
import extrydev.app.tombalam.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {
    var snsToken by remember { mutableStateOf("") }
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@addOnCompleteListener
        }
        val token = task.result
        snsToken = token
    }
    var phoneNumber by remember { mutableStateOf(TextFieldValue()) }
    val password = remember { mutableStateOf(TextFieldValue()) }
    val control = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusRequesterPhoneNumber = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isError by remember { mutableStateOf(false) }
    Scaffold(topBar = {

    }, content =
    { pad ->
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        }
        Column(modifier = Modifier
            .padding(pad)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusRequesterPhoneNumber.freeFocus()
                    focusManager.clearFocus()
                })
            }
            .fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
            Column(verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 15.dp, horizontal = 15.dp)) {
                Text(text = "LOGO", color = MaterialTheme.colorScheme.tertiary, modifier = Modifier)
                Spacer(modifier = Modifier.padding(0.dp))
                Column(Modifier) {
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "+90",
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(start = 15.dp, top = 5.dp)
                        )
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { newValue ->
                                if (newValue.text.length <= 10) {
                                    phoneNumber = newValue
                                    isError = false
                                } else {
                                    isError = true
                                }
                            }, singleLine = true,
                            label = {
                                Text(
                                    text = "Telefon numarası",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontSize = 12.sp
                                )
                            },
                            trailingIcon = {
                                if (isError) {
                                    Icon(Icons.Default.Warning, contentDescription = "Hata", tint = Color.Red)
                                }
                            },
                            isError = isError,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = MaterialTheme.colorScheme.tertiary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
                                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                cursorColor = MaterialTheme.colorScheme.tertiary,
                                errorTrailingIconColor = Color.Transparent,
                                errorBorderColor = Color.Transparent,
                                errorLabelColor = Color.Transparent,
                                errorSupportingTextColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .padding(horizontal = 50.dp)
                                .fillMaxWidth()
                                .focusRequester(focusRequesterPhoneNumber)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.tertiary,
                                    RoundedCornerShape(10.dp)
                                )
                        )
                    }
                    if (isError) {
                        Text("Lütfen en fazla 10 hane giriniz.", color = Color.Red, fontSize = 13.sp, modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 50.dp))
                    }
                    OutlinedTextField(value = password.value, onValueChange = {
                        password.value = it
                    }, singleLine = true , label = {
                        Text("Şifre", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp)
                    }, visualTransformation = PasswordVisualTransformation(), shape = RoundedCornerShape(10.dp), modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp), colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colorScheme.tertiary, unfocusedLabelColor = MaterialTheme.colorScheme.tertiary, focusedLabelColor = MaterialTheme.colorScheme.tertiary, unfocusedBorderColor = MaterialTheme.colorScheme.tertiary, focusedBorderColor = MaterialTheme.colorScheme.tertiary, cursorColor = MaterialTheme.colorScheme.tertiary))

                }
                Button(onClick = {
                    if(phoneNumber.text != "" && phoneNumber.text.length >= 10)
                        if(password.value.text !=""  && password.value.text.length >= 6)
                        {
                            control.value = !control.value
                        }
                        else
                            Toast.makeText(context,"Şifrenizi hatalı girdiniz!", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context,"Telefon numaranızı hatalı girdiniz!", Toast.LENGTH_SHORT).show()

                }, modifier = Modifier, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.background
                )) {
                    Text(text = "Giriş yap",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.background)
                }
                Spacer(modifier = Modifier.padding(0.dp))
                Spacer(modifier = Modifier.padding(0.dp))
                Row(modifier = Modifier
                    .clickable {
                        navController.navigate("registerScreen") {
                            popUpTo("loginScreen") { inclusive = true }
                        }
                    }) {
                    Text(text = "Kayıt Olmak İçin Tıkla",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 15.sp
                    )
                }
                Row(
                    Modifier
                        .clickable {
                            navController.navigate("forgotPasswordScreen") {
                                popUpTo("loginScreen") { inclusive = true }
                            }
                        }) {
                    Text(text = "Şifremi Unuttum",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 15.sp)
                }
            }
        }
    })
    if (control.value) {
        SignIn(phoneNumber = "+90"+phoneNumber.text, password = password.value.text, navController = navController, snsToken)
        control.value = !control.value
    }
}
@Composable
fun SignIn(phoneNumber: String, password: String, navController: NavController, snsToken: String, viewModel: LoginViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserIdPreferences", Context.MODE_PRIVATE)
    val db: UserDatabase = Room.databaseBuilder(context, UserDatabase::class.java,"UserInfo").build()
    val userDao = db.userDao()
    val tokenManager = TokenManager(userDao)
    val authInterceptor = AuthInterceptor(context,tokenManager)
    val userPool = CognitoUserPool(
        context,
        USER_POOL_ID,
        CLIENT_ID,
        null,
        Regions.EU_WEST_1
    )
    val cognitoUser = userPool.getUser(phoneNumber)
    cognitoUser.getSessionInBackground(object : AuthenticationHandler {
        override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
            if(snsToken != "")
            {
                val editor = sharedPreferences.edit()
                editor.putString("phoneNumber", phoneNumber)
                editor.apply()
                Log.d("tombala","Giriş başarılı")
                val jwtToken = userSession!!.idToken.jwtToken
                val refToken = userSession.refreshToken.token
                authInterceptor.updateToken(jwtToken,refToken)
                viewModel.setSnsToken("tr",snsToken,"android","live")
                val userr = UserInfo(jwtToken,refToken)
                viewModel.viewModelScope.launch {
                    userDao.insert(userr)
                    navController.navigate("homeScreen")
                }
            }
            else
                Toast.makeText(context, "SnsToken",Toast.LENGTH_SHORT).show()
        }

        override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, username: String?) {
            // Oturum açma detayları sağlanmalı
            val authenticationDetails = AuthenticationDetails(username, password, null)
            authenticationContinuation?.setAuthenticationDetails(authenticationDetails)
            authenticationContinuation?.continueTask()
        }

        override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
            // Multi-factor authentication (MFA) kodu isteniyor
            // Eğer uygulamanız MFA kullanıyorsa bu metod içinde kodu alıp devam ettirebilirsiniz
        }

        override fun authenticationChallenge(continuation: ChallengeContinuation?) {
            // Auth challenge isteniyor
            // Bu method genellikle CUSTOM_CHALLENGE tipindeki auth challenge'ları handle etmek için kullanılır
        }

        override fun onFailure(exception: Exception?) {
            Log.d("tombala","Login failure")
            // Oturum açma işlemi başarısız oldu
        }
    })
}