import {AfterViewInit, Component} from '@angular/core';

// Объявляем глобальную переменную ymaps
declare const ymaps: any;

@Component({
  selector: 'app-yandex-map',
  standalone: true,
  imports: [],
  templateUrl: './yandex-map.component.html',
  styleUrl: './yandex-map.component.css'
})
export class YandexMapComponent implements AfterViewInit{

  ngAfterViewInit(): void {
    if (typeof ymaps !== 'undefined') {
      ymaps.ready(() => {
        const myMap = new ymaps.Map('map', {
          center: [53.211967, 50.177475],
          zoom: 17
        });

        // Создаем метку
        const myPlacemark = new ymaps.Placemark(
          [53.211967, 50.177475], // Координаты метки
          {
            hintContent: 'Вам сюда' // Подсказка при наведении
          },
          {
            iconLayout: 'default#image', // Использование стандартной иконки
            iconImageSize: [30, 42], // Размер иконки
            iconImageOffset: [-15, -42] // Смещение иконки
          }
        );

        // Добавляем метку на карту
        myMap.geoObjects.add(myPlacemark);
      });
    }
  }
}
