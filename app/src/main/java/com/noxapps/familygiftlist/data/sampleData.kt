package com.noxapps.familygiftlist.data

import java.time.LocalDate
import java.time.Instant
import java.util.Date


class sampleData {
    val previewGift = Gift(
        0,
        "exampleUser",
        "Example Gift",
        "This is an example of a gift",
        "www.google.com",
        5000,
        true,
        LocalDate.of(2024, 9, 10).toString(),
        "Joey Bonzo",
        false,
        LocalDate.now().toString(),
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
            "exampleUser",
            "Example Gift",
            "This is an example of a gift",
            "www.google.com",
            5000,
            true,
            LocalDate.of(2024, 9, 10).toString(),
            "Joey Bonzo",
            false,
            LocalDate.now().toString(),
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
            LocalDate.now().toString()
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
            LocalDate.now().toString()
        )

        val nullList = GiftList(
            -1,
            "empty",
            "empty",
            "empty list",
            "empty placeholder list"
        )

        val nullGift:Gift = Gift(
            0,
            "null User",
            "Null Gift",
            "Null Gift",
            "",
            0,
            false,
            LocalDate.now().toString(),
            "",
            false,
            LocalDate.now().toString(),
            "",
            ""
            )

        val nullListWithGifts = ListWithGifts(
            nullList,
            emptyList()
        )

        val nullGiftWithLists = GiftWithLists(
            nullGift,
            emptyList()
        )

        val nullListOfGifts = emptyList<Gift>()
        val nullListOfLists = emptyList<GiftList>()

    }
}