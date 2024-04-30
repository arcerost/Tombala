package extrydev.app.tombalam.view

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import extrydev.app.tombalam.R
import extrydev.app.tombalam.database.UserDatabase
import extrydev.app.tombalam.util.Constants.IDENTITY_POOL_ID
import extrydev.app.tombalam.util.Constants.USER_POOL_ID
import extrydev.app.tombalam.util.NavigationBarItems
import extrydev.app.tombalam.viewmodel.MoneyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.util.UUID

@SuppressLint("AutoboxingStateCreation", "Recycle")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentInScreen(navController: NavController, viewModel: MoneyViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var userId: String? = null
    val userDb = androidx.room.Room.databaseBuilder(context, UserDatabase::class.java, "UserInfo").build()
    var userToken by remember { mutableStateOf<String?>(null) }
    val credentialsProvider = CognitoCachingCredentialsProvider(context, IDENTITY_POOL_ID, Regions.EU_WEST_1)
    var userTokenCheck by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit){
        viewModel.getUserInfo("tr")
        val user = userDb.userDao().getUser()
        userToken = user.jwtToken
        userTokenCheck = !userTokenCheck
    }
    if(userTokenCheck)
    {
        credentialsProvider.logins = mapOf("cognito-idp.eu-west-1.amazonaws.com/$USER_POOL_ID" to "$userToken")
    }
    val check = viewModel.completedCheck.collectAsState().value
    if(check)
        userId = viewModel.userInfo.collectAsState().value!!.userId
    val navigationBarItems = remember { NavigationBarItems.values() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val defValue = 1
    val selectedIndex = remember { mutableStateOf(getInitialIndex(currentRoute,defValue)) }
    LaunchedEffect(currentRoute) {
        selectedIndex.value = getInitialIndex(currentRoute, selectedIndex.value)
    }
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background)
    var price by remember { mutableStateOf("") }
    var statement by remember { mutableStateOf("") }
    val paymentMethods by viewModel.paymentMethods.collectAsState()
    val bankPaymentMethods = paymentMethods.filter { it.paymentMethodType == "bank" }
    val cryptoPaymentMethods = paymentMethods.filter { it.paymentMethodType == "crypto" }
    var receipt by remember { mutableStateOf(String()) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var objectUrl by remember { mutableStateOf("") }
    val s3Client = AmazonS3Client(credentialsProvider, com.amazonaws.regions.Region.getRegion(Regions.EU_WEST_1))
    Scaffold(Modifier.fillMaxSize(), topBar = {
    }, content = { pd ->
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondary) {
        }
        Column(modifier = Modifier
            .padding(pd)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp)) {
                // Banka Bilgileri
                Text("Banka Havale Bilgileri", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(top=10.dp))
                Divider(thickness = 2.dp, modifier = Modifier.size(200.dp,2.dp), color = Color.Black)
                Spacer(modifier = Modifier.padding(top=10.dp))
                bankPaymentMethods.forEach { bankMethod ->
                    Text(text = bankMethod.pmName, color = Color.White)
                    Text(text= bankMethod.pmAdres, color = Color.White)
                }

                // Kripto Bilgileri
                Text("Kripto Havale Bilgileri", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.padding(top=10.dp))
                Divider(thickness = 2.dp, modifier = Modifier.size(200.dp,2.dp), color = Color.Black)
                Spacer(modifier = Modifier.padding(top=10.dp))
                cryptoPaymentMethods.forEach { cryptoMethod ->
                    Row {
                        Image(painter = rememberAsyncImagePainter(model = getS3ImageUrl(cryptoMethod.pmImage)), contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(text = cryptoMethod.pmAdres, color = Color.White)
                    }
                }
            }
            Column(verticalArrangement = Arrangement.SpaceAround, horizontalAlignment = Alignment.Start, modifier = Modifier.padding(8.dp)){//MiddleContent
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Ödeme yöntemi:", color = Color.Black, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    PaymentMenu(modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Ödenecek tutar:", color = Color.Black, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    TextField(value = price, onValueChange = {
                        price = it
                    },modifier = Modifier.weight(1f), singleLine = true, colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, cursorColor = Color.Black, textColor = Color.Black))
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Açıklama:", color = Color.Black, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    TextField(value = statement, onValueChange = { tf ->
                        statement = tf
                    }, colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, cursorColor = Color.Black, focusedIndicatorColor = Color.Black), modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp)
                        .weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Dekont:", color = Color.Black, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()){ result ->
                        if(result.resultCode == Activity.RESULT_OK){
                            selectedImageUri = result.data?.data
                            receipt = selectedImageUri.toString()
                            val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                            val bytes: ByteArray = inputStream!!.readBytes()
                            val metadata = ObjectMetadata()
                            val genericUserId = UUID.randomUUID().toString()
                            metadata.contentType = "image/png"
                            val putRequest = com.amazonaws.services.s3.model.PutObjectRequest(
                                "tombala-bucket",
                                "publicFolder/receipt/$userId/$genericUserId.png",
                                ByteArrayInputStream(bytes),
                                metadata
                            )
                            viewModel.viewModelScope.launch(Dispatchers.IO) {
                                val response = s3Client.putObject(putRequest)
                                withContext(Dispatchers.Main){
                                    if(response != null )
                                    {
                                        objectUrl = "publicFolder/receipt/$userId/$genericUserId.png"
                                    }
                                }
                            }

                        }
                    }
                    val painter: Painter = if(objectUrl != ""){
                        rememberAsyncImagePainter(model = getS3ImageUrl(objectUrl))
                    } else {
                        rememberAsyncImagePainter(model = receipt)
                    }
                    if(receipt == "" || receipt == "null") {
                        Image(
                            painter = painterResource(id = R.drawable.receipt),
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp, 60.dp)
                                .weight(1f)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_PICK)
                                    intent.type = "image/*"
                                    launcher.launch(intent)
                                }
                        )
                    }
                    else {
                        Image(
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(60.dp, 60.dp)
                                .weight(1f)
                                .clip(CircleShape)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_PICK)
                                    intent.type = "image/*"
                                    launcher.launch(intent)
                                }
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier
                .padding(bottom = 10.dp)) {
                Button(onClick = {
                    if(price != "")
                    {
                        if(statement !="")
                        {
                            if(viewModel.selectedMethod.value!!.paymentMethodId != "")
                            {
                                val priceDouble: Double? = try {
                                    price.toDouble()
                                } catch (e: NumberFormatException) {
                                    null
                                }
                                if(objectUrl != "")
                                {
                                    viewModel.moneyTransfer("tr","deposit",viewModel.selectedMethod.value!!.paymentMethodId,priceDouble!!,statement,"","","", objectUrl)
                                }
                                else
                                {
                                    viewModel.moneyTransfer("tr","deposit",viewModel.selectedMethod.value!!.paymentMethodId,priceDouble!!,statement,"","","", null)
                                }
                                Toast.makeText(context, "Ödeme talebiniz başarıyla alındı", Toast.LENGTH_SHORT).show()
                            }
                            else
                            {
                                Toast.makeText(context, "Bir ödeme yöntemi seçiniz!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "Açıklama giriniz!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "Ödenecek Tutarı giriniz!", Toast.LENGTH_SHORT).show()
                    }
                }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .defaultMinSize(minWidth = 100.dp, minHeight = 30.dp)
                    .background(Brush.linearGradient(colors))) {
                    Text(text = "Gönder")
                }
            }
        }
    }, bottomBar = {
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
}