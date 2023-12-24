package com.syncpeer.syncpeerapp.utils

import org.json.JSONObject

class JsonSerializer {

    companion object{
        @JvmStatic
        fun mapToString(jsonMap: Map<String,String>):String{

            val jsonObject = JSONObject()

            jsonMap.forEach { (k, v) -> jsonObject.put(k,v)}

            return jsonObject.toString()

        }
    }
}