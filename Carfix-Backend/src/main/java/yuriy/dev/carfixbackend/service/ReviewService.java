package yuriy.dev.carfixbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuriy.dev.carfixbackend.dto.ReviewDto;
import yuriy.dev.carfixbackend.mapper.ReviewMapper;
import yuriy.dev.carfixbackend.model.Review;
import yuriy.dev.carfixbackend.repository.ReviewRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final ReviewMapper reviewMapper;
    private final UserService userService;

    public List<ReviewDto> findAllReviews() {
        return  reviewRepository
                .findAll()
                .stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    public ReviewDto createReview(ReviewDto reviewDto) {
        Review review = reviewMapper.toReview(reviewDto);
        review.setUser(userService.getCurrentUser());
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    public ReviewDto findReviewById(UUID id) {
        Review review = reviewRepository.findById(id).orElse(null);
        return reviewMapper.toDto(review);
    }
}


