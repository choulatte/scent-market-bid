package com.choulatte.scentbid.application

import com.choulatte.scentpay.grpc.PaymentServiceOuterClass.Response.Result
import com.choulatte.scentpay.grpc.PaymentServiceGrpc
import com.choulatte.scentpay.grpc.PaymentServiceOuterClass
import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.dto.BidCreateReqDTO
import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.dto.BidReqDTO
import com.choulatte.scentbid.dto.BiddingDTO
import com.choulatte.scentbid.exception.*
import com.choulatte.scentbid.repository.BidRepository
import io.grpc.ManagedChannel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors


@Service
class BidServiceImpl(
    private val bidRepository: BidRepository,
    private val productService: ProductServiceImpl,
    @Qualifier(value = "pay")
    private val payChannel: ManagedChannel
    ): BidService {

    // 상품별 호가 리스트 조회
    // 프로덕트 우선 조회(redis) 프로덕트에 없으면 비드 조회
    override fun getBidListByProduct(bidReqDTO: BidReqDTO): List<BiddingDTO> =
        when(val product = productService.getProduct(bidReqDTO.productIdx!!)) {
            null -> bidRepository.findAllByProductId(bidReqDTO.productIdx).stream().map(Bid::toBiddingDTO).collect(Collectors.toList())
            else -> product.getBiddingList()
        }




    // 사용자가 호가를 눌렀을 때 bid를 생성함
    // 상품이 존재하는가?
    // 존재하면 진행 -> 레디스에도 저장하고 비드에도 저장함
    // 문제가 되는 상황 -> 프로덕트는 null, bid에는 있음 -> 어떻게 할 것인가? grpc 통해서 들고오는 것은 시작가밖에 없음. 필요시 현재가도 들고 오게 해야함
    override fun createBid(bidCreateReqDTO: BidCreateReqDTO, userIdx: Long): BidDTO {
        val product = try {
            productService.getProduct(bidCreateReqDTO.productIdx, bidCreateReqDTO.reqTime)
        } catch (e: Exception) {
            throw e
        }

        when(verifyBiddingPrice(bidCreateReqDTO.biddingPrice, bidCreateReqDTO.productIdx)){
            false -> throw BiddingPriceNotValid()
        }

        // lastBidding 추가하고 갱신
        product!!.bidding(bidCreateReqDTO.toBiddingDTO())

        val stub: PaymentServiceGrpc.PaymentServiceBlockingStub = PaymentServiceGrpc.newBlockingStub(payChannel)

        val response = stub.doHolding(PaymentServiceOuterClass.HoldingRequest.newBuilder()
            .setHolding(PaymentServiceOuterClass.Holding.newBuilder()
                .setAccountId(bidCreateReqDTO.accountIdx)
                .setAmount(bidCreateReqDTO.biddingPrice)
                .setExpiredDate(bidCreateReqDTO.expiredDate.time)
                .build())
            .setUserId(bidCreateReqDTO.userIdx).build())

       when(response.result.result) {
           Result.OK -> return holdingAndClear(bidCreateReqDTO.toBidDTO(userIdx), response.holding.id)
           Result.CONFLICT -> throw HoldingIllegalStateException()
           Result.BAD_REQUEST -> throw HoldingBadRequestException()
           Result.NOT_FOUND -> throw HoldingNotFoundException()
           // Create Holding Illegal State Exception
           else -> throw GrpcIllegalStateException()
       }

    }

    private fun findByHoldingId(holdingIdx: Long): Bid = bidRepository.findByHoldingId(holdingIdx)

    private fun verifyBiddingPrice(biddingPrice: Long, productIdx: Long) : Boolean {

        return checkUnitPrice(biddingPrice) && checkPriceLarger(biddingPrice, productIdx)
    }

    private fun checkUnitPrice(biddingPrice: Long) : Boolean {
        val unitPrice: Long = when(biddingPrice){
            in 0 until 5000 -> 100
            in 5000 until 10000 -> 500
            in 10000 until 100000 -> 1000
            in 100000 until 500000 -> 5000
            in 500000 until Long.MAX_VALUE -> 10000
            else -> throw BiddingPriceNotValid()
        }
        return when (biddingPrice % unitPrice) {
            0L -> true
            else -> false
        }
    }

    fun checkPriceLarger(biddingPrice: Long, productIdx: Long) : Boolean =
         when(val compareTarget = bidRepository.findTopByProductIdOrderByBiddingPriceDesc(productIdx)) {
             null -> true
             else -> when(biddingPrice > compareTarget.getBiddingPrice()) {
                 false -> false
                 true -> true
             }
         }

    private fun holdingAndClear(bidDTO: BidDTO, holdingId: Long) : BidDTO {
        val bid = bidRepository.save(bidDTO.toEntity().updateHoldingId(holdingId).updateStatus(Bid.StatusType.HOLDING)).toDTO()
        val clearTarget = bidRepository.findTopByProductIdAndProcessingStatusOrderByRecordedDateDesc(bidDTO.productIdx, Bid.StatusType.HOLDING) ?: return bid

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
            Result.OK -> bidRepository.save(clearTarget.updateStatus(Bid.StatusType.HOLDING_CLEARED))
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
            Result.OK -> bidRepository.save(findByHoldingId(holdingId).updateExpiredDate(expiredDate).updateStatus(Bid.StatusType.HOLDING_EXTENDED))
            Result.CONFLICT -> throw HoldingIllegalStateException()
            Result.BAD_REQUEST -> throw HoldingBadRequestException()
            Result.NOT_FOUND -> throw HoldingNotFoundException()
            // Holding Extend Illegal State Exception
            else -> throw GrpcIllegalStateException()
        }

        return true
    }
}