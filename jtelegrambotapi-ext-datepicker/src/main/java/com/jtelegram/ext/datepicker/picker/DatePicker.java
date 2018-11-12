package com.jtelegram.ext.datepicker.picker;

import com.jtelegram.ext.datepicker.keyboard.DatePickerKeyboard;
import java.time.LocalDate;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A component interface for specifying a format for how the date picker will be displayed.
 *
 * The only implementation currently is {@link GregorianDatePicker}.
 *
 * @author Nick Robson
 */
public interface DatePicker {

    String CB_PREFIX = "ext:datepicker:";
    String CB_GOTO_MONTH_FORMAT = CB_PREFIX + "%d:%d|%s";    // year:month    |locale
    String CB_SELECT_DAY_FORMAT = CB_PREFIX + "%d:%d:%d|%s"; // year:month:day|locale

    @Nonnull
    Locale getLocale();

    @Nonnull
    default DatePickerKeyboard toKeyboard(@Nonnull LocalDate date) {
        return toKeyboard(date, null);
    }

    @Nonnull
    DatePickerKeyboard toKeyboard(@Nonnull LocalDate date, @Nullable DatePickerOptions options);

}
