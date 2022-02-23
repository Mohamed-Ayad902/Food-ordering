package com.example.e_commerce.fcm;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIInterface {
    @Headers({
            "Authorization: key=AAAAzc3YQpA:APA91bFXDXSULLyEdyEdyO875Khvq71FxqHm0_ZJ9VKc4sdcCpaFg4aFhRQ3eCVNSsVphABOw7jD3CSFegRT52m-l4viEFkzqcsMgCtrDId4Tpk6RXR3AMFeFqGOhKHjfU4EZQ6P9n-Q",
            "Content-Type:application/json"
    })
    @POST("fcm/send")
    Observable<RemoteMassage> sendNotification(@Body RemoteMassage myNotification);
}
