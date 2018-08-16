package com.duobao.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    /*得到当天凌晨的时间戳*/
    public static Long getCurrent0Time() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return zero.getTime();
    }

    public static int compare(Long t1,Long t2) {
        if (t1 == null || t2 == null) {
            return 0;
        }
        return t1>t2?1:-1;
    }
}
