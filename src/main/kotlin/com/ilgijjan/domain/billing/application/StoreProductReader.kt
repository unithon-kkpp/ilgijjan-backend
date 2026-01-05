package com.ilgijjan.domain.billing.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.billing.domain.StoreProduct
import com.ilgijjan.domain.billing.domain.StoreType
import com.ilgijjan.domain.billing.infrastructure.StoreProductRepository
import org.springframework.stereotype.Component

@Component
class StoreProductReader(
    private val storeProductRepository: StoreProductRepository
) {
    fun getByStoreProductId(storeProductId: String): StoreProduct {
        return storeProductRepository.findByStoreProductIdWithProduct(storeProductId)
            .orElseThrow { CustomException(ErrorCode.PRODUCT_NOT_FOUND) }
    }

    fun findAllByStoreType(storeType: StoreType): List<StoreProduct> {
        return storeProductRepository.findAllByStoreType(storeType)
    }
}
