package com.choulatte.scentbid.application

import com.choulatte.scentbid.domain.Product
import java.util.*

interface ProductService {
    fun getProduct(productId: Long, reqTime: Date): Product?

    fun getProduct(productId: Long): Product?
}