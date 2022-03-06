package com.choulatte.scentbid.application

import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.dto.BidCreateReqDTO
import com.choulatte.scentbid.dto.BidReqDTO
import com.choulatte.scentbid.dto.BiddingDTO

interface BidService {
    fun getBidListByProduct(productId: Long): List<BiddingDTO>

    fun createBid(bidCreateReqDTO: BidCreateReqDTO, userIdx: Long, productId: Long): BidDTO
}