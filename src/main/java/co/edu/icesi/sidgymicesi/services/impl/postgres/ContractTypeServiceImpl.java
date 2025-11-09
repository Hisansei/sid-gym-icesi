package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.ContractType;
import co.edu.icesi.sidgymicesi.repository.postgres.IContractTypeRepository;
import co.edu.icesi.sidgymicesi.services.postgres.IContractTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractTypeServiceImpl implements IContractTypeService {

    private final IContractTypeRepository contractTypeRepository;

    @Override
    public ContractType save(ContractType contractType) {
        return contractTypeRepository.save(contractType);
    }

    @Override
    public List<ContractType> findAll() {
        return contractTypeRepository.findAll();
    }

    @Override
    public Optional<ContractType> findById(String name) {
        return contractTypeRepository.findById(name);
    }

    @Override
    public void deleteById(String name) {
        contractTypeRepository.deleteById(name);
    }
}
