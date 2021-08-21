package com.choulatte.scentbid.application

import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.dto.BidCreateReqDTO
import com.choulatte.scentbid.dto.BidReqDTO

interface BidService {
    fun getBidListByProduct(bidReqDTO: BidReqDTO): List<BidDTO>

    fun createBid(bidCreateReqDTO: BidCreateReqDTO, userIdx: Long): BidDTO
}