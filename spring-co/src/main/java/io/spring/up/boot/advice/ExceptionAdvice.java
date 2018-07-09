package io.spring.up.boot.advice;

import io.spring.up.epic.Ut;
import io.spring.up.exception.WebException;
import io.spring.up.exception.web._404NoElementException;
import io.spring.up.exception.web._409ConcurrencyException;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

@ControllerAdvice
@ResponseBody
public class ExceptionAdvice implements ProblemHandling {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(WebException.class)
    public JsonObject handle(final WebException ex,
                             final HttpServletResponse response) {
        response.setStatus(ex.getStatus().value());
        LOGGER.error("[ UP ] Error occurs: " + ex.getMessage());
        return ex.toJson();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Problem> errorNoElement(
            final NoSuchElementException ex,
            final NativeWebRequest request
    ) {
        final WebException error = new _404NoElementException(this.getClass(),
                ex);
        final Problem problem = this.buildProblem(error);
        return this.create(error, problem, request);
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    public ResponseEntity<Problem> errorConcurrency(
            final ConcurrencyFailureException ex,
            final NativeWebRequest request
    ) {
        final WebException error = new _409ConcurrencyException(this.getClass(),
                ex);
        final Problem problem = this.buildProblem(error);
        return this.create(error, problem, request);
    }

    private Problem buildProblem(final WebException error) {
        final ProblemBuilder builder = Problem.builder()
                .withStatus(Status.valueOf(error.getStatus().name()));
        final JsonObject detail = error.toJson();
        Ut.itJObject(detail, builder::with);
        return builder.build();
    }
}
