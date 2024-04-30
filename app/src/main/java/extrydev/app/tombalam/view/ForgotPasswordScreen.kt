package extrydev.app.tombalam.view

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler
import com.amazonaws.regions.Regions
import extrydev.app.tombalam.util.Constants.CLIENT_ID
import extrydev.app.tombalam.util.Constants.USER_POOL_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetStep = remember { mutableStateOf(BottomSheetStep.ENTER_CODE) }
    val focusRequesterPhoneNumber = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isError by remember { mutableStateOf(false) }
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            BottomSheetLayout(navController, phoneNumber, coroutineScope, bottomSheetStep)
        }
    ){
        Scaffold(Modifier.fillMaxSize(), topBar = {
        }, content = { pd ->
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            }
            Column(
                modifier = Modifier
                    .fillMaxSize().pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusRequesterPhoneNumber.freeFocus()
                            focusManager.clearFocus()
                        })
                    }
                    .padding(pd),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(0.dp))
                Spacer(modifier = Modifier.padding(0.dp))
                Box(
                    modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.tertiary).padding(horizontal = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "+90",
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(start = 8.dp, top = 5.dp)
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { newValue ->
                            if (newValue.length <= 10) {
                                phoneNumber = newValue
                                isError = false
                            } else {
                                isError = true
                            }
                        },
                        singleLine = true,
                        label = {
                            Text(
                                text = "Telefon numaranızı giriniz..",
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
                            .padding(start = 32.dp).focusRequester(focusRequesterPhoneNumber)
                    )
                }
                if (isError) {
                    Text("Lütfen en fazla 10 hane giriniz.", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp))
                }
                Button(onClick = {
                    if(phoneNumber != "" && phoneNumber.length >= 10){
                        requestForgotPassword(phoneNumber = "+90$phoneNumber", context, coroutineScope, bottomSheetState)
                        focusManager.clearFocus()
                    }
                    else
                    {
                        Toast.makeText(context, "Telefon numarası boş/eksik olamaz!",Toast.LENGTH_SHORT).show()
                    }
                }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                )) {
                    Text(text = "Şifreyi sıfırla",
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 14.sp,
                        color = Color.Black)
                }
                LoginBottom(navController = navController)
                Spacer(modifier = Modifier.padding(0.dp))
                Spacer(modifier = Modifier.padding(0.dp))
                Spacer(modifier = Modifier.padding(0.dp))

            }
        })
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun requestForgotPassword(phoneNumber: String, context: Context, coroutineScope: CoroutineScope, bottomSheetState: ModalBottomSheetState) {
    val cognitoUserPool = CognitoUserPool(context, USER_POOL_ID, CLIENT_ID, null, Regions.EU_WEST_1)
    val cognitoUser = cognitoUserPool.getUser(phoneNumber)

    cognitoUser.forgotPasswordInBackground(object : ForgotPasswordHandler {
        override fun onSuccess() {
            Log.d("tombala","Forgot Password: Başarıyla sıfırlandı")
        }

        override fun getResetCode(continuation: ForgotPasswordContinuation?) {
            coroutineScope.launch {
                bottomSheetState.show()
            }
        }

        override fun onFailure(exception: Exception?) {
            Log.d("tombala","Şifre sıfırlama hatası.")
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetLayout(navController: NavController, phoneNumber: String, coroutineScope: CoroutineScope, bottomSheetStep: MutableState<BottomSheetStep>) {
    var confirmationCodeFromLayout by remember { mutableStateOf("") }
    var newPasswordFromLayout by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = confirmationCodeFromLayout, singleLine = true,
            onValueChange = { confirmationCodeFromLayout = it },
            label = { Text("Onay Kodu") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = newPasswordFromLayout, singleLine = true,
            onValueChange = { newPasswordFromLayout = it },
            label = { Text("Yeni Şifre") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            checkConfirmationCodeWithCognito(context,phoneNumber, newPasswordFromLayout, confirmationCodeFromLayout, coroutineScope, bottomSheetStep)
        }) {
            Text("Onayla")
        }
        when(bottomSheetStep.value){
            BottomSheetStep.SET_NEW_PASSWORD -> {
                navController.navigate("homeScreen"){
                    popUpTo("forgotPasswordScreen"){ inclusive = true}
                }
            }
            else -> {}
        }
    }
}

fun checkConfirmationCodeWithCognito(
    context: Context,
    phoneNumber: String,
    newPassword: String,
    confirmationCode: String,
    coroutineScope: CoroutineScope,
    bottomSheetStep: MutableState<BottomSheetStep>
) {
    val cognitoUserPool = CognitoUserPool(context, USER_POOL_ID, CLIENT_ID, null, Regions.EU_WEST_1)
    val cognitoUser = cognitoUserPool.getUser(phoneNumber)
    cognitoUser.confirmPasswordInBackground(confirmationCode,newPassword, object: ForgotPasswordHandler{
        override fun onSuccess() {
            coroutineScope.launch {
                bottomSheetStep.value = BottomSheetStep.SET_NEW_PASSWORD
            }
        }

        override fun getResetCode(continuation: ForgotPasswordContinuation?) {

        }

        override fun onFailure(exception: java.lang.Exception?) {
            Toast.makeText(context,"Onay Kodu Hatalı!", Toast.LENGTH_SHORT).show()
        }

    })
}

enum class BottomSheetStep {
    ENTER_CODE, SET_NEW_PASSWORD
}
