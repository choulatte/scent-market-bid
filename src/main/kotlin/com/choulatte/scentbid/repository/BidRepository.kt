package com.choulatte.scentbid.repository

import com.choulatte.scentbid.domain.Bid
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    fun findAllByProductId(productIdx: Long): List<Bid>
    fun findTopByProductIdOrderByBiddingPriceDesc(productIdx: Long): Bid?=null
    fun findByHoldingId(holdingIdx: Long): Bid
    fun findTopByProductIdAndProcessingStatusOrderByRecordedDateDesc(productId: Long, processingStatusType: Bid.StatusType): Bid?= null
}