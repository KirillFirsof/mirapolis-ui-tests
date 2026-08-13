mirapolis-ui-tests

Автотесты для страницы авторизации Mirapolis LMS.

Технологии: Java 17, Selenide, JUnit 5, Maven.


Запуск:
git clone https://github.com/KirillFirsof/mirapolis-ui-tests.git
cd mirapolis-ui-tests
mvn clean install
mvn test

Настройки:

src/test/resources/selenide.properties

Пример:

selenide.browsers=chrome,firefox          Можно добавить браузеры или оставить только один если излишне

selenide.resolutions=1920x1080,375x812    Можно добавить разрешения или оставить одно, если излишне

тестирование проводится на всех указанных браузерах и расширениях, благодаря использованию параметризированных тестов

Тесты:
loginPageShouldBeVisible - успешная загрузка страницы

successfulLoginTest — успешный вход

negativeLoginTest — неверные данные (различные комбинации неверного ввода реализованы через один параметризированный тест)

forgotPasswordLinkTest — переход на восстановление пароля

backToLoginLinkTest — возврат на страницу входа

recoveryAfterErrorTest — закрытие alert и возврат на страницу входа


Описание всех тест кейсов находится тут:
MirapolisUITests\docs\тест кейсы.docx

