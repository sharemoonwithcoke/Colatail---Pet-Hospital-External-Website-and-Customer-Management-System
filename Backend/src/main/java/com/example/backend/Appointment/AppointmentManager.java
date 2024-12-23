package com.example.backend.Appointment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentManager {
    private List<Appointment> appointmentList;

    public AppointmentManager() {
        this.appointmentList = new ArrayList<>();
    }

    // add a new appointment
    public void addAppointment(Appointment appointment) {
        appointmentList.add(appointment);
    }

    // cancel this appointment
    public boolean cancelAppointment(long appointmentId) {
        for (Appointment appointment : appointmentList) {
            if (appointment.getAppointmentId() == appointmentId && appointment.getStatus() == Appointment.Status.PENDING) {
                appointment.setStatus(Appointment.Status.CANCELLED);
                return true;
            }
        }
        return false;
    }

    // complete this appointment
    public boolean completeAppointment(long appointmentId) {
        for (Appointment appointment : appointmentList) {
            if (appointment.getAppointmentId() == appointmentId && appointment.getStatus() == Appointment.Status.PENDING) {
                appointment.setStatus(Appointment.Status.COMPLETED);
                return true;
            }
        }
        return false;
    }

    // get all appointments
    public List<Appointment> getAppointmentsByStatus(Appointment.Status status) {
        return appointmentList.stream()
                .filter(appointment -> appointment.getStatus() == status)
                .collect(Collectors.toList());
    }

    //get all appointments by date
    public List<Appointment> getAppointmentsForDate(LocalDate date) {
        return appointmentList.stream()
                .filter(appointment -> appointment.getDate().equals(date))
                .collect(Collectors.toList());
    }

    //get appointment count
    public int getAppointmentCount() {
        return appointmentList.size();
    }

  

    public List<Appointment> getAppointmentsForDay(LocalDate date) {
        return appointmentList.stream()
                .filter(appointment -> appointment.getDate().equals(date))
                .collect(Collectors.toList());
    }

    // get all appointments
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointmentList);
    }

    //get appointment by date
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentList.stream()
                .filter(appointment -> appointment.getDate().equals(date))
                .collect(Collectors.toList());
    }
  


}
