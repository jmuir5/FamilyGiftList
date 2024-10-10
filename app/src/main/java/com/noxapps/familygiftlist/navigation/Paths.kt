package com.noxapps.familygiftlist.navigation

sealed class Paths(val Path:String) {
    object Home: Paths("Home")
    object Login: Paths("Login")
    object MyLists: Paths("MyLists")
    object MyGifts: Paths("MyGifts")
    object SingleList: Paths("SingleList")
    object SingleGift: Paths("SingleGift")
}