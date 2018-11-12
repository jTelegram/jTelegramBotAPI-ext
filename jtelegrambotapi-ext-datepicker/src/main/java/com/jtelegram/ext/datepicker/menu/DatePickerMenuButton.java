package com.jtelegram.ext.datepicker.menu;

import com.jtelegram.api.events.inline.keyboard.CallbackQueryEvent;
import com.jtelegram.api.menu.MenuButton;
import com.jtelegram.ext.datepicker.keyboard.DatePickerButton;
import java.time.LocalDate;
import java.util.function.BiPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * @author Nick Robson
 */
@Builder
@AllArgsConstructor
public class DatePickerMenuButton extends MenuButton {

    @Nonnull
    private DatePickerButton button;

    @Nullable
    private final BiPredicate<CallbackQueryEvent, LocalDate> monthSelectionConsumer;

    @Nullable
    private final BiPredicate<CallbackQueryEvent, LocalDate> dateSelectionConsumer;

    @Nonnull
    @Override
    public String getLabel() {
        return button.getLabel();
    }

    @Override
    public boolean onPress(CallbackQueryEvent event) {
        switch (button.getType()) {
            case GOTO_MONTH:
                if (monthSelectionConsumer != null) {
                    return monthSelectionConsumer.test(event, button.getDate());
                }
                break;
            case SELECT_DATE:
                if (dateSelectionConsumer != null) {
                    return dateSelectionConsumer.test(event, button.getDate());
                }
                break;
        }
        return false;
    }
}
