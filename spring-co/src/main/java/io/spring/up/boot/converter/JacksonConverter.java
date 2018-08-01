package io.spring.up.boot.converter;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.spring.up.exception.web._500InternalServerException;
import io.spring.up.exception.web._500JsonResponseException;
import io.spring.up.log.Log;
import io.vertx.core.json.JsonObject;
import io.zero.epic.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Type;

public class JacksonConverter extends MappingJackson2HttpMessageConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonConverter.class);
    private static final MediaType TEXT_EVENT_STREAM = new MediaType("text", "event-stream");
    private final PrettyPrinter ssePrettyPrinter;

    public JacksonConverter() {
        super(Ut.jacksonMapper());
        final DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
        this.ssePrettyPrinter = prettyPrinter;
    }

    @Override
    public void writeInternal(final Object object, final Type type, final HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        final MediaType contentType = outputMessage.getHeaders().getContentType();
        final JsonEncoding encoding = this.getJsonEncoding(contentType);
        final JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
        try {
            this.writePrefix(generator, object);

            Class<?> serializationView = null;
            FilterProvider filters = null;
            Object value = object;
            JavaType javaType = null;
            if (object instanceof MappingJacksonValue) {
                final MappingJacksonValue container = (MappingJacksonValue) object;
                value = container.getValue();
                serializationView = container.getSerializationView();
                filters = container.getFilters();
            }
            final Object dataValue = this.syncData(value);
            if (type != null && dataValue != null && TypeUtils.isAssignable(type, dataValue.getClass())) {
                javaType = this.getJavaType(JsonObject.class, null);
            }

            ObjectWriter objectWriter;
            if (serializationView != null) {
                objectWriter = this.objectMapper.writerWithView(serializationView);
            } else if (filters != null) {
                objectWriter = this.objectMapper.writer(filters);
            } else {
                objectWriter = this.objectMapper.writer();
            }
            if (javaType != null && javaType.isContainerType()) {
                objectWriter = objectWriter.forType(javaType);
            }
            final SerializationConfig config = objectWriter.getConfig();
            if (contentType != null && contentType.isCompatibleWith(TEXT_EVENT_STREAM) &&
                    config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                objectWriter = objectWriter.with(this.ssePrettyPrinter);
            }
            // 转换成Json data节点
            objectWriter.writeValue(generator, this.extractData(dataValue));
            this.writeSuffix(generator, object);
            generator.flush();
        } catch (final JsonProcessingException ex) {
            // TODO: Debug调试用
            ex.printStackTrace();
            throw new _500JsonResponseException(this.getClass(), ex);
        } catch (final Throwable ex) {
            // TODO: Debug调试用
            ex.printStackTrace();
            throw new _500InternalServerException(this.getClass(), ex.getMessage());
        }
    }

    private Object syncData(final Object value) {
        Object dataValue = null;
        if (value != null) {
            final Class<?> dataClass = value.getClass();
            Log.up(LOGGER, "Detected async flow class = {0}!", dataClass);
            if (Single.class == dataClass) {
                Log.up(LOGGER, "Single = {0}!", value);
                dataValue = ((Single<?>) value).blockingGet();
            } else if (Observable.class == dataClass) {
                Log.up(LOGGER, "Observable = {0}!", value);
                dataValue = ((Observable<?>) value).blockingSingle();
            } else if (Flowable.class == dataClass) {
                Log.up(LOGGER, "Flowable = {0}!", value);
                dataValue = ((Flowable<?>) value).blockingSingle();
            } else {
                dataValue = value;
            }
        }
        return dataValue;
    }

    private JsonObject extractData(final Object value) {
        JsonObject data = new JsonObject();
        if (null != value) {
            // data节点
            final Responser responser = Ut.singleton(DataResponser.class);
            data = responser.process(data, value);
        }
        Log.up(LOGGER, "Response Data: {0}", data.encode());
        // 解决Spring中的兼容性问题
        return data;
    }
}
