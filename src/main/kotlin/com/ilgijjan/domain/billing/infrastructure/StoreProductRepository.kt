package com.ilgijjan.domain.billing.infrastructure

import com.ilgijjan.domain.billing.domain.StoreProduct
import com.ilgijjan.domain.billing.domain.StoreType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface StoreProductRepository : JpaRepository<StoreProduct, String> {

    @Query("select sp from StoreProduct sp join fetch sp.product where sp.storeProductId = :storeProductId")
    fun findByStoreProductIdWithProduct(storeProductId: String): Optional<StoreProduct>

    @Query("""
        SELECT sp FROM StoreProduct sp 
        JOIN FETCH sp.product p 
        WHERE sp.storeType = :storeType 
        AND p.isActive = true 
        ORDER BY p.noteAmount ASC
    """)
    fun findAllByStoreType(storeType: StoreType): List<StoreProduct>
}
