package com.choulatte.scentbid.domain

import com.choulatte.scentbid.dto.BiddingDTO
import com.google.type.DateTime
import java.util.*
import org.springframework.data.redis.core.RedisHash
import javax.persistence.*

@RedisHash("product")
class Product (
    @Id
    @Column(nullable = false, name = "product_idx")
    private val productId: Long,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "starting_date")
    private var startingDate: Date,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ending_date")
    private var endingDate: Date,

    @Column(name = "bidding_list")
    private val biddingList: MutableList<BiddingDTO>,

    @Column(name = "last_holder")
    private var lastBidding: BiddingDTO
) {
    fun bidding(bidding: BiddingDTO) {
        if(this.productId != bidding.productId) throw Exception("ProductId doesn't match.")
        if(this.lastBidding.price > bidding.price) throw Exception("Price is invalid. Cheaper than existing price.")
        this.lastBidding = bidding
        biddingList.add(bidding)
    }

    fun getLastBidding(): BiddingDTO = this.lastBidding

    fun getBiddingList(): List<BiddingDTO> = this.biddingList

    fun isTimeValid(reqTime: Date): Boolean =
        this.startingDate.before(reqTime) && this.endingDate.after(reqTime)
}