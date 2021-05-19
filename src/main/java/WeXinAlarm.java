import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WeXinAlarm {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    String corpId = "wwccf8717b9fed87b1";
    String corpSecret = "SOO4aU8k9m_Xz3auXB7Ese1OFIj7yspVvuX3MlzIbqU";

    String getTokenUrl = String.format("https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s",corpId,corpSecret);
    String sendMsgUrlPattern = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";

    String tokenKey = "accessToken";

    Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(7000, TimeUnit.SECONDS)
            .maximumSize(100)
            .build();

    OkHttpClient client = new OkHttpClient();

    public static void main(String[] args) throws IOException {
        WeXinAlarm weXinAlarm = new WeXinAlarm();
        System.out.println(weXinAlarm.getToken());
    }

    public void sendMsg(String msg) throws IOException {
        String token = getToken();
        String sendMsgUrl = String.format(sendMsgUrlPattern, token);
        RequestBody body = RequestBody.create(msg, JSON);
        Request request = new Request.Builder()
                .url(sendMsgUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            JSONObject rsp =  new JSONObject(response.body().string());
            int errorcode = rsp.getInt("errorcode");
            if(errorcode != 0) {
                throw new RuntimeException("send msg errorcode is: " + errorcode);
            }
        }
    }

    public String getToken() throws IOException {

        String token = cache.getIfPresent(tokenKey);

        if(token != null) {
            return token;
        }



        Request request = new Request.Builder()
                .url(getTokenUrl)
                .build();

        JSONObject rsp = null;
        try (Response response = client.newCall(request).execute()) {
            rsp =  new JSONObject(response.body().string());
        }

        rsp.getInt("errorcode");
        token = rsp.getString("access_token");
        cache.put(tokenKey, token);
        return token;

    }
}
