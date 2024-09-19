package com.noxapps.familygiftlist

sealed class Paths(val Path:String) {
    object Home: Paths("Home")
    object MyList: Paths("MyList")
}