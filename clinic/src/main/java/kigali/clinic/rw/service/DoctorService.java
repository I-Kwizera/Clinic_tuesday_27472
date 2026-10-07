package kigali.clinic.rw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private OfficeRepository officeRepo;

    @Autowired
    private SpecializationRepository specializationRepo;

    public String saveDoctor(Doctor doctor) {
        if (doctorRepo.existsByFirstNameAndLastNameAndDateOfBirth(doctor.getFirstName(), doctor.getLastName(), doctor.getDateOfBirth())) {
            return "Doctor with the same first name, last name, and date of birth already exists";
        }

        // Validate office if provided
        if (doctor.getOffice() != null && doctor.getOffice().getId() != null) {
            UUID officeId = doctor.getOffice().getId();
            Optional<Office> officeOpt = officeRepo.findById(officeId);
            if (officeOpt.isEmpty()) {
                return "Office not found with id: " + officeId;
            }

            Optional<Doctor> existingDoctorInOffice = doctorRepo.findByOfficeId(officeId);
            if (existingDoctorInOffice.isPresent()) {
                if (doctor.getId() == null || !existingDoctorInOffice.get().getId().equals(doctor.getId())) {
                    return "Office is already assigned to doctor: " + existingDoctorInOffice.get().getFirstName() + " " + existingDoctorInOffice.get().getLastName();
                }
            }
            doctor.setOffice(officeOpt.get());
        } else {
            doctor.setOffice(null);
        }

        // Handle specializations if passed
        if (doctor.getSpecializations() != null && !doctor.getSpecializations().isEmpty()) {
            List<Specialization> managedSpecs = new ArrayList<>();
            for (Specialization spec : doctor.getSpecializations()) {
                if (spec.getId() != null) {
                    specializationRepo.findById(spec.getId()).ifPresent(managedSpecs::add);
                } else if (spec.getName() != null) {
                    specializationRepo.findByNameIgnoreCase(spec.getName()).ifPresent(managedSpecs::add);
                }
            }
            doctor.setSpecializations(managedSpecs);
        }

        doctorRepo.save(doctor);
        return "Doctor is saved successfully";
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public Optional<Doctor> getDoctorById(UUID id) {
        return doctorRepo.findById(id);
    }

    public String updateDoctor(UUID id, Doctor updatedDoctor) {
        if (updatedDoctor == null) {
            return "Doctor payload cannot be null";
        }

        Optional<Doctor> optionalDoctor = doctorRepo.findById(id);
        if (optionalDoctor.isEmpty()) {
            return "Doctor not found with id: " + id;
        }

        Doctor existing = optionalDoctor.get();
        existing.setFirstName(updatedDoctor.getFirstName());
        existing.setLastName(updatedDoctor.getLastName());
        existing.setDateOfBirth(updatedDoctor.getDateOfBirth());

        if (updatedDoctor.getOffice() != null) {
            if (updatedDoctor.getOffice().getId() != null) {
                UUID officeId = updatedDoctor.getOffice().getId();
                Optional<Office> officeOpt = officeRepo.findById(officeId);
                if (officeOpt.isEmpty()) {
                    return "Office not found with id: " + officeId;
                }
                Optional<Doctor> otherDoc = doctorRepo.findByOfficeId(officeId);
                if (otherDoc.isPresent() && !otherDoc.get().getId().equals(id)) {
                    return "Office is already assigned to another doctor";
                }
                existing.setOffice(officeOpt.get());
            } else {
                existing.setOffice(null);
            }
        }

        if (updatedDoctor.getSpecializations() != null) {
            List<Specialization> managedSpecs = new ArrayList<>();
            for (Specialization spec : updatedDoctor.getSpecializations()) {
                if (spec.getId() != null) {
                    specializationRepo.findById(spec.getId()).ifPresent(managedSpecs::add);
                }
            }
            existing.setSpecializations(managedSpecs);
        }

        doctorRepo.save(existing);
        return "Doctor is updated successfully";
    }

    public String deleteDoctor(UUID id) {
        if (!doctorRepo.existsById(id)) {
            return "Doctor not found with id: " + id;
        }
        doctorRepo.deleteById(id);
        return "Doctor is deleted successfully";
    }

    public String assignOffice(UUID doctorId, UUID officeId) {
        Optional<Doctor> docOpt = doctorRepo.findById(doctorId);
        if (docOpt.isEmpty()) {
            return "Doctor not found with id: " + doctorId;
        }

        Optional<Office> officeOpt = officeRepo.findById(officeId);
        if (officeOpt.isEmpty()) {
            return "Office not found with id: " + officeId;
        }

        Optional<Doctor> otherDoc = doctorRepo.findByOfficeId(officeId);
        if (otherDoc.isPresent() && !otherDoc.get().getId().equals(doctorId)) {
            return "Office is already occupied by doctor: " + otherDoc.get().getFirstName() + " " + otherDoc.get().getLastName();
        }

        Doctor doctor = docOpt.get();
        doctor.setOffice(officeOpt.get());
        doctorRepo.save(doctor);
        return "Office assigned successfully to doctor";
    }

    public String addSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> docOpt = doctorRepo.findById(doctorId);
        if (docOpt.isEmpty()) {
            return "Doctor not found with id: " + doctorId;
        }

        Optional<Specialization> specOpt = specializationRepo.findById(specializationId);
        if (specOpt.isEmpty()) {
            return "Specialization not found with id: " + specializationId;
        }

        Doctor doctor = docOpt.get();
        Specialization spec = specOpt.get();

        if (!spec.getDoctors().contains(doctor)) {
            spec.getDoctors().add(doctor);
            specializationRepo.save(spec);
      }
        return "Specialization added successfully to doctor";
    }
}
