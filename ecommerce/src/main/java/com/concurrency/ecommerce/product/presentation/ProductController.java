package com.concurrency.ecommerce.product.presentation;


import com.concurrency.ecommerce.product.application.HotProductListResponse;
import com.concurrency.ecommerce.product.application.ProductFacade;
import com.concurrency.ecommerce.product.application.ProductListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ecommerce/product")
@RequiredArgsConstructor
@Tag(name = "Product", description = "상품 API")
public class ProductController {

    private final ProductFacade productFacade;

    @GetMapping
    @Operation(summary = "상품 목록 조회 API", description = "등록되어 있는 상품 목록을 조회하는 API")
    public ResponseEntity<ProductListResponse> productList() {
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/hot")
    @Operation(summary = "인기 상품 목록 조회 API", description = "최근 3일동안 판매량 높은 순으로 TOP 5 상품 목록을 조회하는 API")
    public ResponseEntity<HotProductListResponse> hotProductList() {
        return ResponseEntity.ok().body(null);
    }
}
