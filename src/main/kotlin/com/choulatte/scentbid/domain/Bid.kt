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
    private val bidIdx: Long? = null,

    @Column(nullable = false, name = "product_idx")
    private val productIdx: Long,

    @Column(name = "user_idx")
    private val userIdx: Long,

    @Column(name = "account_idx")
    private val accountIdx: Long,

    @Column(name = "bidding_price")
    private val biddingPrice: Long,

    @Column(name = "status")
    private var processingStatus: StatusType,

    @Column(name = "holding_id")
    private var holdingIdx: Long? = null,

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
            bidIdx = this.bidIdx,
            productIdx = this.productIdx,
            userIdx = this.userIdx,
            accountIdx = this.accountIdx,
            biddingPrice = this.biddingPrice,
            processingStatus = this.processingStatus,
            holdingId = this.holdingIdx,
            recordedDate = this.recordedDate,
            lastModifiedDate = this.lastModifiedDate,
            expiredDate = this.expiredDate
        )
    }

    fun updateStatus(processingStatus: StatusType): Bid {
        this.processingStatus = processingStatus
        return this
    }

    fun updateHoldingId(holdingIdx: Long): Bid {
        this.holdingIdx = holdingIdx
        return this
    }

    fun updateExpiredDate(expiredDate: Date): Bid {
        this.expiredDate = expiredDate
        return this
    }

    fun getAccountId(): Long = this.accountIdx

    fun getHoldingId(): Long? = this.holdingIdx

    fun getUserId(): Long = this.userIdx

    fun getBiddingPrice(): Long = this.biddingPrice

    enum class StatusType {
        INITIALIZED, HOLDING, HOLDING_EXTENDED, HOLDING_CLEARED;
    }
}