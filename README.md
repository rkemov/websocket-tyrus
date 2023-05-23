# Deploy

Должна быть установлена JVM. Я использовал JDK 17.0.2. Также должен быть установлен Docker. У меня винда с Docker Desktop.
Для реализации websocket сервера используется tyrus 2.1.3, который использует jakarta.websocket-api.
Также проверена реализация на tyrus 1.2 с javax.websocket-api. В этом случае расход памяти heap в 2 раза выше (на тестах с 10000 клиентов).
Чтобы гарантировать уникальность случайных чисел независимо от количества нод сервера и ребутов, используется переменная lowerBound в Redis.
Все ноды должны быть подключены к одному Редису. Каждый сервер при старте будет читать из Редиса lowerBound и использовать интервал случайных чисел 
ОТ lowerBound ДО lowerBound + randomizeInterval (константа).
Проверка уникальных IP в классе org.example.websocket.RandomServerEndpoint.
1) Можно использовать IntelliJ Idea
2) На всякий случай инструкция по установке Redis на docker:
   https://koukia.ca/installing-redis-on-windows-using-docker-containers-7737d2ebc25e 
Скачиваем образ командой:
```
   docker pull redis
```
3) Запустим докер, затем запустим контейнер командой - по умолчанию порт 6379. 
```
docker run --rm -p 6379:6379 --name some-redis -d redis
```
4) Запускаем Сервер в классе org.example.websocket.AppServer
Он запустится по адресу ws://localhost:8033/webs/app
5) Затем можно запускать любые клиенты методом main:
   1) Один консольный. org.example.websocket.AppConsole
   2) 10000 клиентов, каждый из которых отправляет запрос раз в секунду. org.example.websocket.AppHighloadMultiClient
   3) Один клиент с 10000 моментальных запросов. org.example.websocket.AppHighloadSingleClient
Можно собрать клиент в виде jar.

# Tests
Unit test только для сервиса org.example.websocket.service.RandomSyncService
Адреса и прочие настройки пока не выносил в переменные 