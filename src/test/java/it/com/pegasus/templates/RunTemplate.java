package it.com.pegasus.templates;

import com.pegasus.application.dto.RunDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import it.com.pegasus.templates.security.UserTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class RunTemplate extends UserTemplate {
    public RunDto create(String token, RunDto expected) {
        Response response = httpPost(token, expected, 200);
        RunDto actual = response.getBody().as(RunDto.class);
        assertThat(actual).usingRecursiveComparison().ignoringFields("id","startDate","endDate").isEqualTo(expected);
        return actual;
    }

    public RunDto findById(String token, Long id, int expectedHttpCode) {
        Response response = httpGetPathParams(token, String.valueOf(id), expectedHttpCode);
        return response.getBody().as(RunDto.class);
    }

    public RunDto partialUpdate(String token, RunDto expected) {
        Response response = httpPatch(token, expected, 200);
        RunDto actual = response.getBody().as(RunDto.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        return actual;
    }

    public List<RunDto> getAll(String token, String queryString) throws JsonProcessingException {
        Response response = httpGetQueryString(token, queryString, 200);
        TypeReference<List<RunDto>> typeRef = new TypeReference<List<RunDto>>() {};
        ObjectMapper om = new ObjectMapper();
        return om.readValue(response.getBody().asString(), typeRef);
    }

    @Override
    protected String getEndpoint() {
        return "api/v1/runs";
    }
}
