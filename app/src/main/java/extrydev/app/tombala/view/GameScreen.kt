package extrydev.app.tombala.view

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import extrydev.app.tombala.R
import extrydev.app.tombala.model.RoomDetailResponseCard
import extrydev.app.tombala.model.RoomDetailResponseDetail
import extrydev.app.tombala.util.Constants.S3_URL
import extrydev.app.tombala.viewmodel.GameViewModel
import extrydev.app.tombala.websocket.ChatMessage
import extrydev.app.tombala.websocket.WebSocketAppState
import extrydev.app.tombala.websocket.WebSocketListener
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.times
import coil.compose.rememberAsyncImagePainter
import extrydev.app.tombala.util.LocalWebSocketAppState
import extrydev.app.tombala.util.NavigationBarItems
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, roomId: String, viewModel : GameViewModel = hiltViewModel()) {
    val webSocketState = LocalWebSocketAppState.current
    val webSocket = remember { webSocketState.webSocket }
    val listener = remember { webSocketState.listener }
    var userId by remember { mutableStateOf("") }
    var assignCheck by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit){
        viewModel.getUserInfo("tr")
        viewModel.getRoomDetails("tr",roomId)
    }
    if(viewModel.completedCheck.collectAsState().value){
        userId = viewModel.userInfo.collectAsState().value!!.userId
        if(!assignCheck)
        {
            listener.assignUser(webSocket,userId,roomId)
            assignCheck = true
        }
    }
    val timeLeftMillis by viewModel.timeLeftMillis.observeAsState(9L)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeftMillis)
    var repeatTime by remember { mutableIntStateOf(0) }
    LaunchedEffect(repeatTime) {
        viewModel.startCounter(repeatTime)
    }
    val userInfo = viewModel.userInfo.collectAsState().value
    var currentMessage by remember { mutableStateOf<ChatMessage?>(null) }
    var chatContentCheck by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false)}
    if(currentMessage!=null && userId != "") {
        if(currentMessage!!.userId != userId && !chatContentCheck)
            isVisible = true
    }
    val selectedNumbers = remember { mutableStateListOf<Int>() }
    val navigationBarItems = remember { NavigationBarItems.values() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val defValue = 0
    var selectedIndex by remember { mutableIntStateOf(getInitialIndex(currentRoute,defValue)) }
    LaunchedEffect(currentRoute) {
        selectedIndex = getInitialIndex(currentRoute,selectedIndex)
    }
    val firstLineTriggered = listener.firstLineTriggered.collectAsState().value
    val secondLineTriggered = listener.secondLineTriggered.collectAsState().value
    val thirdLineTriggered = listener.thirdLineTriggered.collectAsState().value
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    var isPopUpEnabled by remember { mutableStateOf(false) }
    var isWinnerPopUpEnabled by remember { mutableStateOf(false) }
    var bingoPrice by remember { mutableIntStateOf(0) }
    var roomName by remember { mutableStateOf("") }
    var isPremium by remember { mutableStateOf("") }
    var firstLinePrice by remember { mutableIntStateOf(0) }
    var secondLinePrice by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val roomDetailValue = viewModel.roomDetail.collectAsState().value
    var cards by remember { mutableStateOf(roomDetailValue) }
    val completedCheckFromRoom by viewModel.completedCheckForRoom.collectAsState()
    var messageFromTextField by remember { mutableStateOf("") }
    if (roomDetailValue != null) {
        cards = roomDetailValue
        bingoPrice = roomDetailValue.bingoPrice
        roomName = roomDetailValue.roomName
        repeatTime = roomDetailValue.repeatTime
        isPremium = roomDetailValue.isPremium
        firstLinePrice = roomDetailValue.firstLinePrice
        secondLinePrice = roomDetailValue.secondLinePrice
    }
    val number by listener.receivedNumber
    val drawnNumbers = remember { mutableStateListOf<Int>() }
    LaunchedEffect(key1 = number) {
        if (number != 0 && !drawnNumbers.contains(number)) {
            drawnNumbers.add(number)
            selectedNumbers.add(number)
        }
        else if(number == 0 && drawnNumbers.isNotEmpty()) {
            drawnNumbers.clear()
        }
        if(drawnNumbers.size >= 8){
            drawnNumbers.removeAt(0)
        }
    }
    val errorTriggered = listener.errorTriggered.collectAsState().value
    val errorMessage = listener.errorMessage.collectAsState().value
    if(errorTriggered){
        LaunchedEffect(errorTriggered){
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    val messages = listener.receivedMessages.value
    LaunchedEffect(key1 = messages){
        if(messages.isNotEmpty())
            currentMessage = messages.last()
    }
    var konfettiCheck by remember { mutableStateOf(false) }
    var konfettiCheck2 by remember { mutableStateOf(false) }
    var konfettiCheck3 by remember { mutableStateOf(false) }
    val firstLineWinner = listener.firstLineWinnerListNames.collectAsState().value
    val secondLineWinner = listener.secondLineWinnerListNames.collectAsState().value
    val thirdLineWinner = listener.thirdLineWinnerListNames.collectAsState().value
    val roomDeleted = listener.roomDeleted.collectAsState().value
    val roomNeedRefresh = listener.roomNeedRefresh.collectAsState().value
    if(roomDeleted){
        Toast.makeText(context,"Oyun Bitti.",Toast.LENGTH_SHORT).show()
        listener.reset()
        viewModel.resetViewModel()
        navController.navigate("homeScreen"){
            popUpTo("gameScreen" )
            launchSingleTop = false
        }
    }
    if(roomNeedRefresh){
        Toast.makeText(context,"Oyun Bitti.",Toast.LENGTH_SHORT).show()
        listener.reset()
        viewModel.resetViewModel()
        navController.navigate("homeScreen"){
            popUpTo("gameScreen" )
            launchSingleTop = false
        }
    }
    if(completedCheckFromRoom){
        if(viewModel.errorMessageFromRoom.collectAsState().value != ""){
            navController.navigate("homeScreen"){
                popUpTo("gameScreen" ){inclusive = true}
            }

        }
        if(roomDetailValue != null)
        {
            if(roomDetailValue.oldDrawnNumbers != null){
                roomDetailValue.oldDrawnNumbers.forEach {
                    if(!selectedNumbers.contains(it) && it != 0)
                        selectedNumbers.add(it)
                }
            }
        }
    }
    DisposableEffect(Unit) {
        if(userId != "")
        {
            listener.assignUser(webSocket, userId, roomId)
        }
        onDispose {
//            webSocket.close(WebSocketListener.NORMAL_CLOSE_STATUS, "Room Composable disposed")
        }
    }
    if(isWinnerPopUpEnabled)
    {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { isWinnerPopUpEnabled = false },
            text = {
                   Column {
                       Text(text = "1. Çinko kazananları:", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                       if(roomDeleted || roomNeedRefresh)
                       {
                           Text(text = "")
                       }
                       else
                       {
                           if(firstLineTriggered){
                               if(firstLineWinner.isNotEmpty()){
                                   firstLineWinner.forEach {
                                       Text(text = it.winners, color = Color.Black, fontSize = 15.sp)
                                   }
                               }
                               else{
                                   Text(text = "Bu kart satılmadı.", color = Color.Black, fontSize = 12.sp)
                               }
                           }
                       }
                       Text(text = "2. Çinko kazananları:", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                       if(roomDeleted || roomNeedRefresh){
                           Text(text = "")
                       }
                       else
                       {
                           if(secondLineTriggered){
                               if(secondLineWinner.isNotEmpty()){
                                   secondLineWinner.forEach {
                                       Text(text = it.winners, color = Color.Black, fontSize = 15.sp)
                                   }
                               }
                               else{
                                   Text(text = "Bu kart satılmadı.", color = Color.Black, fontSize = 12.sp)
                               }
                           }
                       }
                       Text(text = "3. Çinko kazananları:", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                       if(roomDeleted || roomNeedRefresh)
                       {
                           Text(text = "")
                       }
                       else
                       {
                           if(thirdLineTriggered){
                               if(thirdLineWinner.isNotEmpty()){
                                   thirdLineWinner.forEach {
                                       Text(text = it.winners, color = Color.Black, fontSize = 15.sp)
                                   }
                               }
                               else{
                                   Text(text = "Bu kart satılmadı.", color = Color.Black, fontSize = 12.sp)
                               }
                           }
                       }
                   }

            },
            confirmButton = {
                Button(onClick = { isWinnerPopUpEnabled = false }) {
                    Text(text = "Kapat")
                }
            }
        )
    }
    if(isPopUpEnabled)
    {
        AlertDialog(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = { isPopUpEnabled = false },
            text = {
                BallGrid(selectedNumbers)
            },
            confirmButton = {
                Button(onClick = { isPopUpEnabled = false }) {
                    Text(text = "Kapat")
                }
            }
        )
    }
    Scaffold(topBar = {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.End, modifier = Modifier
                .fillMaxWidth()) { //coin
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
        Box(modifier = Modifier.fillMaxSize()){
            Column(modifier = Modifier
                .padding(pad)
                .fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .weight(1f)
                                .defaultMinSize(minHeight = 35.dp)
                                .background(MaterialTheme.colorScheme.primary),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if(isPremium == "yes")
                            {
                                Image(painter = painterResource(id = R.drawable.king), contentDescription = "vip icon",
                                    Modifier
                                        .size(25.dp).weight(0.5f))
                                Text(
                                    text = roomName,
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            else
                            {
                                Text(
                                    text = roomName,
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                if (timeLeftMillis == 9L) "$repeatTime dk sonra yeni oyun başlayacak." else "$minutes dk sonra yeni oyun başlayacak.",
                                color = Color.Black,
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Image(
                                painter = painterResource(id = R.drawable.clock),
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(0.5f)
                                    .size(30.dp)
                            )
                        }
                    }
                    Spacer(Modifier.padding(top= 20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                        .defaultMinSize(minHeight = 70.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable {
                            isWinnerPopUpEnabled = !isWinnerPopUpEnabled
                        }
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary)) {

                        Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)) {
                            Text(
                                text = "1. Çinko",
                                color = Color.White,
                                fontSize = 16.sp,
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
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                                Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, modifier = Modifier
                                    .size(25.dp)
                                    .padding(top = 3.dp))
                                Icon(painter = painterResource(id = R.drawable.check), contentDescription = null, modifier = Modifier
                                    .size(25.dp)
                                    .padding(top = 3.dp), tint =
                                if(!roomDeleted || !roomNeedRefresh){
                                    if(firstLineTriggered) Color.Green else Color.White
                                }
                                    else
                                        Color.White)
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
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                                Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, modifier = Modifier
                                    .size(25.dp)
                                    .padding(top = 3.dp))
                                Icon(painter = painterResource(id = R.drawable.check), contentDescription = null, modifier = Modifier
                                    .size(25.dp)
                                    .padding(top = 3.dp), tint = if(secondLineTriggered) Color.Green else Color.White)
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
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                                Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, modifier = Modifier
                                    .size(25.dp)
                                    .padding(top = 3.dp))
                                Icon(painter = painterResource(id = R.drawable.check), contentDescription = null, modifier = Modifier
                                    .size(25.dp)
                                    .padding(top = 3.dp), tint = if(thirdLineTriggered) Color.Green else Color.White)
                            }
                        }
                    }
                    Spacer(Modifier.padding(top= 10.dp))
                    Row(modifier= Modifier
                        .clickable { isPopUpEnabled = !isPopUpEnabled }
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF000F91),
                                    Color(0xFFFFFFFF),
                                    Color(0xFFFF6C28)
                                )
                            )
                        )
                        .fillMaxWidth()
                        .height(50.dp)
                        .clipToBounds()) {
                        val scope = rememberCoroutineScope()
                        val startValue = 0f
                        val endValue = 360f
                        if(drawnNumbers.isNotEmpty()){
                            val animatedValues = List(drawnNumbers.size) { index ->
                                val animationDelay = index * 100L
                                var animationProgress by remember { mutableFloatStateOf(startValue) }

                                LaunchedEffect(key1 = index, key2 = drawnNumbers[index]) {
                                    scope.launch {
                                        delay(animationDelay)
                                        animate(
                                            initialValue = startValue,
                                            targetValue = endValue,
                                            animationSpec = tween(500)
                                        ) { value, _ ->
                                            animationProgress = value
                                        }
                                    }
                                }
                                animationProgress
                            }
                            drawnNumbers.forEachIndexed { index, number ->
                                TombalaBall2(ballDiameter = 50f, ballColor = Color(167, 53, 0, 255), number = "$number", rotationDegree = animatedValues[index])
                            }
                        }
                    }
                    Spacer(Modifier.padding(top= 10.dp))
                    Column {
                        if(cards != null)
                            if (userInfo != null) {
                                TombalaCardList(roomId, webSocketState, cards!!, userInfo.coins)
                            }
                    }

                        Spacer(Modifier.padding(top = 15.dp))
                        Column(modifier = Modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = {
                                chatContentCheck = true
                            }, modifier = Modifier.size(width = 100.dp, height = 32.dp), colors = ButtonDefaults.buttonColors()) {
                                Text(text = "Sohbet")
                            }
                        }

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter){
                            AnimatedNavigationBar(selectedIndex = selectedIndex,
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
                                                selectedIndex = item.ordinal
                                                navController.navigate(item.route)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(painter = painterResource(id = item.icon), contentDescription = "Icon",
                                            modifier = Modifier.size(26.dp),
                                            tint = if(selectedIndex == item.ordinal) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                        }

                }
            }
        }
        if(chatContentCheck){
            Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier
                .fillMaxSize()
                .height(275.dp)){
                Column(verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary)
                    .height(height = 275.dp)
                    .padding(top = 10.dp)) {
                    Button(onClick = {
                        chatContentCheck = false
                    } , modifier = Modifier
                        .size(width = 100.dp, height = 32.dp)
                        .weight(0.5f), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                        Text(text = "Sohbet", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp)
                    }
                    val listState = rememberLazyListState()
                    LaunchedEffect(key1 = messages) {
                        if(messages.isNotEmpty())
                            listState.animateScrollToItem(index = messages.size - 1)
                    }
                    LazyColumn(modifier = Modifier.weight(2f), state = listState) {
                        items(messages) { message ->

                            MessageRow(message = message)
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
                        var labelCheck by remember { mutableStateOf(false) }
                        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.fillMaxWidth())
                        TextField(value = messageFromTextField, onValueChange ={ messageFromTextField = it }, keyboardActions = KeyboardActions(
                            onDone = {
                                if(messageFromTextField != ""){
                                    listener.sendMessage(webSocket,roomId,messageFromTextField)
                                    messageFromTextField = ""
                                }
                                else
                                {
                                    Toast.makeText(context, "Boş mesaj gönderilemez!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ), keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done), shape = RoundedCornerShape(10.dp), trailingIcon = {
                            IconButton(onClick = {
                                if(messageFromTextField != ""){
                                    listener.sendMessage(webSocket,roomId,messageFromTextField)
                                    messageFromTextField = ""
                                }
                                else
                                {
                                    Toast.makeText(context, "Boş mesaj gönderilemez!", Toast.LENGTH_SHORT).show()
                                }
                            }){
                                Icon(painter = painterResource(id = R.drawable.send), contentDescription = null)
                            }
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                isFocused = it.isFocused
                                labelCheck = it.isFocused
                            }, colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colorScheme.onSurface, containerColor = Color.Transparent, cursorColor = MaterialTheme.colorScheme.onSurface, disabledLabelColor = Color.Transparent, disabledTrailingIconColor = Color.Transparent, disabledIndicatorColor = Color.Transparent, disabledLeadingIconColor = Color.Transparent, disabledPlaceholderColor = Color.Transparent, disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledSupportingTextColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, focusedLabelColor = Color.Transparent, focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface, focusedLeadingIconColor = Color.Transparent, focusedSupportingTextColor = Color.Transparent, unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface, unfocusedLabelColor = MaterialTheme.colorScheme.onSurface, unfocusedIndicatorColor = Color.Transparent, unfocusedLeadingIconColor = Color.Transparent, unfocusedSupportingTextColor = Color.Transparent)
                            , label = {
                                if(!labelCheck){
                                    Text("Mesajınızı giriniz..", color = MaterialTheme.colorScheme.onSurface)
                                }
                            })
                    }
                }
            }
        }
        else {
            if (isVisible) {
                Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 125.dp)){
                    ChatSnackbar(message = currentMessage!!) {
                        currentMessage = null
                        isVisible = false
                    }
                }

            }
        }

        val sellTrigger = listener.sellCardTriggered.collectAsState().value
        val userIdFromSocket = listener.userIdFromSocket.collectAsState().value
        val failControl = listener.failControl.collectAsState().value
        if(sellTrigger){
            LaunchedEffect(sellTrigger) {
                if (userIdFromSocket == userId) { Toast.makeText(context, "Kart Başarıyla Satın Alındı!", Toast.LENGTH_SHORT).show() }
            }
        }
        if(failControl){
            LaunchedEffect(key1 = failControl){
                navController.navigate("homeScreen")
            }
        }
        if(firstLineTriggered){
            LaunchedEffect(key1 = firstLineTriggered){
                konfettiCheck = true
            }
        }
        if(secondLineTriggered){
            LaunchedEffect(key1 = secondLineTriggered){
                konfettiCheck2 = true
            }
        }
        if(thirdLineTriggered){
            LaunchedEffect(key1 = thirdLineTriggered){
                konfettiCheck3 = true
            }
        }
        if(konfettiCheck){
            KonfettiUI()
            LaunchedEffect(key1 = Unit){
                delay(10000L)
                konfettiCheck = false

            }
        }
        if(konfettiCheck2){
            KonfettiUI2()
            LaunchedEffect(key1 = Unit){
                delay(10000L)
                konfettiCheck2 = false

            }
        }
        if(konfettiCheck3){
            KonfettiUI3()
            LaunchedEffect(key1 = Unit){
                delay(10000L)
                konfettiCheck3 = false
            }
        }
    })
}

