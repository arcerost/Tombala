@file:Suppress("DEPRECATION")

package extrydev.app.tombalam.view

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.billingclient.api.BillingFlowParams
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import extrydev.app.tombalam.R
import extrydev.app.tombalam.util.NavigationBarItems
import extrydev.app.tombalam.viewmodel.PaymentViewModel
import kotlinx.coroutines.launch

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(navController: NavController, viewModel : PaymentViewModel = hiltViewModel()) {
    val cherryBombFont = Font(R.font.cherrybomb)
    val concertOneFont = Font(R.font.concertone)
    val navigationBarItems = remember { NavigationBarItems.values() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val defValue = 1
    val selectedIndex = remember { mutableStateOf(getInitialIndex(currentRoute,defValue)) }
    val userInfo = viewModel.userInfo.collectAsState().value
    val info = viewModel.coins.collectAsState().value
    val context = LocalContext.current
    val aylikSubPrice by viewModel.aylikSubPrice.collectAsState(initial = "")
    val aylikSubTitle by viewModel.aylikSubTitle.collectAsState(initial = "")
    val yillikSubPrice by viewModel.yillikSubPrice.collectAsState(initial = "")
    val yillikSubTitle by viewModel.yillikSubTitle.collectAsState(initial = "")
    val gunlukSubPrice by viewModel.gunlukSubPrice.collectAsState(initial = "")
    val gunlukSubTitle by viewModel.gunlukSubTitle.collectAsState(initial = "")
    var config by remember { mutableStateOf(false) }
    val purchases = viewModel.purchases.collectAsState().value
    val isNewPurchaseAcknowledged = viewModel.isNewPurchaseAcknowledged.collectAsState().value
    if(isNewPurchaseAcknowledged == true){
        LaunchedEffect(key1 = isNewPurchaseAcknowledged){
            Toast.makeText(context,"Satın alım başarıyla tamamlandı!", Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(key1 = Unit){
        viewModel.getUserInfo("tr")
        viewModel.getConfig("tr")
    }
    LaunchedEffect(key1 = config){
        viewModel.getUserInfo("tr")
    }
    LaunchedEffect(key1 = viewModel.changeCoinResponse.collectAsState().value){
        config = !config
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
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(1, 1, 1),
                                Color(83, 151, 254, 255),
                                Color(1, 1, 1)
                            )
                        ), shape = RoundedCornerShape(10.dp)
                    )) {
                Text(text = "Vip abonelik paketleri", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 36.sp, fontFamily = FontFamily(concertOneFont), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
            if (purchases == null || purchases.isEmpty()){
                Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                        .defaultMinSize(minHeight = 150.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF00A8),
                                    Color(0xFF5398FF)
                                )
                            ), shape = RoundedCornerShape(10.dp)
                        )
                        .padding(5.dp)
                        .clickable {
                            try {
                                viewModel.viewModelScope.launch {
                                    val productDetailsMap =
                                        viewModel.productWithProductDetails.value
                                    productDetailsMap
                                        ?.get("gunlukabonelik_")
                                        ?.let {
                                            val productDetailsParamsList = listOf(
                                                BillingFlowParams.ProductDetailsParams
                                                    .newBuilder()
                                                    .setProductDetails(it)
                                                    .setOfferToken("")
                                                    .build()
                                            )
                                            val flowParams = BillingFlowParams
                                                .newBuilder()
                                                .setProductDetailsParamsList(
                                                    productDetailsParamsList
                                                )
                                                .build()
                                            viewModel.launchBillingFlow(
                                                context as Activity,
                                                flowParams
                                            )
                                        }
                                }
                            } catch (e: Exception) {
                                Log.d("tombala", "hata: $e")
                            }
                        }) {
                        Text(text = gunlukSubTitle, fontSize = 14.sp, color = Color.White ,fontFamily = FontFamily(cherryBombFont))
                        Image(painter = painterResource(id = R.drawable.king), contentDescription = "subscription icon", Modifier.size(50.dp))
                        Text(text = gunlukSubPrice, fontSize = 20.sp, color = Color.White ,fontFamily = FontFamily(cherryBombFont))
                    }
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                        .defaultMinSize(minHeight = 150.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF00A8),
                                    Color(0xFF5398FF)
                                )
                            ), shape = RoundedCornerShape(10.dp)
                        )
                        .padding(5.dp)
                        .clickable {
                            try {
                                viewModel.viewModelScope.launch {
                                    if (viewModel.billingClient.isReady) {
                                        val productDetailsMap =
                                            viewModel.productWithProductDetails.value
                                        Log.d("tombala", "mapmapmampa: $productDetailsMap")
                                        productDetailsMap
                                            ?.get("aylikabonelik")
                                            ?.let {
                                                try {
                                                    val productDetailsParamsList = listOf(
                                                        BillingFlowParams.ProductDetailsParams
                                                            .newBuilder()
                                                            .setProductDetails(it)
                                                            .setOfferToken("")
                                                            .build()
                                                    )
                                                    val flowParams = BillingFlowParams
                                                        .newBuilder()
                                                        .setProductDetailsParamsList(
                                                            productDetailsParamsList
                                                        )
                                                        .build()
                                                    viewModel.launchBillingFlow(
                                                        context as Activity,
                                                        flowParams
                                                    )
                                                } catch (e: Exception) {
                                                    Log.d("tombala", "in-trycatch error: hata: $e")
                                                }
                                            }
                                    } else {
                                        Log.d("tombala", "billing client is not ready")
                                    }
                                }
                            } catch (e: Exception) {
                                Log.d("tombala", "trycatch error: hata :$e")
                            }
                        }) {
                        Text(text = aylikSubTitle, fontSize = 14.sp, color = Color.White ,fontFamily = FontFamily(cherryBombFont))
                        Image(painter = painterResource(id = R.drawable.king), contentDescription = "subscription icon", Modifier.size(50.dp))
                        Text(text = aylikSubPrice, fontSize = 20.sp, color = Color.White ,fontFamily = FontFamily(cherryBombFont))
                    }
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                        .defaultMinSize(minHeight = 150.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF00A8),
                                    Color(0xFF5398FF)
                                )
                            ), shape = RoundedCornerShape(10.dp)
                        )
                        .padding(5.dp)
                        .clickable {
                            try {
                                viewModel.viewModelScope.launch {
                                    val productDetailsMap =
                                        viewModel.productWithProductDetails.value
                                    productDetailsMap
                                        ?.get("yillikabonelik_")
                                        ?.let {
                                            val productDetailsParamsList = listOf(
                                                BillingFlowParams.ProductDetailsParams
                                                    .newBuilder()
                                                    .setProductDetails(it)
                                                    .setOfferToken("")
                                                    .build()
                                            )
                                            val flowParams = BillingFlowParams
                                                .newBuilder()
                                                .setProductDetailsParamsList(
                                                    productDetailsParamsList
                                                )
                                                .build()
                                            viewModel.launchBillingFlow(
                                                context as Activity,
                                                flowParams
                                            )
                                        }
                                }
                            } catch (e: Exception) {
                                Log.d("tombala", "hata :$e")
                            }
                        }) {
                        Text(text = yillikSubTitle, fontSize = 14.sp, color = Color.White ,fontFamily = FontFamily(cherryBombFont))
                        Image(painter = painterResource(id = R.drawable.king), contentDescription = "subscription icon", Modifier.size(50.dp))
                        Text(text = yillikSubPrice, fontSize = 20.sp, color = Color.White ,fontFamily = FontFamily(cherryBombFont))
                    }
                }
            }
            else{
                Text(text = "Abonelik Aktif!\n Bittiğinde tekrar abone olabilirsiniz.", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 28.sp, fontFamily = FontFamily(concertOneFont), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(1, 1, 1),
                                Color(254, 0, 167, 255),
                                Color(1, 1, 1)
                            )
                        ), shape = RoundedCornerShape(10.dp)
                    )) {
                Text(text = "Altın paketleri", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 36.sp, fontFamily = FontFamily(concertOneFont), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                info.forEach{ coinInfo ->
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                        .defaultMinSize(minHeight = 150.dp, minWidth = 100.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF00A8),
                                    Color(0xFF11215E)
                                )
                            ), shape = RoundedCornerShape(10.dp)
                        )
                        .padding(5.dp)
                        .clickable {
                            userInfo?.let {
                                if (userInfo.wallet >= coinInfo.price) {
                                    viewModel.sendMoney("tr", coinInfo.packageId)
                                    Toast
                                        .makeText(
                                            context,
                                            "İşlem başarıyla tamamlandı.",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else
                                    Toast
                                        .makeText(
                                            context,
                                            "Yetersiz Bakiye!",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                            }
                        }) {
                        Text(text = "${coinInfo.coin} coin", fontSize = 16.sp, color = Color.White ,fontFamily = FontFamily(cherryBombFont))
                        Image(painter = painterResource(id = R.drawable.sub), contentDescription = "subscription icon", Modifier.size(50.dp))
                        Text(text = "Altın yığını", fontSize = 13.sp, color = Color.White ,fontFamily = FontFamily(cherryBombFont))
                        Text(text = "${coinInfo.price} TL", fontSize = 13.sp, color = Color.White ,fontFamily = FontFamily(cherryBombFont))
                    }
                }
            }
            Spacer(modifier = Modifier.padding(bottom = 20.dp))
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

