package com.ilgijjan.domain.billing.presentation

import com.ilgijjan.domain.billing.domain.StoreProduct
import io.swagger.v3.oas.annotations.media.Schema
import kotlin.collections.map

data class ReadProductsResponse(
    @field:Schema(description = "상품 목록")
    val productList: List<ProductItem>
) {
    companion object {
        fun from(storeProducts: List<StoreProduct>): ReadProductsResponse {
            val items = storeProducts.map { sp ->
                ProductItem(
                    storeProductId = sp.storeProductId,
                    noteAmount = sp.product.noteAmount,
                    price = sp.product.price
                )
            }
            return ReadProductsResponse(items)
        }
    }
}

data class ProductItem(
    @field:Schema(description = "스토어 등록 상품 ID", example = "notes_100")
    val storeProductId: String,

    @field:Schema(description = "지급 음표 수", example = "100")
    val noteAmount: Int,

    @field:Schema(description = "가격", example = "4500")
    val price: Long
)
