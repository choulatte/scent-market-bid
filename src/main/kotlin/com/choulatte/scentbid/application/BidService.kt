package com.choulatte.scentbid.application

import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.dto.BidCreateReqDTO
import com.choulatte.scentbid.dto.BidReqDTO
import com.choulatte.scentbid.dto.BiddingDTO

interface BidService {
    fun getBidListByProduct(bidReqDTO: BidReqDTO): List<BiddingDTO>

    fun createBid(bidCreateReqDTO: BidCreateReqDTO, userIdx: Long): BidDTO
}