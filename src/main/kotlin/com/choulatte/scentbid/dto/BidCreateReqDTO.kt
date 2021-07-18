package com.choulatte.scentbid.dto

import com.choulatte.scentbid.domain.ProcessingStatusType
import java.util.*

data class BidCreateReqDTO(
    private val productId: Long,
    private val userId: Long,
    private val biddingPrice: Long,
    private val expirationDate: Date
){
    fun toBidDTO(): BidDTO {
        return BidDTO(
            productId = this.productId,
            userId = this.userId,
            biddingPrice = this.biddingPrice,
            recordedDate = Date(),
            lastModifiedDate = Date(),
            expiredDate = this.expirationDate,
            processingStatus = ProcessingStatusType.INITIALIZED
        )
    }
}
