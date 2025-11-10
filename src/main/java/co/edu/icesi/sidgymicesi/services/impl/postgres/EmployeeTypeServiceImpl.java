package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.EmployeeType;
import co.edu.icesi.sidgymicesi.repository.postgres.IEmployeeTypeRepository;
import co.edu.icesi.sidgymicesi.services.postgres.IEmployeeTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeTypeServiceImpl implements IEmployeeTypeService {

    private final IEmployeeTypeRepository employeeTypeRepository;

    @Override
    public EmployeeType save(EmployeeType employeeType) {
        return employeeTypeRepository.save(employeeType);
    }

    @Override
    public List<EmployeeType> findAll() {
        return employeeTypeRepository.findAll();
    }

    @Override
    public Optional<EmployeeType> findById(String name) {
        return employeeTypeRepository.findById(name);
    }

    @Override
    public void deleteById(String name) {
        employeeTypeRepository.deleteById(name);
    }
}
