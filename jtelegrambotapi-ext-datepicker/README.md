# jTelegram Extensions: Date Picker

The Date Picker component allows users to easily select dates.

It uses menus and instances of `InlineKeyboardMarkup` to represent calendars.

It is fully localisable, using instances of `java.util.Locale`. Day and month names are set to reflect the chosen locale.

## Demo

Below are some examples of how the date picker keyboard looks.

The first shows an American calendar – note the week starting on Sunday, as is the American custom. Day and month names are in English.

The second shows a Swedish calendar – note the week starting on Monday, as is the Swedish custom. Day and month names are in Swedish.

![English](https://github.com/jTelegram/jTelegramBotAPI-ext/blob/master/jtelegrambotapi-ext-datepicker/assets/demo-en.png?raw=true)
![Swedish](https://github.com/jTelegram/jTelegramBotAPI-ext/blob/master/jtelegrambotapi-ext-datepicker/assets/demo-sv.png?raw=true)

## Documentation

### High-level: creating calendar menus

[A full example is available here.](https://gist.github.com/nickrobson/880a1306ffeacea61c14c7a47153a070)

With integration with `jtelegrambotapi-menus`, you can use the Menus API to generate calendars.

This is done through the [`DatePickerMenu`](https://github.com/jTelegram/jTelegramBotAPI-ext/blob/master/jtelegrambotapi-ext-datepicker/src/main/java/com/jtelegram/ext/datepicker/menu/DatePickerMenu.java) class and its builder.

The only required methods are `DatePickerMenuBuilder#bot`, `DatePickerMenuBuilder#datePicker`, and `DatePickerMenuBuilder#datePickerOptions`.

You can choose which days are highlighted on the calendar through methods in the [`DatePickerOptions`](https://github.com/jTelegram/jTelegramBotAPI-ext/blob/master/jtelegrambotapi-ext-datepicker/src/main/java/com/jtelegram/ext/datepicker/picker/DatePickerOptions.java) class and its builder.

You can restrict who can select months (via the top row of buttons) using `DatePickerMenuBuilder#monthSelectionPredicate`.

You can restrict who can select days (via the rows representing the calendar) using `DatePickerMenuBuilder#dateSelectionPredicate`.

You can get callbacks for when days are clicked using `DatePickerMenuBuilder#dateSelectionConsumer`.

You can optionally choose the starting date/month of the menu using `DatePickerMenuBuilder#selectedDate` – by default it is set to `LocalDate.now()`.

You can optionally set the message text of the message the menu is attached to using `DatePickerMenuBuilder#messageSupplier` – by default the message contents will remain unchanged.

You can optionally specify an error handler using `DatePickerMenuBuilder#errorHandler`.

### Low-level: creating calendar markups

You can create a DatePicker instance for a specific locale using `DatePickerExtension#forLocale(Locale)`.

The locale provided dictates day and month names, and which day of the week should be used as the first day of the week. You should use `Locale.ROOT` if you are not targeting one country/culture in particular. Alternatively, you can make the calendar use a user's locale, using `Locale.forLanguageTag(user.getLanguageCode())`.

Example usage:
* creates a calendar with the American locale (which sets the day and month names and which day the week starts on)
* starts showing today's month (using `LocalDate.now()`)
* no dates are highlighted

```java
DatePicker datePicker = DatePickerExtension.forLocale(Locale.US);
DatePickerKeyboard keyboard = datePicker.toKeyboard(LocalDate.now());
InlineKeyboardMarkup keyboardMarkup = keyboard.toInline();
```

Example usage: same as above, but today's date is highlighted (has a • next to it)

```java
LocalDate today = LocalDate.now();
DatePicker datePicker = DatePickerExtension.forLocale(Locale.US);
DatePickerOptions datePickerOptions = DatePickerOptions.builder().dateHighlightedPredicate(today::isEqual).build();
DatePickerKeyboard keyboard = datePicker.toKeyboard(today, datePickerOptions);
InlineKeyboardMarkup keyboardMarkup = keyboard.toInline();
```
