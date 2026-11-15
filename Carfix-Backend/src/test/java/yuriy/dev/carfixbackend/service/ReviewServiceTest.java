package yuriy.dev.carfixbackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import yuriy.dev.carfixbackend.auth.Role;
import yuriy.dev.carfixbackend.dto.ReviewDto;
import yuriy.dev.carfixbackend.mapper.ReviewMapper;
import yuriy.dev.carfixbackend.model.Review;
import yuriy.dev.carfixbackend.model.User;
import yuriy.dev.carfixbackend.repository.ReviewRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewMapper reviewMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    private static final UUID REVIEW_ID = UUID.randomUUID();

    @Test
    void findAllReviews_returnsMappedDtos() {
        Review review = Review.builder().id(REVIEW_ID).reviewContent("Отличный сервис").rating(5).build();
        ReviewDto dto = new ReviewDto(REVIEW_ID, "Отличный сервис", 5, null, null);

        when(reviewRepository.findAll()).thenReturn(List.of(review));
        when(reviewMapper.toDto(review)).thenReturn(dto);

        List<ReviewDto> result = reviewService.findAllReviews();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).rating()).isEqualTo(5);
    }

    @Test
    void createReview_assignsCurrentUserAndSaves() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("user@mail.ru")
                .role(Role.ROLE_USER)
                .build();
        ReviewDto input = new ReviewDto(null, "Всё понравилось", 5, null, null);
        Review review = Review.builder().reviewContent("Всё понравилось").rating(5).build();
        ReviewDto savedDto = new ReviewDto(REVIEW_ID, "Всё понравилось", 5,
                new Date(System.currentTimeMillis()), null);

        when(reviewMapper.toReview(input)).thenReturn(review);
        when(userService.getCurrentUser()).thenReturn(user);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(savedDto);

        ReviewDto result = reviewService.createReview(input);

        assertThat(review.getUser()).isEqualTo(user);
        verify(reviewRepository).save(review);
        assertThat(result.id()).isEqualTo(REVIEW_ID);
    }

    @Test
    void findReviewById_returnsDto() {
        Review review = Review.builder().id(REVIEW_ID).reviewContent("Нормально").rating(4).build();
        ReviewDto dto = new ReviewDto(REVIEW_ID, "Нормально", 4, null, null);

        when(reviewRepository.findById(REVIEW_ID)).thenReturn(Optional.of(review));
        when(reviewMapper.toDto(review)).thenReturn(dto);

        ReviewDto result = reviewService.findReviewById(REVIEW_ID);

        assertThat(result.reviewContent()).isEqualTo("Нормально");
    }
}
