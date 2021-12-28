package com.choulatte.scentbid.domain

import com.choulatte.scentbid.dto.AccountHoldingExtendReqDTO
import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.dto.BiddingDTO
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
    private val productId: Long,

    @Column(name = "user_idx")
    private val userId: Long,

    @Column(name = "account_idx")
    private val accountId: Long,

    @Column(name = "bidding_price")
    private val biddingPrice: Long,

    @Column(name = "status")
    private var processingStatus: StatusType,

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
    fun toDTO() : BidDTO =
        BidDTO(
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

    fun toBiddingDTO() : BiddingDTO =
        BiddingDTO(
            productId = this.productId,
            userId =  this.userId,
            price = this.biddingPrice,
            accountId = this.accountId,
            holdingId = this.holdingId
        )

    fun toAccountHoldingExtendDTO(expiredDate: Date): AccountHoldingExtendReqDTO =
        AccountHoldingExtendReqDTO(
            accountId = this.accountId,
            userId = this.userId,
            holdingId = this.holdingId!!,
            expiredDate = expiredDate
        )

    fun setStatus(processingStatus: StatusType): Bid {
        this.processingStatus = processingStatus
        return this
    }

    fun setHoldingId(holdingIdx: Long): Bid {
        this.holdingId = holdingIdx
        return this
    }

    fun setExpiredDate(expiredDate: Date): Bid {
        this.expiredDate = expiredDate
        return this
    }

    fun getAccountId(): Long = this.accountId

    fun getHoldingId(): Long? = this.holdingId

    fun getUserId(): Long = this.userId

    fun getBiddingPrice(): Long = this.biddingPrice

    enum class StatusType {
        INITIALIZED, HOLDING, HOLDING_EXTENDED, HOLDING_CLEARED;
    }
}