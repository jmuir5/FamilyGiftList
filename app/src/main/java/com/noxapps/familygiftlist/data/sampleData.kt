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
        val sampleGift:Gift = Gift(
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
        val sampleList = GiftList(
            0,
            "0",
            "Example Creator",
            "Example List",
            "This is an example of a gift list used for testing purposes",
        )
        val sampleUser = User(
            "0",
            "example@gmail.com",
            "Example",
            "User",
            LocalDate.now()
        )

        val sampleListOfGifts = (0..10).map{sampleGift}
        val sampleListOfLists = (0..10).map{sampleList}

        val sampleListWithGifts = ListWithGifts(sampleList, sampleListOfGifts)
        val sampleListOfListsWithGifts = (0..10).map{ sampleListWithGifts}

        val nullUser = User(
            "-1",
            "null email",
            "Null",
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
            emptyList()
        )

        val nullListOfGifts = emptyList<Gift>()
        val nullListOfLists = emptyList<GiftList>()

    }
}