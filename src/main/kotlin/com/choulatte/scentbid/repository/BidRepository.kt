package com.choulatte.scentbid.repository

import com.choulatte.scentbid.domain.Bid
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BidRepository : JpaRepository<Bid, Long> {
    fun findAllByProductId(productId: Long): List<Bid>
    fun findByHoldingId(holdingId: Long): Bid
    fun findByProductIdAndBiddingPrice(productId: Long, price: Long): Bid
    @Query("select bid from Bid bid where bid.product_idx = :productId order by bid.price desc limit 1", nativeQuery = true)
    fun findSuccessfulBid(@Param("productId") productId: Long): Bid
}