package io.spring.up.epic;

import io.spring.up.cv.Strings;
import io.spring.up.log.Log;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

class Types {

    private static final Logger LOGGER = LoggerFactory.getLogger(Types.class);

    static boolean isJArray(final String literal) {
        return isJson(JsonArray::new, literal);
    }

    static boolean isJObject(final String literal) {
        return isJson(JsonObject::new, literal);
    }

    private static boolean isJson(final Consumer<String> consumer, final String literal) {
        if (null == literal) {
            return false;
        }
        try {
            consumer.accept(literal);
            return true;
        } catch (final DecodeException ex) {
            Log.jvm(LOGGER, ex);
            return false;
        } catch (final Throwable ex) {
            Log.jvm(LOGGER, ex);
            return false;
        }
    }

    static boolean isJArray(final Object value) {
        return null != value && isJArray(value.getClass());
    }

    static boolean isJArray(final Class<?> clazz) {
        return null != clazz && JsonArray.class == clazz;
    }

    static boolean isJObject(final Object value) {
        return null != value && isJObject(value.getClass());
    }

    static boolean isJObject(final Class<?> clazz) {
        return null != clazz && JsonObject.class == clazz;
    }

    static boolean isEmpty(final String literal) {
        return null == literal || 0 == literal.trim().length();
    }

    static boolean isVoid(final Class<?> clazz) {
        return null != clazz && (Void.class == clazz || void.class == clazz);
    }

    static boolean isInteger(final Class<?> clazz) {
        return null != clazz && (int.class == clazz || Integer.class == clazz
                || long.class == clazz || Long.class == clazz
                || short.class == clazz || Short.class == clazz);
    }

    static boolean isInteger(final Object value) {
        return null != value && isInteger(value.getClass());
    }

    static boolean isDecimal(final Object value) {
        return null != value && isDecimal(value.getClass());
    }

    static boolean isDecimal(final Class<?> clazz) {
        return null != clazz && (BigDecimal.class == clazz
                || double.class == clazz || Double.class == clazz
                || float.class == clazz || Float.class == clazz);
    }

    static boolean isDate(final Object value) {
        return null != value && Period.isValid(value.toString());
    }

    static boolean isBoolean(final Class<?> clazz) {
        return null != clazz && (boolean.class == clazz || Boolean.class == clazz);
    }

    static boolean isBoolean(final Object value) {
        boolean logical = false;
        if (null != value) {
            final String literal = value.toString();
            if (Strings.TRUE.equalsIgnoreCase(literal)
                    || Integer.valueOf(1).toString().equalsIgnoreCase(literal)
                    || Strings.FALSE.equalsIgnoreCase(literal)
                    || Integer.valueOf(0).toString().equalsIgnoreCase(literal)) {
                logical = true;
            }
        }
        return logical;
    }

    static boolean isPrimary(final Class<?> source) {
        return UNBOXES.values().contains(source);
    }

    private static final ConcurrentMap<Class<?>, Class<?>> UNBOXES =
            new ConcurrentHashMap<Class<?>, Class<?>>() {
                {
                    this.put(Integer.class, int.class);
                    this.put(Long.class, long.class);
                    this.put(Short.class, short.class);
                    this.put(Boolean.class, boolean.class);
                    this.put(Character.class, char.class);
                    this.put(Double.class, double.class);
                    this.put(Float.class, float.class);
                    this.put(Byte.class, byte.class);
                }
            };
}
