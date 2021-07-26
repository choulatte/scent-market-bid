package com.choulatte.scentbid.application

import com.choulatte.pay.grpc.PaymentServiceOuterClass.Response.Result
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
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors


@Service
class BidServiceImpl(
    private val bidRepository: BidRepository,
    @Qualifier(value = "pay")
    private val payChannel: ManagedChannel
    ): BidService {

    // 상품별 호가 리스트 조회
    override fun getBidListByProduct(bidReqDTO: BidReqDTO): List<BidDTO> =
        bidRepository.findAllByProductId(bidReqDTO.productId ?: throw BidRequestNotAvailable()).stream().map(Bid::toDTO).collect(Collectors.toList())


    // 사용자가 호가를 눌렀을 때 bid를 생성함
    @Transactional
    override fun createBid(bidCreateReqDTO: BidCreateReqDTO): BidDTO {
        val stub: PaymentServiceGrpc.PaymentServiceBlockingStub = PaymentServiceGrpc.newBlockingStub(payChannel)

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
           Result.OK -> return holdingAndClear(bidCreateReqDTO.toBidDTO(), response.holding.id)
           Result.CONFLICT -> throw HoldingIllegalStateException()
           Result.BAD_REQUEST -> throw HoldingBadRequestException()
           Result.NOT_FOUND -> throw HoldingNotFoundException()
           // Create Holding Illegal State Exception
           else -> throw GrpcIllegalStateException()
       }

    }

    private fun findByHoldingId(holdingId: Long): Bid = bidRepository.findByHoldingId(holdingId)

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

    private fun checkUnitPrice(biddingPrice: Long, unitPrice: Long) : Boolean =
        when(biddingPrice % unitPrice) {
            0L -> true
            else -> false
        }

    fun checkPriceLarger(biddingPrice: Long, productId: Long) : Boolean =
         when(val compareTarget = bidRepository.findTopByProductIdOrderByBiddingPriceDesc(productId)) {
             null -> true
             else -> when(biddingPrice > compareTarget.getBiddingPrice()) {
                 false -> false
                 true -> true
             }
         }



    private fun holdingAndClear(bidDTO: BidDTO, holdingId: Long) : BidDTO {
        val bid = bidRepository.save(bidDTO.toEntity().updateHoldingId(holdingId).updateStatus(ProcessingStatusType.HOLDING)).toDTO()
        val clearTarget = bidRepository.findTopByProductIdAndProcessingStatusOrderByRecordedDateDesc(bidDTO.productId, ProcessingStatusType.HOLDING) ?: return bid

        clear(clearTarget)

        return  bid
    }

    private fun clear(clearTarget: Bid) {
        val stub: PaymentServiceGrpc.PaymentServiceBlockingStub = PaymentServiceGrpc.newBlockingStub(payChannel)

        val response = stub.clearHolding(PaymentServiceOuterClass.HoldingRequest.newBuilder()
            .setHolding(PaymentServiceOuterClass.Holding.newBuilder()
                .setAccountId(clearTarget.getAccountId())
                .setId(clearTarget.getHoldingId() ?: throw IllegalArgumentException("There is no holdingId to clear!"))
                .build())
            .setUserId(clearTarget.getUserId()).build())

        when(response.result){
            Result.OK -> bidRepository.save(clearTarget.updateStatus(ProcessingStatusType.HOLDING_CLEARED))
            Result.CONFLICT -> throw HoldingIllegalStateException()
            Result.BAD_REQUEST -> throw HoldingBadRequestException()
            Result.NOT_FOUND -> throw HoldingNotFoundException()
            // Holding Clear Illegal State Exception
            else -> throw GrpcIllegalStateException()
        }
    }

    private fun updateHoldingExpiredDate(holdingId: Long, accountId:Long, userId: Long, expiredDate: Date) : Boolean {
        val stub: PaymentServiceGrpc.PaymentServiceBlockingStub = PaymentServiceGrpc.newBlockingStub(payChannel)

        val response = stub.extendHolding(PaymentServiceOuterClass.HoldingRequest.newBuilder()
            .setHolding(PaymentServiceOuterClass.Holding.newBuilder()
                .setId(holdingId)
                .setAccountId(accountId)
                .setExpiredDate(expiredDate.time)
                .build())
            .setUserId(userId).build())

        when(response.result.result){
            Result.OK -> bidRepository.save(findByHoldingId(holdingId).updateExpiredDate(expiredDate).updateStatus(ProcessingStatusType.HOLDING_EXTENDED))
            Result.CONFLICT -> throw HoldingIllegalStateException()
            Result.BAD_REQUEST -> throw HoldingBadRequestException()
            Result.NOT_FOUND -> throw HoldingNotFoundException()
            // Holding Extend Illegal State Exception
            else -> throw GrpcIllegalStateException()
        }

        return true
    }
}