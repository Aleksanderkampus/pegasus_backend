package com.pegasus.application.service;

import com.pegasus.application.dto.RunDto;
import com.pegasus.application.exeptions.UserException;
import com.pegasus.application.mapper.RunMapper;
import com.pegasus.application.mapper.RunMapperImpl;
import com.pegasus.application.mocks.dto.RunDtoMock;
import com.pegasus.application.mocks.model.RunMock;
import com.pegasus.application.mocks.model.UserMock;
import com.pegasus.application.models.Run;
import com.pegasus.application.models.User;
import com.pegasus.application.repository.RunRepository;
import com.pegasus.application.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RunServiceTest {
    
    @Spy
    private final RunMapper runMapper = new RunMapperImpl();
    
    @Captor
    private ArgumentCaptor<Run> runCaptor;
    
    @Mock
    private RunRepository runRepository;
    
    @Mock
    private UserDetailsServiceImpl userService;
    
    @InjectMocks
    private  RunService runService;
    
    @BeforeEach
    public void initDependencies() {
        ReflectionTestUtils.setField(runService, "userService", userService);
    }

    @Test
    @DisplayName("Save Run successful")
    void saveRun() {
        //1) Generate an mocked run
        RunDto runDtoToSave = RunDtoMock.createMockRunDto(null);
        //2) prepare mocks for everything they should return
        when(userService.getCurrentUser()).thenAnswer(i -> UserMock.shallowUser(1L));
        when(runRepository.save(Mockito.any(Run.class))) //any object of type Run will match here
                .thenAnswer(i -> i.getArguments()[0]);

        RunDto savedRun = runService.create(runDtoToSave);

        //4) use captor in spy/mocks for everything they get as input
        verify(runRepository).save(runCaptor.capture());
        Run runToSave = runCaptor.getValue();

        //5) check if all dependencies were called (eventually with check on input data)
        verify(runMapper, times(1)).toEntity(runDtoToSave);
        verify(runRepository, times(1)).save(runToSave);
        verify(runMapper, times(1)).toDto(runToSave);

        //6) assertions actual vs expected
        assertThat(runDtoToSave).isEqualTo(savedRun);
    }

    @Test
    @DisplayName("Find one Run by id success")
    void findOneSuccess(){
        final Long runId = 1L;

        Run run = RunMock.createMockRun(runId);

        RunDto runDto = RunDtoMock.createMockRunDto(runId);

        when(runRepository.findById(runId)).thenReturn(Optional.of(run));
        when(runMapper.toDto(Mockito.any(Run.class))).thenReturn(runDto);

        RunDto result = runService.getById(runId);

        verify(runMapper, times(1)).toDto(any(Run.class));
        verify(runRepository, times(1)).findById(runId);

        assertThat(result).isEqualTo(runDto);
    }

    @Test
    @DisplayName("Find one Run by id failure")
    void findOneFailure(){
        final Long runId = 1L;

        when(runRepository.findById(runId)).thenThrow(new UserException("Run wth this id doesn't exist"));

        assertThrows(UserException.class, () -> runService.getById(runId));
    }

    @Test
    @DisplayName("Delete success")
    void deleteSuccess (){
        Long entityIdToDelete = 1L;

        runService.delete(entityIdToDelete);

        verify(runRepository, times(1)).deleteById(entityIdToDelete);
        when(runRepository.findById(entityIdToDelete)).thenThrow(new UserException("Run with this id doesn't exist"));

        assertThrows(UserException.class, () -> runService.getById(entityIdToDelete));

    }

    @Test
    @DisplayName("Partial Update success")
    void partialUpdateRun (){

        RunDto runDto = RunDtoMock.createMockRunDto(1L);
        runDto.setCaloriesBurnt(100D);

        Run foundEntity = runMapper.toEntity(runDto);

        when(runRepository.findById(1L)).thenReturn(Optional.of(foundEntity));

        RunDto updatedDto = runService.partialUpdate(runDto);

        verify(runMapper, times(1)).partialUpdate(foundEntity,runDto);
        verify(runRepository, times(1)).save(foundEntity);
        verify(runMapper, times(1)).toDto(foundEntity);

        assertThat(runDto).isEqualTo(updatedDto);

    }

    @Test
    @DisplayName("Find all Runs")
    void findAllRuns(){
        final Long runId = 1L;

        Run run1 = RunMock.createMockRun(runId);
        Run run2 = RunMock.createMockRun(runId);

        List<Run> runs = List.of(run1, run2);

        RunDto runDto1 = RunDtoMock.createMockRunDto(runId);
        RunDto runDto2 = RunDtoMock.createMockRunDto(runId);

        List<RunDto> runDtos = List.of(runDto1, runDto2);

        User user = UserMock.shallowUser(1L);

        when(userService.getCurrentUser()).thenAnswer(i -> user);
        when(runRepository.findAllByUserEmail(user.getEmail())).thenReturn(runs);
        when(runMapper.toDtos(Mockito.anyList())).thenReturn(runDtos);

        List<RunDto> results = runService.getAll();

        verify(runMapper, times(1)).toDtos(anyList());
        verify(runRepository, times(1)).findAllByUserEmail(user.getEmail());

        assertThat(runs.size()).isEqualTo(results.size());
    }


}
