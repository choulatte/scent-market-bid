package com.choulatte.scentbid.application

import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.domain.StatusType
import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.repository.BidRepository
import java.util.*
import java.util.stream.Collectors

class BidServiceImpl
    (private val bidRepository: BidRepository): BidService {
    override fun getBidList(): List<BidDTO> {
        return bidRepository.findAll().stream().map(Bid::toDTO).collect(Collectors.toList());
    }

    override fun getBidListByProduct(productId: Long): List<BidDTO> {
        return bidRepository.findAllByProductId(productId).stream().map(Bid::toDTO).collect(Collectors.toList());
    }

    override fun getBidListByProductAndBiddingTime(productId: Long, biddingTime: Date): List<BidDTO> {
        return bidRepository.findAllByProductIdAndTimestampBefore(productId, biddingTime).stream().map(Bid::toDTO).collect(Collectors.toList());
    }

    override fun getBidListByProductAndStatus(productId: Long, status: StatusType): List<BidDTO> {
        return bidRepository.findAllByProductIdAndStatus(productId, status).stream().map(Bid::toDTO).collect(Collectors.toList());
    }

    override fun createBid(bidDTO: BidDTO): BidDTO {
        return bidRepository.save(bidDTO.toEntity()).toDTO();
    }

    override fun updateBidStatus(bidDTO: BidDTO, status: StatusType): BidDTO {
        TODO("Not yet implemented")
    }
}