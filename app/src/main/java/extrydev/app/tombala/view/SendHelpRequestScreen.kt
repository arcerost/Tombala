package extrydev.app.tombala.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import extrydev.app.tombala.R
import extrydev.app.tombala.util.NavigationBarItems
import extrydev.app.tombala.viewmodel.SendHelpRequestViewModel

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendHelpRequestScreen(navController: NavController, viewModel: SendHelpRequestViewModel = hiltViewModel()) {
    var descriptionForHelpRequest by remember { mutableStateOf(TextFieldValue()) }
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background)
    val result by viewModel.result.collectAsState("")
    val context = LocalContext.current
    val navigationBarItems = remember { NavigationBarItems.values() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val defValue = 2
    val selectedIndex = remember { mutableStateOf(getInitialIndex(currentRoute, defValue)) }
    LaunchedEffect(currentRoute) {
        selectedIndex.value = getInitialIndex(currentRoute,selectedIndex.value)
    }
    Scaffold(topBar = {

    }, content = {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.secondary) {
        }
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.padding(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.padding(10.dp))
                Icon(Icons.Default.Info, contentDescription = "", tint = Color.Black)
                Spacer(modifier = Modifier.padding(10.dp))
                Text(text = "Bize sorununu bildir!", color = Color.Black, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.padding(10.dp))
            TextField(value = descriptionForHelpRequest, onValueChange = { tf ->
                descriptionForHelpRequest = tf
            }, colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, cursorColor = Color.Black, focusedIndicatorColor = Color.Black), modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp), leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.support),
                    contentDescription = null,
                    tint = Color.Black
                )
            })
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = {
                if(descriptionForHelpRequest.text!="")
                {
                    viewModel.sendHelpRequest("tr",descriptionForHelpRequest.text)
                }
                else{
                    Toast.makeText(context, "Açıklama alanı boş bırakılamaz.", Toast.LENGTH_SHORT).show()
                }
            }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(colors))) {
                Text(text = "Gönder", color = Color.White)
            }
            if(result != "")
            {
                Toast.makeText(context, "İsteğiniz başarıyla gönderildi.", Toast.LENGTH_SHORT).show()
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