package co.edu.icesi.sidgymicesi.services.postgres;

import co.edu.icesi.sidgymicesi.model.postgres.CourseGroup;

import java.util.List;
import java.util.Optional;

public interface ICourseGroupService {

    CourseGroup save(CourseGroup group);

    List<CourseGroup> findAll();

    Optional<CourseGroup> findById(String nrc);

    void deleteById(String nrc);
}
