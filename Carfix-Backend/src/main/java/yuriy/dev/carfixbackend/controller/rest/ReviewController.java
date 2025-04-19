package yuriy.dev.carfixbackend.controller.rest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuriy.dev.carfixbackend.dto.response.ApiResponseDto;
import yuriy.dev.carfixbackend.dto.ReviewDto;
import yuriy.dev.carfixbackend.service.ReviewService;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ReviewDto>>> findAllReviews(){
        return ResponseEntity.ok(new ApiResponseDto<>("OK",reviewService.findAllReviews()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<ReviewDto>> createReview(@RequestBody ReviewDto reviewDto){
        ReviewDto result = reviewService.createReview(reviewDto);
        return ResponseEntity.ok(new ApiResponseDto<>("OK",result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ReviewDto>> findReviewById(@PathVariable UUID id){
        ReviewDto result = reviewService.findReviewById(id);
        return ResponseEntity.ok(new ApiResponseDto<>("OK",result));
    }
}
