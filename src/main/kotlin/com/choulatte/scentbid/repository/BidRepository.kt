package com.choulatte.scentbid.repository

import com.choulatte.scentbid.domain.Bid
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


interface BidRepository : CrudRepository<Bid, Long> {
    fun findAllByProductId(productId: Long): List<Bid>
    fun findTopByProductIdOrderByBiddingPriceDesc(productId: Long): Bid?=null
    fun findByHoldingId(holdingId: Long): Bid
    fun findTopByProductIdAndProcessingStatusOrderByRecordedDateDesc(productId: Long, processingStatusType: Bid.StatusType): Bid?= null
}