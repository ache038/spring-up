package io.spring.up.epic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.Channel;
import io.reactivex.Single;
import io.spring.up.cv.Constants;
import io.spring.up.ipc.model.IpcRequest;
import io.spring.up.ipc.model.IpcResponse;
import io.spring.up.model.Envelop;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 专用工具类
 */
public class Ut {

    public static Object rxDebug(final Object object) {
        System.err.print("[ UP DG ] Error: ");
        System.err.println(object);
        if (null != object) {
            System.err.println("[ UP DG ] Class: " + object.getClass());
        }
        return object;
    }
    // -------------------- IO专用方法 ---------------------------

    /**
     * 从文件中读取配置文件
     *
     * @param filename 传入的文件路径
     * @return {Properties} 返回Java中的属性文件对象
     */
    public static Properties ioProp(final String filename) {
        return IO.getProp(filename);
    }

    public static File ioFile(final String filename) {
        return IO.getFile(filename);
    }

    public static URL ioURL(final String filename) {
        return IO.getURL(filename);
    }

    public static String ioString(final String filename) {
        return IO.getString(filename);
    }

    public static JsonArray ioJArray(final String filename) {
        return IO.getJArray(filename);
    }

    public static JsonObject ioJObject(final String filename) {
        return IO.getJObject(filename);
    }

    public static <T> T ioJYaml(final String filename) {
        return IO.getJYaml(filename);
    }

    public static InputStream ioStream(final String filename) {
        return IO.getStream(filename);
    }

    // ID处理方法
    public static JsonObject inKey(final JsonObject input) {
        return Json.convert(input, "key", "id");
    }

    public static JsonArray inKey(final JsonArray array) {
        return Json.convert(array, "key", "id");
    }

    public static JsonObject outKey(final JsonObject input) {
        return Json.convert(input, "id", "key");
    }

    public static JsonArray outKey(final JsonArray array) {
        return Json.convert(array, "id", "key");
    }

    // 反射方法
    public static <T> T instance(final String name, final Object... args) {
        return Instance.instance(Instance.clazz(name), args);
    }

    public static <T> T instance(final Class<?> clazz, final Object... args) {
        return Instance.instance(clazz, args);
    }

    public static <T> T singleton(final String name, final Object... args) {
        return Instance.singleton(Instance.clazz(name), args);
    }

    public static <T> T singleton(final Class<?> clazz, final Object... args) {
        return Instance.singleton(clazz, args);
    }

    public static <T> T invoke(final Object instance, final String methodName, final Object... args) {
        return Instance.invokeObject(instance, methodName, args);
    }

    public static Class<?> clazz(final String name) {
        return Instance.clazz(name);
    }

    // 集合遍历专用方法
    public static void itJObject(final JsonObject object, final BiConsumer<String, Object> consumer) {
        It.itJObject(object, consumer);
    }

    public static <T> void itJObject(final JsonObject object, final Class<T> clazz, final BiConsumer<String, T> consumer) {
        It.itJObject(object, clazz, consumer);
    }

    public static <T> HashSet<T> rdcHashSet(final HashSet<T> hashset, final T element) {
        return It.rdcHashSet(hashset, element);
    }

    public static <T> void itJArray(final JsonArray array, final Class<T> clazz, final BiConsumer<T, Integer> consumer) {
        It.itJArray(array, clazz, consumer);
    }

    // 快速方法（遍历元素为JsonObject的数组）
    public static void itJArray(final JsonArray array, final BiConsumer<JsonObject, Integer> consumer) {
        It.itJArray(array, JsonObject.class, consumer);
    }

    public static <V> void itList(final List<V> list,
                                  final BiConsumer<V, Integer> fnEach) {
        It.itList(list, fnEach);
    }

    // 类型判断
    public static boolean isPositive(final String original) {
        return Numeric.isPositive(original);
    }

    public static boolean isNegative(final String original) {
        return Numeric.isNegative(original);
    }

    public static boolean isReal(final String original) {
        return Numeric.isReal(original);
    }

    public static boolean isDecimal(final String original) {
        return Numeric.isDecimal(original);
    }

    public static boolean isDecimal(final Object value) {
        return Types.isDecimal(value);
    }

    public static boolean isDecimal(final Class<?> clazz) {
        return Types.isDecimal(clazz);
    }

    public static boolean isEmpty(final String literal) {
        return Types.isEmpty(literal);
    }

    // 类型判断
    public static boolean isInteger(final String original) {
        return Numeric.isInteger(original);
    }

    public static boolean isInteger(final Object value) {
        return Types.isInteger(value);
    }

    public static boolean isInteger(final Class<?> clazz) {
        return Types.isInteger(clazz);
    }

    public static boolean isJArray(final Object value) {
        return Types.isJArray(value);
    }

    public static boolean isJArray(final String literal) {
        return Types.isJArray(literal);
    }

