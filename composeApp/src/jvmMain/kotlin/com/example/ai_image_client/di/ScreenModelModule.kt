package com.example.ai_image_client.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.example.ai_image_client.viewmodel.GalleryScreenModel
import com.example.ai_image_client.viewmodel.GenerationScreenModel
import com.example.ai_image_client.viewmodel.SettingsScreenModel

val screenModelModule = module {
    factoryOf(::GalleryScreenModel)
    factoryOf(::GenerationScreenModel)
    singleOf(::SettingsScreenModel) // singleton — используется в App.kt через koinInject
}
