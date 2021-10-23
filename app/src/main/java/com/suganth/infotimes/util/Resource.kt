package com.suganth.infotimes.util

import android.icu.text.Collator
import androidx.lifecycle.LiveData

/**
 * sealed class is like a kind of abstract class, but we can define which classes
 * are allowed to inherit
 *
 * we use this Resource class in every of our projects to wrap around our response
 * and able to handle success, error and loading state
 */
sealed class Resource<T>(
    val data: T ? =null,
    val message:String? = null
) {
    /**
     * These class are only allowed To inheriT
     */
    class Success<T>(data: T) :Resource<T>(data)
    class Error<T>(message: String, data: T ?= null): Resource<T>(data,message)
    class Loading<T> : Resource<T>()
}