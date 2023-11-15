package extrydev.app.tombala.view

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import extrydev.app.tombala.util.NavigationBarItems
import extrydev.app.tombala.viewmodel.MoneyViewModel

@SuppressLint("AutoboxingStateCreation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentOutScreen(navController: NavController, viewModel: MoneyViewModel = hiltViewModel()) {
    navController.context
    var nameSurname by remember { mutableStateOf(TextFieldValue()) }
    var bankName by remember { mutableStateOf(TextFieldValue()) }
    var iban by remember { mutableStateOf(TextFieldValue()) }
    var price by remember { mutableStateOf(TextFieldValue()) }
    val focusManager = LocalFocusManager.current
    val navigationBarItems = remember { NavigationBarItems.values() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val defValue = 1
    val selectedIndex = remember { mutableStateOf(getInitialIndex(currentRoute,defValue)) }
    LaunchedEffect(currentRoute) {
        selectedIndex.value = getInitialIndex(currentRoute, selectedIndex.value)
    }
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background)
    val result by viewModel.result.collectAsState("")
    val context = LocalContext.current
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
            Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(top = 20.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.Start) {
                        Text(text = "İsim soyisim:", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(text = "Banka adı:", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(text = "Iban:", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(text = "Çekilecek tutar:", fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(text = "Ödeme yöntemi:", fontSize = 16.sp)
                    }
                    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
                        TextField(value = nameSurname, onValueChange = { nameSurname = it}, shape = RoundedCornerShape(10.dp), singleLine = true, colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, cursorColor = Color.Black, textColor = Color.Black))
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(value = bankName, onValueChange = { bankName = it}, shape = RoundedCornerShape(10.dp), singleLine = true, colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, cursorColor = Color.Black, textColor = Color.Black))
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(value = iban, onValueChange = { iban = it}, shape = RoundedCornerShape(10.dp), singleLine = true, colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, cursorColor = Color.Black, textColor = Color.Black))
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(value = price, onValueChange = { price = it}, shape = RoundedCornerShape(10.dp), singleLine = true, colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, cursorColor = Color.Black, textColor = Color.Black))
                        Spacer(modifier = Modifier.height(5.dp))
                        PaymentMenu(modifier = Modifier)
                    }
                }
                Spacer(modifier = Modifier.padding(top = 20.dp))
                Button(onClick = {
                    if(nameSurname.text != "")
                    {
                        if(bankName.text != "")
                        {
                            if(iban.text!="")
                            {
                                if(price.text!="")
                                {
                                    if(viewModel.selectedMethod.value!!.paymentMethodId!="")
                                    {
                                        viewModel.moneyTransfer("tr","withdraw", viewModel.selectedMethod.value!!.paymentMethodId,price.text.toDouble(),"",bankName.text,nameSurname.text,iban.text,"")
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "Bir ödeme yöntemi seçiniz!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else
                                {
                                    Toast.makeText(context, "Ödenecek Tutarı giriniz!", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else
                            {
                                Toast.makeText(context, "İban giriniz!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "Banka adını giriniz!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "İsim soyisim giriniz!", Toast.LENGTH_SHORT).show()
                    }
                }, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(colors))) {
                    Text(text = "Gönder")
                }
                if(result !=""){
                    Toast.makeText(context, "İşleminiz başarıyla gerçekleşti!", Toast.LENGTH_SHORT).show()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMenu(viewModel: MoneyViewModel = hiltViewModel(), modifier: Modifier) {
    val paymentMethods = viewModel.paymentMethods.collectAsState().value
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier){
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = viewModel.selectedMethod.value?.pmName ?: "Seçiniz",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.White, cursorColor = Color.Black, textColor = Color.Black)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                paymentMethods.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.pmName) },
                        onClick = {
                            viewModel.selectedMethod.value = item  // ViewModel'i güncelle
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
