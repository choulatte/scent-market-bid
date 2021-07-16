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
    val bidId: Long? = null,

    @Column(nullable = false, name = "product_idx")
    val productId: Long? = null,

    @Column(name = "user_idx")
    val userId: Long? = null,

    @Column(name = "bidding_price")
    val biddingPrice: Long? = null,

    @Column(name = "status")
    var status: StatusType = StatusType.INITIALIZED,

    @Column(name = "holding_id")
    var holdingId: Long,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "initialized_date")
    val initializedDate: Date = Date(),

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date")
    val lastModifiedDate: Date,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expired_date")
    var expiredDate: Date

    ) {
    fun toDTO() : BidDTO {
        return BidDTO(
            bidId = this.bidId,
            productId = this.productId,
            userId = this.userId,
            biddingPrice = this.biddingPrice,
            status = this.status,
            holdingId = this.holdingId,
            initializedDate = this.initializedDate,
            lastModifiedDate = this.lastModifiedDate,
            expiredDate = this.expiredDate
        )
    }

    fun updateStatus(status: StatusType): Bid {
        this.status = status
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