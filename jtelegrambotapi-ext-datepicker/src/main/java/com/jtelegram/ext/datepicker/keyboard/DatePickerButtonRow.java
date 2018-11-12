package com.jtelegram.ext.datepicker.keyboard;

import com.jtelegram.api.inline.keyboard.InlineKeyboardRow;
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
public class DatePickerButtonRow {

    @Nonnull
    @Singular
    private final List<DatePickerButton> buttons;

    public InlineKeyboardRow toInline(Locale locale) {
        return InlineKeyboardRow.builder()
                .buttons(buttons.stream().map(b -> b.toInline(locale)).collect(Collectors.toList()))
                .build();
    }

}
