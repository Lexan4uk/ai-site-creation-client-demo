package com.example.ai_image_client.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import com.example.ai_image_client.core.AppPreferences
import com.example.ai_image_client.core.JvmAppPreferences

val appModule = module {
    singleOf(::JvmAppPreferences) bind AppPreferences::class
}
