package extrydev.app.tombalam.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import extrydev.app.tombalam.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen(navController: NavController,viewModel: MainViewModel = hiltViewModel()) {
    val currentPageIndex by viewModel.pageIndex.collectAsState()
    val onboardingPages by viewModel.onboardingPages.collectAsState()
    val currentPage = onboardingPages.getOrNull(currentPageIndex)
    if (currentPage != null) {
        Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)) {
            Spacer(modifier = Modifier.padding(0.dp))
            val painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(data = currentPage.image).build()
            )
            Image(painter = painter, contentDescription = "OnBoardingImage", modifier = Modifier.clip(shape = RoundedCornerShape(10.dp)))
            Text(currentPage.text, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(start = 31.dp, end = 31.dp))
            Spacer(modifier = Modifier.padding(0.dp))
            Spacer(modifier = Modifier.padding(0.dp))
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                RadioButtonGroup(currentIndex = currentPageIndex, totalCount = onboardingPages.count())
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Button(
                    onClick = {
                        viewModel.viewModelScope.launch {
                            if (currentPageIndex == onboardingPages.lastIndex) {
                                viewModel.setOnboardingCompleted()
                                navController.navigate("registerScreen") {
                                    popUpTo("onBoardingScreen") { inclusive = true }
                                }
                            } else {
                                viewModel.nextPage()
                            }
                        }
                    }, Modifier.defaultMinSize(200.dp,40.dp)
                ) {
                    Text(text = currentPage.nextBtnText)
                }
                Spacer(modifier = Modifier.padding(top = 8.dp))
                if(currentPage.skipBtnText != null)
                {
                    Button(
                        onClick = {
                            viewModel.viewModelScope.launch {
                                viewModel.setOnboardingCompleted()
                                navController.navigate("registerScreen") {
                                    popUpTo("onBoardingScreen") { inclusive = true }
                                }
                            }
                        }, Modifier.defaultMinSize(200.dp,40.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White)

                    ) {
                        Text(text = currentPage.skipBtnText, color = Color.Black)
                    }
                }
            }
            Spacer(modifier = Modifier.padding(0.dp))
        }
    }
}
@Composable
fun RadioButtonGroup(currentIndex: Int, totalCount: Int, viewModel: MainViewModel = hiltViewModel()) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (i in 0 until totalCount) {
            RadioButton(
                selected = (currentIndex == i),
                onClick =
                {
                    viewModel.viewModelScope.launch {
                        viewModel.setPageIndex(i)
                    }
                }, modifier = Modifier.size(35.dp)
            )
            if (i < totalCount - 1) {
                Spacer(modifier = Modifier.width(3.dp))
            }
        }
    }
}