package riccardomamoli.cib_db.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {

    @Value("${mapbox.access.token}")
    private String accessToken;

    private static final String MAPBOX_API_URL = "https://api.mapbox.com/geocoding/v5/mapbox.places/";

    public double[] getCoordinatesFromAddress(String address) {
        String url = MAPBOX_API_URL + address + ".json?access_token=" + accessToken + "&limit=1";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        // Usa org.json.JSONObject
        JSONObject jsonResponse = new JSONObject(response);
        if (jsonResponse.getJSONArray("features").length() > 0) {
            JSONObject geometry = jsonResponse.getJSONArray("features").getJSONObject(0).getJSONObject("geometry");
            double longitude = geometry.getJSONArray("coordinates").getDouble(0);
            double latitude = geometry.getJSONArray("coordinates").getDouble(1);
            return new double[]{latitude, longitude};
        } else {
            throw new RuntimeException("Indirizzo non trovato!");
        }
    }
}
