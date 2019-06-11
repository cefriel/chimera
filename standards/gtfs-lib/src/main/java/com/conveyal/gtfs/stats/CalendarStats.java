package com.conveyal.gtfs.stats;

import com.conveyal.gtfs.GTFSFeed;

/**
 * Created by landon on 9/2/16.
 */
public class CalendarStats {
    private GTFSFeed feed = null;
    private FeedStats stats = null;

//    public CalendarStats (GTFSFeed f, FeedStats fs) {
//        feed = f;
//        stats = fs;
//    }

//    public Set<String> getServiceIdsForDates (LocalDate from, LocalDate to) {
//        long days = ChronoUnit.DAYS.between(from, to);
//
//        return feed.services.values().stream()
//                .filter(s -> {
//                    for (int i = 0; i < days; i++) {
//                        LocalDate date = from.plusDays(i);
//                        if (s.activeOn(date)) {
//                            return true;
//                        }
//                    }
//                    return false;
//                })
//                .map(s -> s.service_id)
//                .collect(Collectors.toSet());
//    }
}
