package com.choulatte.scentbid.domain

import com.choulatte.scentbid.dto.BiddingDTO
import com.choulatte.scentbid.exception.TimeNotValid
import java.util.*
import org.springframework.data.redis.core.RedisHash
import javax.persistence.*

@RedisHash("product")
class Product(
    @Id
    @Column(nullable = false, name = "product_idx")
    private var productId: Long,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "starting_date")
    private var startingDatetime: Date,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ending_date")
    private var endingDatetime: Date,

    @Column(name = "bidding_list")
    private var biddingList: MutableList<BiddingDTO>,

    @Column(name = "starting_price")
    private val startingPrice: Long,

    @Column(name = "last_holder")
    private var lastBidding: BiddingDTO?
) {
    fun bidding(bidding: BiddingDTO) {
        if(this.productId != bidding.productId) throw Exception("ProductId doesn't match.")
        if((this.lastBidding?.price ?: this.startingPrice) > bidding.price) throw Exception("Price is invalid. Cheaper than existing price.")
        this.lastBidding = bidding
        biddingList.add(bidding)
    }

    fun getLastBidding(): BiddingDTO? = this.lastBidding

    fun getBiddingList(): MutableList<BiddingDTO> = this.biddingList

    private fun isTimeValid(reqTime: Date): Boolean =
        this.startingDatetime.before(reqTime) && this.endingDatetime.after(reqTime)

    fun createProduct(reqTime: Date): Product {
        if(!this.isTimeValid(reqTime)) throw TimeNotValid()
        return this
    }
}

