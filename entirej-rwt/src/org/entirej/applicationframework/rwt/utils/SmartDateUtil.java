package org.entirej.applicationframework.rwt.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class SmartDateUtil
{

    private static Map<Function<String, Boolean>, Function<String, Date>> dateSpecs = new LinkedHashMap<>();

    
    public static boolean supported(String format)
    {

        if (format != null && !format.trim().isEmpty())
        {
            for (Function<String, Boolean> spec : dateSpecs.keySet())
            {
                if (spec.apply(format))
                {
                    return true;
                }
            }
        }

        return false;
    }
    
    public static Date toDate(String format, Function<String, Date> fallback)
    {

        if (format != null && !format.trim().isEmpty())
        {
            for (Function<String, Boolean> spec : dateSpecs.keySet())
            {
                if (spec.apply(format))
                {
                    return dateSpecs.get(spec).apply(format);
                }
            }
        }

        return fallback.apply(format);
    }

    public static void addSpec(Function<String, Boolean> spec, Function<String, Date> calculation)
    {
        dateSpecs.put(spec, calculation);
    }

    public static void installBasic()
    {
        // (+/-)(spec)
        addSpec(format -> validSpec(format, 'd'), format -> calculateDate(format, ChronoUnit.DAYS));
        addSpec(format -> validSpec(format, 'm'), format -> calculateDate(format, ChronoUnit.MONTHS));
        addSpec(format -> validSpec(format, 'y'), format -> calculateDate(format, ChronoUnit.YEARS));
        addSpec(format -> validSpec(format, 'w'), format -> calculateDate(format, ChronoUnit.WEEKS));

    }

    private static Date calculateDate(String format, TemporalUnit unit)
    {
        String offset = format.substring(0, format.length() - 1);

        if (offset.length() > 0)
            try
            {
                ZoneId defaultZoneId = ZoneId.systemDefault();
                LocalDate date = LocalDate.now();

                int offsetDays = Integer.parseInt(offset);

                LocalDate plusDays = date.plus(offsetDays, unit);
                plusDays = offsetWeekends(plusDays);

                Date from = Date.from(plusDays.atStartOfDay(defaultZoneId).toInstant());

                return from;
            }
            catch (NumberFormatException e)
            {
                return null;
            }

        return new Date();
    }

    private static Boolean validSpec(String format, char spec)
    {
        char specID = format.toLowerCase().charAt(format.length() - 1);

        if (specID != spec)
            return false;

        String offset = format.substring(0, format.length() - 1);

        if (offset.length() > 0)
            try
            {
                Integer.parseInt(offset);
            }
            catch (NumberFormatException e)
            {
                return false;
            }

        return true;
    }

    private static LocalDate offsetWeekends(LocalDate day)
    {
        DayOfWeek dayOfWeek = day.getDayOfWeek();
        switch (dayOfWeek)
        {
            case SATURDAY:
                day = day.plusDays(-1);
                break;
            case SUNDAY:
                day = day.plusDays(-2);
                break;

            default:
                break;
        }
        return day;
    }

    static
    {
        installBasic();
    }

    public static void main(String[] args)
    {
        System.out.println(toDate("1d", s -> null));
        System.out.println(toDate("-1w", s -> null));
        System.out.println(toDate("-1y", s -> null));
        System.out.println(toDate("-1m", s -> null));
        System.out.println(toDate("2d", s -> null));
        System.out.println(toDate("-3d", s -> null));
    }

}
