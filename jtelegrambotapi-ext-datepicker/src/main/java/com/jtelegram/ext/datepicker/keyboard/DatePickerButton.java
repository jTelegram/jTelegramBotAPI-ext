package com.jtelegram.ext.datepicker.keyboard;

import com.jtelegram.api.inline.keyboard.InlineKeyboardButton;
import com.jtelegram.ext.datepicker.picker.DatePicker;
import java.time.LocalDate;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Nick Robson
 */
@Getter
@Builder
@AllArgsConstructor
public class DatePickerButton {

    @Nullable
    private final String label;

    @Nonnull
    private final DatePickerButtonType type;

    @Nullable
    private final LocalDate date;

    @Nonnull
    public String getLabel() {
        return label != null ? label : " ";
    }

    public InlineKeyboardButton toInline(Locale locale) {
        switch (type) {
            case SELECT_DATE: {
                assert date != null;
                String callbackData = String.format(DatePicker.CB_SELECT_DAY_FORMAT, date.getYear(), date.getMonthValue(), date.getDayOfMonth(), locale.toLanguageTag());
                return InlineKeyboardButton.builder()
                        .label(getLabel())
                        .callbackData(callbackData)
                        .build();
            }
            case GOTO_MONTH: {
                assert date != null;
                String callbackData = String.format(DatePicker.CB_GOTO_MONTH_FORMAT, date.getYear(), date.getMonthValue(), locale.toLanguageTag());
                return InlineKeyboardButton.builder()
                        .label(getLabel())
                        .callbackData(callbackData)
                        .build();
            }
            case LABEL: {
                return InlineKeyboardButton.builder()
                        .label(getLabel())
                        .callbackData(DatePicker.CB_PREFIX)
                        .build();
            }
            default:
                throw new IllegalStateException("type must be SELECT_DATE, GOTO_MONTH, or LABEL");
        }
    }

}
