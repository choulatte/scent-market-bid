package com.choulatte.scentbid.application

import com.choulatte.scentbid.domain.Product

interface ProductService {
    fun getProduct(productId: Long): Product
}