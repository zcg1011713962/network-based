package com.transmit.utils;

import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionUtils {

    public static void isNull(Object t, Function good, Function bad){
        if(t == null){
            bad.apply(t);
            return;
        }
        good.apply(t);
    }

    public static void isOK(boolean b, Supplier good, Supplier bad){
        if(b){
            good.get();
            return;
        }
        bad.get();
    }
}
