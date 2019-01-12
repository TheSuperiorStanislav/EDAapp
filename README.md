# EDAapp

## Описание

Редактор схем(net-файлы) и трассировка схем

## Архитектура приложения

Использовалась [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html). Само приложение состит из трех частей Presentation, Domain, Data.

![](https://raw.githubusercontent.com/wiki/googlesamples/android-architecture/images/mvp-clean.png)

* Presentation - Отображение данных.
* Domain - Поддерживает всю бизнес-логику. Use case представляют собой все возможные действия (операции).
* Data - Работает с данными.

## Модули приложения

### utils
* math - расчеты: матрицы и трассировка
* gratics - графика
* file - работа с файлами.
* view - для работы view-элементами

### editor
Редактор схемы

### routing
Трассировка схемы
