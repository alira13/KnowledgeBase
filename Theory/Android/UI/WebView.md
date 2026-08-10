# WebView

Компонент для отображения веб-страниц внутри приложения (встроенный браузер на движке системного WebView/Chromium).

## Базовое использование
```kotlin
webView.settings.javaScriptEnabled = true      // включать осознанно — риск XSS
webView.webViewClient = WebViewClient()        // навигация внутри WebView, а не во внешнем браузере
webView.loadUrl("https://example.com")
```

## Взаимодействие Kotlin ↔ JS
- `addJavascriptInterface(obj, "Android")` — вызов Kotlin-методов из JS. **Опасно**: даёт JS доступ к нативному коду; помечайте методы `@JavascriptInterface`, не давайте доступ к чувствительным API, только для доверенного контента.
- `evaluateJavascript("...") { result -> }` — выполнить JS и получить результат.

## Безопасность (частый вопрос)
- Не включайте `javaScriptEnabled` без необходимости.
- Загружайте только доверенные/HTTPS-URL; проверяйте `shouldOverrideUrlLoading`.
- Отключите доступ к файловой системе (`allowFileAccess = false`), если не нужен.
- Уязвимости: XSS, утечка через JS-мост, MITM без TLS. См. [[Security. SSL Pinning, KeyStore, secrets]].

## Производительность и утечки
- WebView держит `Context`. Если передать `Activity` context и не уничтожить (`webView.destroy()` в `onDestroy`), — **утечка Activity**. Иногда WebView помещают в отдельный процесс.
- Тяжёлый компонент: инициализация движка дорогая.

## Когда использовать / альтернативы
- WebView — для веб-контента, гибридных экранов, OAuth-флоу (лучше **Custom Tabs** для внешнего логина — безопаснее и с общими cookies браузера).
- Для отображения простого HTML — иногда достаточно `Html.fromHtml`.

Связано: [[App startup process]], [[Memory leaks. Detection]]
