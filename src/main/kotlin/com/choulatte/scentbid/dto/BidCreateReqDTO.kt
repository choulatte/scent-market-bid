package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.exception.UserNotValid
import java.util.*

data class BidCreateReqDTO(
    val productId: Long,
    val userId: Long,
    val accountId: Long,
    val biddingPrice: Long,
    val expiredDate: Date,
    val reqTime: Date
){
    fun toBidDTO(userId: Long, holdingId: Long): BidDTO {
        if(this.userId != userId) throw UserNotValid()
        return BidDTO(
            productId = this.productId,
            userId = userId,
            accountId = this.accountId,
            biddingPrice = this.biddingPrice,
            recordedDate = Date(),
            lastModifiedDate = Date(),
            expiredDate = this.expiredDate,
            processingStatus = Bid.StatusType.INITIALIZED,
            holdingId = holdingId
        )
    }


    fun toBiddingDTO(holdingId: Long): BiddingDTO =
        BiddingDTO(
            productId = this.productId,
            userId = this.userId,
            price = this.biddingPrice,
            accountId = this.accountId,
            holdingId = holdingId
        )

    fun toAccountHoldReqDTO(): AccountHoldReqDTO =
        AccountHoldReqDTO(
            accountId = this.accountId,
            biddingPrice = this.biddingPrice,
            expiredDate = this.expiredDate,
            userId = this.userId
        )
}
