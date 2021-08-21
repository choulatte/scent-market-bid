package com.choulatte.scentbid.controller

import com.choulatte.scentbid.application.BidService
import com.choulatte.scentbid.dto.BidCreateReqDTO
import com.choulatte.scentbid.dto.BidDTO
import com.choulatte.scentbid.dto.BidReqDTO
import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequiredArgsConstructor
@RequestMapping(value = ["/bid"])
class BidController
    (private val bidService: BidService){

    @GetMapping(value = ["/product"])
    fun getProductBids(@RequestBody bidReqDTO: BidReqDTO): ResponseEntity<List<BidDTO>> = ResponseEntity.ok(bidService.getBidListByProduct(bidReqDTO))


    @PostMapping(value = ["/product"])
    fun createBid(@RequestBody bidCreateReqDTO: BidCreateReqDTO, @RequestHeader(value = "User-Idx") userIdx: Long): ResponseEntity<BidDTO>
        = ResponseEntity.ok(bidService.createBid(bidCreateReqDTO, userIdx))
}
