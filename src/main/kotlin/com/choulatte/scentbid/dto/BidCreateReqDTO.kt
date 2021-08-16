package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.Bid
import java.util.*

data class BidCreateReqDTO(
    val productId: Long,
    val userId: Long,
    val accountId: Long,
    val biddingPrice: Long,
    val expiredDate: Date
){
    fun toBidDTO(): BidDTO =
        BidDTO(
            productId = this.productId,
            userId = this.userId,
            accountId = this.accountId,
            biddingPrice = this.biddingPrice,
            recordedDate = Date(),
            lastModifiedDate = Date(),
            expiredDate = this.expiredDate,
            processingStatus = Bid.StatusType.INITIALIZED
        )

}
