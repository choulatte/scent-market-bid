package com.choulatte.scentbid.dto

import java.util.*

data class AccountHoldingExtendReqDTO(
    val holdingId: Long,
    val accountId: Long,
    val userId: Long,
    val expiredDate: Date
)
