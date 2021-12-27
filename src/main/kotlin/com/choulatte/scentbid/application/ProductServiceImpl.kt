package com.choulatte.scentbid.application

import com.choulatte.scentbid.domain.Product
import com.choulatte.scentbid.dto.ProductDTO
import com.choulatte.scentbid.exception.GrpcIllegalStateException
import com.choulatte.scentbid.exception.ProductNotValid
import com.choulatte.scentbid.exception.TimeNotValid
import com.choulatte.scentbid.repository.ProductRepository
import com.choulatte.scentproduct.grpc.ProductServiceGrpc
import com.choulatte.scentproduct.grpc.ProductServiceOuterClass
import com.choulatte.scentproduct.grpc.ProductServiceOuterClass.ProductDetailResponse.Status
import io.grpc.ManagedChannel
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    @Qualifier(value = "product")
    private val productChannel: ManagedChannel
    ) {

    fun getProduct(productId: Long, reqTime: Date): Product? =
        productRepository.findById(productId).orElse(getProductThroughGrpc(productId, reqTime))

    fun getProduct(productId: Long): Product? =
        productRepository.findById(productId).orElse(null);

    private fun getProductThroughGrpc(productId: Long, reqTime: Date): Product {
        val stub: ProductServiceGrpc.ProductServiceBlockingStub = ProductServiceGrpc.newBlockingStub(productChannel)

        val response = stub.getProductDetail(ProductServiceOuterClass.ProductDetailRequest.newBuilder()
            .setProductId(productId).build())

        when(response.status) {
            Status.OK -> {
                val startingDatetime = Date(response.startingDatetime.seconds)
                val endingDatetime = Date(response.endingDatetime.seconds)
                val productDTO = ProductDTO(productId, startingDatetime, endingDatetime, response.startingPrice)

                return try {
                    productRepository.save(productDTO.toEntity().createProduct(reqTime))
                } catch (e:Exception) {
                    if(e == TimeNotValid()) throw e
                    else throw Exception("Internal Error: Product save error.")
                }
            }

            Status.INVALID -> {
                throw ProductNotValid()
            }

            else -> throw GrpcIllegalStateException()
        }
    }


    private fun saveProduct(product: Product): Product {
        return productRepository.save(product);
    }

}