package com.choulatte.scentbid.repository

import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.domain.ProcessingStatusType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    fun findAllByProductId(productId: Long): List<Bid>
    fun findAllByProductIdAndInitializedDateBefore(productId: Long, biddingTime: Date): List<Bid>
    fun findAllByProductIdAndProcessingStatus(productId: Long, processingStatusType: ProcessingStatusType): List<Bid>
}