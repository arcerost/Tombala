package extrydev.app.tombala.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import extrydev.app.tombala.R
import extrydev.app.tombala.database.UserDatabase
import extrydev.app.tombala.util.Constants
import extrydev.app.tombala.util.NavigationBarItems
import extrydev.app.tombala.viewmodel.EditProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.util.UUID

@SuppressLint("AutoboxingStateCreation", "Recycle")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, viewModel: EditProfileViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var userId by remember { mutableStateOf<String?>(null) }
    val userDb = androidx.room.Room.databaseBuilder(context, UserDatabase::class.java, "UserInfo").build()
    var userToken by remember { mutableStateOf<String?>(null) }
    var userTokenCheck by remember { mutableStateOf(false) }
    var userPhoto by remember { mutableStateOf("")}
    var userName by remember { mutableStateOf(String())}
    var userPhoneNumber by remember { mutableStateOf(String())}
    var userEmail by remember { mutableStateOf(String())}
    var config by remember { mutableStateOf(false) }
    val focusRequesterName = remember { FocusRequester() }
    val focusRequesterPhoneNumber = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val isNameFocused = remember { mutableStateOf(false) }
    val isPhoneNumberFocused = remember { mutableStateOf(false) }
    val isEmailFocused = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val error = viewModel.errorMessageFromUserInfo.collectAsState().value
    if(error.isNotEmpty() && error != "")
        Toast.makeText(context,"Kullanıcı Bulunamadı.",Toast.LENGTH_SHORT).show()
    LaunchedEffect(key1 = config){
        viewModel.getUserInfo("tr")
    }
    LaunchedEffect(key1 = Unit){
        viewModel.config()
        viewModel.getUserInfo("tr")
        val user = userDb.userDao().getUser()
        userToken = user.jwtToken
        userTokenCheck = !userTokenCheck
    }
    if(viewModel.completedCheck.collectAsState().value){
        userId = viewModel.userInfo.collectAsState().value!!.userId
        userPhoto = viewModel.userInfo.collectAsState().value!!.profileImage.toString()
        userName = viewModel.userInfo.collectAsState().value!!.username
        userPhoneNumber = viewModel.userInfo.collectAsState().value!!.phoneNumber
        userEmail = viewModel.userInfo.collectAsState().value!!.email
    }
    val credentialsProvider = CognitoCachingCredentialsProvider(context, Constants.IDENTITY_POOL_ID, Regions.EU_WEST_1)
    if(userTokenCheck)
    {
        credentialsProvider.logins = mapOf("cognito-idp.eu-west-1.amazonaws.com/${Constants.USER_POOL_ID}" to "$userToken")
    }
    var convertPopup by remember { mutableStateOf(false) }
    var coinValueFromText by remember { mutableStateOf("") }
    val userInfo = viewModel.userInfo.collectAsState().value
    val s3Client = AmazonS3Client(credentialsProvider, com.amazonaws.regions.Region.getRegion(Regions.EU_WEST_1))
    val navigationBarItems = remember { NavigationBarItems.values() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val defIndex = 3
    val selectedIndex = remember { mutableStateOf(getInitialIndex(currentRoute,defIndex)) }
    LaunchedEffect(currentRoute) {
        selectedIndex.value = getInitialIndex(currentRoute, selectedIndex.value)
    }
    val nameFromText = remember {mutableStateOf(userName)}
    val phoneNumberFrommText = remember {mutableStateOf(userPhoneNumber)}
    val emailFromText = remember {mutableStateOf(userEmail)}
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background)
    var nameMode by remember { mutableStateOf(false) }
    var phoneNumberMode by remember { mutableStateOf(false) }
    var emailMode by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var objectUrl by remember { mutableStateOf("") }
    val coinPerMoney = viewModel.coinPerMoney.collectAsState().value
    Scaffold(Modifier.fillMaxSize(), topBar = {
    }, content = { pd ->
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondary) {
        }
        Column(modifier = Modifier
            .padding(pd)
            .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.End, modifier = Modifier.align(Alignment.End)) { //top bar alanı
                Image(painter = painterResource(id = R.drawable.coin), contentDescription = "in-game money", modifier = Modifier
                    .size(50.dp)
                    .offset(x = 20.dp)
                    .zIndex(2f))
                if(userInfo != null){
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .zIndex(1f)
                            .defaultMinSize(60.dp, 20.dp)
                    ) {
                        Text(text = "${userInfo.coins}")
                    }
                }
                else
                {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier
                            .zIndex(1f)
                            .defaultMinSize(60.dp, 20.dp)
                    ) {
                        Text(text = "0")
                    }
                }
            }
            Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.padding(0.dp))
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()){ result ->
                        if(result.resultCode == Activity.RESULT_OK){
                            selectedImageUri = result.data?.data
                            val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                            val bytes: ByteArray = inputStream!!.readBytes()
                            val metadata = ObjectMetadata()
                            val genericUserId = UUID.randomUUID().toString()
                            metadata.contentType = "image/png"
                            val putRequest = com.amazonaws.services.s3.model.PutObjectRequest(
                                "tombala-bucket",
                                "publicFolder/userPhoto/$userId/$genericUserId.png",
                                ByteArrayInputStream(bytes),
                                metadata
                            )
                            viewModel.viewModelScope.launch(Dispatchers.IO) {
                                val response = s3Client.putObject(putRequest)
                                withContext(Dispatchers.Main){
                                    if(response != null )
                                    {
                                        val emailToSend = emailFromText.value.ifEmpty { userEmail }
                                        val name = nameFromText.value.ifEmpty { userName }
                                        objectUrl = "publicFolder/userPhoto/$userId/$genericUserId.png"
                                        viewModel.editProfile("tr", email = emailToSend, username = name, profileImage =  objectUrl)
                                    }
                                    else
                                        Toast.makeText(context,"boş", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    if(objectUrl != ""){
                        Image(
                            painter = rememberAsyncImagePainter(model = getS3ImageUrl(objectUrl)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_PICK)
                                    intent.type = "image/*"
                                    launcher.launch(intent)
                                }
                        )
                    }
                    else if(userPhoto != "" && userPhoto != "null")
                    {
                        Image(
                            painter = rememberAsyncImagePainter(model = getS3ImageUrl(userPhoto)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_PICK)
                                    intent.type = "image/*"
                                    launcher.launch(intent)
                                }
                        )
                    }
                    else
                    {
                        Image(
                            painter = painterResource(id = R.drawable.nopp),
                            contentDescription = null,
                            modifier = Modifier
                                .size(75.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_PICK)
                                    intent.type = "image/*"
                                    launcher.launch(intent)
                                }
                        )
                    }
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    TextField(value = nameFromText.value, onValueChange = {
                        if(nameMode)
                        {
                            nameFromText.value = it
                        }
                    }, singleLine = true, modifier = Modifier
                        .focusRequester(focusRequesterName)
                        .onFocusChanged {
                            isNameFocused.value = it.isFocused
                        },label = {
                        Text(text = if (!isNameFocused.value && nameFromText.value.isNotBlank()) nameFromText.value else userName, color = Color.Black)
                    }, trailingIcon = {
                        IconButton(onClick = {
                            nameMode = !nameMode
                        }){
                            Icon(painter = painterResource(id = R.drawable.edit), contentDescription = "edit icon", tint = Color.Black)
                        }
                    }, shape = RoundedCornerShape(10.dp), enabled = nameMode, colors = TextFieldDefaults.textFieldColors(containerColor = if(nameMode)Color.White else Color(
                        216,
                        216,
                        216,
                        255
                    ), disabledTextColor = Color.Black, textColor = Color.Black))
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    TextField(value = phoneNumberFrommText.value, onValueChange = {
                        if(phoneNumberMode)
                            phoneNumberFrommText.value = it
                    }, singleLine = true, modifier = Modifier
                        .focusRequester(focusRequesterPhoneNumber)
                        .onFocusChanged {
                            isPhoneNumberFocused.value = it.isFocused
                        },label = {
                        Text(text = if (!isPhoneNumberFocused.value && phoneNumberFrommText.value.isNotBlank()) phoneNumberFrommText.value else userPhoneNumber, color = Color.Black)
                    }, trailingIcon = {
                        IconButton(onClick = {
                            phoneNumberMode = !phoneNumberMode
                        }){
                            Icon(painter = painterResource(id = R.drawable.edit), contentDescription = "edit icon", tint = Color.Black)
                        }
                    }, shape = RoundedCornerShape(10.dp), enabled = phoneNumberMode, colors = TextFieldDefaults.textFieldColors(containerColor = if(phoneNumberMode)Color.White else Color(
                        216,
                        216,
                        216,
                        255
                    ), textColor = Color.Black))
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    TextField(value = emailFromText.value, onValueChange = {
                        if(emailMode)
                            emailFromText.value = it
                    }, singleLine = true, modifier = Modifier
                        .focusRequester(focusRequesterEmail)
                        .onFocusChanged {
                            isEmailFocused.value = it.isFocused
                        },label = {
                        Text(text = if (!isEmailFocused.value && emailFromText.value.isNotBlank()) emailFromText.value else userEmail, color = Color.Black)
                    }, trailingIcon = {
                        IconButton(onClick = {
                            emailMode = !emailMode
                        }){
                            Icon(painter = painterResource(id = R.drawable.edit), contentDescription = "edit icon", tint = Color.Black)
                        }
                    }, shape = RoundedCornerShape(10.dp), enabled = emailMode, colors = TextFieldDefaults.textFieldColors(containerColor = if(emailMode)Color.White else Color(
                        216,
                        216,
                        216,
                        255
                    ), textColor = Color.Black))
                }
                Spacer(modifier = Modifier.padding(0.dp))
                Button(onClick = {
                    if(objectUrl != "")
                    {
                        val emailToSend = emailFromText.value.ifEmpty { userEmail }
                        val name = nameFromText.value.ifEmpty { userName }
                        viewModel.viewModelScope.launch {
                            viewModel.editProfile("tr", email = emailToSend, username = name, profileImage =  objectUrl)
                            Toast.makeText(context,"Başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else if(userPhoto != "")
                    {
                        val emailToSend = emailFromText.value.ifEmpty { userEmail }
                        val name = nameFromText.value.ifEmpty { userName }
                        viewModel.viewModelScope.launch {
                            viewModel.editProfile("tr", email = emailToSend, username = name, profileImage =  userPhoto)
                            Toast.makeText(context,"Başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        val emailToSend = emailFromText.value.ifEmpty { userEmail }
                        val name = nameFromText.value.ifEmpty { userName }
                        viewModel.viewModelScope.launch {
                            viewModel.editProfile("tr", email = emailToSend, username = name, profileImage =  "")
                            Toast.makeText(context,"Başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    focusManager.clearFocus()
                    nameMode = false
                    emailMode = false
                    phoneNumberMode = false
                }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(colors))) {
                    Text(text = "Kaydet", color = MaterialTheme.colorScheme.tertiary)
                }
                Spacer(modifier = Modifier.padding(0.dp))

                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Mevcut Bakiye", color = MaterialTheme.colorScheme.background, fontSize = 20.sp)
                    Spacer(modifier = Modifier.padding(top = 5.dp))
                    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.background, modifier = Modifier.padding(horizontal = 90.dp))
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)){
                        Column(
                            Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 50.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                            Text(text = "Mevcut Tutar", color = MaterialTheme.colorScheme.background, fontSize = 16.sp)
                            Box(modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .size(150.dp, 50.dp), contentAlignment = Alignment.Center){
                                if(userInfo != null){
                                    Text(text = "${userInfo.wallet} TL", color = MaterialTheme.colorScheme.tertiary, fontSize = 16.sp)
                                }
                                else {
                                    Text(text = "0 TL", color = MaterialTheme.colorScheme.tertiary, fontSize = 16.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.padding(start = 10.dp))
                        Column(
                            Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 50.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly ) {
                            Text(text = "Mevcut Altın", color = MaterialTheme.colorScheme.background, fontSize = 16.sp)
                            if(userInfo != null)
                            {
                                Button(onClick = { convertPopup = !convertPopup
                                }, modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .size(200.dp, 50.dp)
                                    .background(Brush.linearGradient(colors)), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), shape = RoundedCornerShape(10.dp)) {
                                    Text(text = "${coinPerMoney * userInfo.coins} TL", color = Color.White, fontSize = 16.sp)
                                    Spacer(modifier =Modifier.padding(start = 5.dp))
                                    Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, Modifier.size(50.dp))
                                }
                            }
                            else
                            {
                                Button(onClick = {
                                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background), shape = RoundedCornerShape(10.dp)) {
                                    Text(text = "0 TL", color = Color.White, fontSize = 16.sp)
                                    Spacer(modifier =Modifier.padding(start = 5.dp))
                                    Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, Modifier.size(50.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(0.dp)) //design spacers
                Spacer(modifier = Modifier.padding(0.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.padding(0.dp))
                    Button(onClick = {
                                     navController.navigate("paymentInScreen")
                    }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(colors))) {
                        Text(text = "Para Yatır", color = MaterialTheme.colorScheme.tertiary)
                    }
                    Button(onClick = {
                                     navController.navigate("paymentOutScreen")
                    }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(colors))) {
                        Text(text = "Para Çek", color = MaterialTheme.colorScheme.tertiary)
                    }

                    Button(onClick = {
                        navController.navigate("showPaymentScreen")
                    }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(colors))) {
                        Text(text = "Geçmiş ödemeler", color = MaterialTheme.colorScheme.tertiary)
                    }
                    Spacer(modifier = Modifier.padding(0.dp))
                }
                Spacer(modifier = Modifier.padding(0.dp))
                Spacer(modifier = Modifier.padding(0.dp))
                Spacer(modifier = Modifier.padding(0.dp))
            }
        }
    },
        bottomBar = {
            AnimatedNavigationBar(selectedIndex =selectedIndex.value,
                modifier = Modifier.height(64.dp),
                cornerRadius = shapeCornerRadius(cornerRadius = 0.dp),
                ballAnimation = Parabolic(tween(300)),
                indentAnimation = Height(tween(300)),
                barColor = MaterialTheme.colorScheme.background,
                ballColor = MaterialTheme.colorScheme.background
            ) {
                navigationBarItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .noRippleClickable {
                                selectedIndex.value = item.ordinal
                                navController.navigate(item.route)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painter = painterResource(id = item.icon), contentDescription = "Icon",
                            modifier = Modifier.size(26.dp),
                            tint = if(selectedIndex.value == item.ordinal) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        })
    if(convertPopup){
        AlertDialog(
            modifier = Modifier,
            onDismissRequest = { convertPopup = false },
            text = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier
                    .defaultMinSize(minHeight = 300.dp)
                    .background(
                        Color(0, 0, 0, 128), RoundedCornerShape(10.dp)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            convertPopup = !convertPopup
                        })
                    }
                ) {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .padding(horizontal = 30.dp)) {
                        Text(text = "TL'ye çevirmek istediğiniz coin miktarını giriniz", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier)
                        Spacer(modifier = Modifier.padding(top= 10.dp))
                        Divider(thickness = 1.dp, modifier = Modifier.fillMaxWidth(), color = Color.White)
                        Spacer(modifier = Modifier.padding(top= 10.dp))
                        TextField(value = coinValueFromText, onValueChange = { coinValueFromText = it}, shape = RoundedCornerShape(10.dp), colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, cursorColor = Color.Black, textColor = Color.Black), modifier = Modifier)
                        Spacer(modifier = Modifier.padding(top= 10.dp))
                        Button(onClick = {
                            viewModel.changeCoin("tr",coinValueFromText.toInt())
                            Toast.makeText(context,"TL'ye çevirme işlemi tamamlandı.", Toast.LENGTH_SHORT).show()
                            convertPopup = !convertPopup
                        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background), shape = RoundedCornerShape(10.dp), modifier = Modifier) {
                            Text(text = "DEĞİŞTİR", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { convertPopup = false }) {
                    Text(text = "Kapat")
                }
            }
        )
    }
    LaunchedEffect(key1 = viewModel.changeCoinResponse.collectAsState().value){
        config = !config
    }
}

fun getInitialIndex(route: String?, currentIndex: Int): Int {
    return when (route) {
        "homeScreen" -> NavigationBarItems.Homepage.ordinal
        "paymentScreen" -> NavigationBarItems.Payment.ordinal
        "helpScreen" -> NavigationBarItems.Help.ordinal
        "editProfileScreen" -> NavigationBarItems.Profile.ordinal
        else -> currentIndex
    }
}

