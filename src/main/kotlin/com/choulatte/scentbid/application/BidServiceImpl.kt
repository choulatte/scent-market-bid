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
import org.aspectj.weaver.tools.cache.AsynchronousFileCacheBacking
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
        return bidRepository.findAllByProductId(bidReqDTO.productId ?: throw BidRequestNotAvailable()).stream().map(Bid::toDTO).collect(Collectors.toList())
    }

    // 사용자가 호가를 눌렀을 때 bid를 생성함
    @Transactional
    override fun createBid(bidCreateReqDTO: BidCreateReqDTO): BidDTO {

        when(verifyBiddingPrice(bidCreateReqDTO.biddingPrice, bidCreateReqDTO.productId)){
            false -> throw BiddingPriceNotValid()
        }

        val response = stub.doHolding(PaymentServiceOuterClass.HoldingRequest.newBuilder()
            .setHolding(PaymentServiceOuterClass.Holding.newBuilder()
                .setAccountId(bidCreateReqDTO.accountId)
                .setAmount(bidCreateReqDTO.biddingPrice)
                .setExpiredDate(bidCreateReqDTO.expiredDate.time)
                .build())
            .setUserId(bidCreateReqDTO.userId).build())

       when(response.result.result) {
           PaymentServiceOuterClass.Response.Result.OK -> return holdingAndClear(bidCreateReqDTO.toBidDTO(), response.holding.id)
           PaymentServiceOuterClass.Response.Result.CONFLICT -> throw HoldingIllegalStateException()
           PaymentServiceOuterClass.Response.Result.BAD_REQUEST -> throw HoldingBadRequestException()
           PaymentServiceOuterClass.Response.Result.NOT_FOUND -> throw HoldingNotFoundException()
           else -> throw RuntimeException("Unrecognized exception!!!")
       }

    }

    private fun findByHoldingId(holdingId: Long): Bid {
        return bidRepository.findByHoldingId(holdingId)
    }

    private fun verifyBiddingPrice(biddingPrice: Long, productId: Long) : Boolean {
        val unitPrice: Long = when(biddingPrice){
            in 0 until 5000 -> 100
            in 5000 until 10000 -> 500
            in 10000 until 100000 -> 1000
            in 100000 until 500000 -> 5000
            in 500000 until Long.MAX_VALUE -> 10000
            else -> throw BiddingPriceNotValid()
        }

        return checkUnitPrice(biddingPrice, unitPrice) && checkPriceLarger(biddingPrice, productId)
    }

    private fun checkUnitPrice(biddingPrice: Long, unitPrice: Long) : Boolean {
        return when(biddingPrice % unitPrice) {
            0L -> true
            else -> false
        }
    }

    private fun checkPriceLarger(biddingPrice: Long, productId: Long) : Boolean {
        val compareTarget = bidRepository.findTopByProductIdOrderByBiddingPriceDesc(productId) ?: return true

        return when(biddingPrice > compareTarget.getBiddingPrice()){
            false -> false
            true -> true
        }
    }

    private fun holdingAndClear(bidDTO: BidDTO, holdingId: Long) : BidDTO {
        val clearTarget = bidRepository.findTopByProductIdAndProcessingStatusOrderByRecordedDateDesc(bidDTO.productId, ProcessingStatusType.HOLDING)
        val bid = bidRepository.save(bidDTO.toEntity().updateHoldingId(holdingId).updateStatus(ProcessingStatusType.HOLDING)).toDTO()

        clear(clearTarget ?: return bid)

        return  bid
    }

    private fun clear(clearTarget: Bid) {
        val response = stub.clearHolding(PaymentServiceOuterClass.HoldingRequest.newBuilder()
            .setHolding(PaymentServiceOuterClass.Holding.newBuilder()
                .setAccountId(clearTarget.getAccountId())
                .setId(clearTarget.getHoldingId() ?: throw IllegalArgumentException("There is no holdingId to clear!"))
                .build())
            .setUserId(clearTarget.getUserId()).build())

        when(response.result){
            PaymentServiceOuterClass.Response.Result.OK -> bidRepository.save(clearTarget.updateStatus(ProcessingStatusType.HOLDING_CLEARED))
            PaymentServiceOuterClass.Response.Result.CONFLICT -> throw HoldingIllegalStateException()
            PaymentServiceOuterClass.Response.Result.BAD_REQUEST -> throw HoldingBadRequestException()
            PaymentServiceOuterClass.Response.Result.NOT_FOUND -> throw HoldingNotFoundException()
            else -> throw RuntimeException("Unrecognized exception!!!")
        }
    }


}