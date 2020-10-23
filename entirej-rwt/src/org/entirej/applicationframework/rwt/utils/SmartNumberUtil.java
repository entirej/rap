package org.entirej.applicationframework.rwt.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class SmartNumberUtil
{

    private static Map<Function<String, Boolean>, Function<String, Number>> numberSpecs = new LinkedHashMap<>();

    public static boolean supported(String format)
    {

        if (format != null && !format.trim().isEmpty())
        {
            for (Function<String, Boolean> spec : numberSpecs.keySet())
            {
                if (spec.apply(format))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static Number toNumber(String format, Function<String, Number> fallback)
    {

        if (format != null && !format.trim().isEmpty())
        {
            for (Function<String, Boolean> spec : numberSpecs.keySet())
            {
                if (spec.apply(format))
                {
                    return numberSpecs.get(spec).apply(format);
                }
            }
        }

        return fallback.apply(format);
    }

    public static void addSpec(Function<String, Boolean> spec, Function<String, Number> calculation)
    {
        numberSpecs.put(spec, calculation);
    }

    public static void installBasic()
    {
        // (+/-)(spec)
        addSpec(format -> validSpec(format, 't'), format -> calculateNumber(format, 1000));
        addSpec(format -> validSpec(format, 'm'), format -> calculateNumber(format, 1000000));
        addSpec(format -> validSpec(format, 'b'), format -> calculateNumber(format, 1000000000));

    }

    private static Double calculateNumber(String format, long factor)
    {
        String val = format.substring(0, format.length() - 1);

        if (val.length() > 0)
            try
            {
               

                Double specValue = Double.parseDouble(val);

               

                return Double.valueOf(specValue*factor);
            }
            catch (NumberFormatException e)
            {
                return null;
            }

        return null;
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
                Double.parseDouble(offset);
            }
            catch (NumberFormatException e)
            {
                return false;
            }

        return true;
    }

    static
    {
        installBasic();
    }

    public static void main(String[] args)
    {

        /*
         * 1t = 1'000
         * 
         * 100t = 100'000
         * 
         * 12.5m = 12'500'000
         * 
         * 5.555b = 5'555'000'000
         */
        System.out.println(toNumber("1t", s -> null));
        System.out.println(toNumber("2t", s -> null));
        System.out.println(toNumber("0.5t", s -> null));
        System.out.println(toNumber("1.5t", s -> null));
        System.out.println(toNumber("1.55t", s -> null));
        System.out.println(toNumber("5.555b", s -> null));
        System.out.println(toNumber("ab", s -> null));
    }

}
