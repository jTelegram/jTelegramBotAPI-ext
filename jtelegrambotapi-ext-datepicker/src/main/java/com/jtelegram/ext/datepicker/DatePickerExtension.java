package com.jtelegram.ext.datepicker;

import com.jtelegram.ext.datepicker.picker.DatePicker;
import com.jtelegram.ext.datepicker.picker.GregorianDatePicker;
import java.util.Locale;
import javax.annotation.Nonnull;

/**
 * @author Nick Robson
 */
public class DatePickerExtension {

    private DatePickerExtension() {}

    @Nonnull
    public static DatePicker forLocale(@Nonnull Locale locale) {
        // currently only supports Gregorian calendars
        // probably won't support others, so we'll see if anyone wants others
        return new GregorianDatePicker(locale);
    }

}
