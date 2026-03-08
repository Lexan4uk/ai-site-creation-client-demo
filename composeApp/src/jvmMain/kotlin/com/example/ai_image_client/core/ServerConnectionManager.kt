package com.example.ai_image_client.core

/**
 * Управляет подключением к серверу.
 *
 * В мок-версии прототипа этот класс не используется.
 * При подключении к реальному серверу — добавить зависимость client-utils
 * и раскомментировать тело.
 *
 * Использование (будущее):
 *   val manager = ServerConnectionManager(preferences)
 *   val types = manager.getController(IImageTypeController::class.java).getAll()
 */
class ServerConnectionManager(
    private val preferences: AppPreferences,
) {
    private var currentUrl: String? = null

    // TODO: раскомментировать при добавлении client-utils
    // private var clientUtil: ClientUtil? = null
    // private val controllerCache = mutableMapOf<Class<*>, Any>()

    @Synchronized
    fun <T> getController(clazz: Class<T>): T {
        throw UnsupportedOperationException(
            "ServerConnectionManager не активен в мок-режиме. " +
            "Добавьте client-utils зависимость и реализацию."
        )
    }
}
