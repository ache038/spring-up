package io.spring.up.epic;

import io.spring.up.epic.fn.Fn;

class To {

    static <T extends Enum<T>> T toEnum(final Class<T> clazz, final String input) {
        return Fn.getJvm(null, () -> Enum.valueOf(clazz, input), clazz, input);
    }

    static Integer toInteger(final Object value) {
        return Fn.getNull(null, () -> Integer.parseInt(value.toString()), value);
    }
}
