package com.example.ai_image_client.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import com.example.ai_image_client.repository.AssetRepository
import com.example.ai_image_client.repository.GenerationRepository
import com.example.ai_image_client.repository.ImageTypeRepository
import com.example.ai_image_client.repository.StyleRepository
import com.example.ai_image_client.repository.mock.MockAssetRepository
import com.example.ai_image_client.repository.mock.MockGenerationRepository
import com.example.ai_image_client.repository.mock.MockImageTypeRepository
import com.example.ai_image_client.repository.mock.MockStyleRepository

val repositoryModule = module {
    singleOf(::MockImageTypeRepository) bind ImageTypeRepository::class
    singleOf(::MockStyleRepository) bind StyleRepository::class
    singleOf(::MockAssetRepository) bind AssetRepository::class
    singleOf(::MockGenerationRepository) bind GenerationRepository::class
}
