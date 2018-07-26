package io.zero.epic;

import io.reactivex.Single;
import io.spring.up.exception.web._500URIInvalidException;
import io.spring.up.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

class Responsor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Responsor.class);

    static <T> Single<ResponseEntity<T>> steam201(final T entity, final String inputUri) {
        try {
            final URI uri = new URI(inputUri);
            final ResponseEntity<T> responseEntity = ResponseEntity.created(uri)
                    .body(entity);
            return Single.just(responseEntity);
        } catch (final URISyntaxException ex) {
            Log.jvm(LOGGER, ex);
            throw new _500URIInvalidException(Responsor.class.getClass(), inputUri);
        }
    }

    static <T> Single<ResponseEntity<T>> steam200(final T entity) {
        return Single.just(ResponseEntity.ok(entity));
    }
}
