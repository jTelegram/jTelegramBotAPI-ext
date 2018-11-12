package com.jtelegram.ext.datepicker.picker;

import com.jtelegram.ext.datepicker.keyboard.DatePickerButton;
import com.jtelegram.ext.datepicker.keyboard.DatePickerButtonRow;
import com.jtelegram.ext.datepicker.keyboard.DatePickerButtonType;
import com.jtelegram.ext.datepicker.keyboard.DatePickerKeyboard;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Nick Robson
 */
public class GregorianDatePicker implements DatePicker {

    @Nonnull private final Locale locale;
    @Nonnull private final WeekFields weekFields;
    @Nonnull private final DateTimeFormatter monthAndYearFormat;
    @Nonnull private final DateTimeFormatter shortDayFormat;
    @Nonnull private final DateTimeFormatter dayNumberFormat;

    public GregorianDatePicker(@Nonnull Locale locale) {
        this.locale = locale;
        this.weekFields = WeekFields.of(locale);
        this.monthAndYearFormat = DateTimeFormatter.ofPattern("MMMM y", locale);
        this.shortDayFormat = DateTimeFormatter.ofPattern("EE", locale);
        this.dayNumberFormat = DateTimeFormatter.ofPattern("d", locale);
    }

    @Nonnull
    @Override
    public Locale getLocale() {
        return locale;
    }

    @Nonnull
    @Override
    public DatePickerKeyboard toKeyboard(@Nonnull LocalDate date, @Nullable DatePickerOptions options) {
        Objects.requireNonNull(date, "date cannot be null");

        if (options == null) {
            options = DatePickerOptions.builder().build();
        }

        List<DatePickerButtonRow> rows = new ArrayList<>();
        rows.addAll(getMonthRows(date, options));
        rows.addAll(getWeeksRows(date, options));

        return DatePickerKeyboard.builder()
                .locale(locale)
                .rows(rows)
                .build();
    }

    @Nonnull
    private List<DatePickerButtonRow> getMonthRows(@Nonnull LocalDate date, @Nonnull DatePickerOptions options) {
        final LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        final LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        DatePickerButtonRow.DatePickerButtonRowBuilder rowBuilder = DatePickerButtonRow.builder();

        // show << (previous year) when not first year
        if (date.getYear() != LocalDate.MIN.getYear()) {
            LocalDate previousYear = firstDayOfMonth.minusYears(1);
            rowBuilder.button(
                    DatePickerButton.builder()
                            .type(DatePickerButtonType.GOTO_MONTH)
                            .date(previousYear)
                            .label("\u00ab")
                            .build()
            );
        }
        // show < (previous month) when not first month of first year
        if (firstDayOfMonth.isAfter(LocalDate.MIN)) {
            LocalDate previousMonth = firstDayOfMonth.minusMonths(1);
            rowBuilder.button(
                    DatePickerButton.builder()
                            .type(DatePickerButtonType.GOTO_MONTH)
                            .date(previousMonth)
                            .label("\u2039")
                            .build()
            );
        }

        // show > (next month) when not last month of last year
        if (lastDayOfMonth.isBefore(LocalDate.MAX)) {
            LocalDate nextMonth = lastDayOfMonth.plusMonths(1);
            rowBuilder.button(
                    DatePickerButton.builder()
                            .type(DatePickerButtonType.GOTO_MONTH)
                            .date(nextMonth)
                            .label("\u203A")
                            .build()
            );
        }

        // show >> (next year) when not last year
        if (date.getYear() != LocalDate.MAX.getYear()) {
            LocalDate nextYear = lastDayOfMonth.plusYears(1);
            rowBuilder.button(
                    DatePickerButton.builder()
                            .type(DatePickerButtonType.GOTO_MONTH)
                            .date(nextYear)
                            .label("\u00bb")
                            .build()
            );
        }

        // show month name and year; e.g. "January 2018"
        return Arrays.asList(
                rowBuilder.build(),
                DatePickerButtonRow.builder()
                        .button(
                                DatePickerButton.builder()
                                        .type(DatePickerButtonType.LABEL)
                                        .label(monthAndYearFormat.format(date))
                                        .build())
                        .build()
        );
    }

    @Nonnull
    private List<DatePickerButtonRow> getWeeksRows(@Nonnull LocalDate date, @Nonnull DatePickerOptions options) {
        List<DatePickerButtonRow> rows = new ArrayList<>();

        int weekStart = weekFields.getFirstDayOfWeek().getValue();
        long daysInWeek = ChronoField.DAY_OF_WEEK.range().getMaximum();

        LocalDate firstDayOfWeek = date.with(ChronoField.DAY_OF_WEEK, weekStart);
        LocalDate firstDayOfNextWeek = firstDayOfWeek.plusDays(daysInWeek);

        DatePickerButtonRow.DatePickerButtonRowBuilder headerRow = DatePickerButtonRow.builder();
        LocalDate current = firstDayOfWeek;
        while (current.isBefore(firstDayOfNextWeek)) {
            headerRow.button(DatePickerButton.builder()
                    .type(DatePickerButtonType.LABEL)
                    .label(shortDayFormat.format(current))
                    .build());
            current = current.plusDays(1);
        }
        rows.add(headerRow.build());

        LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        firstDayOfWeek = firstDayOfMonth.with(ChronoField.DAY_OF_WEEK, weekStart);
        if (firstDayOfMonth.isBefore(firstDayOfWeek))
            firstDayOfNextWeek = firstDayOfWeek;
        else
            firstDayOfNextWeek = firstDayOfWeek.plusDays(daysInWeek);

        long paddingDays = firstDayOfMonth.getDayOfWeek().getValue() - weekStart;
        if (paddingDays < 0) paddingDays += daysInWeek;
        current = firstDayOfMonth;
        while (!current.isAfter(lastDayOfMonth)) {
            DatePickerButtonRow.DatePickerButtonRowBuilder rowBuilder = DatePickerButtonRow.builder();
            // if month starts on Monday, but the 1st is a Wednesday, pad in 2 days
            while (paddingDays > 0) {
                rowBuilder.button(DatePickerButton.builder().type(DatePickerButtonType.LABEL).build());
                --paddingDays;
            }

            // add in all the days from current to firstDayOfNextWeek or lastDayOfMonth, whichever's earliest
            while (current.isBefore(firstDayOfNextWeek) && !current.isAfter(lastDayOfMonth)) {
                boolean selected = options.getDateHighlightedPredicate() != null && options.getDateHighlightedPredicate().test(current);
                String label = dayNumberFormat.format(current) + (selected ? "•" : "");
                rowBuilder.button(DatePickerButton.builder()
                        .type(DatePickerButtonType.SELECT_DATE)
                        .date(current)
                        .label(label)
                        .build());
                current = current.plusDays(1);
            }

            firstDayOfNextWeek = firstDayOfNextWeek.plusDays(daysInWeek);

            // if we've finished filling out the month with real days, pad the rest of the row out
            if (current.isAfter(lastDayOfMonth)) {
                // if month ends on Sunday, but the last day is a Friday, pad in 2 days
                paddingDays = weekStart + daysInWeek - lastDayOfMonth.getDayOfWeek().getValue() - 1;
                if (paddingDays >= daysInWeek) paddingDays -= daysInWeek;
                while (paddingDays > 0) {
                    rowBuilder.button(DatePickerButton.builder().type(DatePickerButtonType.LABEL).build());
                    --paddingDays;
                }
            }
            rows.add(rowBuilder.build());
        }
        return rows;
    }
}
