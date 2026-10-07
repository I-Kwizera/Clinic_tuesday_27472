package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepo;

    public String savePatient(Patient patient) {
        if (patient == null) {
            return "Patient cannot be null";
        }
        if (patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()) {
            return "Patient first name cannot be empty";
        }
        if (patient.getLastName() == null || patient.getLastName().trim().isEmpty()) {
            return "Patient last name cannot be empty";
        }

        patientRepo.save(patient);
        return "Patient is saved successfully";
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Optional<Patient> getPatientById(UUID id) {
        return patientRepo.findById(id);
    }
    public List<Patient> getPatientsByLastName(String lastName) {
    return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
}
    public String updatePatient(UUID id, Patient updatedPatient) {
        if (updatedPatient == null) {
            return "Patient payload cannot be null";
        }

        Optional<Patient> optionalPatient = patientRepo.findById(id);
        if (optionalPatient.isEmpty()) {
            return "Patient not found with id: " + id;
        }

        Patient existing = optionalPatient.get();
        existing.setFirstName(updatedPatient.getFirstName());
        existing.setLastName(updatedPatient.getLastName());
        existing.setDateOfBirth(updatedPatient.getDateOfBirth());

        patientRepo.save(existing);
        return "Patient is updated successfully";
    }

    public String deletePatient(UUID id) {
        if (!patientRepo.existsById(id)) {
            return "Patient not found with id: " + id;
        }
        patientRepo.deleteById(id);
        return "Patient is deleted successfully";
    }
}
