import {AfterViewInit, Component, ElementRef, OnInit, signal, ViewChild} from '@angular/core';
import {AuthenticationService} from '../../services/AuthenticationService/authentication.service';
import {HeaderComponent} from '../header/header.component';
import {FooterComponent} from '../footer/footer.component';
import {ReviewService} from '../../services/ReviewService/review.service';
import {Review} from '../../model/Review';
import {catchError, of} from 'rxjs';
import {DatePipe, NgClass} from '@angular/common';
import {BarRating, BarRatingEffect} from 'ngx-bar-rating';
import {FormsModule} from '@angular/forms';


@Component({
  selector: 'app-reviews',
  standalone: true,
  imports: [
    DatePipe,
    NgClass,
    BarRating,
    BarRatingEffect,
    FormsModule
  ],
  templateUrl: './reviews.component.html',
  styleUrl: './reviews.component.css'
})
export class ReviewsComponent implements OnInit{

  //@ViewChild('reviewsContainer') reviewContainer!: ElementRef<HTMLElement>;
  @ViewChild('counter') counter!: ElementRef<HTMLElement>;
  @ViewChild('reviewArea') reviewArea!: ElementRef<HTMLTextAreaElement>;
  reviews = signal<Review[]>([]);
  rating: number = -1;
  review: Review  = {id: null, reviewContent: '', rating: 0, reviewDate: new Date(), userDto: null };


  constructor(protected authService: AuthenticationService, private reviewService: ReviewService) {
  }

  ngOnInit() {
    this.reviewService.getReviews().pipe(
      catchError(err => {
        console.error('Не удалось загрзить отзывы', err);
        return of([]);
      })
    ).subscribe(response => {
      this.reviews.set(response.data);
    })
  }

  updateCounter(){
    // Получаем доступ к DOM-элементам через nativeElement
    const reviewArea = this.reviewArea.nativeElement;
    const counter = this.counter.nativeElement;

    // Вычисляем оставшееся количество символов
    const remaining = 1000 - reviewArea.value.length;

    // Обновляем текст счетчика
    counter.textContent = `${reviewArea.value.length}/1000`;

    // Опционально: изменение цвета текста, если превышен лимит
    if (remaining < 0) {
      counter.style.color = 'red';
    } else {
      counter.style.color = 'inherit';
    }
  }

  getRatingClass(rating: number): string {
    if (rating >= 4) {
      return 'high-rating'; // Высокий рейтинг
    } else if (rating >= 2 && rating < 4) {
      return 'medium-rating'; // Средний рейтинг
    } else {
      return 'low-rating'; // Низкий рейтинг
    }
  }

  postReview(){
    this.review!.rating = this.rating;
    this.reviewService.createReview(this.review!).pipe(
      catchError(err => {
        console.error('Ошибка при создании отзыва',err);
        return of(null);
      })
    ).subscribe(response => {
      console.log('Успешное сохранение отзыва');
      this.reviews.update(reviews => [...reviews, response.data]);
    });
  }



}
