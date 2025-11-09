package co.edu.icesi.sidgymicesi.services.impl.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.CourseGroup;
import co.edu.icesi.sidgymicesi.repository.postgres.ICourseGroupRepository;
import co.edu.icesi.sidgymicesi.services.postgres.ICourseGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseGroupServiceImpl implements ICourseGroupService {

    private final ICourseGroupRepository courseGroupRepository;

    @Override
    public CourseGroup save(CourseGroup group) {
        return courseGroupRepository.save(group);
    }

    @Override
    public List<CourseGroup> findAll() {
        return courseGroupRepository.findAll();
    }

    @Override
    public Optional<CourseGroup> findById(String nrc) {
        return courseGroupRepository.findById(nrc);
    }

    @Override
    public void deleteById(String nrc) {
        courseGroupRepository.deleteById(nrc);
    }
}
