package com.choulatte.scentbid.dto

import java.util.*

data class AccountHoldReqDTO(
    val accountId: Long,
    val biddingPrice: Long,
    val expiredDate: Date,
    val userId: Long
)
