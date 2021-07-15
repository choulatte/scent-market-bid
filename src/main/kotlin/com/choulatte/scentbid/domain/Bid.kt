package com.choulatte.scentbid.domain

import com.choulatte.scentbid.dto.BidDTO
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import java.util.*
import javax.persistence.*

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bid")
data class Bid(
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp")
    val timestamp: Date
) {
    fun toDTO() : BidDTO {
        return BidDTO(
            bidId = bidId,
            productId = productId,
            userId = userId,
            biddingPrice = biddingPrice,
            timestamp = timestamp
        )
    }
}