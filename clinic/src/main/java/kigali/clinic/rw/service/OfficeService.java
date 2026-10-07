package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;

@Service 
public class OfficeService {

    @Autowired 
    private OfficeRepository offRepo;

    public String saveOffice(Office office) {
        if (office == null) {
            return "Office cannot be null";
        }

        Optional<Office> existingOffice = offRepo.findByOfficeNumber(office.getOfficeNumber());
        if (existingOffice.isPresent()) {
            if (office.getId() == null || !existingOffice.get().getId().equals(office.getId())) {
                return "Office with number " + office.getOfficeNumber() + " already exists";
            }
        }

        offRepo.save(office);
        return "Office is saved successfully";
    }

    public List<Office> getAllOffices() {
        return offRepo.findAll();
    }

    public Optional<Office> getOfficeById(UUID id) {
        return offRepo.findById(id);
    }

    public String updateOffice(UUID id, Office updatedOffice) {
        if (updatedOffice == null) {
            return "Office payload cannot be null";
        }

        Optional<Office> optionalExisting = offRepo.findById(id);
        if (optionalExisting.isEmpty()) {
            return "Office not found with id: " + id;
        }

        Optional<Office> officeWithSameNumber = offRepo.findByOfficeNumber(updatedOffice.getOfficeNumber());
        if (officeWithSameNumber.isPresent() && !officeWithSameNumber.get().getId().equals(id)) {
            return "Office with number " + updatedOffice.getOfficeNumber() + " already exists";
        }

        Office existing = optionalExisting.get();
        existing.setName(updatedOffice.getName());
        existing.setOfficeNumber(updatedOffice.getOfficeNumber());

        offRepo.save(existing);
        return "Office is updated successfully";
    }

    public String deleteOffice(UUID id) {
        if (!offRepo.existsById(id)) {
            return "Office not found with id: " + id;
        }
        offRepo.deleteById(id);
        return "Office is deleted successfully";
    }
}
