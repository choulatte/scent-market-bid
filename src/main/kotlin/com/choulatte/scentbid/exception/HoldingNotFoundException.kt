package com.choulatte.scentbid.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class HoldingNotFoundException : RuntimeException("Holding is Not Found.")