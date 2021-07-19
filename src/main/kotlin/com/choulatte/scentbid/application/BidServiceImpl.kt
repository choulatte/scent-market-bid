package com.choulatte.scentbid.application

import com.choulatte.pay.grpc.PaymentServiceGrpc
import com.choulatte.pay.grpc.PaymentServiceOuterClass
import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.domain.ProcessingStatusType
import com.choulatte.scentbid.dto.BidCreateReqDTO
import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.dto.BidReqDTO
import com.choulatte.scentbid.exception.*
import com.choulatte.scentbid.repository.BidRepository
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors

@Service
class BidServiceImpl(private val bidRepository: BidRepository): BidService {

    private val channel: ManagedChannel = ManagedChannelBuilder
        .forAddress("172.20.10.3", 8090)
        .usePlaintext().build()

    private val stub: PaymentServiceGrpc.PaymentServiceBlockingStub = PaymentServiceGrpc.newBlockingStub(channel)

    // 상품별 호가 리스트 조회
    override fun getBidListByProduct(bidReqDTO: BidReqDTO): List<BidDTO> {
        return bidRepository.findAllByProductId(bidReqDTO.productId ?: throw BidRequestNotAvailable()).stream().map(Bid::toDTO).collect(Collectors.toList());
    }

    // 사용자가 호가를 눌렀을 때 bid를 생성함
    @Transactional
    override fun createBid(bidCreateReqDTO: BidCreateReqDTO): BidDTO {

        // TODO("Bidding Unit verify")

        val response = stub.doHolding(PaymentServiceOuterClass.HoldingRequest.newBuilder()
            .setHolding(PaymentServiceOuterClass.Holding.newBuilder()
                .setAccountId(bidCreateReqDTO.accountId)
                .setAmount(bidCreateReqDTO.biddingPrice)
                .setExpiredDate(bidCreateReqDTO.expiredDate.time)
                .build())
            .setUserId(bidCreateReqDTO.userId).build())

        // TODO("Clear Holding after save")
       when(response.result.result) {
           PaymentServiceOuterClass.Response.Result.OK ->
               return bidRepository.save(bidCreateReqDTO.toBidDTO().toEntity().updateHoldingId(response.holding.id).updateStatus(ProcessingStatusType.HOLDING)).toDTO();

           PaymentServiceOuterClass.Response.Result.CONFLICT -> throw HoldingIllegalStateException()

           PaymentServiceOuterClass.Response.Result.BAD_REQUEST -> throw HoldingBadRequestException()

           PaymentServiceOuterClass.Response.Result.NOT_FOUND -> throw HoldingNotFoundException()

           else -> throw RuntimeException("Unrecognized exception!!!")
       }

    }

    private fun findBidById(bidId: Long): Bid {
        return bidRepository.findById(bidId).get() ?: throw BidNotFoundException()
    }

    private fun findByHoldingId(holdingId: Long): Bid {
        return bidRepository.findByHoldingId(holdingId)
    }

    private fun updateHoldingExpiredDate(holdingId: Long, expiredDate: Date) {
        bidRepository.save(findByHoldingId(holdingId).updateExpiredDate(expiredDate).updateStatus(ProcessingStatusType.HOLDING_EXTENDED))
    }

    private fun clearHolding(holdingId: Long) {
        bidRepository.save(findByHoldingId(holdingId).updateStatus(ProcessingStatusType.HOLDING_CLEARED))
    }
}