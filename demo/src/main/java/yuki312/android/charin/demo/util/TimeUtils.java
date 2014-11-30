/*
 * Copyright 2014 yuki312 All Right Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yuki312.android.charin.demo.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

import yuki312.android.charin.demo.R;

public class TimeUtils {
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String formatHumanFriendly(final Context context, long timestamp) {
        long localTimestamp, localTime;
        long now = getCurrentTime();

        TimeZone tz = getCurrentTimeZone(context);
        localTimestamp = timestamp + tz.getOffset(timestamp);
        localTime = now + tz.getOffset(now);

        long dayOrd = localTimestamp / 86400000L /*1day*/;
        long nowOrd = localTime / 86400000L /*1day*/;

        if (dayOrd == nowOrd) {
            return context.getString(R.string.day_human_friendly_today);
        } else if (dayOrd == nowOrd - 1) {
            return context.getString(R.string.day_human_friendly_yesterday);
        } else if (dayOrd == nowOrd + 1) {
            return context.getString(R.string.day_human_friendly_tomorrow);
        } else {
            return formatShortDate(context, new Date(timestamp));
        }
    }

    public static TimeZone getCurrentTimeZone(final Context context) {
        return TimeZone.getDefault();
    }

    public static String formatShortDate(Context context, Date date) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(),
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR,
                getCurrentTimeZone(context).getID()).toString();
    }
}