    public static boolean isJArray(final Class<?> clazz) {
        return Types.isJArray(clazz);
    }

    public static boolean isJObject(final Class<?> clazz) {
        return Types.isJObject(clazz);
    }

    public static boolean isJObject(final String literal) {
        return Types.isJObject(literal);
    }

    public static boolean isJObject(final Object value) {
        return Types.isJObject(value);
    }

    public static boolean isBoolean(final Object value) {
        return Types.isBoolean(value);
    }

    public static boolean isBoolean(final Class<?> clazz) {
        return Types.isBoolean(clazz);
    }

    public static boolean isPrimary(final Class<?> clazz) {
        return Types.isPrimary(clazz);
    }

    public static boolean isVoid(final Class<?> clazz) {
        return Types.isVoid(clazz);
    }

    public static boolean isDate(final Object value) {
        return Types.isDate(value);
    }

    // 值域处理
    public static boolean inRange(final Integer value, final Integer min, final Integer max) {
        return Numeric.inRange(value, min, max);
    }

    // 读取数据
    public static Object readJson(final JsonObject data, final String key) {
        return Jackson.readJson(null, data, key);
    }

    public static Object readJson(final Object value, final JsonObject data, final String key) {
        return Jackson.readJson(value, data, key);
    }

    public static JsonObject readJson(final JsonObject value, final JsonObject data, final String key) {
        final Object result = Jackson.readJson(value, data, key);
        return null == result ? new JsonObject() : (JsonObject) result;
    }

    public static String readJson(final String value, final JsonObject data, final String key) {
        final Object result = Jackson.readJson(value, data, key);
        return null == result ? value : result.toString();
    }

    public static Integer readInt(final Integer value, final JsonObject data, final String key) {
        return Jackson.readInt(value, data, key);
    }

    // 类型转换
    public static <T extends Enum<T>> T toEnum(final Class<T> clazz, final String input) {
        return To.toEnum(clazz, input);
    }

    public static LocalDateTime toDateTime(final String literal) {
        return Period.toDateTime(literal);
    }

    public static LocalDate toDate(final String literal) {
        return Period.toDate(literal);
    }

    public static LocalTime toTime(final String literal) {
        return Period.toTime(literal);
    }

    public static int toMonth(final String literal) {
        return Period.toMonth(literal);
    }

    public static int toMonth(final Date date) {
        return Period.toMonth(date);
    }

    public static int toYear(final String literal) {
        return Period.toYear(literal);
    }

    public static int toYear(final Date date) {
        return Period.toYear(date);
    }

    public static Integer toInteger(final Object value) {
        return To.toInteger(value);
    }

    public static String toString(final Object value) {
        return To.toString(value);
    }

    public static JsonObject toJObject(final Map<String, Object> map) {
        return To.toJObject(map);
    }

    /**
     * 加密
     *
     * @param literal
     * @return
     */
    public static String encryptMD5(final String literal) {
        return Codec.encryptMD5(literal);
    }

    public static String encryptSHA256(final String literal) {
        return Codec.encryptSHA256(literal);
    }

    public static String encryptSHA512(final String literal) {
        return Codec.encryptSHA512(literal);
    }

    /**
     * 时间全解析
     *
     * @param literal
     * @return
     */
    public static Date parse(final String literal) {
        return Period.parse(literal);
    }

    public static Date parse(final LocalTime time) {
        return Period.parse(time);
    }

    public static Date parse(final LocalDateTime datetime) {
        return Period.parse(datetime);
    }

    public static Date parse(final LocalDate date) {
        return Period.parse(date);
    }

    public static Date parseFull(final String literal) {
        return Period.parseFull(literal);
    }

    /**
     * Json序列化
     */
    public static <T, R extends Iterable> R serializeJson(final T t) {
        return Jackson.serializeJson(t);
    }

    public static <T> String serialize(final T t) {
        return Jackson.serialize(t);
    }

    public static <T> T deserialize(final JsonObject value, final Class<T> type) {
        return Jackson.deserialize(value, type);
    }

    public static <T> T deserialize(final JsonArray value, final Class<T> type) {
        return Jackson.deserialize(value, type);
    }

    public static <T> List<T> deserialize(final JsonArray value, final TypeReference<List<T>> type) {
        return Jackson.deserialize(value, type);
    }

    public static <T> T deserialize(final String value, final Class<T> type) {
        return Jackson.deserialize(value, type);
    }

    public static <T> T deserialize(final String value, final TypeReference<T> type) {
        return Jackson.deserialize(value, type);
    }

    public static ObjectMapper getJacksonMapper() {
        return Jackson.getMapper();
    }

    public static ObjectMapper registryJacksonMapper(final ObjectMapper mapper) {
        return Jackson.registerMapper(mapper);
    }

