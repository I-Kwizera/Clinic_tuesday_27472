package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specializationRepo;

    public String saveSpecialization(Specialization specialization) {
        if (specialization == null) {
            return "Specialization cannot be null";
        }
        if (specialization.getName() == null || specialization.getName().trim().isEmpty()) {
            return "Specialization name cannot be empty";
        }

        if (specializationRepo.existsByNameIgnoreCase(specialization.getName().trim())) {
            return "Specialization with name '" + specialization.getName() + "' already exists";
        }

        specialization.setName(specialization.getName().trim());
        specializationRepo.save(specialization);
        return "Specialization is saved successfully";
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepo.findAll();
    }

    public Optional<Specialization> getSpecializationById(UUID id) {
        return specializationRepo.findById(id);
    }

    public String updateSpecialization(UUID id, Specialization updatedSpec) {
        if (updatedSpec == null) {
            return "Specialization payload cannot be null";
        }

        Optional<Specialization> optionalSpec = specializationRepo.findById(id);
        if (optionalSpec.isEmpty()) {
            return "Specialization not found with id: " + id;
        }

        if (updatedSpec.getName() != null && !updatedSpec.getName().trim().isEmpty()) {
            Optional<Specialization> specWithSameName = specializationRepo.findByNameIgnoreCase(updatedSpec.getName().trim());
            if (specWithSameName.isPresent() && !specWithSameName.get().getId().equals(id)) {
                return "Specialization with name '" + updatedSpec.getName() + "' already exists";
            }
            optionalSpec.get().setName(updatedSpec.getName().trim());
        }

        specializationRepo.save(optionalSpec.get());
        return "Specialization is updated successfully";
    }

    public String deleteSpecialization(UUID id) {
        if (!specializationRepo.existsById(id)) {
            return "Specialization not found with id: " + id;
        }
        specializationRepo.deleteById(id);
        return "Specialization is deleted successfully";
    }
}
