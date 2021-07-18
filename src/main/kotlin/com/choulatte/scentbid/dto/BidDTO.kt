package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.domain.ProcessingStatusType
import lombok.Getter
import java.util.*

data class BidDTO(
    val bidId: Long? = null,
    private val productId: Long,
    val userId: Long,
    private val biddingPrice: Long,
    private var processingStatus: ProcessingStatusType,
    private var holdingId: Long? = null,
    private val recordedDate: Date,
    private val lastModifiedDate: Date,
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
