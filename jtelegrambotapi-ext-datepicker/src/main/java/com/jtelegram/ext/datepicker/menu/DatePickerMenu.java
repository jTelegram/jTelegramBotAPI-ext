package com.jtelegram.ext.datepicker.menu;

import com.jtelegram.api.TelegramBot;
import com.jtelegram.api.events.inline.keyboard.CallbackQueryEvent;
import com.jtelegram.api.ex.TelegramException;
import com.jtelegram.api.menu.Menu;
import com.jtelegram.api.menu.MenuRow;
import com.jtelegram.api.util.TextBuilder;
import com.jtelegram.ext.datepicker.keyboard.DatePickerKeyboard;
import com.jtelegram.ext.datepicker.picker.DatePicker;
import com.jtelegram.ext.datepicker.picker.DatePickerOptions;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Builder;

/**
 * @author Nick Robson
 */
public class DatePickerMenu extends Menu {

    @Nonnull
    private final DatePicker datePicker;

    @Nonnull
    private final DatePickerOptions datePickerOptions;

    @Nonnull
    private final BiPredicate<CallbackQueryEvent, LocalDate> monthSelectionPredicate;

    @Nonnull
    private final BiPredicate<CallbackQueryEvent, LocalDate> dateSelectionPredicate;

    @Nonnull
    private final BiConsumer<CallbackQueryEvent, LocalDate> dateSelectionConsumer;

    @Nonnull
    private final Consumer<TelegramException> errorHandler;

    @Nonnull
    private LocalDate selectedDate;

    @Nonnull
    private LocalDate selectedMonth;

    @Nullable
    private Supplier<TextBuilder> messageSupplier;

    @Builder
    protected DatePickerMenu(
            @Nonnull TelegramBot bot,
            @Nonnull DatePicker datePicker,
            @Nonnull DatePickerOptions datePickerOptions,
            @Nullable LocalDate selectedDate,
            @Nullable BiPredicate<CallbackQueryEvent, LocalDate> monthSelectionPredicate,
            @Nullable BiPredicate<CallbackQueryEvent, LocalDate> dateSelectionPredicate,
            @Nullable BiConsumer<CallbackQueryEvent, LocalDate> dateSelectionConsumer,
            @Nullable Consumer<TelegramException> errorHandler,
            @Nullable Supplier<TextBuilder> messageSupplier) {
        super(bot);
        this.datePicker = datePicker;
        this.datePickerOptions = datePickerOptions;
        this.selectedDate = selectedDate != null ? selectedDate : LocalDate.now();
        this.selectedMonth = this.selectedDate.withDayOfMonth(1);
        this.monthSelectionPredicate = monthSelectionPredicate != null ? monthSelectionPredicate : (e, d) -> true;
        this.dateSelectionPredicate = dateSelectionPredicate != null ? dateSelectionPredicate : (e, d) -> true;
        this.dateSelectionConsumer = dateSelectionConsumer != null ? dateSelectionConsumer : (e, d) -> {};
        this.errorHandler = errorHandler != null ? errorHandler : Throwable::printStackTrace;
        this.messageSupplier = messageSupplier;
    }

    private boolean isDateHighlighted(LocalDate date) {
        return date.isEqual(selectedDate)
                || (this.datePickerOptions.getDateHighlightedPredicate() != null
                    && this.datePickerOptions.getDateHighlightedPredicate().test(date));
    }

    private boolean onClickDate(CallbackQueryEvent event, LocalDate date) {
        if (dateSelectionPredicate.test(event, date)) {
            dateSelectionConsumer.accept(event, date);
            if (!selectedDate.isEqual(date)) {
                selectedDate = date;
                return true;
            }
        }
        return false;
    }

    private boolean onClickMonth(CallbackQueryEvent event, LocalDate date) {
        if (monthSelectionPredicate.test(event, date)) {
            if (!selectedMonth.isEqual(date)) {
                selectedMonth = date;
                return true;
            }
        }
        return false;
    }

    @Override
    public TextBuilder getMenuMessage() {
        return messageSupplier != null ? messageSupplier.get() : super.getMenuMessage();
    }

    @Override
    public List<MenuRow> getRows() {
        DatePickerKeyboard menu = datePicker.toKeyboard(
                selectedMonth,
                datePickerOptions.toBuilder()
                        .dateHighlightedPredicate(this::isDateHighlighted)
                        .build()
        );

        return menu.getRows().stream()
                .map(r -> new MenuRow(r.getButtons()
                        .stream()
                        .map(b -> DatePickerMenuButton.builder()
                                .button(b)
                                .monthSelectionConsumer(this::onClickMonth)
                                .dateSelectionConsumer(this::onClickDate)
                                .build())
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public void handleException(TelegramException exception) {
        errorHandler.accept(exception);
    }

}
