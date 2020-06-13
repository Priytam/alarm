package com.clock;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;


class RequestComparator implements Comparator<AlarmClockRequest>, Serializable {
    private static final long serialVersionUID = 1L;

    public RequestComparator() {
        super();
    }

    @Override
    public int compare(AlarmClockRequest req1, AlarmClockRequest req2) {
        if (null == req1 && req2 == null) {
            return 0;
        }
        if (null == req1) {
            return 1;
        } else if (null == req2) {
            return -1;
        }

        Date m_dateTime1 = req1.getDateTypeAbsoluteTime();
        Date m_dateTime2 = req2.getDateTypeAbsoluteTime();

        int compResult = m_dateTime1.compareTo(m_dateTime2);
        if (0 == compResult) {
            long id1 = req1.getRequestID();
            long id2 = req2.getRequestID();
            if (id1 < id2) {
                compResult = -1;
            } else if (id1 > id2) {
                compResult = 1;
            }
        }
        return compResult;
    }
}
