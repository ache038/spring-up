package io.spring.up.epic;

import io.grpc.Channel;
import io.reactivex.Single;
import io.spring.up.cv.Constants;
import io.spring.up.epic.fn.Fn;
import io.spring.up.ipc.model.IpcRequest;
import io.spring.up.ipc.model.IpcResponse;
import io.spring.up.ipc.service.UnityServiceGrpc;
import io.spring.up.model.Envelop;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.Future;

public class RpcClient {

    private final transient Channel channel;

    private RpcClient(final Channel channel) {
        this.channel = channel;
    }

    public static RpcClient newInstance(final Channel channel) {
        return new RpcClient(channel);
    }

    private UnityServiceGrpc.UnityServiceBlockingStub getBlockStub() {
        return UnityServiceGrpc.newBlockingStub(this.channel).withWaitForReady();
    }

    private UnityServiceGrpc.UnityServiceFutureStub getFutureStub() {
        return UnityServiceGrpc.newFutureStub(this.channel).withWaitForReady();
    }

    private JsonObject wrapperData(final String address, final JsonObject data) {
        final JsonObject result = new JsonObject();
        result.put(Constants.DATA, data);
        result.put(Constants.ADDRESS, address);
        return result;
    }

    public JsonObject sendJsonSync(final String address, final JsonObject data) {
        final IpcRequest request = Rpc.in(this.wrapperData(address, data));
        final IpcResponse response = this.getBlockStub().unityCall(request);
        return Rpc.outJson(response);
    }

    public Envelop sendJsonSync(final String address, final Envelop envelop) {
        final JsonObject result = this.wrapperData(address, envelop.json());
        final IpcRequest request = Rpc.in(Envelop.success(result));
        final IpcResponse response = this.getBlockStub().unityCall(request);
        return Rpc.outEnvelop(response);
    }

    public Single<JsonObject> sendJson(final String address, final JsonObject data) {
        final IpcRequest request = Rpc.in(this.wrapperData(address, data));
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return Single.just(Fn.getJvm(() -> Rpc.outJson(response.get()), response));
    }

    public Single<Envelop> sendJson(final String address, final Envelop data) {
        final JsonObject result = this.wrapperData(address, data.json());
        final IpcRequest request = Rpc.in(Envelop.success(result));
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return Single.just(Fn.getJvm(() -> Rpc.outEnvelop(response.get()), response));
    }
}
