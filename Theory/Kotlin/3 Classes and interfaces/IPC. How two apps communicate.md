# IPC — межпроцессное взаимодействие

Каждое Android-приложение (обычно) — отдельный **процесс** с изолированной памятью. Чтобы обмениваться данными между процессами/приложениями, нужен IPC (Inter-Process Communication). Также встречается внутри одного приложения при `android:process`.

## Binder — фундамент Android IPC
**Binder** — механизм ядра Linux (драйвер), на котором построено **всё** взаимодействие с системными сервисами и между приложениями. Данные упаковываются в **Parcel** и передаются через binder-драйвер из одного процесса в другой. Поверх Binder работают `Intent`, `ContentProvider`, `Messenger`, системные `Service`-ы (ActivityManager и т.д.).

## Способы (от простого к сложному)
1. **Implicit Intent** — попросить систему выполнить действие; ответит любое подходящее приложение (открыть ссылку, поделиться, камера). См. [[Intent]].
2. **Explicit Intent** к компоненту другого приложения (нужны его package/class и разрешение/exported).
3. **BroadcastReceiver** — разослать событие подписчикам (сейчас ограничено с Android 8+). См. [[4 Broadcast Receiver]].
4. **ContentProvider** — структурированный доступ к данным другого приложения по URI (контакты, медиа). Другое приложение **не** может напрямую лезть в чужую Room/SharedPreferences — только через ContentProvider с разрешениями. См. [[3 Content Provider]].
5. **Messenger** — очередь сообщений между процессами (обёртка над Handler + Binder), последовательная обработка.
6. **AIDL** — когда нужен **прямой вызов методов** чужого сервиса с параллельными запросами.

## AIDL (Android Interface Definition Language)
Описывает интерфейс сервиса; из `.aidl` генерируется **Stub** (на стороне сервиса) и **Proxy** (на стороне клиента), которые маршалят/демаршалят параметры через Binder.
```aidl
// IRemoteService.aidl
interface IRemoteService {
    int add(int a, int b);
}
```
```kotlin
// клиент: bind к сервису и вызов «как локальный»
val api = IRemoteService.Stub.asInterface(binder)
val sum = api.add(2, 3)   // под капотом — транзакция через Binder
```
Типы: примитивы, `String`, `Parcelable`, коллекции. Вызовы приходят из **binder thread pool** → сервис должен быть потокобезопасным.

## Обмен данными «попроще»
- **Файлы / SharedPreferences / Room** — приватны для приложения; делиться через них между приложениями нельзя (только через ContentProvider/FileProvider).
- **Backend-сервер** — если приложения не на одном устройстве.

## Внутри приложения (не IPC, но частый смежный вопрос)
Передача между фрагментами/экранами: общая `ViewModel` (`activityViewModels`), **Fragment Result API**, аргументы (`Bundle`), Navigation Safe Args. См. [[1 ViewModel, ViewModelProvider]], [[Bundle]].

## Вопрос-ловушка
«Может ли одно приложение читать Room/SharedPreferences другого?» → нет, они в приватной песочнице; только через **ContentProvider**/**FileProvider** с разрешениями.

Связано: [[Intent]], [[3 Content Provider]], [[4 Broadcast Receiver]], [[0 Serialization. Serializable vs Parcelable]]
