package extrydev.app.tombalam.util

import extrydev.app.tombalam.R

enum class NavigationBarItems(val icon: Int, val route: String) {
    Homepage(icon = R.drawable.homepage_icon, route = "homeScreen"),
    Payment(icon = R.drawable.payment, route = "paymentScreen"),
    Help(icon = R.drawable.help, route = "helpScreen"),
    Profile(icon = R.drawable.profile, route = "editProfileScreen")
}