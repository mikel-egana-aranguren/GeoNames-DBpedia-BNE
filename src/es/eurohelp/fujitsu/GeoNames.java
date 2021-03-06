package es.eurohelp.fujitsu;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import java.util.Iterator;
import java.util.Map;

public class GeoNames {
	private String GeoNamesURL;
	private String UserName;

	public GeoNames(String geonamesurl, String username) {
		this.GeoNamesURL = geonamesurl;
		this.UserName = username;
	}

	// http://hc.apache.org/httpcomponents-client-ga/quickstart.html
	public String findNearbyPlaceNameJSON(String latitude, String longitude)
			throws ClientProtocolException, IOException {
		String entity = http_get(GeoNamesURL + "findNearbyPlaceNameJSON?"
				+ "lat=" + latitude + "&lng=" + longitude + "&username="
				+ UserName);
		return entity;
	}

	public String obtainProperPopulatedPlace(String geonameID)
			throws ClientProtocolException, IOException {
		String entity = http_get(GeoNamesURL + "hierarchyJSON?geonameId="
				+ geonameID + "&username=" + UserName);

//		System.out.println(entity);
		Object json_response = JSONValue.parse(entity);
		JSONArray geonames_array = (JSONArray) ((Map) json_response)
				.get("geonames");
		Iterator geonames_array_iterator = geonames_array.iterator();
		while (geonames_array_iterator.hasNext()) {
			Map parent = (Map) geonames_array_iterator.next();
			String fcode = (String) parent.get("fcode");
//			System.out.println(geonameID +"-----"+fcode);
			if (!(fcode.equals("PPLX") || fcode.equals("PPL"))) {
				geonameID = String.valueOf(parent.get("geonameId"));
			}
		}
//		System.out.println("->" + geonameID);
		return geonameID;
	}

	private static String http_get(String url_get)
			throws ClientProtocolException, IOException, ParseException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url_get);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		String entity_string = EntityUtils.toString(entity);
		response.close();
		return entity_string;
	}
}
