package com.jtelegram.ext.datepicker;

import com.jtelegram.api.TelegramBot;
import com.jtelegram.api.events.inline.keyboard.CallbackQueryEvent;
import com.jtelegram.api.inline.keyboard.InlineKeyboardMarkup;
import com.jtelegram.api.requests.inline.AnswerCallbackQuery;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * @author Nick Robson
 */
public class DatePickerReplyMarkup {

    private static final Set<Long> registeredCallbackBots = new HashSet<>();

    private DatePickerReplyMarkup() {}

    public static InlineKeyboardMarkup forLocale(Locale locale, LocalDate date) {
        // currently only supports Gregorian calendars
        // probably won't support others, so we'll see if anyone wants others
        DatePicker picker = new GregorianDatePicker(locale);
        return picker.toKeyboardMarkup(date);
    }

    public static void register(TelegramBot bot, BiConsumer<CallbackQueryEvent, LocalDate> onDateChosen) {
        if (onDateChosen != null) {
            if (!registeredCallbackBots.add(bot.getBotInfo().getId())) return; // already registered
            bot.getEventRegistry().registerEvent(CallbackQueryEvent.class, event -> {
                String callbackData = event.getQuery().getData();
                if (callbackData.startsWith(DatePicker.CB_PREFIX)) {
                    bot.perform(AnswerCallbackQuery.builder().queryId(event.getQuery().getId()).build());
                    String[] cbSplit = callbackData.substring(DatePicker.CB_PREFIX.length()).split(Pattern.quote("|"));
                    if (cbSplit.length != 2) return;
                    String[] dateSplit = cbSplit[0].split(":");
                    Locale locale = Locale.forLanguageTag(cbSplit[1]);
                    if (dateSplit.length == 2 || dateSplit.length == 3) {
                        int[] vals = new int[dateSplit.length];
                        for (int i = 0; i < vals.length; i++) {
                            try {
                                vals[i] = Integer.parseInt(dateSplit[i]);
                            } catch (NumberFormatException ignored) {
                                return; // invalid format
                            }
                        }
                        // 2: go-to month
                        // 3: chosen date
                        LocalDate localDate = LocalDate.of(vals[0], vals[1], vals.length > 2 ? vals[2] : 1);
                        if (vals.length == 2) {
                            InlineKeyboardMarkup replyMarkup = forLocale(locale, localDate);
                            bot.perform(event.getQuery().getMessage().toEditReplyMarkupRequest()
                                    .replyMarkup(replyMarkup)
                                    .build());
                        } else {
                            onDateChosen.accept(event, localDate);
                        }
                    }
                }
            });
        }
    }

}
