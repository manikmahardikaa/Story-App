package com.dicoding.picodiploma.loginwithanimation.utils

import com.dicoding.picodiploma.loginwithanimation.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100){
            val story = ListStoryItem(
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1699029584215_eUSrsEnE.jpg",
                id = "story-$i",
                name = "author + $i",
                description = "Ini deskripsi $i",
                lat = -0.402,
                lon = 118.234,
                createdAt = "2023-11-03T16:45:28.052Z"
            )
            items.add(story)
        }
        return items
    }
}