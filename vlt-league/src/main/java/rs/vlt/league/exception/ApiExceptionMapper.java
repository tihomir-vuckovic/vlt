package rs.vlt.league.exception;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<RuntimeException> {
    @Override public Response toResponse(RuntimeException exception) {
        int status = exception instanceof NotFoundException ? 404 : exception instanceof BadRequestException ? 400 : 500;
        return Response.status(status).entity(Map.of("status", status, "message", status == 500 ? "Unexpected server error" : exception.getMessage())).build();
    }
}