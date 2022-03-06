package com.choulatte.scentbid.application

import com.choulatte.scentbid.dto.AccountHoldReqDTO
import com.choulatte.scentbid.dto.AccountHoldingClearReqDTO
import com.choulatte.scentbid.dto.AccountHoldingExtendReqDTO
import com.choulatte.scentbid.exception.GrpcIllegalStateException
import com.choulatte.scentbid.exception.HoldingBadRequestException
import com.choulatte.scentbid.exception.HoldingIllegalStateException
import com.choulatte.scentbid.exception.HoldingNotFoundException
import com.choulatte.scentpay.grpc.PaymentServiceGrpc
import com.choulatte.scentpay.grpc.PaymentServiceOuterClass
import io.grpc.ManagedChannel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
@Service
class HoldingServiceImpl(
    @Qualifier(value = "pay")
    private val payChannel: ManagedChannel
): HoldingService {
    override fun holdAccount(accountHoldReqDTO: AccountHoldReqDTO): Long {
        val stub: PaymentServiceGrpc.PaymentServiceBlockingStub = PaymentServiceGrpc.newBlockingStub(payChannel)

        val response = stub.doHolding(
            PaymentServiceOuterClass.HoldingRequest.newBuilder()
            .setHolding(
                PaymentServiceOuterClass.Holding.newBuilder()
                .setAccountId(accountHoldReqDTO.accountId)
                .setAmount(accountHoldReqDTO.biddingPrice)
                .setExpiredDate(accountHoldReqDTO.expiredDate.time)
                .build())
            .setUserId(accountHoldReqDTO.userId).build())

        when(response.result.result) {
            PaymentServiceOuterClass.Response.Result.OK -> return response.holding.id
            PaymentServiceOuterClass.Response.Result.CONFLICT -> throw HoldingIllegalStateException()
            PaymentServiceOuterClass.Response.Result.BAD_REQUEST -> throw HoldingBadRequestException()
            PaymentServiceOuterClass.Response.Result.NOT_FOUND -> throw HoldingNotFoundException()
            // Create Holding Illegal State Exception
            else -> throw GrpcIllegalStateException()
        }
    }

    override fun clearAccount(accountHoldingClearReqDTO: AccountHoldingClearReqDTO): Boolean {
        val stub: PaymentServiceGrpc.PaymentServiceBlockingStub = PaymentServiceGrpc.newBlockingStub(payChannel)

        val response = stub.clearHolding(PaymentServiceOuterClass.HoldingRequest.newBuilder()
            .setHolding(PaymentServiceOuterClass.Holding.newBuilder()
                .setAccountId(accountHoldingClearReqDTO.accountId)
                .setId(accountHoldingClearReqDTO.holdingId ?: throw IllegalArgumentException("There is no holdingId to clear!"))
                .build())
            .setUserId(accountHoldingClearReqDTO.userId).build())

         return when(response.result){
            PaymentServiceOuterClass.Response.Result.OK -> true
            PaymentServiceOuterClass.Response.Result.CONFLICT -> throw HoldingIllegalStateException()
            PaymentServiceOuterClass.Response.Result.BAD_REQUEST -> throw HoldingBadRequestException()
            PaymentServiceOuterClass.Response.Result.NOT_FOUND -> throw HoldingNotFoundException()
            // Holding Clear Illegal State Exception
            else -> throw GrpcIllegalStateException()
        }
    }

    override fun extendAccountHolding(accountHoldingExtendReqDTO: AccountHoldingExtendReqDTO) : Boolean {
        val stub: PaymentServiceGrpc.PaymentServiceBlockingStub = PaymentServiceGrpc.newBlockingStub(payChannel)

        val response = stub.extendHolding(PaymentServiceOuterClass.HoldingRequest.newBuilder()
            .setHolding(PaymentServiceOuterClass.Holding.newBuilder()
                .setId(accountHoldingExtendReqDTO.holdingId)
                .setAccountId(accountHoldingExtendReqDTO.accountId)
                .setExpiredDate(accountHoldingExtendReqDTO.expiredDate.time)
                .build())
            .setUserId(accountHoldingExtendReqDTO.userId).build())

        when(response.result.result){
            PaymentServiceOuterClass.Response.Result.OK -> return true
            PaymentServiceOuterClass.Response.Result.CONFLICT -> throw HoldingIllegalStateException()
            PaymentServiceOuterClass.Response.Result.BAD_REQUEST -> throw HoldingBadRequestException()
            PaymentServiceOuterClass.Response.Result.NOT_FOUND -> throw HoldingNotFoundException()
            // Holding Extend Illegal State Exception
            else -> throw GrpcIllegalStateException()
        }
    }
}