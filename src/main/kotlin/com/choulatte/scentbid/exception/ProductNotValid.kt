package com.choulatte.scentbid.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ProductNotValid : IllegalArgumentException("Requested Product is not valid!")