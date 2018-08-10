package io.spring.up.log;

import io.spring.up.config.Node;
import io.spring.up.exception.internal.ErrorMissingException;
import io.vertx.core.json.JsonObject;
import io.zero.epic.Ut;
import io.zero.epic.fn.Fn;

import java.text.MessageFormat;

public class Errors {

    public static String formatUp(final Class<?> clazz,
                                  final int code,
                                  final Object... args) {
        return normalize(clazz, code, "error", Tpl.E_UP, args);
    }

    public static String formatPlugin(final Class<?> clazz,
                                      final int code,
                                      final Object... args) {
        return normalize(clazz, code, "error", Tpl.E_PLUGIN, args);
    }

    public static String formatWeb(final Class<?> clazz,
                                   final int code,
                                   final Object... args) {
        return normalize(clazz, code, "error", Tpl.E_WEB, args);
    }

    public static String formatReadible(final Class<?> clazz,
                                        final int code,
                                        final Object... args) {
        return normalize(clazz, code, "info", null, args);
    }

    private static String normalize(final Class<?> clazz,
                                    final int code,
                                    final String nodeKey,
                                    final String tpl,
                                    final Object... args) {
        return Fn.getJvm(() -> {
            final String key = ("E" + Math.abs(code)).intern();
            // 这里会影响到文件名
            JsonObject errors = Node.infix("error");
            // 错误信息读取
            if (null == errors) {
                throw new ErrorMissingException(code);
            }
            errors = Ut.readJson(new JsonObject(), errors, nodeKey);
            if (errors.containsKey(key)) {
                final String pattern = errors.getString(key);
                final String error = MessageFormat.format(pattern, args);
                return Ut.isNil(tpl) ? error : MessageFormat.format(tpl, String.valueOf(code), error);
            } else {
                throw new ErrorMissingException(code);
            }
        }, clazz);
    }
}
