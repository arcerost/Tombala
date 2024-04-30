package extrydev.app.tombalam.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Room
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.amazonaws.regions.Regions
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult
import com.google.firebase.messaging.FirebaseMessaging
import extrydev.app.tombalam.database.UserDatabase
import extrydev.app.tombalam.database.UserInfo
import extrydev.app.tombalam.util.Constants.CLIENT_ID
import extrydev.app.tombalam.util.Constants.USER_POOL_ID
import extrydev.app.tombalam.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController, viewModel: RegisterViewModel = hiltViewModel()) {
    LaunchedEffect(key1 = Unit, block = {
        viewModel.getConfig()
    })
    var snsToken by remember { mutableStateOf("") }
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            return@addOnCompleteListener
        }
        val token = task.result
        snsToken = token
    }
    var phoneNumber by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }
    var pw2 by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val control = remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var isError by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(false) }
    var selected2 by remember { mutableStateOf(false) }
    val terms = viewModel.termsList.collectAsState().value
    val context = LocalContext.current
    val emailRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)"
    fun isEmailValid(email: String): Boolean {
        return emailRegex.toRegex().matches(email)
    }
    Scaffold(Modifier.fillMaxSize(), topBar = {
    }, content = { pd ->
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(pd)
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
            .padding(pd), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.padding())
            OutlinedTextField(value = userName,
                onValueChange = {
                    userName = it
                },
                label = {
                    Text(
                        text = "Kullanıcı Adı",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 12.sp
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    cursorColor = MaterialTheme.colorScheme.tertiary
                )
            )

            OutlinedTextField(
                value = "+90$phoneNumber",
                onValueChange = {
                    val userInput = if (it.startsWith("+90")) it.drop(3) else it

                    if (userInput.all { char -> char.isDigit() }) {
                        if (userInput.length <= 10) {
                            phoneNumber = userInput
                            isError = false
                        } else {
                            isError = true
                        }
                    }
                },
                singleLine = true,
                isError = isError,
                trailingIcon = {
                    if (isError) {
                        Icon(Icons.Default.Warning, contentDescription = "Hata", tint = Color.Red)
                    }
                },
                label = {
                    Text(
                        text = "Telefon numarası",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 12.sp
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    cursorColor = MaterialTheme.colorScheme.tertiary,
                    errorTrailingIconColor = MaterialTheme.colorScheme.tertiary,
                    errorBorderColor = MaterialTheme.colorScheme.tertiary,
                    errorLabelColor = MaterialTheme.colorScheme.tertiary,
                    errorSupportingTextColor = MaterialTheme.colorScheme.tertiary
                )
            )
            OutlinedTextField(value = pw, onValueChange = {
                pw = it
            }, label = {
                Text(text = "Parola", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp)
            }, visualTransformation = PasswordVisualTransformation(), singleLine = true, colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.tertiary, unfocusedLabelColor = MaterialTheme.colorScheme.tertiary, focusedLabelColor = MaterialTheme.colorScheme.tertiary, unfocusedBorderColor = MaterialTheme.colorScheme.tertiary, focusedBorderColor = MaterialTheme.colorScheme.tertiary, cursorColor = MaterialTheme.colorScheme.tertiary))

            OutlinedTextField(value = pw2, onValueChange = {
                pw2 = it
            }, visualTransformation = PasswordVisualTransformation(), singleLine = true, label = {
                Text(text = "Parola Tekrar", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp)
            }, colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.tertiary, unfocusedLabelColor = MaterialTheme.colorScheme.tertiary, focusedLabelColor = MaterialTheme.colorScheme.tertiary, unfocusedBorderColor = MaterialTheme.colorScheme.tertiary, focusedBorderColor = MaterialTheme.colorScheme.tertiary, cursorColor = MaterialTheme.colorScheme.tertiary))

            OutlinedTextField(value = email, onValueChange = {
                email = it
            }, singleLine = true, label = {
                Text(text = "Email", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp)
            }, colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.tertiary, unfocusedLabelColor = MaterialTheme.colorScheme.tertiary, focusedLabelColor = MaterialTheme.colorScheme.tertiary, unfocusedBorderColor = MaterialTheme.colorScheme.tertiary, focusedBorderColor = MaterialTheme.colorScheme.tertiary, cursorColor = MaterialTheme.colorScheme.tertiary))
            Spacer(modifier = Modifier.padding())
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selected, onClick = {
                    selected = !selected
                }, Modifier.size(35.dp), colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.tertiary, unselectedColor = MaterialTheme.colorScheme.tertiary, disabledSelectedColor = MaterialTheme.colorScheme.tertiary, disabledUnselectedColor = MaterialTheme.colorScheme.tertiary)
                )
                Spacer(modifier = Modifier.width(1.dp))
                val text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline, fontSize = 12.sp)) {
                        pushStringAnnotation(tag = "LINK", annotation = "Sözleşmeyi")
                        append("Sözleşmeyi")
                        pop()
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                        append(" okudum,")
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                        append(" anladım,")
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                        append(" kabul ediyorum.")
                    }
                }
                ClickableText(text = text, onClick = { offset ->
                    val annotation = text.getStringAnnotations(tag = "LINK", start = offset, end = offset).firstOrNull()
                    annotation?.let {
                        if (it.item == "Sözleşmeyi") {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(terms[0]))
                            context.startActivity(intent)
                        }
                    }
                })
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selected2, onClick = {
                    selected2 = !selected2
                }, Modifier.size(35.dp), colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.tertiary, unselectedColor = MaterialTheme.colorScheme.tertiary, disabledSelectedColor = MaterialTheme.colorScheme.tertiary, disabledUnselectedColor = MaterialTheme.colorScheme.tertiary)
                )
                Spacer(modifier = Modifier.width(1.dp))
                val text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline, fontSize = 12.sp)) {
                        pushStringAnnotation(tag = "LINK", annotation = "Sözleşmeyi")
                        append("Sözleşmeyi")
                        pop()
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                        append(" okudum,")
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                        append(" anladım,")
                    }
                    withStyle(style = SpanStyle(color = Color.White, fontSize = 12.sp)) {
                        append(" kabul ediyorum.")
                    }
                }
                ClickableText(text = text, onClick = { offset ->
                    val annotation = text.getStringAnnotations(tag = "LINK", start = offset, end = offset).firstOrNull()
                    annotation?.let {
                        if (it.item == "Sözleşmeyi") {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(terms[1]))
                            context.startActivity(intent)
                        }
                    }
                })
            }
            Spacer(modifier = Modifier.padding())
            Button(onClick = { control.value = !control.value
                focusManager.clearFocus()
                             }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            )) {
                Text(text = "Kayıt Ol",
                    style = MaterialTheme.typography.headlineLarge,
                    fontSize = 14.sp,
                    color = Color.Black)
            }
            Spacer(modifier = Modifier.padding())
            Spacer(modifier = Modifier.padding())
            LoginBottom(navController)
        }
    }, bottomBar = {})
    if(control.value) {
        if (userName != "")
            if (phoneNumber != "")
                if (pw != "")
                    if (pw2 != "")
                        if (pw.length >= 6 && pw2.length >= 6)
                            if (email != "") {
                                if(isEmailValid(email))
                                {
                                    if(selected && selected2){
                                        Pool(userName = userName, phoneNumber = "+90$phoneNumber", password = pw, email = email, navController, snsToken, viewModel)
                                        control.value = !control.value
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "Sözleşmeleri onaylayınız!", Toast.LENGTH_SHORT).show()
                                        control.value = !control.value
                                    }
                                }
                                else
                                {
                                    Toast.makeText(context, "Geçerli bir e-mail giriniz!", Toast.LENGTH_SHORT).show()
                                    control.value = !control.value
                                }
                            }
                            else
                            {
                                Toast.makeText(context, "E-mail alanı boş bırakılamaz!", Toast.LENGTH_SHORT).show()
                                control.value = !control.value
                            }
                        else
                        {
                            Toast.makeText(context, "Şifreler en az 6 karakter olmalı!", Toast.LENGTH_SHORT).show()
                            control.value = !control.value
                        }
                    else
                    {
                        Toast.makeText(context, "Şifreyi yeniden giriniz!", Toast.LENGTH_SHORT).show()
                        control.value = !control.value
                    }
                else
                {
                    Toast.makeText(context, "Şifre alanı boş bırakılamaz!", Toast.LENGTH_SHORT).show()
                    control.value = !control.value
                }
            else
            {
                Toast.makeText(context, "Telefon numarası alanı boş bırakılamaz!", Toast.LENGTH_SHORT).show()
                control.value = !control.value
            }
        else
        {
            Toast.makeText(context, "Kullanıcı adı alanı boş bırakılamaz!", Toast.LENGTH_SHORT).show()
            control.value = !control.value
        }
    }
}
@Composable
fun Pool(userName: String, phoneNumber: String, password: String, email: String, navController: NavController, snsToken: String, viewModel: RegisterViewModel = hiltViewModel()) {
    var jwtToken : String
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserIdPreferences", Context.MODE_PRIVATE)
    var refreshToken: String
    val db: UserDatabase = Room.databaseBuilder(context,UserDatabase::class.java,"UserInfo").build()
    val userDao = db.userDao()
    val userPool = CognitoUserPool(
        context,
        USER_POOL_ID,
        CLIENT_ID,
        null,
        Regions.EU_WEST_1
    )
    val userAttributes = CognitoUserAttributes()
    userPool.signUpInBackground(phoneNumber,password,userAttributes,null,object: SignUpHandler {
        override fun onSuccess(user: CognitoUser?, signUpResult: SignUpResult?) {
            user!!.getSessionInBackground(object : AuthenticationHandler {
                override fun onSuccess(
                    userSession: CognitoUserSession?,
                    newDevice: CognitoDevice?
                ) {
                    if(snsToken != "")
                    {
                        jwtToken = userSession!!.idToken.jwtToken
                        refreshToken = userSession.refreshToken.token
                        runBlocking {
                            val userr = UserInfo(jwtToken, refreshToken)
                            userDao.insert(userr)
                        }
                        viewModel.setAuthToken(jwtToken, refreshToken)
                        val editor = sharedPreferences.edit()
                        editor.putString("phoneNumber", phoneNumber)
                        editor.apply()
                        viewModel.beRegister("tr", userName, phoneNumber, email)
                        viewModel.setSnsToken("tr",snsToken,"android","live")
                        viewModel.viewModelScope.launch {
                            viewModel.completed.collect { message ->
                                if (message) {
                                    navController.navigate("homeScreen"){
                                        popUpTo("registerScreen") { inclusive = true }
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(context, "SnsToken", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun getAuthenticationDetails(
                    authenticationContinuation: AuthenticationContinuation?,
                    userId: String?
                ) {
                    // Oturum açma detayları sağlanmalı
                    val authenticationDetails = AuthenticationDetails(phoneNumber, password, null)
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
                    Log.d("tombala","Register failure")
                    // Oturum açma işlemi başarısız oldu
                }
            })
            Log.d("tombala","Sign Up is Success")
            Log.d("tombala","userConfirm: ${signUpResult!!.userConfirmed}")
        }
        override fun onFailure(exception: Exception?) {
            Log.d("tombala","Sign Up Failure: exception = $exception")
        }
    })
}


@Composable
fun LoginBottom(navController: NavController) {
    Column(modifier = Modifier.clickable {
        navController.navigate("loginScreen"){
            popUpTo("registerScreen") { inclusive = true }
        }
    }) {
        Text(text = "Zaten hesabın var mı?\n" +
                "Giriş yap",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 12.sp
        )
    }
}
