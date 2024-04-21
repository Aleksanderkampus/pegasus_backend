package com.pegasus.application.service;

import com.pegasus.application.dto.RunDto;
import com.pegasus.application.exeptions.UserException;
import com.pegasus.application.mapper.RunMapper;
import com.pegasus.application.models.Run;
import com.pegasus.application.repository.RunRepository;
import com.pegasus.application.service.common.GenericService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RunService extends GenericService {

    private final RunRepository runRepository;
    private final RunMapper mapper;

    public List<RunDto> getAll() {
        String userEmail = getCurrentUserAsEntity().getEmail();
        List<Run> runs = runRepository.findAllByUserEmail(userEmail);
        return mapper.toDtos(runs);
    }

    public RunDto create(RunDto runDto) {
        Run run = mapper.toEntity(runDto);
        run.setUser(getCurrentUserAsEntity());
        return mapper.toDto(runRepository.save(run));
    }
    @Transactional
    public RunDto update(RunDto runDto) {

        Run run = runRepository.findById(runDto.getId()).orElseThrow(()
                -> new UserException("Run#" + runDto.getId() + " not found"));
        mapper.update(run, runDto);
        runRepository.save(run);
        return mapper.toDto(run);
    }

    @Transactional
    public RunDto getById(Long id) {
        Run run = runRepository.findById(id).orElseThrow(()
                -> new UserException("Run#" + id + " not found"));
        return mapper.toDto(run);
    }

    @Transactional
    public RunDto partialUpdate(RunDto runDto) {

        Run run = runRepository.findById(runDto.getId()).orElseThrow(()
                -> new UserException("Run#" + runDto.getId() + " not found"));
        mapper.partialUpdate(run, runDto);
        runRepository.save(run);
        return mapper.toDto(run);
    }

    public void delete(Long id) {
        runRepository.deleteById(id);
    }

}
