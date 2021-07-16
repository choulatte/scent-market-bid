package com.choulatte.scentbid.application

import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.domain.StatusType
import java.util.*

interface BidService {
    fun getBidList(): List<BidDTO>
    fun getBidListByProduct(productId: Long): List<BidDTO>
    fun getBidListByProductAndBiddingTime(productId: Long, biddingTime: Date): List<BidDTO>
    fun getBidListByProductAndStatus(productId: Long, status: StatusType): List<BidDTO>

    fun createBid(bidDTO: BidDTO): BidDTO
    fun updateBidStatus(bidDTO: BidDTO, status: StatusType): BidDTO
}