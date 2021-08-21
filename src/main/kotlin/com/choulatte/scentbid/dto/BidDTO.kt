package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.Bid
import java.util.*

data class BidDTO(
    val bidIdx: Long? = null,
    val productIdx: Long,
    val userIdx: Long,
    private val accountIdx: Long,
    private val biddingPrice: Long,
    private var processingStatus: Bid.StatusType,
    private var holdingId: Long? = null,
    val recordedDate: Date,
    private val lastModifiedDate: Date,
    private var expiredDate: Date
) {
    fun toEntity() : Bid =
        Bid(
            bidIdx = this.bidIdx,
            productIdx = this.productIdx,
            userIdx = this.userIdx,
            accountIdx = this.accountIdx,
            biddingPrice = this.biddingPrice,
            processingStatus = this.processingStatus,
            holdingIdx = this.holdingId,
            lastModifiedDate = Date(),
            expiredDate = this.expiredDate
        )

}
