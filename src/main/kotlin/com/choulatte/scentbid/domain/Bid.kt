package com.choulatte.scentbid.domain

import com.choulatte.scentbid.dto.BidDTO
import java.util.*
import javax.persistence.*
import kotlin.RuntimeException

@Entity
@Table(name = "bid")
class Bid(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_idx")
    private val bidId: Long? = null,

    @Column(nullable = false, name = "product_idx")
    private val productId: Long,

    @Column(name = "user_idx")
    private val userId: Long,

    @Column(name = "account_idx")
    private val accountId: Long,

    @Column(name = "bidding_price")
    private val biddingPrice: Long,

    @Column(name = "status")
    private var processingStatus: ProcessingStatusType,

    @Column(name = "holding_id")
    private var holdingId: Long? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "recorded_date")
    private var recordedDate: Date = Date(),

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
            accountId = this.accountId,
            biddingPrice = this.biddingPrice,
            processingStatus = this.processingStatus,
            holdingId = this.holdingId,
            recordedDate = this.recordedDate,
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

    fun getAccountId(): Long = this.accountId

    fun getHoldingId(): Long? = this.holdingId

    fun getUserId(): Long = this.userId

    fun getBiddingPrice(): Long = this.biddingPrice
}