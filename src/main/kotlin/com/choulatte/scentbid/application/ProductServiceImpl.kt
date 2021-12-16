package com.choulatte.scentbid.application

import com.choulatte.scentbid.domain.Product
import com.choulatte.scentbid.repository.ProductRepository
import io.grpc.ManagedChannel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    @Qualifier(value = "product")
    private val productChannel: ManagedChannel
    ) : ProductService{
    override fun getProduct(productId: Long): Product = productRepository.findByProductId(productId)

}