    // -------------------- 网络检查方法 ---------------------------
    public static boolean netOk(final String host, final int port) {
        return Net.isReach(host, port);
    }

    public static String netIPv4() {
        return Net.getIPv4();
    }

    public static String netIPv6() {
        return Net.getIPv6();
    }

    public static String netHostname() {
        return Net.netHostname();
    }

    public static String netIP() {
        return Net.getIP();
    }

    // 响应专用方法（生成ResponseEntity都是最后一个方法）
    // （200）T -> Single<ResponseEntity<T>>
    public static <T> Single<ResponseEntity<T>> end(final T entity) {
        return Async.ok(() -> entity);
    }

    // （200）Single<T> -> Single<ResponseEntity<T>>
    public static <T> Single<ResponseEntity<T>> end(final Single<T> item) {
        return Async.ok(item);
    }

    // （200）Supplier<T> -> T -> Single<ResponseEntity<T>>
    public static <T> Single<ResponseEntity<T>> end(final Supplier<T> supplierFun) {
        return Async.ok(supplierFun);
    }

    // （200）Function<T,T> -> T -> Single<ResponseEntity<T>> -> 最后需要apply
    public static <T> Function<T, Single<ResponseEntity<T>>> end(final Function<T, T> applyFun) {
        return Async.ok(applyFun);
    }

    // （201）T -> Single<ResponseEntity<T>> -> 最后需要apply
    public static <T> Function<String, Single<ResponseEntity<T>>> created(final T item) {
        return Async.created(Single.just(item));
    }

    // （201）Single<T> -> Single<ResponseEntity<T>> -> 最后需要apply
    public static <T> Function<String, Single<ResponseEntity<T>>> created(final Single<T> item) {
        return Async.created(item);
    }

    // （200）T -> Single<T>
    public static <T> Single<T> ok(final T start) {
        return Single.just(start);
    }

    // （200）Supplier<T> -> T -> Single<T>
    public static <T> Single<T> ok(final Supplier<T> supplier) {
        return Single.just(supplier.get());
    }

    // （200）Function<T,T> -> T -> Single<T>
    public static <T> Function<T, Single<T>> ok(final Function<T, T> applyFun) {
        return Async.start(applyFun);
    }


    // Rpc专用方法
    public static class Rpc {
        public static IpcRequest request(final JsonObject data) {
            return io.spring.up.epic.Rpc.in(data);
        }

        public static IpcRequest request(final Envelop envelop) {
            return io.spring.up.epic.Rpc.in(envelop);
        }

        public static IpcResponse response(final JsonObject data) {
            return io.spring.up.epic.Rpc.out(data);
        }

        public static IpcResponse response(final Envelop envelop) {
            return io.spring.up.epic.Rpc.out(envelop);
        }

        public static Envelop envelop(final IpcRequest request) {
            return io.spring.up.epic.Rpc.inEnvelop(request);
        }

        public static Envelop envelop(final IpcResponse response) {
            return io.spring.up.epic.Rpc.outEnvelop(response);
        }

        public static JsonObject json(final IpcRequest request) {
            return io.spring.up.epic.Rpc.inJson(request);
        }

        public static JsonObject json(final IpcResponse response) {
            return io.spring.up.epic.Rpc.outJson(response);
        }

        public static RpcClient getClient(final Channel channel) {
            return RpcClient.newInstance(channel);
        }
    }

    public static Optional<String> fetchLogin() {
        return Secure.getCurrentUserLogin();
    }

    public static String fetchUserId() {
        return Secure.getAuthorities().getString(Constants.USER_ID);
    }

    public static String fetchRoleId() {
        return Secure.getAuthorities().getString(Constants.ROLE_ID);
    }

    public static String fetchRoleName() {
        return Secure.getAuthorities().getString(Constants.ROLE_NAME);
    }

    public static String toJsonAuthority(final String literal) {
        final String content = new JsonObject(literal).encode();
        return Base64.getEncoder().encodeToString(content.getBytes(Charset.forName("UTF-8")));
    }

    public static String toJsonAuthority(final String userId, final String roleName, final String roleId) {
        final String content = new JsonObject().put(Constants.USER_ID, userId).put(Constants.ROLE_ID, roleId).put(Constants.ROLE_NAME, roleName).encode();
        return Base64.getEncoder().encodeToString(content.getBytes(Charset.forName("UTF-8")));
    }

    public static boolean inAuthoried() {
        return Secure.isAuthenticated();
    }

    public static boolean inRole(final String authority) {
        return Secure.isInRole(authority);
    }

    // 格式化专用方法
    public static String fromExpression(final String expr, final JsonObject data) {
        return Expr.expression(expr, data);
    }

    public static String fromExpression(final String expr, final Map<String, Object> map) {
        return Expr.expression(expr, To.toJObject(map));
    }

    public static String toUri(final String url) {
        return Expr.uri(url);
    }
}
