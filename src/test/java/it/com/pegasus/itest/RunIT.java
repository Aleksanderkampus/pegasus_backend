package it.com.pegasus.itest;

import com.pegasus.application.dto.RunDto;
import com.pegasus.application.exeptions.UserException;
import com.pegasus.application.mocks.dto.RunDtoMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import it.com.pegasus.templates.RunTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RunIT extends RunTemplate {

    @Test
    public void testCreateRunSuccess () {
        String token = loginAndGetToken();
        RunDto saved = createFlow(token);

        RunDto foundById = findById(token,saved.getId(), 200);
        assertThat(foundById).usingRecursiveComparison().isEqualTo(saved);

        delete(token,foundById.getId());
    }

    @Test
    public void testDeleteSuccessful() throws Exception {
        String token = loginAndGetToken();
        RunDto saved = createFlow(token);
        Long orgId = saved.getId();

        delete(token, orgId);

        Response res =  httpGetPathParams(token, orgId.toString(), HttpStatus.BAD_REQUEST.value());

        UserException exception = res.getBody().as(UserException.class);

        assertThat(exception.getMessage()).isEqualTo("Run#" + orgId + " not found");

    }

    @Test
    public void testUpdateRunSuccess () {
        String token = loginAndGetToken();
        RunDto saved = createFlow(token);

        saved.setCaloriesBurnt(1D);

        RunDto updatedRunDto = partialUpdate(token, saved);

        assertThat(updatedRunDto.getCaloriesBurnt()).isEqualTo(saved.getCaloriesBurnt());
        assertThat(updatedRunDto.getId()).isEqualTo(saved.getId());

        delete(token,saved.getId());
    }

    @Test
    public void testGetAllRunsSuccess () throws JsonProcessingException {
        String token = loginAndGetToken();
        RunDto saved = createFlow(token);
        RunDto saved2 = createFlow(token);

        List<RunDto> allRuns = getAll(token,"");

        assertThat(allRuns.size()).isEqualTo(2);
        assertThat(allRuns.get(0)).isEqualTo(saved);
        assertThat(allRuns.get(1)).isEqualTo(saved2);

        delete(token,saved.getId());
        delete(token,saved2.getId());
    }

    @Test
    public void testFindByIdWithNoExistingRun () {
        String token = loginAndGetToken();

        Response res =  httpGetPathParams(token, "100", HttpStatus.BAD_REQUEST.value());

        UserException exception = res.getBody().as(UserException.class);

        assertThat(exception.getMessage()).isEqualTo("Run#" + 100 + " not found");
    }

    @Test
    public void testCreateRunWithLocationBlank () {
        String token = loginAndGetToken();
        RunDto expected = RunDtoMock.createMockRunDto(null);
        expected.setLocationCity("");
        Response response = httpPost(token, expected, 400);;
        System.out.println(response.getBody().print());

    }

    private RunDto createFlow(String token) {
        RunDto expected = RunDtoMock.createMockRunDto(null);
        return create(token,expected);
    }
    
}
