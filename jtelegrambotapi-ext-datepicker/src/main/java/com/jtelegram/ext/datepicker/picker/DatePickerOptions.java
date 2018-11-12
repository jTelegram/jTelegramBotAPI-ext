package com.jtelegram.ext.datepicker.picker;

import java.time.LocalDate;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Nick Robson
 */
@Getter
@Builder(toBuilder = true)
public class DatePickerOptions {

    @Nullable
    private final Predicate<LocalDate> dateHighlightedPredicate;

}