@Composable
fun MessageRow(message: ChatMessage) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(25.dp))
        .padding(top = 5.dp, bottom = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        Spacer(modifier = Modifier.padding(start = 5.dp))
        if(message.profileImage !=null && message.profileImage != "")
        {
            Image(painter = rememberAsyncImagePainter(model = getS3ImageUrl(message.profileImage)), contentDescription = "profile image", modifier = Modifier
                .size(30.dp)
                .clip(CircleShape))
        }
        else
        {
            Image(painter = painterResource(id = R.drawable.nopp), contentDescription = "profile image", modifier = Modifier
                .size(30.dp)
                .clip(CircleShape))
        }
        Spacer(modifier = Modifier.padding(start = 5.dp))
        Text(text = "${message.username}:", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.padding(start = 5.dp))
        Text(text = message.message, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
    }
}

@Composable
fun ChatSnackbar(message: ChatMessage, onDismiss: () -> Unit) {
    LaunchedEffect(key1 = message) {
        delay(3000L)
        onDismiss()
    }
    Card(colors = CardDefaults.cardColors(containerColor = Color(255, 255, 255, 217), contentColor = Color.Transparent), modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(25.dp))
        .padding(bottom = 50.dp, start = 50.dp, end = 50.dp)) {
        MessageRow(message = message)
    }
}
fun getS3ImageUrl(s3key: String) :String{
    return "$S3_URL$s3key"
}

