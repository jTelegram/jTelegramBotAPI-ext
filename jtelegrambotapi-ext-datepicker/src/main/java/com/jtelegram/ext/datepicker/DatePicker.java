package com.jtelegram.ext.datepicker;

import com.jtelegram.api.inline.keyboard.InlineKeyboardMarkup;
import java.time.LocalDate;
import java.util.Locale;

/**
 * A component interface for specifying a format for how the date picker will be displayed.
 *
 * The only implementation currently is {@link GregorianDatePicker}.
 *
 * @author Nick Robson
 */
public interface DatePicker {

    String CB_PREFIX = "ext:datepicker:";
    String GOTO_MONTH_FORMAT = CB_PREFIX + "%d:%d|%s";    // year:month    |locale
    String SELECT_DAY_FORMAT = CB_PREFIX + "%d:%d:%d|%s"; // year:month:day|locale

    Locale getLocale();

    InlineKeyboardMarkup toKeyboardMarkup(LocalDate date);

}
