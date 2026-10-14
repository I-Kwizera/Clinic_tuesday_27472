package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    public String savePrescription(UUID appointmentId, Prescription prescription) {
        Appointment appointment = appointmentRepo.findById(appointmentId).get();
        
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not completed");
        }

        prescription.setAppointment(appointment);
        prescription.setIssuedDate(LocalDate.now());
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    public List<Prescription> getPrescriptionsByAppointment(UUID appointmentId) {
        return prescriptionRepo.findByAppointmentId(appointmentId);
    }

    public List<Prescription> getPrescriptionsByPatientSsNumber(String ssNumber) {
        return prescriptionRepo.findByPatientSsNumberOrdered(ssNumber);
    }

    public String updatePrescription(UUID id, Prescription prescription) {
        prescription.setId(id);
        prescriptionRepo.save(prescription);
        return "Prescription updated successfully";
    }

    public String deletePrescription(UUID id) {
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }
}