@Composable
fun TombalaBall2(ballDiameter: Float, ballColor: Color, number: String, rotationDegree: Float = 0f) {
    Box(
        modifier = Modifier
            .size(ballDiameter.dp)
            .padding(start = 10.dp)
            .rotate(rotationDegree)
        ,contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            drawCircle(
                color = ballColor.copy(alpha = 0.3f),
                radius = radius - 2f,
                center = center.copy(x = center.x + 4f, y = center.y + 4f)
            )
            drawCircle(
                color = ballColor,
                radius = radius - 4f,
                center = center
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = (radius - 10f),
                center = center.copy(x = center.x - 10f, y = center.y - 10f)
            )
        }
        Text(
            text = AnnotatedString(number),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            ),
            fontFamily = FontFamily.Default,
        )
    }
}

@Composable
fun TombalaBall(ballDiameter: Float, ballColor: Color, number: String, isSelected: Boolean ) {
    val actualTopColor = if (isSelected) Color(3, 0, 0, 255) else ballColor
    Box(
        modifier = Modifier
            .size(ballDiameter.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            // Dış gölge
            drawCircle(
                color = actualTopColor.copy(alpha = 0.3f),
                radius = radius - 2f,
                center = center.copy(x = center.x + 4f, y = center.y + 4f)
            )
            // Ana top rengi
            drawCircle(
                color = actualTopColor,
                radius = radius - 4f,
                center = center
            )
            // Iç gölge (üst sol köşe)
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = (radius - 10f),
                center = center.copy(x = center.x - 10f, y = center.y - 10f)
            )
        }
        Text(
            text = AnnotatedString(number),
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            ),
            fontFamily = FontFamily.Default,
        )
    }
}

