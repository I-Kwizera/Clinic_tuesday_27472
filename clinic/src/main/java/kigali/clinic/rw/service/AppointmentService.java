package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    public String bookAppointment(Appointment appointment) {
        if (appointment == null) {
            return "Appointment cannot be null";
        }

        if (appointment.getAppointmentDate() != null && appointment.getAppointmentDate().toLocalDate().isBefore(java.time.LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        if (appointment.getPatient() == null || appointment.getPatient().getId() == null) {
            return "Patient ID is required to book an appointment";
        }

        Optional<Patient> patientOpt = patientRepo.findById(appointment.getPatient().getId());
        if (patientOpt.isEmpty()) {
            return "Patient not found with id: " + appointment.getPatient().getId();
        }

        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return "Doctor ID is required to book an appointment";
        }

        Optional<Doctor> doctorOpt = doctorRepo.findById(appointment.getDoctor().getId());
        if (doctorOpt.isEmpty()) {
            return "Doctor not found with id: " + appointment.getDoctor().getId();
        }

        if (appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(appointment.getDoctor().getId(), appointment.getAppointmentDate(), AppointmentStatus.CANCELLED)) {
            return "Doctor is already booked on that date";
        }

        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        }

        appointment.setPatient(patientOpt.get());
        appointment.setDoctor(doctorOpt.get());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        appointmentRepo.save(appointment);
        return "Appointment is booked successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public Optional<Appointment> getAppointmentById(UUID id) {
        return appointmentRepo.findById(id);
    }

    public List<Appointment> getAppointmentsByPatient(UUID patientId) {
        return appointmentRepo.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(UUID doctorId) {
        return appointmentRepo.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatus(status);
    }

    public List<Appointment> getAppointmentsByStatusOrdered(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetween(Date start, Date end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> getAppointmentsPerStatus() {
        return appointmentRepo.countAppointmentsByStatus();
    }

    @Transactional
    public String cancelDay(UUID doctorId, Date date) {
        int count = appointmentRepo.cancelDay(doctorId, date);
        return count + " appointments cancelled";
    }

    public Page<Appointment> getAppointmentsPage(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    @Transactional
    public String deleteCancelledBefore(Date date) {
        return appointmentRepo.deleteCancelledBefore(date) + " appointments deleted";
    }

    public String updateAppointment(UUID id, Appointment updatedAppointment) {
        if (updatedAppointment == null) {
            return "Appointment payload cannot be null";
        }

        Optional<Appointment> optionalAppointment = appointmentRepo.findById(id);
        if (optionalAppointment.isEmpty()) {
            return "Appointment not found with id: " + id;
        }

        Appointment existing = optionalAppointment.get();

        if (updatedAppointment.getAppointmentDate() != null) {
            existing.setAppointmentDate(updatedAppointment.getAppointmentDate());
        }
        if (updatedAppointment.getReason() != null) {
            existing.setReason(updatedAppointment.getReason());
        }
        if (updatedAppointment.getStatus() != null) {
            existing.setStatus(updatedAppointment.getStatus());
        }

        if (updatedAppointment.getDoctor() != null && updatedAppointment.getDoctor().getId() != null) {
            Optional<Doctor> docOpt = doctorRepo.findById(updatedAppointment.getDoctor().getId());
            if (docOpt.isEmpty()) {
                return "Doctor not found with id: " + updatedAppointment.getDoctor().getId();
            }
            existing.setDoctor(docOpt.get());
        }

        if (updatedAppointment.getPatient() != null && updatedAppointment.getPatient().getId() != null) {
            Optional<Patient> patientOpt = patientRepo.findById(updatedAppointment.getPatient().getId());
            if (patientOpt.isEmpty()) {
                return "Patient not found with id: " + updatedAppointment.getPatient().getId();
            }
            existing.setPatient(patientOpt.get());
        }

        appointmentRepo.save(existing);
        return "Appointment is updated successfully";
    }

    @Transactional
    public String updateAppointmentStatusFlow(UUID id, AppointmentStatus newStatus) {
        Optional<Appointment> optionalAppointment = appointmentRepo.findById(id);
        if (optionalAppointment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The appointment with that id does not exist");
        }

        Appointment app = optionalAppointment.get();
        AppointmentStatus current = app.getStatus();

        if (current == AppointmentStatus.SCHEDULED && newStatus != AppointmentStatus.CONFIRMED && newStatus != AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change status from " + current + " to " + newStatus);
        }
        if (current == AppointmentStatus.CONFIRMED && newStatus != AppointmentStatus.COMPLETED && newStatus != AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change status from " + current + " to " + newStatus);
        }
        if (current == AppointmentStatus.COMPLETED && newStatus != AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change status from " + current + " to " + newStatus);
        }

        app.setStatus(newStatus);
        appointmentRepo.save(app);
        return "Appointment status updated successfully";
    }

    @Transactional
    public String rescheduleAppointment(UUID id, Date newDate) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The appointment with that id does not exist"));

        if (newDate != null && newDate.toLocalDate().isBefore(java.time.LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment date cannot be in the past");
        }

        AppointmentStatus status = appointment.getStatus();
        if (status == AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A COMPLETED appointment cannot be rescheduled");
        }
        if (status == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A CANCELLED appointment cannot be rescheduled");
        }

        UUID doctorId = appointment.getDoctor().getId();
        if (appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(doctorId, newDate, AppointmentStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Doctor is already booked on that date");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);

        return "Appointment rescheduled successfully";
    }

    public String deleteAppointment(UUID id) {
        if (!appointmentRepo.existsById(id)) {
            return "Appointment not found with id: " + id;
        }

        if (prescriptionRepo.existsByAppointmentId(id)) {
            return "That appointment has prescriptions, delete them first";
        }
        appointmentRepo.deleteById(id);
        return "Appointment is deleted successfully";
    }

    public List<Appointment> getDoctorAgenda(UUID doctorId, LocalDate date) {
    return appointmentRepo.findByDoctorIdAndDateAndStatusNot(doctorId, date, AppointmentStatus.CANCELLED);
}
}
