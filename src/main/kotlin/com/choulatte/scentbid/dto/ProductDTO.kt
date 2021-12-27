package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.Product
import java.util.*

data class ProductDTO(
    val productId: Long,
    val startingDatetime: Date,
    val endingDatetime: Date,
    val startingPrice: Long
) {
    fun toEntity(): Product {
        return Product(
            productId = this.productId,
            startingDatetime = this.startingDatetime,
            endingDatetime = this.endingDatetime,
            startingPrice = this.startingPrice,
            biddingList = mutableListOf(),
            lastBidding = null
        )
    }
}
