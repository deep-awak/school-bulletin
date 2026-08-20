package api.poja.app.service;

import api.poja.app.dto.request.CreateTeacherRequest;
import api.poja.app.exception.ResourceNotFoundException;
import api.poja.app.jpa.TeacherEntity;
import api.poja.app.mapper.TeacherMapper;
import api.poja.app.model.Teacher;
import api.poja.app.repository.TeacherRepository;
import api.poja.app.validator.TeacherValidator;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TeacherService {
  private final TeacherRepository teacherRepository;
  private final TeacherValidator teacherValidator;

  public Teacher create(CreateTeacherRequest request) {
    teacherValidator.validate(request);
    TeacherEntity entity =
        TeacherEntity.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .build();
    return TeacherMapper.toModel(teacherRepository.save(entity));
  }

  public Teacher getById(Long id) {
    return TeacherMapper.toModel(
        teacherRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + id)));
  }

  public List<Teacher> listAll() {
    return teacherRepository.findAll().stream().map(TeacherMapper::toModel).toList();
  }
}
