package com.choulatte.scentbid.application

import com.choulatte.scentpay.grpc.PaymentServiceOuterClass.Response.Result
import com.choulatte.scentpay.grpc.PaymentServiceGrpc
import com.choulatte.scentpay.grpc.PaymentServiceOuterClass
import com.choulatte.scentbid.domain.Bid
import com.choulatte.scentbid.dto.*
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
    private val productService: ProductService,
    private val holdingService: HoldingService,
    @Qualifier(value = "pay")
    private val payChannel: ManagedChannel
    ): BidService {

    // 상품별 호가 리스트 조회
    // 프로덕트 우선 조회(redis) 프로덕트에 없으면 비드 조회
    override fun getBidListByProduct(bidReqDTO: BidReqDTO): List<BiddingDTO> =
        when(val product = productService.getProduct(bidReqDTO.productId!!)) {
            null -> bidRepository.findAllByProductId(bidReqDTO.productId).stream().map(Bid::toBiddingDTO).collect(Collectors.toList())
            else -> product.getBiddingList()
        }


    // 사용자가 호가를 눌렀을 때 bid를 생성함
    // 상품이 존재하는가?
    // 존재하면 진행 -> 레디스에도 저장하고 비드에도 저장함
    // 문제가 되는 상황 -> 프로덕트는 null, bid에는 있음 -> 어떻게 할 것인가? grpc 통해서 들고오는 것은 시작가밖에 없음. 필요시 현재가도 들고 오게 해야함
    override fun createBid(bidCreateReqDTO: BidCreateReqDTO, userId: Long): BidDTO {
        val product = try {
            productService.getProduct(bidCreateReqDTO.productId, bidCreateReqDTO.reqTime)
        } catch (e: Exception) {
            throw e
        }!!

        when(verifyBiddingPrice(bidCreateReqDTO.biddingPrice, bidCreateReqDTO.productId, product.getLastBidding())){
            false -> throw BiddingPriceNotValid()
        }

        val toClear = product.getLastBidding()
        val holdingId = holdingService.holdAccount(bidCreateReqDTO.toAccountHoldReqDTO())
        var bid = bidCreateReqDTO.toBidDTO(userId, holdingId)

        // holding
        bid = updateBidHolding(bid, holdingId).toDTO()

        //clear
        updateBidHoldingClear(bidCreateReqDTO.productId, toClear)

        return bid
    }

    private fun updateBidHolding(bidDTO: BidDTO, holdingId: Long): Bid =
        bidRepository.save(bidDTO.toEntity().setHoldingId(holdingId).setStatus(Bid.StatusType.HOLDING))

    private fun updateBidHoldingClear(productId: Long, toClear: BiddingDTO?): Boolean {
        if(toClear != null){
            if(holdingService.clearAccount(toClear.toAccountHoldingClearReqDTO())) {
                val bidToClear = bidRepository.findByProductIdAndBiddingPrice(productId, toClear.price)
                bidRepository.save(bidToClear.setStatus(Bid.StatusType.HOLDING_CLEARED))
            }
            else return false
        }
        return true
    }


    private fun verifyBiddingPrice(biddingPrice: Long, productIdx: Long, lastBidding: BiddingDTO?) : Boolean {

        return checkUnitPrice(biddingPrice) && checkPriceLarger(biddingPrice, lastBidding)
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

    private fun checkPriceLarger(biddingPrice: Long, lastBidding: BiddingDTO?) : Boolean =
         when(lastBidding) {
             null -> true
             else -> when(biddingPrice > lastBidding.price) {
                 false -> false
                 true -> true
             }
         }

    private fun getBidByHoldingId(holdingIdx: Long): Bid = bidRepository.findByHoldingId(holdingIdx)
    private fun getSuccessfulBid(productId: Long): Bid = bidRepository.findSuccessfulBid(productId)

    private fun updateBidHoldingExpiredDate(productId: Long, expiredDate: Date) {
        val bid = getSuccessfulBid(productId)
        holdingService.extendAccountHolding(bid.toAccountHoldingExtendDTO(expiredDate))
        bidRepository.save(bid.setExpiredDate(expiredDate))
    }
}