@Composable
fun BallGrid(selectedNumbers: MutableList<Int>) {
    val numList = (1..90).toList()
    Column {
        for (rowIndex in 0 until 9) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (colIndex in 0 until 10) {
                    val index = rowIndex * 10 + colIndex
                    if (index < numList.size) {
                        val number = numList[index]
                        val isSelected = selectedNumbers.contains(number)
                        TombalaBall(
                            ballDiameter = 25f,
                            ballColor = Color(167, 53, 0, 255),
                            number = number.toString(),
                            isSelected = isSelected
                        )
                    } else {
                        Spacer(modifier = Modifier.width(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TombalaCard(roomId: String, appState: WebSocketAppState, card: RoomDetailResponseCard, userCoin: Int, viewModel: GameViewModel = hiltViewModel()) {
    val myFont = Font(R.font.cherrybomb)
    val context = LocalContext.current
    val webSocket = remember { appState.webSocket }
    val listener = remember { appState.listener }
    val soldInfo  = listener.soldCardsState.collectAsState().value[card.cardId]
    var isThisCardWinner by remember { mutableStateOf(false) }
    val cardId = card.cardId
    val firstWinners = listener.firstLineWinnerList.collectAsState().value
    val secondWinners = listener.secondLineWinnerList.collectAsState().value
    val thirdWinners = listener.thirdLineWinnerList.collectAsState().value
    val isSelled = card.isSelled
    LaunchedEffect(key1 = listener.sellCardTriggered.collectAsState().value){
        viewModel.getUserInfo("tr")
    }
    firstWinners.forEach {
        if(cardId == it.cardId)
            isThisCardWinner = true
    }
    secondWinners.forEach {
        if(cardId == it.cardId)
            isThisCardWinner = true
    }
    thirdWinners.forEach {
        if(cardId == it.cardId)
            isThisCardWinner = true
    }
    val drawnNumbers = listener.drawnNumbers.value
    if(isThisCardWinner) {
        Row(Modifier.fillMaxWidth()) {
            Row(modifier = Modifier
                .weight(4f)
                .padding(start = 4.dp, end = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier
                    .border(3.dp, Color.Black)
                    .background(Color.Black)
                    .padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                    TombalaLine(card.firstLine, card.bgColor.toComposeColor(), card.bgColor.toComposeColor(), drawnNumbers, listener)
                    Divider(thickness = 3.dp, color = Color.Black, modifier = Modifier.fillMaxWidth())
                    TombalaLine(card.secondLine, card.bgColor.toComposeColor(), card.bgColor.toComposeColor(), drawnNumbers, listener)
                    Divider(thickness = 3.dp, color = Color.Black, modifier = Modifier.fillMaxWidth())
                    TombalaLine(card.thirdLine, card.bgColor.toComposeColor(), card.bgColor.toComposeColor(), drawnNumbers, listener)
                }
            }
            if(isSelled == "yes"){
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                    if (card.profileImage != "" && card.profileImage != null) {
                        Image(painter = rememberAsyncImagePainter(model = getS3ImageUrl(card.profileImage)
                        ), contentDescription = "profile photo",
                            Modifier
                                .size(50.dp)
                                .clip(CircleShape), contentScale = ContentScale.FillBounds)
                    } else {
                        Image(painter = painterResource(id = R.drawable.nopp), contentDescription = "profile photo",
                            Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(15.dp)))
                    }
                    Spacer(modifier = Modifier.padding(0.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Text(text = card.username, color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily(myFont))
                    }
                }
            }
            else
            {
                if(soldInfo != null)
                {
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(top = 10.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                        if (soldInfo.profileImage != null) {
                            Image(painter = rememberAsyncImagePainter(model = getS3ImageUrl(soldInfo.profileImage)
                            ), contentDescription = "profile photo",
                                Modifier
                                    .size(50.dp)
                                    .clip(CircleShape), contentScale = ContentScale.FillBounds)
                        } else {
                            Image(painter = painterResource(id = R.drawable.nopp), contentDescription = "profile photo",
                                Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(15.dp)))
                        }
                        Spacer(modifier = Modifier.padding(0.dp))
                        Row(Modifier.fillMaxWidth()) {
                            Text(text = soldInfo.username, color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily(myFont))
                        }
                    }
                }
                else {
                    Row(modifier = Modifier
                        .padding(top = 15.dp)
                        .weight(1f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {
                                if(userCoin >= card.cardPrice)
                                {
                                    listener.onSell(webSocket, roomId, card.cardId)
                                }
                                else
                                    Toast.makeText(context, "Yetersiz bakiye",Toast.LENGTH_SHORT).show()
                            }, colors = ButtonDefaults.buttonColors(containerColor = Color(255, 255, 255, 255))
                        ) {
                            Column {
                                Row {
                                    Text(text = "${card.cardPrice}", color = Color.Black, fontSize = 17.sp, fontFamily = FontFamily(myFont))
                                    Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, Modifier.size(25.dp))
                                }
                                Text(text = "AL", color = Color.Black, fontSize = 13.sp, fontFamily = FontFamily(myFont))
                            }
                        }
                    }
                }
            }
        }
    }
    else
    {
        Row(Modifier.fillMaxWidth()) {
            Row(modifier = Modifier
                .weight(4f)
                .padding(start = 4.dp, end = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier
                    .border(3.dp, Color.Black)
                    .background(Color.Black)
                    .padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                    TombalaLine(card.firstLine, card.bgColor.toComposeColor(), card.bgColor.toComposeColor(), drawnNumbers, listener)
                    Divider(thickness = 3.dp, color = Color.Black, modifier = Modifier.fillMaxWidth())
                    TombalaLine(card.secondLine, card.bgColor.toComposeColor(), card.bgColor.toComposeColor(), drawnNumbers, listener)
                    Divider(thickness = 3.dp, color = Color.Black, modifier = Modifier.fillMaxWidth())
                    TombalaLine(card.thirdLine, card.bgColor.toComposeColor(), card.bgColor.toComposeColor(), drawnNumbers, listener)
                }
            }
            if(isSelled == "yes"){
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(top = 10.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                    if(card.profileImage != "" && card.profileImage != null)
                    {
                        Image(painter = rememberAsyncImagePainter(model = getS3ImageUrl(card.profileImage)), contentDescription = "profile photo",
                            Modifier
                                .size(60.dp)
                                .clip(CircleShape), contentScale = ContentScale.FillBounds)
                    }
                    else
                    {
                        Image(painter = painterResource(id = R.drawable.nopp), contentDescription = "profile photo",
                            Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(15.dp)))
                    }
                    Spacer(modifier = Modifier.padding(0.dp))
                    Text(text = card.username, color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily(myFont), modifier = Modifier.fillMaxWidth())
                }
            }
            else
            {
                if(soldInfo != null){
                    Column(modifier = Modifier
                        .weight(1f)
                        .padding(top = 10.dp), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                        if(soldInfo.profileImage != null)
                        {
                            Image(painter = rememberAsyncImagePainter(model = getS3ImageUrl(soldInfo.profileImage)), contentDescription = "profile photo",
                                Modifier
                                    .size(60.dp)
                                    .clip(CircleShape), contentScale = ContentScale.FillBounds)
                        }
                        else
                        {
                            Image(painter = painterResource(id = R.drawable.nopp), contentDescription = "profile photo",
                                Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(15.dp)))
                        }
                        Spacer(modifier = Modifier.padding(0.dp))
                        Text(text = soldInfo.username, color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily(myFont), modifier = Modifier.fillMaxWidth())
                    }
                }
                else {
                    Row(modifier = Modifier
                        .padding(top = 15.dp)
                        .weight(1f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Button(onClick = {
                            if(userCoin >= card.cardPrice)
                            {
                                listener.onSell(webSocket, roomId, card.cardId)
                            }
                            else
                                Toast.makeText(context, "Yetersiz bakiye",Toast.LENGTH_SHORT).show()
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(255, 255, 255, 255))) {
                            Column {
                                Row {
                                    Text(text = "${card.cardPrice}", color = Color.Black, fontSize = 17.sp, fontFamily = FontFamily(myFont))
                                    Image(painter = painterResource(id = R.drawable.coin), contentDescription = null, Modifier.size(25.dp))
                                }
                                Text(text = "AL", color = Color.Black, fontSize = 13.sp, fontFamily = FontFamily(myFont))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TombalaLine(numbers: List<Int>, textColor: Color, bgColor: Color, drawnNumbers: List<Int>, listener: WebSocketListener, viewModel: GameViewModel = hiltViewModel()) {
    val roomDetailValue = viewModel.roomDetail.collectAsState().value
    val oldDrawnNumbers = roomDetailValue?.oldDrawnNumbers
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black)) {
        numbers.forEach { num ->
            Box(modifier = Modifier
                .size(30.dp)
                .background(if (num == -1) Color.White else bgColor), contentAlignment = Alignment.Center) {
                if (num != -1) {
                    if(!oldDrawnNumbers.isNullOrEmpty() && oldDrawnNumbers.contains(num) || drawnNumbers.contains(num)){
                        Image(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Heart Icon",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    else {
                        Text(text = "$num", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
enum class DeviceType {
    PHONE, TABLET_7_INCH, TABLET_10_INCH
}

@Composable
fun determineDeviceType(screenWidthDp: Int): DeviceType {
    return when {
        screenWidthDp < 600 -> DeviceType.PHONE
        screenWidthDp in 600..719 -> DeviceType.TABLET_7_INCH
        else -> DeviceType.TABLET_10_INCH
    }
}

@Composable
fun TombalaCardList(roomId: String, appState: WebSocketAppState, response: RoomDetailResponseDetail, userCoin: Int) {
    val screenWidthPx = LocalContext.current.resources.displayMetrics.widthPixels
    val screenWidthPxForPhone = screenWidthPx * 0.8
    val screenWidthPxFor7 = screenWidthPx * 0.5
    val screenWidthPxFor10 = screenWidthPx * 0.26
    val configuration = LocalConfiguration.current
    val screenWidthChoose = when(determineDeviceType(configuration.screenWidthDp)) {
        DeviceType.PHONE -> screenWidthPxForPhone
        DeviceType.TABLET_7_INCH -> screenWidthPxFor7
        DeviceType.TABLET_10_INCH -> screenWidthPxFor10
    }
    val lineHeight = 40.dp
    val spacerHeight = 4.dp
    val cardHeight = 3 * lineHeight + 2 * spacerHeight
    val totalHeight = 2 * cardHeight + 2 * 8.dp
    LazyColumn(modifier = Modifier
        .height(totalHeight + 100.dp)
        .width(screenWidthChoose.dp)) {
        items(response.cards) { card ->
            TombalaCard(roomId, appState, card = card, userCoin)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun KonfettiUI(viewModel: GameViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val paradeCalled = remember { mutableStateOf(false) }
    when (state) {
        is GameViewModel.State.Idle -> {
            if (!paradeCalled.value) {
                viewModel.parade()
                paradeCalled.value = true
            }
        }
        is GameViewModel.State.Started -> {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = (state as GameViewModel.State.Started).party,
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        viewModel.ended()
                    }
                }
            )
        }
    }
}
@Composable
fun KonfettiUI2(viewModel: GameViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val paradeCalled = remember { mutableStateOf(false) }
    when (state) {
        is GameViewModel.State.Idle -> {
            if (!paradeCalled.value) {
                viewModel.parade()
                paradeCalled.value = true
            }
        }
        is GameViewModel.State.Started -> {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = (state as GameViewModel.State.Started).party,
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        viewModel.ended()
                    }
                }
            )
        }
    }
}
@Composable
fun KonfettiUI3(viewModel: GameViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val paradeCalled = remember { mutableStateOf(false) }
    when (state) {
        is GameViewModel.State.Idle -> {
            if (!paradeCalled.value) {
                viewModel.rain()
                paradeCalled.value = true
            }
        }
        is GameViewModel.State.Started -> {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = (state as GameViewModel.State.Started).party,
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        viewModel.ended()
                    }
                }
            )
        }
    }
}