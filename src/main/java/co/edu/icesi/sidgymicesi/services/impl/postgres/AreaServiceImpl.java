package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.Area;
import co.edu.icesi.sidgymicesi.repository.postgres.IAreaRepository;
import co.edu.icesi.sidgymicesi.services.postgres.IAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AreaServiceImpl implements IAreaService {

    private final IAreaRepository areaRepository;

    @Override
    public Area save(Area area) {
        return areaRepository.save(area);
    }

    @Override
    public List<Area> findAll() {
        return areaRepository.findAll();
    }

    @Override
    public Optional<Area> findById(Integer code) {
        return areaRepository.findById(code);
    }

    @Override
    public void deleteById(Integer code) {
        areaRepository.deleteById(code);
    }
}
