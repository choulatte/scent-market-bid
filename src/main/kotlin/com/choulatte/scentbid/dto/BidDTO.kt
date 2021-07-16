package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.domain.StatusType
import org.springframework.data.annotation.LastModifiedDate
import java.util.*

data class BidDTO(
    val bidId: Long? = null,
    val productId: Long? = null,
    val userId: Long? = null,
    val biddingPrice: Long? = null,
    var status: StatusType,
    var holdingId: Long,
    val initializedDate: Date,
    val lastModifiedDate: Date? = null,
    var expiredDate: Date
) {
    fun toEntity() : Bid {
        return Bid(
            bidId = this.bidId,
            productId = this.productId,
            userId = this.userId,
            biddingPrice = this.biddingPrice,
            status = this.status,
            holdingId = this.holdingId,
            lastModifiedDate = Date(),
            expiredDate = this.expiredDate
        )
    }
}
