package com.suganth.infotimes.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.suganth.infotimes.ui.models.Source
import java.io.Serializable

/**
 * converted a response model into a entity ,
 * where we can save the news which we fetched from api
 */
@Entity(
    tableName = "articles"
)
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String,
    val content: String,
    val description: String,
    val publishedAt: String,
    /**
     * here the problem is room only handle primitive datatypes not custom classes (Source),
     * for that we need to create type converter to tell room ,it should interpret source class
     * and to convert that source class into a string
     */
    val source: Source,
    val title: String,
    val url: String,
    val urlToImage: String
    /**
     * Article is not a primitive data type like int or float, since it is
     * a more complex datatype we need to mark this class as Serializable
     * which tells kotlin that we want to pass this class b/w several fragments
     * with the navigation components so kotlin will do this behind the scenes
     * for us
     * And so declare this article as an argument we need to add argument at news_nav_graph
     */
): Serializable