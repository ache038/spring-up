package io.spring.up.epic;

import io.grpc.Channel;
import io.spring.up.epic.fn.Fn;
import io.spring.up.ipc.model.IpcRequest;
import io.spring.up.ipc.model.IpcResponse;
import io.spring.up.ipc.service.UnityServiceGrpc;
import io.spring.up.model.Envelop;
import io.vertx.core.json.JsonObject;
import org.springframework.scheduling.annotation.AsyncResult;

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
        return UnityServiceGrpc.newBlockingStub(this.channel);
    }

    private UnityServiceGrpc.UnityServiceFutureStub getFutureStub() {
        return UnityServiceGrpc.newFutureStub(this.channel);
    }

    public JsonObject sendJsonSync(final JsonObject data) {
        final IpcRequest request = Rpc.in(data);
        final IpcResponse response = this.getBlockStub().unityCall(request);
        return Rpc.outJson(response);
    }

    public Envelop sendJsonSync(final Envelop envelop) {
        final IpcRequest request = Rpc.in(envelop);
        final IpcResponse response = this.getBlockStub().unityCall(request);
        return Rpc.outEnvelop(response);
    }

    public Future<JsonObject> sendJson(final JsonObject data) {
        final IpcRequest request = Rpc.in(data);
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return AsyncResult.forValue(Fn.getJvm(() -> Rpc.outJson(response.get()), response));
    }

    public Future<Envelop> sendJson(final Envelop data) {
        final IpcRequest request = Rpc.in(data);
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return AsyncResult.forValue(Fn.getJvm(() -> Rpc.outEnvelop(response.get()), response));
    }
}
