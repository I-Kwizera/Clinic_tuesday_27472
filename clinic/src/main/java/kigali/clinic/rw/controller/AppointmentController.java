package kigali.clinic.rw.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping(value = {"/api/appointment", "/api/appointments"})
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping(value = {"", "/book", "/save"})
    public ResponseEntity<?> bookAppointment(@RequestBody Appointment appointment) {
        String result = appointmentService.bookAppointment(appointment);
        if (result.equals("Appointment is booked successfully")) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }else if (result.equals("Doctor is already booked on that date")) {
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = {"", "/all"})
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        if (appointment.isPresent()) {
            return new ResponseEntity<>(appointment.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Appointment not found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable UUID patientId) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByPatient(patientId), HttpStatus.OK);
    }

    @GetMapping(value = "/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctor(@PathVariable UUID doctorId) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByDoctor(doctorId), HttpStatus.OK);
    }

    @GetMapping(value = "/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByStatus(status), HttpStatus.OK);
    }

    @GetMapping(value = "/by-status")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatusOrdered(@RequestParam AppointmentStatus status) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByStatusOrdered(status), HttpStatus.OK);
    }

    @GetMapping(value = "/between")
    public ResponseEntity<List<Appointment>> getAppointmentsBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(appointmentService.getAppointmentsBetween(Date.valueOf(startDate), Date.valueOf(endDate)), HttpStatus.OK);
    }

    @PutMapping(value = {"/{id}", "/update/{id}"})
    public ResponseEntity<?> updateAppointment(@PathVariable UUID id, @RequestBody Appointment appointment) {
        String result = appointmentService.updateAppointment(id, appointment);
        if (result.equals("Appointment is updated successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else if (result.contains("not found")) {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}/status/{status}")
    public ResponseEntity<?> updateStatus(@PathVariable UUID id, @PathVariable AppointmentStatus status) {
        String result = appointmentService.updateStatus(id, status);
        if (result.startsWith("Appointment status updated")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = {"/{id}", "/delete/{id}"})
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {
        String result = appointmentService.deleteAppointment(id);
        if (result.equals("Appointment is deleted successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }
}
