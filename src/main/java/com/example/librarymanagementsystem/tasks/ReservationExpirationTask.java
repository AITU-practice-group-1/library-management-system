package com.example.librarymanagementsystem.tasks;

import com.example.librarymanagementsystem.Services.ReservationsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpirationTask {

    private static final Logger logger = LoggerFactory.getLogger(ReservationExpirationTask.class);
    private final ReservationsServices reservationsServices;

    public ReservationExpirationTask(ReservationsServices reservationsServices) {
        this.reservationsServices = reservationsServices;
    }

    /**
     * This task runs every hour to check for and expire reservations
     * that have passed their expiration date.
     * Cron format: second, minute, hour, day of month, month, day(s) of week
     * "0 0 * * * *" means it runs at the start of every hour.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void handleExpiredReservations() {
        logger.info("Executing scheduled task: handleExpiredReservations");
        try {
            reservationsServices.expireReservations();
        } catch (Exception e) {
            logger.error("Error occurred during scheduled reservation expiration task", e);
        }
    }
}