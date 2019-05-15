package kz.woop.iot.utils;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageFactory {


    public static String stepSensorPayload(int stepCount) {
        JsonObject payload = new JsonObject();
        DateFormat isoDateTimeFormat = new SimpleDateFormat("yy-MM-dd'T'HH:mm");
        String timestamp = isoDateTimeFormat.format(new Date());
        payload.addProperty("step_count", stepCount);
        payload.addProperty("timestamp", timestamp);
        return payload.toString();
    }


}
