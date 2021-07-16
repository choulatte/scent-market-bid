package com.choulatte.scentbid.repository

import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.domain.StatusType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    fun findAllByProductId(productId: Long): List<Bid>
    fun findAllByProductIdAndTimestampBefore(productId: Long, biddingTime: Date): List<Bid>
    fun findAllByProductIdAndStatus(productId: Long, status: StatusType): List<Bid>
}