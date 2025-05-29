package gl2.example.patientservice.service;


import gl2.example.patientservice.dto.PatientRequestDTO;
import gl2.example.patientservice.dto.PatientResponseDTO;
import gl2.example.patientservice.exception.EmailAlreadyExistsException;
import gl2.example.patientservice.exception.PatientNotFoundException;
import gl2.example.patientservice.grpc.BillingServiceGrpcClient;
import gl2.example.patientservice.kafka.kafkaProducer;
import gl2.example.patientservice.mapper.PatientMapper;
import gl2.example.patientservice.model.Patient;
import gl2.example.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final kafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, kafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient=billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        List<PatientResponseDTO> patientResponseDTOs=patients.stream().map(
                PatientMapper::toDTO).toList();

        return patientResponseDTOs;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists"+ patientRequestDTO.getEmail());
        }

        Patient newPatient=patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(newPatient.getId().toString(),newPatient.getName(),newPatient.getEmail());

        kafkaProducer.sendEvent(newPatient);

        return PatientMapper.toDTO(newPatient);
    }



    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient =patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("Patient not found with Id: " +id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)) {
            throw new EmailAlreadyExistsException("A patient with this email already exists"+ patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient=patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);


    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }


}
