package com.example.ai_image_client.repository.mock

import com.example.ai_image_client.repository.AssetData
import com.example.ai_image_client.repository.AssetRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset

class MockAssetRepository : AssetRepository {

    private val assets = listOf(
        // Фотография (typeId=2)
        AssetData(101, 2, 2, "", OffsetDateTime.of(2026, 2, 28, 14, 30, 0, 0, ZoneOffset.UTC)),
        AssetData(102, 2, 3, "", OffsetDateTime.of(2026, 2, 28, 14, 31, 0, 0, ZoneOffset.UTC)),
        AssetData(103, 2, 6, "", OffsetDateTime.of(2026, 2, 27, 10, 0, 0, 0, ZoneOffset.UTC)),
        AssetData(104, 2, 2, "", OffsetDateTime.of(2026, 2, 26, 9, 15, 0, 0, ZoneOffset.UTC)),
        AssetData(105, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 45, 0, 0, ZoneOffset.UTC)),
        AssetData(106, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 44, 0, 0, ZoneOffset.UTC)),
        AssetData(107, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 43, 0, 0, ZoneOffset.UTC)),
        AssetData(1051, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 42, 0, 0, ZoneOffset.UTC)),
        AssetData(1052, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 41, 0, 0, ZoneOffset.UTC)),
        AssetData(1053, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 40, 0, 0, ZoneOffset.UTC)),
        AssetData(1054, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 39, 0, 0, ZoneOffset.UTC)),
        AssetData(1055, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 38, 0, 0, ZoneOffset.UTC)),
        AssetData(1056, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 37, 0, 0, ZoneOffset.UTC)),
        AssetData(1057, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 36, 0, 0, ZoneOffset.UTC)),
        AssetData(1058, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 35, 0, 0, ZoneOffset.UTC)),
        AssetData(1050, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 34, 0, 0, ZoneOffset.UTC)),
        AssetData(1095, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 33, 0, 0, ZoneOffset.UTC)),
        AssetData(1105, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 32, 0, 0, ZoneOffset.UTC)),
        AssetData(1205, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 31, 0, 0, ZoneOffset.UTC)),
        AssetData(1305, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 30, 0, 0, ZoneOffset.UTC)),
        AssetData(1405, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 29, 0, 0, ZoneOffset.UTC)),
        AssetData(1505, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 28, 0, 0, ZoneOffset.UTC)),
        AssetData(1605, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 27, 0, 0, ZoneOffset.UTC)),
        AssetData(1705, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 26, 0, 0, ZoneOffset.UTC)),
        AssetData(1805, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 25, 0, 0, ZoneOffset.UTC)),
        AssetData(1905, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 24, 0, 0, ZoneOffset.UTC)),
        AssetData(2105, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 23, 0, 0, ZoneOffset.UTC)),
        AssetData(3105, 2, 4, "", OffsetDateTime.of(2026, 2, 25, 16, 22, 0, 0, ZoneOffset.UTC)),
        // Иконка (typeId=3)
        AssetData(201, 3, 6, "", OffsetDateTime.of(2026, 2, 28, 12, 0, 0, 0, ZoneOffset.UTC)),
        AssetData(202, 3, 3, "", OffsetDateTime.of(2026, 2, 27, 11, 30, 0, 0, ZoneOffset.UTC)),
        AssetData(203, 3, 5, "", OffsetDateTime.of(2026, 2, 26, 8, 0, 0, 0, ZoneOffset.UTC)),
        // Картина (typeId=4)
        AssetData(301, 4, 2, "", OffsetDateTime.of(2026, 2, 28, 15, 0, 0, 0, ZoneOffset.UTC)),
        AssetData(302, 4, 4, "", OffsetDateTime.of(2026, 2, 27, 14, 0, 0, 0, ZoneOffset.UTC)),
        AssetData(303, 4, 3, "", OffsetDateTime.of(2026, 2, 26, 13, 0, 0, 0, ZoneOffset.UTC)),
        // Абстракция (typeId=5)
        AssetData(401, 5, 6, "", OffsetDateTime.of(2026, 2, 28, 16, 0, 0, 0, ZoneOffset.UTC)),
    )

    override suspend fun getByFilter(
        imageTypeId: Long,
        styleId: Long?,
        page: Int,
        size: Int,
    ): List<AssetData> {
        val filtered = assets
            .filter { it.imageTypeId == imageTypeId }
            .let { list ->
                if (styleId != null) list.filter { it.styleId == styleId }
                else list
            }
            .sortedByDescending { it.id }

        val fromIndex = (page * size).coerceAtMost(filtered.size)
        val toIndex = ((page + 1) * size).coerceAtMost(filtered.size)
        return filtered.subList(fromIndex, toIndex)
    }
}
