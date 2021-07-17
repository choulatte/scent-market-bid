package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.domain.ProcessingStatusType
import java.util.*

data class BidDTO(
    private val bidId: Long? = null,
    private val productId: Long? = null,
    private val userId: Long? = null,
    private val biddingPrice: Long? = null,
    private var processingStatus: ProcessingStatusType,
    private var holdingId: Long,
    private val initializedDate: Date,
    private val lastModifiedDate: Date? = null,
    private var expiredDate: Date
) {
    fun toEntity() : Bid {
        return Bid(
            bidId = this.bidId,
            productId = this.productId,
            userId = this.userId,
            biddingPrice = this.biddingPrice,
            processingStatus = this.processingStatus,
            holdingId = this.holdingId,
            lastModifiedDate = Date(),
            expiredDate = this.expiredDate
        )
    }
}
