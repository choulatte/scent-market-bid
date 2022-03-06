package com.choulatte.scentbid.controller

import com.choulatte.scentbid.application.BidService
import com.choulatte.scentbid.dto.BidCreateReqDTO
import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.dto.BidReqDTO
import com.choulatte.scentbid.dto.BiddingDTO
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequiredArgsConstructor
@RequestMapping(value = ["/bid"])
class BidController
    (private val bidService: BidService){

    @GetMapping(value = ["/{productId}"])
    fun getProductBids(@PathVariable("productId")productId: Long): ResponseEntity<List<BiddingDTO>> = ResponseEntity.ok(bidService.getBidListByProduct(productId))


    @PostMapping(value = ["/{productId}"])
    fun createBid(@RequestBody bidCreateReqDTO: BidCreateReqDTO, @RequestHeader(value = "User-Idx") userIdx: Long, @PathVariable("productId")productId: Long): ResponseEntity<BidDTO>
        = ResponseEntity.ok(bidService.createBid(bidCreateReqDTO, userIdx, productId))
}
