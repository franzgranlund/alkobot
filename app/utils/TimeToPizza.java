package utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TimeToPizza {
    final public int day;
    final public long hour;
    final public long minute;
    final public long second;
    final public long secPizza;

    public TimeToPizza(Supplier<LocalDateTime> pizzaTime) {
        secPizza = secondsToPizza(pizzaTime);

        day = (int) TimeUnit.SECONDS.toDays(secPizza);
        hour = TimeUnit.SECONDS.toHours(secPizza) - (day *24);
        minute = TimeUnit.SECONDS.toMinutes(secPizza) - (TimeUnit.SECONDS.toHours(secPizza)* 60);
        second = TimeUnit.SECONDS.toSeconds(secPizza) - (TimeUnit.SECONDS.toMinutes(secPizza) *60);
    }

    public static long secondsToPizza(Supplier<LocalDateTime> pizzaTime) {
        return LocalDateTime.now().until(pizzaTime.get(), ChronoUnit.SECONDS);
    }

    public static Supplier<LocalDateTime> nextFridayAtEleven =
            () -> LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).atTime(11, 0);
}