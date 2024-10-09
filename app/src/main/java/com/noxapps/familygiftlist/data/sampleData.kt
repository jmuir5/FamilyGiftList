package com.noxapps.familygiftlist.data

import java.time.LocalDate

class sampleData {
    val previewGift = Gift(
        0,
        "Example Gift",
        "This is an example of a gift",
        "www.google.com",
        5000,
        true,
        LocalDate.of(2024, 9, 10),
        "Joey Bonzo",
        false,
        LocalDate.now(),
        "Joey Bonzo",
        "",

        )
    fun getGift():Gift{
        return previewGift
    }

    val previewGiftList = GiftList(
        0,
        "0",
        "Example Creator",
        "Example List",
        "This is an example of a gift list used for testing purposes",
    )

    companion object {
        val gift:Gift = Gift(
            0,
            "Example Gift",
            "This is an example of a gift",
            "www.google.com",
            5000,
            true,
            LocalDate.of(2024, 9, 10),
            "Joey Bonzo",
            false,
            LocalDate.now(),
            "Joey Bonzo",
            "",

            )
        val list = GiftList(
            0,
            "0",
            "Example Creator",
            "Example List",
            "This is an example of a gift list used for testing purposes",
        )
        val user = User(
            "0",
            "Empty user",
            "Empty",
            "User",
            LocalDate.now()
        )

        val nullGift = GiftList(
            -1,
            "empty",
            "empty",
            "empty list",
            "empty placeholder list"
        )

        val nullListWithGifts = ListWithGifts(
            nullGift,
            emptyList())

    }
}