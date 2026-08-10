до 1мб

## **Ограничения объёма данных для передачи через Intent/Bundle**

## **1. Ограничения Binder-транзакций**

При передаче данных между компонентами Android (например, через `Intent`) используется **Binder**, который имеет жёсткое ограничение:

- **Максимальный размер транзакции**: **1 МБ** для всех активных транзакций процесса[1](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android)[2](https://habr.com/ru/companies/dododev/articles/694746/).
    
- **Последствия превышения**: выбрасывается исключение `TransactionTooLargeException` с сообщением `!!! FAILED BINDER TRANSACTION !!!`[1](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android).
    

**Примеры ситуаций**:

kotlin

`// Передача Intent между Activity val intent = Intent(this, AnotherActivity::class.java) intent.putExtra("large_data", largeSerializableObject) // Риск ошибки при размере >1MB`

## **2. Сохранение состояния Activity (onSaveInstanceState)**

Для сохранения состояния Activity через `Bundle`:

- **Рекомендуемый лимит**: **50 КБ** (официальная рекомендация Google)[1](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android)[5](https://ru.stackoverflow.com/questions/486966/%D0%A1%D0%BA%D0%BE%D0%BB%D1%8C%D0%BA%D0%BE-%D0%BC%D0%B0%D0%BA%D1%81%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE-%D0%BC%D0%BE%D0%B6%D0%BD%D0%BE-%D1%81%D0%BE%D1%85%D1%80%D0%B0%D0%BD%D0%B8%D1%82%D1%8C-%D0%B2-bundle-%D0%BF%D1%80%D0%B8-%D1%81%D0%B2%D0%BE%D1%80%D0%B0%D1%87%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B8).
    
- **Фактический лимит**: **~500 КБ** (начиная с Android 7.0), превышение вызывает исключение[1](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android)[5](https://ru.stackoverflow.com/questions/486966/%D0%A1%D0%BA%D0%BE%D0%BB%D1%8C%D0%BA%D0%BE-%D0%BC%D0%B0%D0%BA%D1%81%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE-%D0%BC%D0%BE%D0%B6%D0%BD%D0%BE-%D1%81%D0%BE%D1%85%D1%80%D0%B0%D0%BD%D0%B8%D1%82%D1%8C-%D0%B2-bundle-%D0%BF%D1%80%D0%B8-%D1%81%D0%B2%D0%BE%D1%80%D0%B0%D1%87%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B8).
    
- **Риски**:
    
    - Система хранит данные до тех пор, пока пользователь может вернуться к Activity, что может привести к утечкам памяти.
        
    - На старых версиях Android (до 7.0) превышение лимита вызывало только предупреждения, но не крах[1](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android).
        

## **3. Рекомендации по работе с большими данными**

|**Ситуация**|**Решение**|
|---|---|
|Передача между Activity|Используйте **файлы** или **базу данных** вместо Bundle[1](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android)[2](https://habr.com/ru/companies/dododev/articles/694746/).|
|Сохранение состояния|Ограничьте данные до **50 КБ**. Для больших объектов используйте **ViewModel** или **Room**[1](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android)[5](https://ru.stackoverflow.com/questions/486966/%D0%A1%D0%BA%D0%BE%D0%BB%D1%8C%D0%BA%D0%BE-%D0%BC%D0%B0%D0%BA%D1%81%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE-%D0%BC%D0%BE%D0%B6%D0%BD%D0%BE-%D1%81%D0%BE%D1%85%D1%80%D0%B0%D0%BD%D0%B8%D1%82%D1%8C-%D0%B2-bundle-%D0%BF%D1%80%D0%B8-%D1%81%D0%B2%D0%BE%D1%80%D0%B0%D1%87%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B8).|
|Обмен между процессами|Используйте **ContentProvider** или **IPC-механизмы** с ограничением объёма[2](https://habr.com/ru/companies/dododev/articles/694746/).|

## **4. Примеры кода**

**Ошибка при передаче большого объекта**:

kotlin

`// Ошибка при размере >1MB val intent = Intent() intent.putExtra("data", largeByteArray) // TransactionTooLargeException`

**Оптимизация через файл**:

kotlin

`// Сохранение в файл val file = File(cacheDir, "data.bin") largeByteArray.writeTo(file.outputStream()) // Передача пути val intent = Intent() intent.putExtra("file_path", file.absolutePath)`

## **Итог**

- **Intent/Binder**: **1 МБ** — жёсткий лимит для всех транзакций процесса[1](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android)[2](https://habr.com/ru/companies/dododev/articles/694746/).
    
- **onSaveInstanceState**: **~500 КБ** (Android 7.0+), но лучше не превышать **50 КБ**[1](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android)[5](https://ru.stackoverflow.com/questions/486966/%D0%A1%D0%BA%D0%BE%D0%BB%D1%8C%D0%BA%D0%BE-%D0%BC%D0%B0%D0%BA%D1%81%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE-%D0%BC%D0%BE%D0%B6%D0%BD%D0%BE-%D1%81%D0%BE%D1%85%D1%80%D0%B0%D0%BD%D0%B8%D1%82%D1%8C-%D0%B2-bundle-%D0%BF%D1%80%D0%B8-%D1%81%D0%B2%D0%BE%D1%80%D0%B0%D1%87%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B8).
    
- **Оптимизация**: Для больших данных используйте **файлы**, **базы данных** или **ViewModel**.
    

### Citations:

1. [https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android](https://stackoverflow.com/questions/8552514/is-there-any-limit-of-bundle-in-android)
2. [https://habr.com/ru/companies/dododev/articles/694746/](https://habr.com/ru/companies/dododev/articles/694746/)
3. [https://vk.com/@yandex4developers-post-leshi-cvetkova-android-appbundle-i-dynamic-feature-modu](https://vk.com/@yandex4developers-post-leshi-cvetkova-android-appbundle-i-dynamic-feature-modu)
4. [https://startandroid.ru/ru/uroki/vse-uroki-spiskom/131-urok-68-nemnogo-o-parcel.html](https://startandroid.ru/ru/uroki/vse-uroki-spiskom/131-urok-68-nemnogo-o-parcel.html)
5. [https://ru.stackoverflow.com/questions/486966/%D0%A1%D0%BA%D0%BE%D0%BB%D1%8C%D0%BA%D0%BE-%D0%BC%D0%B0%D0%BA%D1%81%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE-%D0%BC%D0%BE%D0%B6%D0%BD%D0%BE-%D1%81%D0%BE%D1%85%D1%80%D0%B0%D0%BD%D0%B8%D1%82%D1%8C-%D0%B2-bundle-%D0%BF%D1%80%D0%B8-%D1%81%D0%B2%D0%BE%D1%80%D0%B0%D1%87%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B8](https://ru.stackoverflow.com/questions/486966/%D0%A1%D0%BA%D0%BE%D0%BB%D1%8C%D0%BA%D0%BE-%D0%BC%D0%B0%D0%BA%D1%81%D0%B8%D0%BC%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE-%D0%BC%D0%BE%D0%B6%D0%BD%D0%BE-%D1%81%D0%BE%D1%85%D1%80%D0%B0%D0%BD%D0%B8%D1%82%D1%8C-%D0%B2-bundle-%D0%BF%D1%80%D0%B8-%D1%81%D0%B2%D0%BE%D1%80%D0%B0%D1%87%D0%B8%D0%B2%D0%B0%D0%BD%D0%B8%D0%B8)
6. [https://support.google.com/googleplay/android-developer/answer/9859372](https://support.google.com/googleplay/android-developer/answer/9859372)
7. [https://qna.habr.com/q/270046](https://qna.habr.com/q/270046)
8. [https://metanit.com/java/android/12.3.php](https://metanit.com/java/android/12.3.php)

---

Answer from Perplexity: [pplx.ai/share](https://www.perplexity.ai/search/pplx.ai/share)