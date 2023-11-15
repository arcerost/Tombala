package extrydev.app.tombala.util

import extrydev.app.tombala.R

enum class NavigationBarItems(val icon: Int, val route: String) {
    Homepage(icon = R.drawable.homepage_icon, route = "homeScreen"),
    Payment(icon = R.drawable.payment, route = "paymentScreen"),
    Help(icon = R.drawable.help, route = "helpScreen"),
    Profile(icon = R.drawable.profile, route = "editProfileScreen")
}