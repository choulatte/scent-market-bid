package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.Bid
import java.util.*

data class BidDTO(
    val bidId: Long? = null,
    val productId: Long? = null,
    val userId: Long? = null,
    val biddingPrice: Long? = null,
    val timestamp: Date? = null
) {
    fun toEntity() : Bid {
        return Bid(
            bidId = bidId,
            productId = productId,
            userId = userId,
            biddingPrice = biddingPrice,
            timestamp = Date()
        )
    }
}
