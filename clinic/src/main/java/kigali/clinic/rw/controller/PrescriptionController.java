package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.PrescriptionService;

@RestController 
@RequestMapping ("/api/prescriptions")
public class PrescriptionController {
    
    @Autowired
    private PrescriptionService prescriptionService;
    
    @PostMapping("/save")
    public String savePrescription(@RequestParam UUID appointmentId, @RequestBody Prescription prescription) {
        return prescriptionService.savePrescription(appointmentId, prescription);
    }

    @GetMapping("/appointment/{appointmentId}")
    public List<Prescription> getPrescriptionsByAppointment(@PathVariable UUID appointmentId) {
        return prescriptionService.getPrescriptionsByAppointment(appointmentId);
    }

    @GetMapping("/patient/{ssNumber}")
    public List<Prescription> getPrescriptionsByPatientSsNumber(@PathVariable String ssNumber) {
        return prescriptionService.getPrescriptionsByPatientSsNumber(ssNumber);
    }

    @PutMapping("/update/{id}")
    public String updatePrescription(@PathVariable UUID id, @RequestBody Prescription prescription) {
        return prescriptionService.updatePrescription(id, prescription);
    }

    @DeleteMapping("/delete/{id}")
    public String deletePrescription(@PathVariable UUID id) {
        return prescriptionService.deletePrescription(id);
    }
}
