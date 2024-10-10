package com.noxapps.familygiftlist.navigation

sealed class Paths(val Path:String) {
    object Home: Paths("Home")
    object MyList: Paths("MyList")
    object Login: Paths("Login")
    object SingleList: Paths("SingleList")
}