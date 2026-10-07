package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public String bookAppointment(Appointment appointment) {
        if (appointment == null) {
            return "Appointment cannot be null";
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

    public String updateStatus(UUID id, AppointmentStatus status) {
        Optional<Appointment> optionalAppointment = appointmentRepo.findById(id);
        if (optionalAppointment.isEmpty()) {
            return "Appointment not found with id: " + id;
        }
        Appointment app = optionalAppointment.get();
        app.setStatus(status);
        appointmentRepo.save(app);
        return "Appointment status updated to " + status;
    }

    public String deleteAppointment(UUID id) {
        if (!appointmentRepo.existsById(id)) {
            return "Appointment not found with id: " + id;
        }
        appointmentRepo.deleteById(id);
        return "Appointment is deleted successfully";
    }
}
