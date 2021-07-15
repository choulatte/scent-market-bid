package com.choulatte.scentbid.application

import com.choulatte.scentbid.dto.BidDTO
import java.util.*

interface BidService {
    fun getBidListByProduct(productId: Long): List<BidDTO>
    fun getBidListByProductAndBiddingTime(productId: Long, biddingTime: Date): List<BidDTO>

}