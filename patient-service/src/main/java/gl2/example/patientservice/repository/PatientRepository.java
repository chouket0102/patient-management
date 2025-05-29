package gl2.example.patientservice.repository;

import java.util.UUID;

import gl2.example.patientservice.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID id);
}

