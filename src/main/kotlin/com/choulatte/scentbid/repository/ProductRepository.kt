package com.choulatte.scentbid.repository

import com.choulatte.scentbid.domain.Product
import org.springframework.data.repository.CrudRepository

interface ProductRepository : CrudRepository<Product, Long>{
    fun findByProductId(productId: Long): Product
}