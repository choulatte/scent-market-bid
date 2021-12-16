package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.Bid
import java.util.*

data class BidCreateReqDTO(
    val productIdx: Long,
    val userIdx: Long,
    val accountIdx: Long,
    val biddingPrice: Long,
    val expiredDate: Date,
    val reqTime: Date
){
    fun toBidDTO(userIdx: Long): BidDTO =
        BidDTO(
            productIdx = this.productIdx,
            userIdx = userIdx,
            accountIdx = this.accountIdx,
            biddingPrice = this.biddingPrice,
            recordedDate = Date(),
            lastModifiedDate = Date(),
            expiredDate = this.expiredDate,
            processingStatus = Bid.StatusType.INITIALIZED
        )

}
