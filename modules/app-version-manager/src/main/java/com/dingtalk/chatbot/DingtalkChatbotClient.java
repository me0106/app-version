package com.dingtalk.chatbot;

import java.io.IOException;

import com.dingtalk.chatbot.message.Message;

public class DingtalkChatbotClient {
//    private HttpClient httpclient = HttpClients.createDefault();

    public DingtalkChatbotClient() {
    }

    public SendResult send(String webhook, Message message) throws IOException {
        //TODO
        return null;
//        HttpPost httppost = new HttpPost(webhook);
//        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
//        StringEntity se = new StringEntity(message.toJsonString(), "utf-8");
//        httppost.setEntity(se);
//        SendResult sendResult = new SendResult();
//        HttpResponse response = this.httpclient.execute(httppost);
//        if (response.getStatusLine().getStatusCode() == 200) {
//            String result = EntityUtils.toString(response.getEntity());
//            JSONObject obj = JSONObject.parseObject(result);
//            Integer errcode = obj.getInteger("errcode");
//            sendResult.setErrorCode(errcode);
//            sendResult.setErrorMsg(obj.getString("errmsg"));
//            sendResult.setIsSuccess(errcode.equals(0));
//        }
//
//        return sendResult;
    }
}
