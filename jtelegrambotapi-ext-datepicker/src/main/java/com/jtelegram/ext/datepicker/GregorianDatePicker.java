package com.jtelegram.ext.datepicker;

import com.jtelegram.api.inline.keyboard.InlineKeyboardButton;
import com.jtelegram.api.inline.keyboard.InlineKeyboardMarkup;
import com.jtelegram.api.inline.keyboard.InlineKeyboardRow;
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

/**
 * @author Nick Robson
 */
public class GregorianDatePicker implements DatePicker {

    private final Locale locale;
    private final WeekFields weekFields;
    private final DateTimeFormatter monthAndYearFormat;
    private final DateTimeFormatter shortDayFormat;
    private final DateTimeFormatter dayNumberFormat;

    public GregorianDatePicker(Locale locale) {
        this.locale = locale;
        this.weekFields = WeekFields.of(locale);
        this.monthAndYearFormat = DateTimeFormatter.ofPattern("MMMM y", locale);
        this.shortDayFormat = DateTimeFormatter.ofPattern("EE", locale);
        this.dayNumberFormat = DateTimeFormatter.ofPattern("d", locale);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public InlineKeyboardMarkup toKeyboardMarkup(LocalDate date) {
        Objects.requireNonNull(date, "date cannot be null");

        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.addAll(getMonthRows(date));
        rows.addAll(getWeeksRows(date));

        return InlineKeyboardMarkup.builder()
                .inlineKeyboard(rows)
                .build();
    }

    private List<InlineKeyboardRow> getMonthRows(LocalDate date) {
        final LocalDate firstDayOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        final LocalDate lastDayOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());

        InlineKeyboardRow.InlineKeyboardRowBuilder rowBuilder = InlineKeyboardRow.builder();

        // show << (previous year) when not first year
        if (date.getYear() != LocalDate.MIN.getYear()) {
            LocalDate previousYear = firstDayOfMonth.minusYears(1);
            String callbackDataYear = String.format(GOTO_MONTH_FORMAT, previousYear.getYear(), previousYear.getMonthValue(), locale.toLanguageTag());
            rowBuilder.button(
                    InlineKeyboardButton.builder()
                            .label("\u00ab")
                            .callbackData(callbackDataYear)
                            .build()
            );
        }
        // show < (previous month) when not first month of first year
        if (firstDayOfMonth.isAfter(LocalDate.MIN)) {
            LocalDate previousMonth = firstDayOfMonth.minusMonths(1);
            String callbackDataMonth = String.format(GOTO_MONTH_FORMAT, previousMonth.getYear(), previousMonth.getMonthValue(), locale.toLanguageTag());
            rowBuilder.button(
                    InlineKeyboardButton.builder()
                            .label("\u2039")
                            .callbackData(callbackDataMonth)
                            .build()
            );
        }

        // show > (next month) when not last month of last year
        if (lastDayOfMonth.isBefore(LocalDate.MAX)) {
            LocalDate nextMonth = lastDayOfMonth.plusMonths(1);
            String callbackDataMonth = String.format(GOTO_MONTH_FORMAT, nextMonth.getYear(), nextMonth.getMonthValue(), locale.toLanguageTag());
            rowBuilder.button(
                    InlineKeyboardButton.builder()
                            .label("\u203A")
                            .callbackData(callbackDataMonth)
                            .build()
            );
        }

        // show >> (next year) when not last year
        if (date.getYear() != LocalDate.MAX.getYear()) {
            LocalDate nextYear = lastDayOfMonth.plusYears(1);
            String callbackDataYear = String.format(GOTO_MONTH_FORMAT, nextYear.getYear(), nextYear.getMonthValue(), locale.toLanguageTag());
            rowBuilder.button(
                    InlineKeyboardButton.builder()
                            .label("\u00bb")
                            .callbackData(callbackDataYear)
                            .build()
            );
        }

        // show month name and year; e.g. "January 2018"
        return Arrays.asList(
                rowBuilder.build(),
                InlineKeyboardRow.builder()
                        .button(InlineKeyboardButton.builder().label(monthAndYearFormat.format(date)).callbackData(" ").build())
                        .build()
        );
    }

    private List<InlineKeyboardRow> getWeeksRows(LocalDate date) {
        List<InlineKeyboardRow> rows = new ArrayList<>();

        int weekStart = weekFields.getFirstDayOfWeek().getValue();
        long daysInWeek = ChronoField.DAY_OF_WEEK.range().getMaximum();

        LocalDate firstDayOfWeek = date.with(ChronoField.DAY_OF_WEEK, weekStart);
        LocalDate firstDayOfNextWeek = firstDayOfWeek.plusDays(daysInWeek);

        InlineKeyboardRow.InlineKeyboardRowBuilder headerRow = InlineKeyboardRow.builder();
        LocalDate current = firstDayOfWeek;
        while (current.isBefore(firstDayOfNextWeek)) {
            headerRow.button(InlineKeyboardButton.builder()
                    .label(shortDayFormat.format(current))
                    .callbackData(" ")
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
            InlineKeyboardRow.InlineKeyboardRowBuilder rowBuilder = InlineKeyboardRow.builder();
            // if month starts on Monday, but the 1st is a Wednesday, pad in 2 days
            while (paddingDays > 0) {
                rowBuilder.button(InlineKeyboardButton.builder().label(" ").callbackData(" ").build());
                --paddingDays;
            }

            // add in all the days from current to firstDayOfNextWeek or lastDayOfMonth, whichever's earliest
            while (current.isBefore(firstDayOfNextWeek) && !current.isAfter(lastDayOfMonth)) {
                rowBuilder.button(InlineKeyboardButton.builder()
                        .label(dayNumberFormat.format(current))
                        .callbackData(String.format(SELECT_DAY_FORMAT, current.getYear(), current.getMonthValue(), current.getDayOfMonth(), locale.toLanguageTag()))
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
                    rowBuilder.button(InlineKeyboardButton.builder().label(" ").callbackData(" ").build());
                    --paddingDays;
                }
            }
            rows.add(rowBuilder.build());
        }
        return rows;
    }
}
