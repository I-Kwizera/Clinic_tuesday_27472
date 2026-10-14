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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping(value = {"/api/appointment", "/api/appointments"})
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/doctors/{doctorId}/agenda")
    public List<Appointment> getDoctorAgenda(@PathVariable UUID doctorId, @RequestParam LocalDate date) {
        return appointmentService.getDoctorAgenda(doctorId, date);
    }

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

    @GetMapping(value = "/stats/by-status")
    public ResponseEntity<List<Object[]>> getAppointmentsPerStatus() {
        return new ResponseEntity<>(appointmentService.getAppointmentsPerStatus(), HttpStatus.OK);
    }

    @PatchMapping(value = "/cancel-day")
    public ResponseEntity<String> cancelDay(@RequestParam UUID doctorId, @RequestParam String date) {
        LocalDate day = LocalDate.parse(date);
        return new ResponseEntity<>(appointmentService.cancelDay(doctorId, Date.valueOf(day)), HttpStatus.OK);
    }

    @GetMapping(value = "/page")
    public ResponseEntity<Page<Appointment>> getAppointmentsPage(@RequestParam int page, @RequestParam int size, @RequestParam String sort) {
        String[] sortParts = sort.split(",");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]));
        return new ResponseEntity<>(appointmentService.getAppointmentsPage(pageable), HttpStatus.OK);
}

    @DeleteMapping(value = "/cancelled-before")
    public ResponseEntity<String> deleteCancelledBefore(@RequestParam String date) {
        return new ResponseEntity<>(appointmentService.deleteCancelledBefore(Date.valueOf(LocalDate.parse(date))), HttpStatus.OK);
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

    @DeleteMapping(value = {"/{id}", "/delete/{id}"})
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {
        String result = appointmentService.deleteAppointment(id);
        if (result.equals("Appointment is deleted successfully")) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateAppointmentStatus(
            @PathVariable UUID id, 
            @RequestParam AppointmentStatus status) {
        String message = appointmentService.updateAppointmentStatusFlow(id, status);
        return ResponseEntity.ok(message);
    }
@PatchMapping("/{id}/reschedule")
    public ResponseEntity<String> rescheduleAppointment(
            @PathVariable UUID id, 
            @RequestParam Date newDate) {
        String message = appointmentService.rescheduleAppointment(id, newDate);
        return ResponseEntity.ok(message);
    }
}
