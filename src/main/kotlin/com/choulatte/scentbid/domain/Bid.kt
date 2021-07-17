package com.choulatte.scentbid.domain

import com.choulatte.scentbid.dto.BidDTO
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "bid")
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_idx")
    private val bidId: Long? = null,

    @Column(nullable = false, name = "product_idx")
    private val productId: Long? = null,

    @Column(name = "user_idx")
    private val userId: Long? = null,

    @Column(name = "bidding_price")
    private val biddingPrice: Long? = null,

    @Column(name = "status")
    private var processingStatus: ProcessingStatusType = ProcessingStatusType.INITIALIZED,

    @Column(name = "holding_id")
    private var holdingId: Long,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "initialized_date")
    private var initializedDate: Date = Date(),

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    private val lastModifiedDate: Date,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expired_date")
    private var expiredDate: Date

    ) {
    fun toDTO() : BidDTO {
        return BidDTO(
            bidId = this.bidId,
            productId = this.productId,
            userId = this.userId,
            biddingPrice = this.biddingPrice,
            processingStatus = this.processingStatus,
            holdingId = this.holdingId,
            initializedDate = this.initializedDate ?: Date(),
            lastModifiedDate = this.lastModifiedDate,
            expiredDate = this.expiredDate
        )
    }

    fun updateStatus(processingStatus: ProcessingStatusType): Bid {
        this.processingStatus = processingStatus
        return this
    }

    fun updateHoldingId(holdingId: Long): Bid {
        this.holdingId = holdingId
        return this
    }

    fun updateExpiredDate(expiredDate: Date): Bid {
        this.expiredDate = expiredDate
        return this
    }
}