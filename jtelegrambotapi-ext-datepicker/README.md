# jTelegram Extensions: Date Picker

The Date Picker component allows users to easily select a date.

It generates `InlineKeyboardMarkup` instances to represent calendars.

It is fully localisable, using instances of `java.util.Locale`. Day and month names are set to reflect the chosen locale.

## Demo

Below are some examples of how the date picker keyboard looks.

The first shows an American calendar – note the week starting on Sunday, as is the American custom. Day and month names are in English.

The second shows a Swedish calendar – note the week starting on Monday, as is the Swedish custom. Day and month names are in Swedish.

![English](https://github.com/jTelegram/jTelegramBotAPI-ext/blob/master/jtelegrambotapi-ext-datepicker/assets/demo-en.png?raw=true)
![Swedish](https://github.com/jTelegram/jTelegramBotAPI-ext/blob/master/jtelegrambotapi-ext-datepicker/assets/demo-sv.png?raw=true)

## Documentation

### Registration

You should register your bot using `DatePickerReplyMarkup#register(TelegramBot, BiConsumer<CallbackQueryEvent, LocalDate>)`.

The first parameter is the bot instance.

The second parameter is a callback which will be called with two arguments:
* `event` – the `CallbackQueryEvent` that caused the callback, containing the chat and user
* `date` – a `LocalDate` instance corresponding to the date the user clicked on

Example usage, printing out dates as users click on them:

```java
DatePickerReplyMarkup.register(bot, (event, date) -> {
    System.out.format(
            "@%s clicked %s\n",
            event.getQuery().getFrom().getUsername(),
            date.toString()
    );
});
```

### Generating calendars

You can generate calendars using `DatePickerReplyMarkup#forLocale(Locale, LocalDate)`.

The first parameter is the locale to use. This dictates day and month names, and which day of the week should be used as the first day of the week. You should use `Locale.ROOT` if you are not targeting one country/culture in particular. Alternatively, you can make the calendar use a user's locale, using `Locale.forLanguageTag(user.getLanguageCode())`.

The second parameter is a date inside the month that will be displayed first. It is recommended to use `LocalDate.now()`, as this will start from the current month.

This methods returns an `InlineKeyboardMarkup` instance, which can then be used in sending or editing messages.

Example usage, creating a calendar with the American locale (which sets the day and month names and which day the week starts on) and today's date to begin showing the month of:

```java
DatePickerReplyMarkup.forLocale(Locale.US, LocalDate.now())
```
