package com.choulatte.scentbid.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BidRequestNotAvailable : RuntimeException("Bid Request is not Available. Check productId or userId.")