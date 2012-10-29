/*
 * This file is part of LibrePlan
 *
 * Copyright (C) 2012 Igalia, S.L.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.libreplan.business.effortsummary.entities;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.libreplan.business.calendars.entities.ResourceCalendar;
import org.libreplan.business.common.BaseEntity;
import org.libreplan.business.resources.entities.Resource;
import org.libreplan.business.workingday.EffortDuration;
import org.libreplan.business.workingday.IntraDayDate.PartialDay;

public class EffortSummary extends BaseEntity {

    private LocalDate startDate;

    private LocalDate endDate;

    private int[] availableEffort;

    private int[] assignedEffort;

    private Resource resource;

    public static EffortSummary create(LocalDate startDate, LocalDate endDate,
            int[] availableEffort, int[] assignedEffort,
            Resource resource) {
        EffortSummary newObject = new EffortSummary();
        newObject.setStartDate(startDate);
        newObject.setEndDate(endDate);
        newObject.setAvailableEffort(availableEffort);
        newObject.setAssignedEffort(assignedEffort);
        newObject.setResource(resource);
        return create(newObject);
    }

    public static EffortSummary createFromNewResource(Resource resource) {
        final int defaultNumberOfElements = 1000;

        // get start and end dates
        ResourceCalendar resourceCalendar = resource.getCalendar();
        LocalDate startDate = resourceCalendar.getFistCalendarAvailability()
                .getStartDate();
        LocalDate endDate = resourceCalendar.getLastCalendarAvailability()
                .getEndDate();
        if (endDate == null) {
            endDate = startDate.plusDays(defaultNumberOfElements - 1);
        }
        int numberOfElements = Days.daysBetween(startDate, endDate).getDays() + 1;

        // fill availability data
        int[] availableEffort = new int[numberOfElements];
        int[] assignedEffort = new int[numberOfElements];
        for (int i = 0; i < numberOfElements; i++) {
            PartialDay day = PartialDay.wholeDay(startDate.plusDays(i));
            availableEffort[i] = resourceCalendar.getCapacityOn(day)
                    .getSeconds();
            assignedEffort[i] = 0; // because the object is new
        }
        return EffortSummary.create(startDate, endDate,
                    availableEffort, assignedEffort, resource);

    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public int[] getAvailableEffort() {
        return availableEffort;
    }

    public void setAvailableEffort(int[] availableEffort) {
        this.availableEffort = availableEffort;
    }

    public int[] getAssignedEffort() {
        return assignedEffort;
    }

    public void setAssignedEffort(int[] assignedEffort) {
        this.assignedEffort = assignedEffort;
    }

    public EffortDuration getAvailableEffortForDate(LocalDate date) {
        int positionInArray = Days.daysBetween(startDate, date).getDays();
        if (availableEffort.length < positionInArray) {
            return EffortDuration.zero();
        }
        return EffortDuration.seconds(availableEffort[positionInArray]);
    }

    public EffortDuration getAssignedEffortForDate(LocalDate date) {
        int positionInArray = Days.daysBetween(startDate, date).getDays();
        if (assignedEffort.length < positionInArray) {
            return EffortDuration.zero();
        }
        return EffortDuration.seconds(assignedEffort[positionInArray]);
    }

    /**
     * Update the availability data in the EffortSummary object reading it again
     * from the attached resource.
     */
    public void updateAvailabilityFromResource() {
        final int defaultNumberOfElements = 1000;

        // get start and end dates
        ResourceCalendar resourceCalendar = resource.getCalendar();
        LocalDate startDate = resourceCalendar.getFistCalendarAvailability()
                .getStartDate();
        LocalDate endDate = resourceCalendar.getFistCalendarAvailability()
                .getEndDate();
        if (endDate == null) {
            endDate = startDate.plusDays(defaultNumberOfElements - 1);
        }
        int numberOfElements = Days.daysBetween(startDate, endDate).getDays() + 1;

        // fill availability data
        int[] availableEffort = new int[numberOfElements];
        for (int i = 0; i < numberOfElements; i++) {
            PartialDay day = PartialDay.wholeDay(startDate.plusDays(i));
            availableEffort[i] = resourceCalendar.getCapacityOn(day)
                    .getSeconds();
        }
        setAvailableEffort(availableEffort);

    }

}