package com.choulatte.scentbid.dto

data class BiddingDTO(
    val productId: Long,
    val userId: Long,
    val price: Long
) {
    @Override
    fun isEqual(biddingDTO: BiddingDTO) =
        this.productId == biddingDTO.productId && this.userId == biddingDTO.userId && this.price == biddingDTO.price
}
