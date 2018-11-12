package com.jtelegram.ext.datepicker.keyboard;

import com.jtelegram.api.inline.keyboard.InlineKeyboardMarkup;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * @author Nick Robson
 */
@Getter
@Builder
@AllArgsConstructor
public class DatePickerKeyboard {

    @Nonnull
    private final Locale locale;

    @Nonnull
    @Singular
    private final List<DatePickerButtonRow> rows;

    public InlineKeyboardMarkup toInline() {
        return InlineKeyboardMarkup.builder()
                .inlineKeyboard(rows.stream().map(r -> r.toInline(locale)).collect(Collectors.toList()))
                .build();
    }

}
