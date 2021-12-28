package com.choulatte.scentbid.application

import com.choulatte.scentbid.dto.AccountHoldReqDTO
import com.choulatte.scentbid.dto.AccountHoldingClearReqDTO
import com.choulatte.scentbid.dto.AccountHoldingExtendReqDTO

interface HoldingService {
    fun holdAccount(accountHoldReqDTO: AccountHoldReqDTO): Long
    fun clearAccount(accountHoldingClearReqDTO: AccountHoldingClearReqDTO): Boolean
    fun extendAccountHolding(accountHoldingExtendReqDTO: AccountHoldingExtendReqDTO): Boolean
}