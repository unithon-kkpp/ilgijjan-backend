    package com.ilgijjan.domain.billing.domain

    enum class StoreType {
        ONE_STORE, GOOGLE, APPLE;

        companion object {
            fun fromPath(path: String): StoreType? {
                val formattedPath = path.uppercase().replace("-", "_")

                return entries.find { it.name == formattedPath }
            }
        }
    }
