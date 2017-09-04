package com.phicomm.smartplug.modules.data.remote.http;

import com.phicomm.smartplug.modules.data.remote.beans.scene.CancelSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CreateSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.DeleteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.EditSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.ExecuteSceneResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.GetSceneDetailResponseBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.GetSceneListResponseBean;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

public interface HttpSceneInterface {
    // create scene
    @Headers({"Content-Type:application/json", "Accept: application/json"})
    @PUT("scenario")
    Observable<CreateSceneResponseBean> createScene(@Header("Authorization") String access_token,
                                                    @Body RequestBody body);

    // get scene list
    @GET("scenario/all")
    Observable<GetSceneListResponseBean> getSceneList(@Header("Authorization") String access_token,
                                                      @Query("productID") String productID);

    // get scene detail
    @GET("scenario")
    Observable<GetSceneDetailResponseBean> getSceneDetail(@Header("Authorization") String access_token,
                                                          @Query("scenario_id") long sceneId);

    // modify scene
    @POST("scenario")
    Observable<EditSceneResponseBean> editScene(@Header("Authorization") String access_token,
                                                @Body RequestBody body);

    // delete scene
    @DELETE("scenario")
    Observable<DeleteSceneResponseBean> deleteScene(@Header("Authorization") String access_token,
                                                    @Query("scenario_id") long sceneId);

    // execute scene
    @POST("scenario/do")
    Observable<ExecuteSceneResponseBean> executeScene(@Header("Authorization") String access_token,
                                                      @Query("scenario_id") long sceneId);

    // cancel scene
    @POST("scenario/cancel")
    Observable<CancelSceneResponseBean> cancelScene(@Header("Authorization") String access_token,
                                                    @Query("scenario_id") long sceneId);
}
