# SpringDemoBot
Дипломна робота Ковальчука Ярослава.

- Телеграм Бот написаний на мові програмування Java.

- Головная Ідея боту - полегшення роботи та скорочення часу на пошук інформації для людей,
які працюють в сфері Зовнішньо Економічної Діяльності, Міжнародної Логістики та Ланцюгів Поставок.

- Перелік Проблем та їх вирішення:

1. Доступ до інформації про прибуття контейнера в порт. 
Зазвичай Менеджерам ЗЕД які купують товар за кордоном, цю інформацію надають логісти, які працюють в логістичних компаніях, 
які в свою чергу працюють з судоходними лініями.
Ланцюг: Менеджер ЗЕД працюючий в Компанії "А", звертаеється до Лолгіста працюючого в компанії "Б",
який в свою чергу для надання відповіді, звертається до сайту, або менеджера судоходної лінії компанії "В" для оновлення інформації, що до прибуття контейнерів в порт.
Або ж, раз в тиждень Логіст компанії Б, збирає всю інформацію по контейнерам в таблицю, звертаючись до всіх судоходних компаній "В",
для надання повної інформації по контейнерам компанії "А".
Тобто або швидко потрібно задіяти двох людей щоб отримати інформацію (в залежності від зайнятості кожної людини це від 15хв. до 1 год.),
або чекати на таблицю раз в тиждень.

Для чого потрібно знати дату приходу контейнера в порт доставки? 
- Для вчасної підготовки та відправки необхідних документів для Порту та Митниці.
- Для вчасного попередження Брокерів які подають єлектронні запити до порту та портової митниці (ПП/ПД) щоб контейнер випустили з порту.
- Для завчасного замовлення автомобіля який довезе контейнер до складу.
- Для планування та правильного розподілу робочого часу.
Для розуміння, зазвичай в порту контейнер безкоштовно може стояти від 5 до 14 днів, після цього кожен з наступних 7 днів коштує 50$, після цього кожен день 100$.
Не кажучи вже про кожний день простоя, при якому товар зімість того щоб продаватись і якнайшвидше відбивати вкладені кошти, лежить в контейнері,
а ще додавши сюди сезонність продажів можна взагалі заморозити кошти в товарі на цілий рік.

Вирішення: Будь хто, з ланцюгу "А","Б" чи "В" може дізнатися потрібну інформацію просто відправивши номер контейнера Чат Боту в Телеграмі.
Список дій - 1. відкрити телеграм, 2. зайти в чат Бота, 3. скопіювати номер контейнера, 4. вставити номер контейнера в чат бота, 5. натиснути відправити.
Результат - відповід за 1 секнду:

**
Container №: MRKU5091136
Container size: 40

FROM: Itapoa
Brazil

TO: Qingdao
China

ETA (ESTIMATED TIME of ARRIVAL): 16.05.2023
**

2. Додаткова інформація, Курс Валют НБУ.
Для чого потрібно? -  Актуальний курс валют можна побачити в будь якому додатку банку, яким користується людина, але там зазвичай курс обмінника.
Для розрахунку митних платежів та розрахунків між компаніями викристовується курс НБУ, це все враховується в затратах.
Телеграм бот парсить реальну та свіжу інформацію з сайту мінфін.

3. Додаткова інформація, об'єм контейнерів.
Шпаглака для кожного працівника вище перераховних сфер. 
Для чого потрібно? - для розрахунку можливості замовлення товару який точно зможе поміститися в контейнер і навпаки.
Якщо пошукати в гуглі інформацію про розміри контейнерів, знайти можна тільки об'єм повітря в контейнері. 
В цій шпаргалці записано реальний(корисний) об'єм контейнеру, при завантаженній його коробками з товаром.

