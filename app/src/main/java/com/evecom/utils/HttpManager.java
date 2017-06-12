package com.evecom.utils;

import android.content.Context;
import android.util.Log;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;

public class HttpManager {/*

	public static RequestQueue queue = null;

	public static void loadRoot(Context context, String url,
								final LoadRootListener dataListener) {
		try {
			if (queue == null) {
				queue = Volley.newRequestQueue(context);
			}
			StringRequest request = new StringRequest(url,
					new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							Gson gson = new Gson();
							JsonReader reader = new JsonReader(
									new StringReader(response));

							reader.setLenient(true);
							//
							Root bean = gson.fromJson(reader, Root.class);
							dataListener.OnRootLoadEnd(bean);
						}
					}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Log.e("TAG:ERROR", "请求失败");
				}
			});
			// 把请求对象添加到队列中
			queue.add(request);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface LoadRootListener {
		public void OnRootLoadEnd(Root bean);
	}*/
}
