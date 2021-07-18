package com.choulatte.scentbid.application

import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.domain.ProcessingStatusType
import com.choulatte.scentbid.dto.BidCreateReqDTO
import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.dto.BidReqDTO
import com.choulatte.scentbid.exception.BidNotFoundException
import com.choulatte.scentbid.exception.BidRequestNotAvailable
import com.choulatte.scentbid.repository.BidRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors

@Service
class BidServiceImpl(private val bidRepository: BidRepository): BidService {

    // 상품별 호가 리스트 조회
    override fun getBidListByProduct(bidReqDTO: BidReqDTO): List<BidDTO> {
        return bidRepository.findAllByProductId(bidReqDTO.productId ?: throw BidRequestNotAvailable()).stream().map(Bid::toDTO).collect(Collectors.toList());
    }

    // 사용자가 호가를 눌렀을 때 bid를 생성함
    @Transactional
    override fun createBid(bidCreateReqDTO: BidCreateReqDTO): BidDTO {
        val bid =  bidRepository.save(bidCreateReqDTO.toBidDTO().toEntity()).toDTO();

        //TODO("Request Holding to gRPC")
        val holdingId = 0

        return setHoldingId(bid.bidId!!, bid.userId).toDTO();
    }

    private fun findBidById(bidId: Long): Bid {
        return bidRepository.findById(bidId).get() ?: throw BidNotFoundException()
    }

    private fun findByHoldingId(holdingId: Long): Bid {
        return bidRepository.findByHoldingId(holdingId)
    }

    private fun setHoldingId(bidId: Long, holdingId: Long): Bid {
        return bidRepository.save(findBidById(bidId).updateHoldingId(holdingId).updateStatus(ProcessingStatusType.HOLDING))
    }

    private fun updateHoldingExpiredDate(holdingId: Long, expiredDate: Date) {
        bidRepository.save(findByHoldingId(holdingId).updateExpiredDate(expiredDate).updateStatus(ProcessingStatusType.HOLDING_EXTENDED))
    }

    private fun clearHolding(holdingId: Long) {
        bidRepository.save(findByHoldingId(holdingId).updateStatus(ProcessingStatusType.HOLDING_CLEARED))
    }
}