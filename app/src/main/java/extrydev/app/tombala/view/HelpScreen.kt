package extrydev.app.tombala.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import extrydev.app.tombala.util.NavigationBarItems

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavController) {
    val navigationBarItems = remember { NavigationBarItems.values() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val defValue = 2
    val selectedIndex = remember { mutableStateOf(getInitialIndex(currentRoute,defValue)) }
    LaunchedEffect(currentRoute) {
        selectedIndex.value = getInitialIndex(currentRoute, selectedIndex.value)
    }
    Scaffold(topBar = {},
        content = {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondary) {
            }
            Column(modifier = Modifier
                .padding(it)
                .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(top = 30.dp))
                Text(text = "Yardıma mı ihtiyacın var?")
                Spacer(modifier = Modifier.padding(top = 30.dp))
                Button(onClick = {
                    navController.navigate("sendHelpRequestScreen")
                }, modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(
                    255,
                    255,
                    255,
                    201
                )
                )) {
                    Text(text = "Bize sorununu bildir!", color = Color.Black)
                }
                Spacer(modifier = Modifier.padding(top = 15.dp))
                Button(onClick = {
                    navController.navigate("faqScreen")
                }, modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(
                    255,
                    255,
                    255,
                    201
                )
                )) {
                    Text(text = "Hazır soruların cevaplarını incele.", color = Color.Black)
                }
            }
    }, bottomBar = {
            AnimatedNavigationBar(selectedIndex = selectedIndex.value,
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