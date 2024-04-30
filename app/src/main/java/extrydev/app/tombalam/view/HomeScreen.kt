package extrydev.app.tombalam.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.commandiron.spin_wheel_compose.SpinWheel
import com.commandiron.spin_wheel_compose.SpinWheelDefaults
import com.commandiron.spin_wheel_compose.state.rememberSpinWheelState
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import extrydev.app.tombalam.R
import extrydev.app.tombalam.model.Ad
import extrydev.app.tombalam.model.RoomListDetailList
import extrydev.app.tombalam.util.LocalWebSocketAppState
import extrydev.app.tombalam.viewmodel.HomeViewModel
import extrydev.app.tombalam.websocket.WebSocketAppState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import coil.compose.rememberAsyncImagePainter
import extrydev.app.tombalam.util.NavigationBarItems

@SuppressLint("AutoboxingStateCreation", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val webSocketState = LocalWebSocketAppState.current
    val navigationBarItems = remember { NavigationBarItems.values() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val defValue = 0
    val selectedIndex = remember { mutableStateOf(getInitialIndex(currentRoute,defValue)) }
    var showSpinWheel by remember { mutableStateOf(!didSpinToday(context = context)) }
    val list = viewModel.keyList.collectAsState().value
    val dailyWheelItems = viewModel.dailyWheelItems.collectAsState().value
    val purchase = viewModel.purchases.collectAsState().value
    var hasPurchase by remember { mutableStateOf(false) }
    val refreshCheck = viewModel.wheelCheck.collectAsState().value
    if (purchase != null) {
        if(purchase.isNotEmpty()) {
            hasPurchase = true
        }
    }
    LaunchedEffect(key1 = refreshCheck){
        viewModel.getUserInfo("tr")
    }
    LaunchedEffect(currentRoute) {
        selectedIndex.value = getInitialIndex(currentRoute,selectedIndex.value)
    }
    val userInfo = viewModel.userInfo.collectAsState().value
    if(userInfo != null)
        Log.d("user", userInfo.userId)
    LaunchedEffect(key1 = Unit){
        viewModel.getConfig()
        viewModel.getRooms("tr")
        viewModel.getUserInfo("tr")
    }
    Scaffold(topBar = {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) { //top bar alanı
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = "in-game money",
                    modifier = Modifier
                        .size(50.dp)
                        .offset(x = 20.dp)
                        .zIndex(2f)
                )
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
                else{
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
    }, content =
    { pad ->
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondary) {
        }
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoomList(navController, webSocketState, list, hasPurchase)
        }
    }, bottomBar =
    {
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
    if(showSpinWheel && dailyWheelItems.isNotEmpty())
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    showSpinWheel = false
                })
            }, contentAlignment = Alignment.Center) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0, 0, 0, 128)) {
            }
            val pieCount = dailyWheelItems.size
            val pieColors by remember {
                mutableStateOf(dailyWheelItems.map { hexToComposeColor(it.color) })
            }
            val pieTexts by remember {
                mutableStateOf(dailyWheelItems.map { it.text })
            }
            val scope = rememberCoroutineScope()
            val state = rememberSpinWheelState(
                pieCount = pieCount,
                durationMillis = 5000,
                delayMillis = 200,
                rotationPerSecond = 2f,
                easing = LinearOutSlowInEasing,
                startDegree = 0f
            )
            SpinWheel(state = state, dimensions = SpinWheelDefaults.spinWheelDimensions(spinWheelSize = 400.dp, frameWidth = 20.dp, selectorWidth = 25.dp),
                colors = SpinWheelDefaults.spinWheelColors(frameColor = Color(0xFFFFFFFF), dividerColor = Color(
                    0xFF000000
                ), selectorColor = Color(0xFFFFC700),
                    pieColors = pieColors), onClick = {
                    scope.launch {
                        state.animate { pieIndex ->
                            val selectedPrice = dailyWheelItems[pieIndex].price
                            viewModel.postWheel("tr",selectedPrice)
                        }
                        saveLastSpinDate(context)
                        delay(1000)
                        showSpinWheel = false
                    }
                }
            ) { pieIndex ->
                Text(text = pieTexts[pieIndex], color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RoomList(navController: NavController, webSocketAppState: WebSocketAppState, adList: List<Ad>, hasPurchase: Boolean, viewModel: HomeViewModel = hiltViewModel()) {
    val roomList = viewModel.roomList.collectAsState().value
    val errorMessage = viewModel.errorMessage.collectAsState().value
    val context = LocalContext.current
    if (errorMessage != "" && roomList.isEmpty()) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    } else {
        RoomListView(rooms = roomList, webSocketAppState, adList, navController = navController, hasPurchase = hasPurchase)
    }
}

fun hexToComposeColor(hex: String): Color {
    return Color(android.graphics.Color.parseColor(hex))
}

@Composable
fun AdsComposable(s3key: String, url: String) {
    val context = LocalContext.current
    Surface(modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, top = 8.dp), color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(350.dp, 125.dp)
                .clickable
                {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }, contentAlignment = Alignment.TopStart
        ) {
            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .components {
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .build()
            Image(
                painter = rememberAsyncImagePainter(model = getS3ImageUrl(s3key), imageLoader),
                contentDescription = "ads image",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}


@Composable
fun RoomListView(rooms: List<RoomListDetailList>, webSocketAppState: WebSocketAppState, adList: List<Ad>, hasPurchase: Boolean, navController: NavController) {
    val state = rememberLazyListState()
    LazyColumn(contentPadding = PaddingValues(top = 5.dp, bottom = 5.dp), verticalArrangement = Arrangement.SpaceEvenly, state = state) {
        items(rooms.size * 2) { index ->
            if (index % 2 == 0) {
                if(rooms.isNotEmpty()){
                    Room(rooms[index / 2], webSocketAppState, navController = navController)
                }
            } else {
                if(!hasPurchase)
                {
                    if(adList.isNotEmpty())
                    {
                        if(adList.size > index){
                            AdsComposable(adList[index].s3key, adList[index].url)
                        }
                        else
                            AdsComposable(adList[0].s3key, adList[0].url)
                    }
                }
            }
        }
    }
}

@Composable
fun Room(room: RoomListDetailList, webSocketAppState: WebSocketAppState, navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val myFont = Font(R.font.cherrybomb)
    val maxUser by remember { mutableIntStateOf(room.maxUser) }
    val maxCard by remember { mutableIntStateOf(room.maxCard)}
    val firstLinePrice by remember { mutableIntStateOf(room.firstLinePrice) }
    val secondLinePrice by remember { mutableIntStateOf(room.secondLinePrice) }
    val timeLeftMillis by viewModel.timeLeftMillis.observeAsState(9L)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftMillis)
    val seconds = (TimeUnit.MILLISECONDS.toSeconds(timeLeftMillis) - (minutes * 60))
    val repeatTime by remember { mutableIntStateOf(room.repeatTime) }
    if(repeatTime > 0 ) {
        LaunchedEffect(repeatTime) {
            viewModel.startCounter(repeatTime)
        }
    }
    val totalPrice by remember { mutableIntStateOf(room.totalPrice) }
    val bingoPrice by remember { mutableIntStateOf(room.bingoPrice) }
    val roomId by remember { mutableStateOf(room.roomId) }
    var activeUser by remember { mutableIntStateOf(room.activeUser) }
    val roomName by remember { mutableStateOf(room.roomName) }
    val isPremium by remember { mutableStateOf(room.isPremium) }
    activeUser = webSocketAppState.activeUserState.value
    Surface(modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, top = 8.dp)
        .clickable {
            navController.navigate("gameScreen/$roomId")
        }, color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp)
    ) {
        Box(modifier = Modifier
            .size(350.dp, 175.dp)
            .clickable
            {
                navController.navigate("gameScreen/$roomId")
            }, contentAlignment = Alignment.TopStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "background image",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
            )
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.5f))
                .clickable {
                    navController.navigate("gameScreen/$roomId")
                }
            )
            Column(modifier = Modifier
                .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) { // sayaç row'u
                    if(isPremium == "yes"){
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.weight(2f)) {
                            Image(painter = painterResource(id = R.drawable.king), contentDescription = "vip icon", Modifier.size(25.dp))
                            Text(
                                text = roomName,
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(myFont),
                                modifier = Modifier,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .weight(1f)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier
                                .padding(0.dp))
                            Text(if(timeLeftMillis == 9L)"$repeatTime dk." else "$minutes dk.", color = Color.White, modifier = Modifier, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Image(painter = painterResource(id = R.drawable.clock), contentDescription = null, modifier = Modifier
                                .size(30.dp), colorFilter = ColorFilter.tint(Color.White))
                        }
                    }
                    else
                    {
                        Text(
                            text = roomName,
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(2f),
                            fontWeight = FontWeight.Bold
                        )
                        Row(modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.background), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                            Text("")
                            Text(if(timeLeftMillis == 9L)"$repeatTime:00" else "$minutes:$seconds", color = Color.Black, modifier = Modifier, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Image(painter = painterResource(id = R.drawable.clock), contentDescription = null, modifier = Modifier
                                .weight(1f)
                                .size(30.dp), colorFilter = ColorFilter.tint(Color.White))
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                        Text(
                            text = "Toplam Ödül",
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(myFont),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$totalPrice",
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(myFont),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                        Text(
                            text = "Kart Sayısı",
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(myFont),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$maxCard",
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(myFont),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                        Text(
                            text = "Oyuncu sayısı",
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(myFont),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$maxUser/$activeUser",
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(myFont),
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                    .defaultMinSize(minHeight = 70.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)) {
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                        Text(
                            text = "1. Çinko",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(myFont),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.White)
                        Spacer(modifier = Modifier.padding(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                            Text(
                                text = "$firstLinePrice",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(myFont),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, modifier = Modifier
                                .size(25.dp)
                                .padding(top = 3.dp))
                        }
                    }
                    Divider(color = Color.White, modifier = Modifier
                        .height(75.dp)
                        .width(1.dp))
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                        Text(
                            text = "2. Çinko",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(myFont),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.White)
                        Spacer(modifier = Modifier.padding(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                            Text(
                                text = "$secondLinePrice",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(myFont),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, modifier = Modifier
                                .size(25.dp)
                                .padding(top = 3.dp))
                        }

                    }
                    Divider(color = Color.White, modifier = Modifier
                        .height(75.dp)
                        .width(1.dp))
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                        Text(
                            text = "Tombala",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(myFont),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(2.dp))
                        Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.White)
                        Spacer(modifier = Modifier.padding(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                            Text(
                                text = "$bingoPrice",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(myFont),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                            Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, modifier = Modifier
                                .size(25.dp)
                                .padding(top = 3.dp))
                        }
                    }
                }
            }
        }
    }
}
fun findMinutesUntilNextInterval(time: Calendar, interval: Int): Long {
    val minutesOfHour = time.get(Calendar.MINUTE)
    val currentMinuteSlot = (minutesOfHour / interval) * interval
    val timeUntilNextInterval = interval - (minutesOfHour - currentMinuteSlot)
    return timeUntilNextInterval.toLong()
}

fun saveLastSpinDate(context: Context) {
    val sharedPreferences = context.getSharedPreferences("SPIN_WHEEL_PREFS", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putLong("LAST_SPIN_DATE", System.currentTimeMillis())
    editor.apply()
}

fun didSpinToday(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("SPIN_WHEEL_PREFS", Context.MODE_PRIVATE)
    val lastSpinTime = sharedPreferences.getLong("LAST_SPIN_DATE", -1)

    if (lastSpinTime == -1L) {
        return false
    }

    val lastSpinCalendar = Calendar.getInstance().apply { timeInMillis = lastSpinTime }
    val currentCalendar = Calendar.getInstance()

    return (lastSpinCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
            lastSpinCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR))
}