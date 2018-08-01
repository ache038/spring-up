package io.spring.up.aiki;

import io.grpc.Channel;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.spring.up.cv.Constants;
import io.spring.up.ipc.model.IpcRequest;
import io.spring.up.ipc.model.IpcResponse;
import io.spring.up.ipc.service.UnityServiceGrpc;
import io.spring.up.model.Envelop;
import io.vertx.core.json.JsonObject;
import io.zero.epic.fn.Fn;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

public class RpcClient {

    private final transient Channel channel;

    private final ConcurrentMap<Integer, UnityServiceGrpc.UnityServiceBlockingStub>
            BLOCK_POOL = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, UnityServiceGrpc.UnityServiceFutureStub>
            FUTURE_POOL = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, UnityServiceGrpc.UnityServiceStub>
            STUB_POOL = new ConcurrentHashMap<>();

    private RpcClient(final Channel channel) {
        this.channel = channel;
    }

    public static RpcClient newInstance(final Channel channel) {
        return new RpcClient(channel);
    }

    private UnityServiceGrpc.UnityServiceBlockingStub getBlockStub() {
        return Fn.pool(this.BLOCK_POOL, this.channel.hashCode(),
                () -> UnityServiceGrpc.newBlockingStub(this.channel).withWaitForReady());
    }

    private UnityServiceGrpc.UnityServiceFutureStub getFutureStub() {
        return Fn.pool(this.FUTURE_POOL, this.channel.hashCode(),
                () -> UnityServiceGrpc.newFutureStub(this.channel).withWaitForReady());
    }

    private UnityServiceGrpc.UnityServiceStub getStub() {
        return Fn.pool(this.STUB_POOL, this.channel.hashCode(),
                () -> UnityServiceGrpc.newStub(this.channel).withWaitForReady());
    }

    private JsonObject wrapperData(final String address, final JsonObject data) {
        final JsonObject result = new JsonObject();
        result.put(Constants.DATA, data);
        result.put(Constants.ADDRESS, address);
        return result;
    }


    public JsonObject syncJson(final String address, final JsonObject data) {
        final IpcRequest request = Rpc.in(this.wrapperData(address, data));
        final IpcResponse response = this.getBlockStub().unityCall(request);
        return Rpc.outJson(response);
    }

    public Envelop syncEnvelop(final String address, final Envelop envelop) {
        final JsonObject result = this.wrapperData(address, envelop.json());
        final IpcRequest request = Rpc.in(Envelop.success(result));
        final IpcResponse response = this.getBlockStub().unityCall(request);
        return Rpc.outEnvelop(response);
    }

    public Observable<JsonObject> rxObservable(
            final String address, final JsonObject data) {
        final IpcRequest request = Rpc.in(this.wrapperData(address, data));
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return Observable.fromFuture(response)
                .map(Rpc::outJson);
    }

    public Observable<Envelop> rxObservable(
            final String address, final Envelop data
    ) {
        final JsonObject result = this.wrapperData(address, data.json());
        final IpcRequest request = Rpc.in(Envelop.success(result));
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return Observable.fromFuture(response)
                .map(Rpc::outEnvelop);
    }

    public Single<JsonObject> rxSingle(final String address, final JsonObject data) {
        final IpcRequest request = Rpc.in(this.wrapperData(address, data));
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return Single.fromFuture(response)
                .map(Rpc::outJson);
    }


    public Single<Envelop> rxSingle(final String address, final Envelop data) {
        final JsonObject result = this.wrapperData(address, data.json());
        final IpcRequest request = Rpc.in(Envelop.success(result));
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return Single.fromFuture(response)
                .map(Rpc::outEnvelop);
    }

    public Flowable<JsonObject> rxFlow(final String address, final JsonObject data) {
        final IpcRequest request = Rpc.in(this.wrapperData(address, data));
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return Flowable.fromFuture(response)
                .map(Rpc::outJson);
    }

    public Flowable<Envelop> rxFlow(final String address, final Envelop data) {
        final JsonObject result = this.wrapperData(address, data.json());
        final IpcRequest request = Rpc.in(Envelop.success(result));
        final Future<IpcResponse> response = this.getFutureStub().unityCall(request);
        return Flowable.fromFuture(response)
                .map(Rpc::outEnvelop);
    }
}
