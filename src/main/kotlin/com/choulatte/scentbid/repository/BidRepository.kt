package com.choulatte.scentbid.repository

import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.domain.ProcessingStatusType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    fun findAllByProductId(productId: Long): List<Bid>
    fun findByHoldingId(holdingId: Long): Bid
    fun findAllByProductIdAndRecordedDateBeforeAndProcessingStatus(productId: Long, recordedDate: Date, processingStatusType: ProcessingStatusType): List<Bid>
}