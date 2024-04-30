package extrydev.app.tombalam.view

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import extrydev.app.tombalam.R
import extrydev.app.tombalam.util.NavigationBarItems
import extrydev.app.tombalam.viewmodel.ShowPaymentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowPaymentScreen(navController: NavController, viewModel: ShowPaymentViewModel = hiltViewModel()) {
    val navigationBarItems = remember { NavigationBarItems.values() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val defValue = 0
    val selectedIndex = remember { mutableStateOf(getInitialIndex(currentRoute,defValue)) }
    val focusManager = LocalFocusManager.current
    val userInfo = viewModel.userInfo.collectAsState().value
    val history = viewModel.historyList.collectAsState().value
    LaunchedEffect(key1 = Unit){
        viewModel.getUserInfo("tr")
        viewModel.getHistory("tr")
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
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(Color.White, RoundedCornerShape(10.dp))) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp)) {
                    Icon(painter = painterResource(id = R.drawable.payments), contentDescription = "payment icon",
                        Modifier
                            .size(30.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.padding(start = 15.dp))
                    Text(text = "Geçmiş ödemeler(Para Çekme)", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, modifier = Modifier
                        .fillMaxWidth())
                }
            }
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start, modifier = Modifier.background(Color.White, RoundedCornerShape(10.dp)).defaultMinSize(minHeight = 150.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 25.dp)) {
                    Text(text = "Tarih", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "Ödenen Tutar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                history.forEach { item ->
                    if(item.action == "withdraw")
                    {
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)){
                            Text(text = transformDate(item.createDate), modifier = Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(text = "${item.price}", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(Color.White, RoundedCornerShape(10.dp))) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp)) {
                    Icon(painter = painterResource(id = R.drawable.payments), contentDescription = "payment icon",
                        Modifier
                            .size(30.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.padding(start = 15.dp))
                    Text(text = "Geçmiş ödemeler(Para Yatırma)", color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, modifier = Modifier
                        .fillMaxWidth())
                }
            }
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start, modifier = Modifier.background(Color.White, RoundedCornerShape(10.dp)).defaultMinSize(minHeight = 150.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth().padding(horizontal = 25.dp)) {
                    Text(text = "Tarih", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "Ödenen Tutar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                history.forEach { item ->
                    if(item.action == "deposit")
                    {
                        Spacer(modifier = Modifier.padding(top = 5.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)){
                            Text(text = transformDate(item.createDate), modifier = Modifier.weight(2f), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Text(text = "${item.price}", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                    }
                }
            }
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
}


@SuppressLint("NewApi")
fun transformDate(datex: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val date = LocalDate.parse(datex, inputFormatter)

    val turkish = Locale("tr", "TR")
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy EEEE", turkish)

    return date.format(outputFormatter)
}