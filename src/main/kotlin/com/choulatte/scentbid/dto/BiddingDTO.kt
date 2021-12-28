package com.choulatte.scentbid.dto

data class BiddingDTO(
    val productId: Long,
    val userId: Long,
    val price: Long,
    val accountId: Long,
    var holdingId: Long ?= null
) {
    fun toAccountHoldingClearReqDTO(): AccountHoldingClearReqDTO =
        AccountHoldingClearReqDTO(
            accountId = this.accountId,
            holdingId = this.holdingId!!,
            userId = this.userId
        )
}
